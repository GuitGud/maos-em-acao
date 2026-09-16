package br.org.maosemacao.application;

import br.org.maosemacao.domain.*;
import java.time.*;
import java.util.*;

/** Casos de uso independentes de HTTP, Spring e JPA. O adaptador de entrada define a transação. */
public class Actions {
    private final Store store;
    private final Events events;
    private final Clock clock;
    private final Reports reports;
    public Actions(Store store, Events events, Clock clock, Reports reports) {
        this.store=store; this.events=events; this.clock=clock; this.reports=reports;
    }
    private LocalDateTime now() { return LocalDateTime.now(clock); }
    public void admin(Volunteer actor) { if(!actor.role().equals("ADMIN")) throw new Rule("Acesso restrito à gestão",403); }
    private void owner(Volunteer actor, long ownerId) {
        if(!actor.role().equals("ADMIN") && actor.id()!=ownerId) throw new Rule("Acesso não autorizado",403);
    }
    private Campaign campaign(long id) { return store.lock(Campaign.class,id); }
    public Campaign createCampaign(Volunteer actor,String name,String description) {
        admin(actor); return store.save(new Campaign(null,Rule.text(name,120),Rule.text(description,2000),"RASCUNHO"));
    }
    public Campaign editCampaign(Volunteer actor,long id,String name,String description) {
        admin(actor); Campaign updated=store.save(campaign(id).edit(name,description));
        notifyCampaign(id,"Campanha atualizada: "+updated.name()); return updated;
    }
    public Campaign publishCampaign(Volunteer actor,long id) {
        admin(actor); Campaign c=campaign(id);
        return store.save(c.publish(store.all(Need.class).stream().anyMatch(n->n.campaignId()==id)));
    }
    public Campaign closeCampaign(Volunteer actor,long id) {
        admin(actor); Campaign c=campaign(id);
        Campaign result=store.save(c.close(store.all(Shift.class).stream().anyMatch(s->s.campaignId()==id && s.status().equals("ABERTO"))));
        notifyCampaign(id,"Campanha encerrada: "+c.name()); return result;
    }
    public Item createItem(Volunteer actor,String name,String unit) {
        admin(actor); String normalized=Rule.text(name,100);
        Rule.require(store.all(Item.class).stream().noneMatch(i->i.name().equalsIgnoreCase(normalized)),"Item já cadastrado");
        return store.save(new Item(null,normalized,Rule.text(unit,30),0));
    }
    public Need need(Volunteer actor,long campaignId,long itemId,int target) {
        admin(actor); Campaign c=campaign(campaignId);
        Rule.require(!c.status().equals("ENCERRADA"),"Campanha encerrada"); store.get(Item.class,itemId);
        Rule.require(target>0,"Meta deve ser positiva");
        Need previous=store.all(Need.class).stream().filter(n->n.campaignId()==campaignId && n.itemId()==itemId).findFirst().orElse(null);
        Need saved=store.save(new Need(previous==null?null:previous.id(),campaignId,itemId,target));
        notifyCampaign(campaignId,"Necessidades atualizadas: "+c.name()); return saved;
    }
    public Movement movement(Volunteer actor,long campaignId,long itemId,int quantity,String kind,String reason) {
        admin(actor); campaign(campaignId).requirePublished();
        Rule.require("DOACAO".equals(kind) || "AJUSTE".equals(kind),"Tipo de movimentação inválido");
        Rule.require(!kind.equals("DOACAO") || quantity>0,"Doação deve ser positiva");
        Item item=store.lock(Item.class,itemId);
        store.save(item.move(quantity));
        return store.save(new Movement(null,itemId,campaignId,quantity,kind,Rule.text(reason,500),now(),actor.id()));
    }
    public Volunteer approve(Volunteer actor,long id) {
        admin(actor); Volunteer v=store.lock(Volunteer.class,id);
        Rule.require(!v.approved(),"Voluntário já aprovado");
        Volunteer saved=store.save(new Volunteer(v.id(),v.name(),v.email(),v.passwordHash(),true,v.role()));
        events.publish(new Events.Change(List.of(id),"Seu cadastro foi aprovado. Você já pode participar dos turnos.")); return saved;
    }
    public Volunteer updateProfile(Volunteer actor,String name) {
        Volunteer v=store.lock(Volunteer.class,actor.id());
        return store.save(new Volunteer(v.id(),Rule.text(name,100),v.email(),v.passwordHash(),v.approved(),v.role()));
    }
    public Shift createShift(Volunteer actor,long campaignId,String title,LocalDateTime start,LocalDateTime end,int capacity) {
        admin(actor); campaign(campaignId).requirePublished(); Shift.validate(start,end,capacity,now());
        return store.save(new Shift(null,campaignId,Rule.text(title,120),start,end,capacity,"ABERTO"));
    }
    public Shift editShift(Volunteer actor,long id,String title,LocalDateTime start,LocalDateTime end,int capacity) {
        admin(actor); Shift original=store.get(Shift.class,id); campaign(original.campaignId()).requirePublished();
        Shift s=store.lock(Shift.class,id); Rule.require(s.status().equals("ABERTO"),"Turno não está aberto");
        Shift.validate(start,end,capacity,now()); List<Registration> active=active(id);
        Rule.require(capacity>=active.size(),"Capacidade menor que inscrições existentes");
        for(long volunteerId:active.stream().mapToLong(Registration::volunteerId).sorted().toArray()) {
            store.lock(Volunteer.class,volunteerId);
            checkConflict(volunteerId,id,start,end);
        }
        Shift updated=store.save(new Shift(id,s.campaignId(),Rule.text(title,120),start,end,capacity,s.status()));
        notifyShift(id,"Turno alterado: "+updated.title()+". Confira as novas datas."); return updated;
    }
    public Shift finishShift(Volunteer actor,long id,boolean cancel) {
        admin(actor); Shift original=store.get(Shift.class,id); campaign(original.campaignId());
        Shift s=store.lock(Shift.class,id); Rule.require(s.status().equals("ABERTO"),"Turno já finalizado");
        Rule.require(cancel || !s.endsAt().isAfter(now()),"Aguarde o término do turno para concluí-lo");
        if(cancel) for(Registration r:active(id)) store.save(new Registration(r.id(),id,r.volunteerId(),"CANCELADA",0));
        // Publicar antes do cancelamento preservaria destinatários; usar todo histórico também avisa ex-inscritos.
        notifyShift(id,cancel?"Turno cancelado: "+s.title():"Turno concluído: "+s.title());
        return store.save(new Shift(id,s.campaignId(),s.title(),s.startsAt(),s.endsAt(),s.capacity(),cancel?"CANCELADO":"CONCLUIDO"));
    }
    private List<Registration> active(long id) {
        return store.all(Registration.class).stream().filter(r->r.shiftId()==id && r.status().equals("INSCRITO")).toList();
    }
    private void checkConflict(long volunteerId,long except,LocalDateTime start,LocalDateTime end) {
        for(Registration r:store.all(Registration.class)) if(r.volunteerId()==volunteerId && r.shiftId()!=except && r.status().equals("INSCRITO")) {
            Shift other=store.get(Shift.class,r.shiftId());
            Rule.require(!start.isBefore(other.endsAt()) || !end.isAfter(other.startsAt()),"Conflito com outro turno inscrito");
        }
    }
    public Registration enroll(Volunteer actor,long shiftId) {
        Shift original=store.get(Shift.class,shiftId); campaign(original.campaignId()).requirePublished();
        Shift shift=store.lock(Shift.class,shiftId);
        Volunteer volunteer=store.lock(Volunteer.class,actor.id());
        Rule.require(volunteer.approved(),"Cadastro ainda não aprovado"); shift.canEnroll(active(shiftId).size(),now());
        Registration previous=store.all(Registration.class).stream().filter(r->r.shiftId()==shiftId && r.volunteerId()==actor.id()).findFirst().orElse(null);
        Rule.require(previous==null || previous.status().equals("CANCELADA"),"Inscrição já existe");
        checkConflict(actor.id(),shiftId,shift.startsAt(),shift.endsAt());
        return store.save(new Registration(previous==null?null:previous.id(),shiftId,actor.id(),"INSCRITO",0));
    }
    public Registration cancelRegistration(Volunteer actor,long id) {
        Registration original=store.get(Registration.class,id); Shift s=store.lock(Shift.class,original.shiftId());
        Registration r=store.get(Registration.class,id); owner(actor,r.volunteerId());
        Rule.require(r.status().equals("INSCRITO") && s.status().equals("ABERTO") && s.startsAt().isAfter(now()),"Inscrição não pode mais ser cancelada");
        return store.save(new Registration(id,r.shiftId(),r.volunteerId(),"CANCELADA",0));
    }
    public Registration attendance(Volunteer actor,long id,boolean attended) {
        admin(actor); Registration original=store.get(Registration.class,id); Shift s=store.lock(Shift.class,original.shiftId());
        Registration r=store.lock(Registration.class,id);
        Rule.require(s.status().equals("CONCLUIDO") && r.status().equals("INSCRITO"),"Presença exige turno concluído e inscrição pendente");
        double hours=attended?Duration.between(s.startsAt(),s.endsAt()).toMinutes()/60.0:0;
        return store.save(new Registration(id,r.shiftId(),r.volunteerId(),attended?"PRESENTE":"AUSENTE",hours));
    }
    public Delivery deliver(Volunteer actor,long campaignId,long itemId,int quantity,String beneficiary,int vulnerability,int members,String strategy) {
        Campaign c=campaign(campaignId); c.requirePublished();
        if(!actor.role().equals("ADMIN")) {
            Rule.require(actor.approved(),"Cadastro ainda não aprovado");
            Rule.require(store.all(Registration.class).stream().anyMatch(r->r.volunteerId()==actor.id() && (r.status().equals("INSCRITO")||r.status().equals("PRESENTE")) && store.get(Shift.class,r.shiftId()).campaignId()==campaignId),"É necessário participar desta campanha para registrar entregas");
        }
        Rule.require(quantity>0,"Quantidade deve ser positiva");
        Rule.require(vulnerability>=1 && vulnerability<=5 && members>=1 && members<=100,"Vulnerabilidade ou número de membros inválido");
        String recipient=Rule.text(beneficiary,150); int priority=PriorityStrategy.named(strategy).score(vulnerability,members);
        Item item=store.lock(Item.class,itemId); store.save(item.move(-quantity));
        Delivery d=store.save(new Delivery(null,campaignId,itemId,quantity,recipient,vulnerability,members,priority,"ENTREGUE",now(),actor.id(),"Entrega registrada"));
        store.save(new Movement(null,itemId,campaignId,-quantity,"ENTREGA","Entrega #"+d.id(),now(),actor.id())); return d;
    }
    public Delivery reverse(Volunteer actor,long id,String reason) {
        admin(actor); Delivery original=store.get(Delivery.class,id); campaign(original.campaignId());
        Delivery d=store.lock(Delivery.class,id); Rule.require(d.status().equals("ENTREGUE"),"Entrega já estornada");
        String why=Rule.text(reason,500); Item i=store.lock(Item.class,d.itemId()); store.save(i.move(d.quantity()));
        store.save(new Movement(null,d.itemId(),d.campaignId(),d.quantity(),"ESTORNO","Estorno #"+id+": "+why,now(),actor.id()));
        return store.save(new Delivery(id,d.campaignId(),d.itemId(),d.quantity(),d.beneficiary(),d.vulnerability(),d.members(),d.priority(),"ESTORNADA",d.createdAt(),d.actorId(),why));
    }
    public Notification read(Volunteer actor,long id) {
        Notification n=store.lock(Notification.class,id); owner(actor,n.volunteerId());
        return store.save(new Notification(id,n.volunteerId(),n.message(),true,n.createdAt()));
    }
    public Feedback feedback(Volunteer actor,long registrationId,int rating,String comment) {
        Registration r=store.lock(Registration.class,registrationId); owner(actor,r.volunteerId());
        Rule.require(r.status().equals("PRESENTE"),"Feedback exige presença confirmada");
        Rule.require(rating>=1 && rating<=5,"Nota deve estar entre 1 e 5");
        Feedback old=store.all(Feedback.class).stream().filter(f->f.registrationId()==registrationId).findFirst().orElse(null);
        return store.save(new Feedback(old==null?null:old.id(),registrationId,rating,Rule.text(comment,1000)));
    }
    public Report report(Volunteer actor,String title) {
        String content=reports.generate(actor);
        return store.save(new Report(null,Rule.text(title,120),content,now(),actor.id()));
    }
    public void deleteReport(Volunteer actor,long id) { admin(actor); store.get(Report.class,id); store.delete(Report.class,id); }
    private void notifyShift(long id,String message) {
        events.publish(new Events.Change(store.all(Registration.class).stream().filter(r->r.shiftId()==id).map(Registration::volunteerId).toList(),message));
    }
    private void notifyCampaign(long id,String message) {
        Set<Long> shifts=new HashSet<>(); store.all(Shift.class).stream().filter(s->s.campaignId()==id).forEach(s->shifts.add(s.id()));
        events.publish(new Events.Change(store.all(Registration.class).stream().filter(r->shifts.contains(r.shiftId())).map(Registration::volunteerId).toList(),message));
    }
}
