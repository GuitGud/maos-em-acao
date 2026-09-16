package br.org.maosemacao;
import br.org.maosemacao.application.*;
import br.org.maosemacao.domain.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:concurrency;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000")
@ActiveProfiles("test") @Import(TestConfiguration.class)
class ConcurrencyTest {
    @Autowired Store store; @Autowired Actions actions; @Autowired PlatformTransactionManager manager;
    record Fixture(Volunteer admin,Volunteer a,Volunteer b,Campaign campaign,Item item,Shift shift) {}
    Fixture fixture() {
        return new TransactionTemplate(manager).execute(s->{
            String suffix=UUID.randomUUID().toString();
            var admin=store.save(new Volunteer(null,"Gestão","admin-"+suffix,"hash",true,"ADMIN"));
            var a=store.save(new Volunteer(null,"Ana","a-"+suffix,"hash",true,"VOLUNTEER"));
            var b=store.save(new Volunteer(null,"Bia","b-"+suffix,"hash",true,"VOLUNTEER"));
            var c=actions.createCampaign(admin,"Campanha concorrente","Teste real de transações");var i=actions.createItem(admin,"Item-"+suffix,"un");
            actions.need(admin,c.id(),i.id(),20);c=actions.publishCampaign(admin,c.id());actions.movement(admin,c.id(),i.id(),10,"DOACAO","Teste");
            var shift=actions.createShift(admin,c.id(),"Última vaga",LocalDateTime.of(2030,1,3,12,0),LocalDateTime.of(2030,1,3,16,0),1);
            return new Fixture(admin,a,b,c,i,shift);
        });
    }
    List<Boolean> race(Runnable a,Runnable b) throws Exception {
        CountDownLatch ready=new CountDownLatch(2),go=new CountDownLatch(1);
        try(var pool=Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Boolean>> futures=new ArrayList<>();
            for(Runnable command:List.of(a,b)) futures.add(pool.submit(()->{
                ready.countDown();go.await();
                try { new TransactionTemplate(manager).executeWithoutResult(s->command.run());return true; }
                catch(Rule expected) { return false; }
            }));
            assertThat(ready.await(5,TimeUnit.SECONDS)).isTrue();go.countDown();
            return List.of(futures.get(0).get(15,TimeUnit.SECONDS),futures.get(1).get(15,TimeUnit.SECONDS));
        }
    }
    @Test void onlyOnePersonGetsTheLastSeat() throws Exception {
        var f=fixture();assertThat(race(()->actions.enroll(f.a(),f.shift().id()),()->actions.enroll(f.b(),f.shift().id()))).containsExactlyInAnyOrder(true,false);
        new TransactionTemplate(manager).executeWithoutResult(s->assertThat(store.all(Registration.class).stream().filter(r->r.shiftId()==f.shift().id())).hasSize(1));
    }
    @Test void simultaneousDeliveriesCannotSpendTheSameStock() throws Exception {
        var f=fixture();Runnable delivery=()->actions.deliver(f.admin(),f.campaign().id(),f.item().id(),7,"F-01",3,2,"VULNERABILIDADE");
        assertThat(race(delivery,delivery)).containsExactlyInAnyOrder(true,false);
        new TransactionTemplate(manager).executeWithoutResult(s->assertThat(store.get(Item.class,f.item().id()).balance()).isEqualTo(3));
    }
    @Test void exceptionAfterMutationRollsBackBothBalanceAndLedger() {
        var f=fixture();
        assertThatThrownBy(()->new TransactionTemplate(manager).executeWithoutResult(s->{
            actions.movement(f.admin(),f.campaign().id(),f.item().id(),5,"DOACAO","Não pode persistir");throw new Rule("Falha posterior");
        })).isInstanceOf(Rule.class);
        new TransactionTemplate(manager).executeWithoutResult(s->{
            assertThat(store.get(Item.class,f.item().id()).balance()).isEqualTo(10);
            assertThat(store.all(Movement.class).stream().filter(m->m.itemId()==f.item().id())).hasSize(1);
        });
    }
}
