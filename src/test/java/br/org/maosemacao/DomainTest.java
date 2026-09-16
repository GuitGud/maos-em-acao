package br.org.maosemacao;
import br.org.maosemacao.domain.*;
import br.org.maosemacao.application.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.time.*;
import java.util.*;
class DomainTest {
    @Test void stockCannotBeNegativeOrOverflow() {
        assertThatThrownBy(()->new Item(1L,"Arroz","kg",3).move(-4)).isInstanceOf(Rule.class);
        assertThatThrownBy(()->new Item(1L,"Arroz","kg",Integer.MAX_VALUE).move(1)).isInstanceOf(Rule.class);
        assertThat(new Item(1L,"Arroz","kg",3).move(-3).balance()).isZero();
    }
    @Test void noZeroMovement() { assertThatThrownBy(()->new Item(1L,"Arroz","kg",0).move(0)).isInstanceOf(Rule.class); }
    @Test void campaignLifecycleEnforcesNeedsAndOpenShifts() {
        Campaign draft=new Campaign(1L,"Campanha","Alimentos","RASCUNHO");
        assertThatThrownBy(()->draft.publish(false)).hasMessageContaining("necessidade");
        Campaign published=draft.publish(true);
        assertThatThrownBy(()->published.close(true)).hasMessageContaining("turnos");
        Campaign closed=published.close(false);
        assertThatThrownBy(()->closed.edit("Novo","Texto")).hasMessageContaining("encerrada");
        assertThatThrownBy(()->closed.publish(true)).isInstanceOf(Rule.class);
    }
    @Test void shiftDatesAndCapacityValidated() {
        LocalDateTime now=LocalDateTime.of(2030,1,1,12,0);
        assertThatThrownBy(()->Shift.validate(now,now.plusHours(2),1,now)).isInstanceOf(Rule.class);
        assertThatThrownBy(()->Shift.validate(now.plusDays(1),now.plusDays(1),1,now)).isInstanceOf(Rule.class);
        assertThatThrownBy(()->Shift.validate(now.plusDays(1),now.plusDays(3),1,now)).isInstanceOf(Rule.class);
        assertThatThrownBy(()->Shift.validate(now.plusDays(1),now.plusDays(1).plusHours(2),0,now)).isInstanceOf(Rule.class);
    }
    @Test void strategiesChangeTheRanking() {
        assertThat(PriorityStrategy.named("VULNERABILIDADE").score(5,1)).isGreaterThan(PriorityStrategy.named("VULNERABILIDADE").score(1,8));
        assertThat(PriorityStrategy.named("TAMANHO_FAMILIA").score(5,1)).isLessThan(PriorityStrategy.named("TAMANHO_FAMILIA").score(1,8));
        assertThatThrownBy(()->PriorityStrategy.named("inexistente")).isInstanceOf(Rule.class);
    }
    @Test void decoratorsComposeWithoutChangingBase() {
        assertThat(VolunteerBadge.forHours(0)).isEqualTo("Voluntário");
        assertThat(VolunteerBadge.forHours(1)).contains("Mãos presentes").doesNotContain("Dedicação");
        assertThat(VolunteerBadge.forHours(20)).contains("Mãos presentes","Dedicação");
    }
    @Test void iteratorIsIndependentImmutableAndThrowsAtEnd() {
        var d=new Delivery(1L,1,1,1,"F-1",1,1,101,"ENTREGUE",LocalDateTime.now(),1,"Entrega");
        var list=new ArrayList<>(List.of(d)); var book=new DeliveryBook(list);list.clear();
        var a=book.iterator(); var b=book.iterator();assertThat(a.next()).isEqualTo(d);assertThat(b.hasNext()).isTrue();
        assertThatThrownBy(a::next).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(a::remove).isInstanceOf(UnsupportedOperationException.class);
    }
    @Test void observerBroadcastsToAllSubscribers() {
        var called=new ArrayList<String>(); var bus=new Events(e->called.add("A:"+e.message()),e->called.add("B:"+e.message()));
        bus.publish(new Events.Change(List.of(1L),"Mudou"));assertThat(called).containsExactly("A:Mudou","B:Mudou");
    }
    @Test void proxyDoesNotInvokeTargetForVolunteers() {
        Reports proxy=new Reports.AccessProxy(a->{throw new AssertionError("Não deveria executar");});
        assertThatThrownBy(()->proxy.generate(new Volunteer(1L,"Ana","a@b.c","hash",true,"VOLUNTEER"))).isInstanceOf(Rule.class).hasMessageContaining("restrito");
    }
}
