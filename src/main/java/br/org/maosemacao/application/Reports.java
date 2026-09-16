package br.org.maosemacao.application;
import br.org.maosemacao.domain.*;
public interface Reports {
    String generate(Volunteer actor);
    class Summary implements Reports {
        private final Store store;
        public Summary(Store store) { this.store=store; }
        public String generate(Volunteer actor) {
            long delivered=0;
            for(Delivery d : new DeliveryBook(store.all(Delivery.class))) if(d.status().equals("ENTREGUE")) delivered+=d.quantity();
            long donated=store.all(Movement.class).stream().filter(m->m.kind().equals("DOACAO")).mapToLong(Movement::quantity).sum();
            long stock=store.all(Item.class).stream().mapToLong(Item::balance).sum();
            double hours=store.all(Registration.class).stream().mapToDouble(Registration::hours).sum();
            return "Doações recebidas: " + donated + " unidades\nEstoque atual: " + stock + " unidades\nItens entregues: " + delivered + " unidades\nHoras voluntárias: " + hours;
        }
    }
    class AccessProxy implements Reports {
        private final Reports target;
        public AccessProxy(Reports target) { this.target=target; }
        public String generate(Volunteer actor) {
            if(!actor.role().equals("ADMIN")) throw new Rule("Relatório restrito à gestão",403);
            return target.generate(actor);
        }
    }
}
