package br.org.maosemacao.infrastructure.persistence;
import br.org.maosemacao.domain.*;
import br.org.maosemacao.application.Store;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaStore implements Store {
    @PersistenceContext private EntityManager em;
    private Class<?> rowClass(Class<?> type) {
        if (type == Campaign.class) return CampaignRow.class;
        if (type == Item.class) return ItemRow.class;
        if (type == Need.class) return NeedRow.class;
        if (type == Volunteer.class) return VolunteerRow.class;
        if (type == Shift.class) return ShiftRow.class;
        if (type == Registration.class) return RegistrationRow.class;
        if (type == Movement.class) return MovementRow.class;
        if (type == Delivery.class) return DeliveryRow.class;
        if (type == Notification.class) return NotificationRow.class;
        if (type == Feedback.class) return FeedbackRow.class;
        if (type == Report.class) return ReportRow.class;
        throw new IllegalArgumentException("Tipo desconhecido");
    }
    @SuppressWarnings("unchecked")
    private <T> T domain(Object row) {
        if (row instanceof CampaignRow r) return (T) new Campaign(r.id, r.name, r.description, r.status);
        if (row instanceof ItemRow r) return (T) new Item(r.id, r.name, r.unit, r.balance);
        if (row instanceof NeedRow r) return (T) new Need(r.id, r.campaignId, r.itemId, r.target);
        if (row instanceof VolunteerRow r) return (T) new Volunteer(r.id, r.name, r.email, r.passwordHash, r.approved, r.role);
        if (row instanceof ShiftRow r) return (T) new Shift(r.id, r.campaignId, r.title, r.startsAt, r.endsAt, r.capacity, r.status);
        if (row instanceof RegistrationRow r) return (T) new Registration(r.id, r.shiftId, r.volunteerId, r.status, r.hours);
        if (row instanceof MovementRow r) return (T) new Movement(r.id, r.itemId, r.campaignId, r.quantity, r.kind, r.reason, r.createdAt, r.actorId);
        if (row instanceof DeliveryRow r) return (T) new Delivery(r.id, r.campaignId, r.itemId, r.quantity, r.beneficiary, r.vulnerability, r.members, r.priority, r.status, r.createdAt, r.actorId, r.reason);
        if (row instanceof NotificationRow r) return (T) new Notification(r.id, r.volunteerId, r.message, r.seen, r.createdAt);
        if (row instanceof FeedbackRow r) return (T) new Feedback(r.id, r.registrationId, r.rating, r.comment);
        if (row instanceof ReportRow r) return (T) new Report(r.id, r.title, r.content, r.createdAt, r.actorId);
        throw new IllegalArgumentException("Tipo desconhecido");
    }
    public <T> T get(Class<T> type, long id) {
        Object row=em.find(rowClass(type), id);
        if (row==null) throw new Rule("Registro não encontrado", 404);
        return domain(row);
    }
    public <T> T lock(Class<T> type, long id) {
        Object row=em.find(rowClass(type), id, LockModeType.PESSIMISTIC_WRITE);
        if (row==null) throw new Rule("Registro não encontrado", 404);
        em.refresh(row, LockModeType.PESSIMISTIC_WRITE);
        return domain(row);
    }
    public <T> List<T> all(Class<T> type) {
        return em.createQuery("select r from "+rowClass(type).getSimpleName()+" r order by r.id", rowClass(type))
            .getResultList().stream().map(r -> this.<T>domain(r)).toList();
    }
    public <T> T save(T value) {
        if (value instanceof Campaign v) {
            CampaignRow r=v.id()==null ? new CampaignRow() : em.find(CampaignRow.class, v.id());
            r.name=v.name();
            r.description=v.description();
            r.status=v.status();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Item v) {
            ItemRow r=v.id()==null ? new ItemRow() : em.find(ItemRow.class, v.id());
            r.name=v.name();
            r.unit=v.unit();
            r.balance=v.balance();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Need v) {
            NeedRow r=v.id()==null ? new NeedRow() : em.find(NeedRow.class, v.id());
            r.campaignId=v.campaignId();
            r.itemId=v.itemId();
            r.target=v.target();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Volunteer v) {
            VolunteerRow r=v.id()==null ? new VolunteerRow() : em.find(VolunteerRow.class, v.id());
            r.name=v.name();
            r.email=v.email();
            r.passwordHash=v.passwordHash();
            r.approved=v.approved();
            r.role=v.role();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Shift v) {
            ShiftRow r=v.id()==null ? new ShiftRow() : em.find(ShiftRow.class, v.id());
            r.campaignId=v.campaignId();
            r.title=v.title();
            r.startsAt=v.startsAt();
            r.endsAt=v.endsAt();
            r.capacity=v.capacity();
            r.status=v.status();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Registration v) {
            RegistrationRow r=v.id()==null ? new RegistrationRow() : em.find(RegistrationRow.class, v.id());
            r.shiftId=v.shiftId();
            r.volunteerId=v.volunteerId();
            r.status=v.status();
            r.hours=v.hours();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Movement v) {
            MovementRow r=v.id()==null ? new MovementRow() : em.find(MovementRow.class, v.id());
            r.itemId=v.itemId();
            r.campaignId=v.campaignId();
            r.quantity=v.quantity();
            r.kind=v.kind();
            r.reason=v.reason();
            r.createdAt=v.createdAt();
            r.actorId=v.actorId();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Delivery v) {
            DeliveryRow r=v.id()==null ? new DeliveryRow() : em.find(DeliveryRow.class, v.id());
            r.campaignId=v.campaignId();
            r.itemId=v.itemId();
            r.quantity=v.quantity();
            r.beneficiary=v.beneficiary();
            r.vulnerability=v.vulnerability();
            r.members=v.members();
            r.priority=v.priority();
            r.status=v.status();
            r.createdAt=v.createdAt();
            r.actorId=v.actorId();
            r.reason=v.reason();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Notification v) {
            NotificationRow r=v.id()==null ? new NotificationRow() : em.find(NotificationRow.class, v.id());
            r.volunteerId=v.volunteerId();
            r.message=v.message();
            r.seen=v.seen();
            r.createdAt=v.createdAt();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Feedback v) {
            FeedbackRow r=v.id()==null ? new FeedbackRow() : em.find(FeedbackRow.class, v.id());
            r.registrationId=v.registrationId();
            r.rating=v.rating();
            r.comment=v.comment();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        if (value instanceof Report v) {
            ReportRow r=v.id()==null ? new ReportRow() : em.find(ReportRow.class, v.id());
            r.title=v.title();
            r.content=v.content();
            r.createdAt=v.createdAt();
            r.actorId=v.actorId();
            if(v.id()==null) em.persist(r);
            em.flush();
            return domain(r);
        }
        throw new IllegalArgumentException("Tipo desconhecido");
    }
    public <T> void delete(Class<T> type, long id) { em.remove(em.getReference(rowClass(type),id)); }
}
