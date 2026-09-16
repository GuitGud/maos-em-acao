package br.org.maosemacao.bdd;
import br.org.maosemacao.*;
import br.org.maosemacao.application.*;
import br.org.maosemacao.domain.*;
import io.cucumber.java.*;
import io.cucumber.java.pt.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.*;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.time.*;
import static org.assertj.core.api.Assertions.*;
@CucumberContextConfiguration @SpringBootTest @ActiveProfiles("test") @Import(TestConfiguration.class)
public class Steps {
    @Autowired Actions actions; @Autowired Store store; @Autowired PlatformTransactionManager transactions;
    TransactionStatus tx;Volunteer admin,vol;Campaign campaign;Item item;Shift shift;Registration registration;Delivery delivery;Rule error;
    @Before public void before() {
        tx=transactions.getTransaction(new DefaultTransactionDefinition());error=null;
        admin=store.save(new Volunteer(null,"Gestora","bdd-admin@test.local","hash",true,"ADMIN"));
        vol=store.save(new Volunteer(null,"Ana","bdd-vol@test.local","hash",true,"VOLUNTEER"));
        campaign=actions.createCampaign(admin,"Campanha BDD","Arrecadação comunitária");item=actions.createItem(admin,"Arroz BDD","kg");
    }
    @After public void after() { transactions.rollback(tx); }
    private void attempt(Runnable r) { try { r.run(); } catch(Rule e) { error=e; } }
    @Dado("uma campanha publicada com estoque de {int} unidades") public void published(int quantity) {
        actions.need(admin,campaign.id(),item.id(),100);campaign=actions.publishCampaign(admin,campaign.id());
        if(quantity>0) actions.movement(admin,campaign.id(),item.id(),quantity,"DOACAO","Comunidade");
    }
    @Quando("registro uma entrega de {int} unidades") public void delivery(int quantity) { attempt(()->delivery=actions.deliver(admin,campaign.id(),item.id(),quantity,"FAM-001",3,4,"VULNERABILIDADE")); }
    @Entao("o estoque deve ser {int}") public void stock(int expected) { assertThat(store.get(Item.class,item.id()).balance()).isEqualTo(expected); }
    @Entao("a operação deve ser recusada com {string}") public void failed(String message) { assertThat(error).isNotNull().hasMessageContaining(message); }
    @Entao("a operação deve ser concluída") public void succeeded() { assertThat(error).isNull(); }
    @Quando("estorno a entrega") public void reverse() { attempt(()->actions.reverse(admin,delivery.id(),"Devolução comprovada")); }
    @Dado("um turno futuro com {int} vagas") public void shift(int capacity) { shift=actions.createShift(admin,campaign.id(),"Coleta",LocalDateTime.of(2030,1,2,12,0),LocalDateTime.of(2030,1,2,16,0),capacity); }
    @Dado("um voluntário com cadastro pendente") public void pending() { vol=store.save(new Volunteer(vol.id(),vol.name(),vol.email(),vol.passwordHash(),false,vol.role())); }
    @Quando("o voluntário se inscreve") public void enroll() { attempt(()->registration=actions.enroll(vol,shift.id())); }
    @Quando("o voluntário cancela a inscrição") public void cancel() { attempt(()->actions.cancelRegistration(vol,registration.id())); }
    @Entao("há {int} inscrições ativas") public void count(int count) { assertThat(store.all(Registration.class).stream().filter(r->r.status().equals("INSCRITO")).count()).isEqualTo(count); }
    @Quando("a gestão altera o título do turno") public void edit() { attempt(()->actions.editShift(admin,shift.id(),"Novo local de coleta",shift.startsAt(),shift.endsAt(),shift.capacity())); }
    @Entao("o voluntário recebe uma notificação") public void notified() { assertThat(store.all(Notification.class)).anyMatch(n->n.volunteerId()==vol.id()&&!n.seen()); }
    @Quando("a gestão encerra a campanha") public void close() { attempt(()->actions.closeCampaign(admin,campaign.id())); }
    @Quando("a gestão cancela o turno") public void cancelShift() { attempt(()->actions.finishShift(admin,shift.id(),true)); }
    @Quando("publico a campanha sem necessidades") public void publishWithoutNeeds() { attempt(()->actions.publishCampaign(admin,campaign.id())); }
    @Quando("o voluntário gera um relatório") public void report() { attempt(()->actions.report(vol,"Relatório")); }
    @Quando("registro um ajuste de {int} unidades") public void adjust(int quantity) { attempt(()->actions.movement(admin,campaign.id(),item.id(),quantity,"AJUSTE","Contagem física")); }
    @Dado("que o turno inscrito já terminou") public void past() { store.save(new Shift(shift.id(),campaign.id(),shift.title(),LocalDateTime.of(2029,12,30,12,0),LocalDateTime.of(2029,12,30,16,0),shift.capacity(),"ABERTO"));actions.finishShift(admin,shift.id(),false); }
    @Quando("a gestão confirma a presença") public void attendance() { attempt(()->registration=actions.attendance(admin,registration.id(),true)); }
    @Entao("o voluntário acumula {int} horas") public void hours(int hours) { assertThat(store.get(Registration.class,registration.id()).hours()).isEqualTo(hours); }
    @Quando("o voluntário avalia a ação com nota {int}") public void feedback(int rating) { attempt(()->actions.feedback(vol,registration.id(),rating,"Ação solidária")); }
    @Quando("a gestão aprova o voluntário") public void approve() { attempt(()->vol=actions.approve(admin,vol.id())); }
}
