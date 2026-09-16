package br.org.maosemacao.web;
import br.org.maosemacao.application.*;
import br.org.maosemacao.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/api") @Transactional
public class Api {
    private final Store store; private final Actions actions; private final PasswordEncoder passwords;
    public Api(Store store,Actions actions,PasswordEncoder passwords) { this.store=store;this.actions=actions;this.passwords=passwords; }
    private Volunteer actor(Principal p) {
        if(p==null) throw new Rule("Entre para continuar",401);
        return store.all(Volunteer.class).stream().filter(v->v.email().equalsIgnoreCase(p.getName())).findFirst().orElseThrow(()->new Rule("Usuário não encontrado",401));
    }
    public record Person(Long id,String name,String email,boolean approved,String role,double hours,String badge) { }
    private Person person(Volunteer v) {
        double hours=store.all(Registration.class).stream().filter(r->r.volunteerId()==v.id()).mapToDouble(Registration::hours).sum();
        return new Person(v.id(),v.name(),v.email(),v.approved(),v.role(),hours,VolunteerBadge.forHours(hours));
    }
    public record Signup(@NotBlank @Size(max=100) String name,@NotBlank @Email @Size(max=180) String email,@NotBlank @Size(min=12,max=64) String password) { }
    public record CampaignInput(String name,String description) { }
    public record ItemInput(String name,String unit) { }
    public record NeedInput(long itemId,int target) { }
    public record MovementInput(long campaignId,long itemId,int quantity,String kind,String reason) { }
    public record ShiftInput(long campaignId,String title,LocalDateTime startsAt,LocalDateTime endsAt,int capacity) { }
    public record AttendanceInput(boolean attended) { }
    public record DeliveryInput(long campaignId,long itemId,int quantity,String beneficiary,int vulnerability,int members,@NotNull String strategy) { }
    public record ReasonInput(String reason) { }
    public record FeedbackInput(long registrationId,int rating,String comment) { }
    public record TitleInput(String title) { }
    public record ProfileInput(String name) { }
    @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken token) { return Map.of("token",token.getToken(),"headerName",token.getHeaderName()); }
    @PostMapping("/register") public Person register(@Valid @RequestBody Signup input) {
        String email=input.email().strip().toLowerCase(Locale.ROOT);
        Rule.require(store.all(Volunteer.class).stream().noneMatch(v->v.email().equals(email)),"E-mail já cadastrado");
        return person(store.save(new Volunteer(null,input.name().strip(),email,passwords.encode(input.password()),false,"VOLUNTEER")));
    }
    @GetMapping("/state") @Transactional(readOnly=true) public Map<String,Object> state(Principal p) {
        Volunteer me=actor(p); boolean admin=me.role().equals("ADMIN");
        Map<String,Object> result=new LinkedHashMap<>(); result.put("me",person(me));
        result.put("campaigns",store.all(Campaign.class).stream().filter(c->admin || !c.status().equals("RASCUNHO")).toList());
        Set<Long> visible=store.all(Campaign.class).stream().filter(c->admin || !c.status().equals("RASCUNHO")).map(Campaign::id).collect(java.util.stream.Collectors.toSet());
        result.put("items",store.all(Item.class)); result.put("needs",store.all(Need.class).stream().filter(n->visible.contains(n.campaignId())).toList());
        result.put("shifts",store.all(Shift.class).stream().filter(s->visible.contains(s.campaignId())).toList());
        result.put("registrations",store.all(Registration.class).stream().filter(r->admin || r.volunteerId()==me.id()).toList());
        result.put("occupancy",store.all(Registration.class).stream().filter(r->r.status().equals("INSCRITO")).collect(java.util.stream.Collectors.groupingBy(Registration::shiftId,java.util.stream.Collectors.counting())));
        result.put("volunteers",admin?store.all(Volunteer.class).stream().map(this::person).toList():List.of(person(me)));
        result.put("movements",admin?store.all(Movement.class):List.of());
        result.put("deliveries",store.all(Delivery.class).stream().filter(d->admin || d.actorId()==me.id()).toList());
        result.put("notifications",store.all(Notification.class).stream().filter(n->n.volunteerId()==me.id()).toList());
        Set<Long> own=store.all(Registration.class).stream().filter(r->r.volunteerId()==me.id()).map(Registration::id).collect(java.util.stream.Collectors.toSet());
        result.put("feedback",store.all(Feedback.class).stream().filter(f->admin || own.contains(f.registrationId())).toList());
        result.put("reports",admin?store.all(Report.class):List.of()); return result;
    }
    @PostMapping("/campaigns") public Campaign campaign(Principal p,@RequestBody CampaignInput i) { return actions.createCampaign(actor(p),i.name(),i.description()); }
    @PutMapping("/campaigns/{id}") public Campaign campaign(Principal p,@PathVariable long id,@RequestBody CampaignInput i) { return actions.editCampaign(actor(p),id,i.name(),i.description()); }
    @PostMapping("/campaigns/{id}/publish") public Campaign publish(Principal p,@PathVariable long id) { return actions.publishCampaign(actor(p),id); }
    @PostMapping("/campaigns/{id}/close") public Campaign close(Principal p,@PathVariable long id) { return actions.closeCampaign(actor(p),id); }
    @PostMapping("/campaigns/{id}/needs") public Need need(Principal p,@PathVariable long id,@RequestBody NeedInput i) { return actions.need(actor(p),id,i.itemId(),i.target()); }
    @PostMapping("/items") public Item item(Principal p,@RequestBody ItemInput i) { return actions.createItem(actor(p),i.name(),i.unit()); }
    @PostMapping("/movements") public Movement move(Principal p,@RequestBody MovementInput i) { return actions.movement(actor(p),i.campaignId(),i.itemId(),i.quantity(),i.kind(),i.reason()); }
    @PostMapping("/volunteers/{id}/approve") public Person approve(Principal p,@PathVariable long id) { return person(actions.approve(actor(p),id)); }
    @PutMapping("/profile") public Person profile(Principal p,@RequestBody ProfileInput i) { return person(actions.updateProfile(actor(p),i.name())); }
    @PostMapping("/shifts") public Shift shift(Principal p,@RequestBody ShiftInput i) { return actions.createShift(actor(p),i.campaignId(),i.title(),i.startsAt(),i.endsAt(),i.capacity()); }
    @PutMapping("/shifts/{id}") public Shift shift(Principal p,@PathVariable long id,@RequestBody ShiftInput i) { return actions.editShift(actor(p),id,i.title(),i.startsAt(),i.endsAt(),i.capacity()); }
    @PostMapping("/shifts/{id}/cancel") public Shift cancel(Principal p,@PathVariable long id) { return actions.finishShift(actor(p),id,true); }
    @PostMapping("/shifts/{id}/finish") public Shift finish(Principal p,@PathVariable long id) { return actions.finishShift(actor(p),id,false); }
    @PostMapping("/shifts/{id}/enroll") public Registration enroll(Principal p,@PathVariable long id) { return actions.enroll(actor(p),id); }
    @PostMapping("/registrations/{id}/cancel") public Registration cancelRegistration(Principal p,@PathVariable long id) { return actions.cancelRegistration(actor(p),id); }
    @PostMapping("/registrations/{id}/attendance") public Registration attendance(Principal p,@PathVariable long id,@RequestBody AttendanceInput i) { return actions.attendance(actor(p),id,i.attended()); }
    @PostMapping("/deliveries") public Delivery delivery(Principal p,@Valid @RequestBody DeliveryInput i) { return actions.deliver(actor(p),i.campaignId(),i.itemId(),i.quantity(),i.beneficiary(),i.vulnerability(),i.members(),i.strategy()); }
    @PostMapping("/deliveries/{id}/reverse") public Delivery reverse(Principal p,@PathVariable long id,@RequestBody ReasonInput i) { return actions.reverse(actor(p),id,i.reason()); }
    @PostMapping("/notifications/{id}/read") public Notification read(Principal p,@PathVariable long id) { return actions.read(actor(p),id); }
    @PostMapping("/feedback") public Feedback feedback(Principal p,@RequestBody FeedbackInput i) { return actions.feedback(actor(p),i.registrationId(),i.rating(),i.comment()); }
    @PostMapping("/reports") public Report report(Principal p,@RequestBody TitleInput i) { return actions.report(actor(p),i.title()); }
    @DeleteMapping("/reports/{id}") public void deleteReport(Principal p,@PathVariable long id) { actions.deleteReport(actor(p),id); }
}
