package br.org.maosemacao;
import br.org.maosemacao.application.*;
import br.org.maosemacao.domain.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.*;
import java.time.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Import(TestConfiguration.class) @Transactional
class ApplicationTest {
    @Autowired Actions actions; @Autowired Store store; @Autowired MockMvc mvc; @Autowired PasswordEncoder passwords;
    Volunteer admin,vol,other;Campaign campaign;Item item;Shift shift;
    @BeforeEach void setup() {
        admin=store.save(new Volunteer(null,"Gestora","gestor@test.local",passwords.encode("GestorSenha123!"),true,"ADMIN"));
        vol=store.save(new Volunteer(null,"Ana","ana@test.local",passwords.encode("Voluntario123!"),true,"VOLUNTEER"));
        other=store.save(new Volunteer(null,"Bia","bia@test.local",passwords.encode("Voluntario123!"),false,"VOLUNTEER"));
        campaign=actions.createCampaign(admin,"Alimentos","Distribuição de alimentos");item=actions.createItem(admin,"Arroz","kg");
        actions.need(admin,campaign.id(),item.id(),100);campaign=actions.publishCampaign(admin,campaign.id());
        shift=actions.createShift(admin,campaign.id(),"Coleta",LocalDateTime.of(2030,1,2,12,0),LocalDateTime.of(2030,1,2,15,0),1);
    }
    @Test void donationsAdjustmentsAndReversalKeepLedger() {
        actions.movement(admin,campaign.id(),item.id(),20,"DOACAO","Mercado");
        var d=actions.deliver(admin,campaign.id(),item.id(),7,"F-001",5,4,"VULNERABILIDADE");
        assertThat(store.get(Item.class,item.id()).balance()).isEqualTo(13);
        actions.reverse(admin,d.id(),"Devolução integral");
        assertThat(store.get(Item.class,item.id()).balance()).isEqualTo(20);
        assertThat(store.all(Movement.class).stream().mapToInt(Movement::quantity).sum()).isEqualTo(20);
        assertThatThrownBy(()->actions.reverse(admin,d.id(),"Repetição")).hasMessageContaining("já estornada");
    }
    @Test void insufficientStockDoesNotCreateDelivery() {
        actions.movement(admin,campaign.id(),item.id(),2,"DOACAO","Mercado");
        assertThatThrownBy(()->actions.deliver(admin,campaign.id(),item.id(),3,"F-01",1,2,"VULNERABILIDADE")).hasMessageContaining("insuficiente");
        assertThat(store.all(Delivery.class)).isEmpty();assertThat(store.get(Item.class,item.id()).balance()).isEqualTo(2);
    }
    @Test void capacityDuplicateCancellationAndReentry() {
        var r=actions.enroll(vol,shift.id());
        assertThatThrownBy(()->actions.enroll(vol,shift.id())).isInstanceOf(Rule.class);
        actions.approve(admin,other.id());
        assertThatThrownBy(()->actions.enroll(other,shift.id())).hasMessageContaining("lotado");
        actions.cancelRegistration(vol,r.id());var again=actions.enroll(vol,shift.id());
        assertThat(again.id()).isEqualTo(r.id());assertThat(again.status()).isEqualTo("INSCRITO");
    }
    @Test void pendingVolunteerCannotEnroll() { assertThatThrownBy(()->actions.enroll(other,shift.id())).hasMessageContaining("aprovado"); }
    @Test void overlappingShiftsAreRejected() {
        actions.enroll(vol,shift.id());var second=actions.createShift(admin,campaign.id(),"Distribuição",shift.startsAt().plusHours(1),shift.endsAt().plusHours(1),4);
        assertThatThrownBy(()->actions.enroll(vol,second.id())).hasMessageContaining("Conflito");
    }
    @Test void shiftChangeAndCampaignChangeNotifyParticipants() {
        actions.enroll(vol,shift.id());actions.editShift(admin,shift.id(),"Novo local",shift.startsAt(),shift.endsAt(),2);
        actions.editCampaign(admin,campaign.id(),"Alimentos 2030","Novo propósito");
        assertThat(store.all(Notification.class)).hasSize(2).allMatch(n->n.volunteerId()==vol.id());
        assertThat(actions.read(vol,store.all(Notification.class).getFirst().id()).seen()).isTrue();
    }
    @Test void cannotReduceCapacityBelowOccupancy() {
        actions.editShift(admin,shift.id(),shift.title(),shift.startsAt(),shift.endsAt(),2);actions.enroll(vol,shift.id());actions.approve(admin,other.id());actions.enroll(other,shift.id());
        assertThatThrownBy(()->actions.editShift(admin,shift.id(),"Menor",shift.startsAt(),shift.endsAt(),1)).hasMessageContaining("Capacidade menor");
    }
    @Test void cancelledShiftReleasesRegistrationsAndNotifies() {
        actions.enroll(vol,shift.id());actions.finishShift(admin,shift.id(),true);
        assertThat(store.all(Registration.class).getFirst().status()).isEqualTo("CANCELADA");
        assertThat(store.all(Notification.class)).hasSize(1);
        assertThat(actions.closeCampaign(admin,campaign.id()).status()).isEqualTo("ENCERRADA");
    }
    @Test void campaignCannotCloseWithOpenShifts() { assertThatThrownBy(()->actions.closeCampaign(admin,campaign.id())).hasMessageContaining("turnos abertos"); }
    @Test void cannotFinishFutureShift() { assertThatThrownBy(()->actions.finishShift(admin,shift.id(),false)).hasMessageContaining("Aguarde"); }
    @Test void attendanceHoursFeedbackAndBadge() {
        var r=actions.enroll(vol,shift.id());
        store.save(new Shift(shift.id(),campaign.id(),"Passado",LocalDateTime.of(2029,12,30,8,0),LocalDateTime.of(2029,12,30,11,30),1,"ABERTO"));
        actions.finishShift(admin,shift.id(),false);assertThat(actions.attendance(admin,r.id(),true).hours()).isEqualTo(3.5);
        var feedback=actions.feedback(vol,r.id(),5,"Excelente organização");
        assertThat(actions.feedback(vol,r.id(),4,"Atualizando avaliação").id()).isEqualTo(feedback.id());
        assertThatThrownBy(()->actions.attendance(admin,r.id(),true)).isInstanceOf(Rule.class);
        assertThat(actions.report(admin,"Balanço").content()).contains("Horas voluntárias: 3.5");
    }
    @Test void feedbackRequiresConfirmedPresenceAndValidRating() {
        var r=actions.enroll(vol,shift.id());assertThatThrownBy(()->actions.feedback(vol,r.id(),5,"Bom")).hasMessageContaining("presença");
        store.save(new Registration(r.id(),shift.id(),vol.id(),"PRESENTE",2));
        assertThatThrownBy(()->actions.feedback(vol,r.id(),6,"Bom")).hasMessageContaining("Nota");
    }
    @Test void registrationAndNoticeOwnershipIsEnforced() {
        var r=actions.enroll(vol,shift.id());assertThatThrownBy(()->actions.cancelRegistration(other,r.id())).isInstanceOf(Rule.class).hasMessageContaining("autorizado");
        actions.editCampaign(admin,campaign.id(),"Mudança","Nova descrição");
        assertThatThrownBy(()->actions.read(other,store.all(Notification.class).getFirst().id())).hasMessageContaining("autorizado");
    }
    @Test void volunteerCanDeliverOnlyForOwnCampaign() {
        actions.movement(admin,campaign.id(),item.id(),10,"DOACAO","Teste");
        assertThatThrownBy(()->actions.deliver(vol,campaign.id(),item.id(),1,"F1",2,2,"VULNERABILIDADE")).hasMessageContaining("participar");
        actions.enroll(vol,shift.id());assertThat(actions.deliver(vol,campaign.id(),item.id(),1,"F1",2,2,"VULNERABILIDADE").status()).isEqualTo("ENTREGUE");
    }
    @Test void reportsSeparateUnitsAndPreserveSnapshotsAfterReversalAndAdjustment() {
        var beans=actions.createItem(admin,"Feijão","kg");
        var kit=actions.createItem(admin,"Higiene","kit");
        var grams=actions.createItem(admin,"Tempero","g");
        actions.movement(admin,campaign.id(),item.id(),120,"DOACAO","Arroz");
        actions.movement(admin,campaign.id(),beans.id(),100,"DOACAO","Feijão");
        actions.movement(admin,campaign.id(),kit.id(),2,"DOACAO","Kits");
        actions.movement(admin,campaign.id(),grams.id(),500,"DOACAO","Tempero");
        actions.movement(admin,campaign.id(),item.id(),-5,"AJUSTE","Contagem física");
        actions.deliver(admin,campaign.id(),beans.id(),10,"F1",2,2,"VULNERABILIDADE");
        var reversed=actions.deliver(admin,campaign.id(),kit.id(),1,"F2",2,2,"VULNERABILIDADE");
        actions.reverse(admin,reversed.id(),"Devolução");
        var saved=actions.report(admin,"Unidades distintas");
        assertThat(saved.content()).isEqualTo("Doações recebidas: 500 g; 220 kg; 2 kit\nEstoque atual: 500 g; 205 kg; 2 kit\nItens entregues: 0 g; 10 kg; 0 kit\nHoras voluntárias: 0.0");
        actions.movement(admin,campaign.id(),kit.id(),3,"DOACAO","Nova doação");
        assertThat(store.get(Report.class,saved.id()).content()).isEqualTo(saved.content());
        assertThat(actions.report(admin,"Novo retrato").content()).contains("Estoque atual: 500 g; 205 kg; 5 kit");
    }
    @Test void reportsHandleZeroStockAndEmptyCatalog() {
        assertThat(actions.report(admin,"Sem movimentos").content()).isEqualTo("Doações recebidas: 0 kg\nEstoque atual: 0 kg\nItens entregues: 0 kg\nHoras voluntárias: 0.0");
        for(var need:store.all(Need.class)) store.delete(Need.class,need.id());
        store.delete(Item.class,item.id());
        assertThat(actions.report(admin,"Sem catálogo").content()).isEqualTo("Doações recebidas: Nenhum item cadastrado\nEstoque atual: Nenhum item cadastrado\nItens entregues: Nenhum item cadastrado\nHoras voluntárias: 0.0");
    }
    @Test void reportTotalsUseLongAcrossItemsWithTheSameUnit() {
        var beans=actions.createItem(admin,"Feijão","kg");
        actions.movement(admin,campaign.id(),item.id(),Integer.MAX_VALUE,"DOACAO","Arroz");
        actions.movement(admin,campaign.id(),beans.id(),Integer.MAX_VALUE,"DOACAO","Feijão");
        assertThat(actions.report(admin,"Total grande").content()).contains("Doações recebidas: 4294967294 kg\nEstoque atual: 4294967294 kg");
    }
    @Test void reportAccessAndDeletion() {
        assertThatThrownBy(()->actions.report(vol,"Proibido")).hasMessageContaining("restrito");
        var r=actions.report(admin,"Mensal"); actions.deleteReport(admin,r.id()); assertThat(store.all(Report.class)).isEmpty();
    }
    @Test void stateNeverLeaksPasswordHashOrOtherPeopleToVolunteer() throws Exception {
        mvc.perform(get("/api/state").with(user(vol.email()).roles("VOLUNTEER"))).andExpect(status().isOk())
            .andExpect(jsonPath("$.volunteers.length()").value(1)).andExpect(jsonPath("$.me.passwordHash").doesNotExist())
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("$2a$"))));
    }
    @Test void anonymousIsUnauthorizedAndMutationsRequireCsrf() throws Exception {
        mvc.perform(get("/api/state")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/campaigns").with(user(admin.email())).contentType("application/json").content("{\"name\":\"X\",\"description\":\"Y\"}")).andExpect(status().isForbidden());
    }
    @Test void volunteerCannotCreateCampaignOverHttp() throws Exception {
        mvc.perform(post("/api/campaigns").with(user(vol.email())).with(csrf()).contentType("application/json").content("{\"name\":\"X\",\"description\":\"Y\"}")).andExpect(status().isForbidden());
    }
    @Test void signupValidatesEmailAndPasswordsAndStoresHash() throws Exception {
        mvc.perform(post("/api/register").with(csrf()).contentType("application/json").content("{\"name\":\"João\",\"email\":\"invalid\",\"password\":\"short\"}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/register").with(csrf()).contentType("application/json").content("{\"name\":\"João\",\"email\":\"joao@test.local\",\"password\":\"SenhaSegura123!\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.approved").value(false)).andExpect(jsonPath("$.role").value("VOLUNTEER"));
        var v=store.all(Volunteer.class).stream().filter(u->u.email().equals("joao@test.local")).findFirst().orElseThrow();
        assertThat(passwords.matches("SenhaSegura123!",v.passwordHash())).isTrue();
    }
    @Test void loginRejectsWrongPasswordAndAcceptsCorrectPassword() throws Exception {
        mvc.perform(post("/api/login").with(csrf()).param("email",vol.email()).param("password","wrong")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/login").with(csrf()).param("email",vol.email()).param("password","Voluntario123!")).andExpect(status().isNoContent());
    }
    @Test void invalidDeliveryHttpRollsBackStock() throws Exception {
        actions.movement(admin,campaign.id(),item.id(),10,"DOACAO","Teste");
        mvc.perform(post("/api/deliveries").with(user(admin.email())).with(csrf()).contentType("application/json")
            .content("{\"campaignId\":"+campaign.id()+",\"itemId\":"+item.id()+",\"quantity\":2,\"beneficiary\":\"F1\",\"vulnerability\":3,\"members\":1,\"strategy\":\"INVALIDA\"}"))
            .andExpect(status().isUnprocessableEntity());
        assertThat(store.get(Item.class,item.id()).balance()).isEqualTo(10);
    }
}
