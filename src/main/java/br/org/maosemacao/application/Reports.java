package br.org.maosemacao.application;
import br.org.maosemacao.domain.*;
import java.util.*;
import java.util.stream.Collectors;
public interface Reports {
    String generate(Volunteer actor);
    class Summary implements Reports {
        private final Store store;
        public Summary(Store store) { this.store=store; }
        public String generate(Volunteer actor) {
            Map<Long,Item> items=new HashMap<>();
            Map<String,Long> donated=new TreeMap<>(), stock=new TreeMap<>(), delivered=new TreeMap<>();
            for(Item item:store.all(Item.class)) {
                items.put(item.id(),item);
                donated.putIfAbsent(item.unit(),0L);
                delivered.putIfAbsent(item.unit(),0L);
                stock.merge(item.unit(),(long)item.balance(),Long::sum);
            }
            for(Movement m:store.all(Movement.class)) if(m.kind().equals("DOACAO"))
                donated.merge(items.get(m.itemId()).unit(),(long)m.quantity(),Long::sum);
            for(Delivery d:new DeliveryBook(store.all(Delivery.class))) if(d.status().equals("ENTREGUE"))
                delivered.merge(items.get(d.itemId()).unit(),(long)d.quantity(),Long::sum);
            double hours=store.all(Registration.class).stream().mapToDouble(Registration::hours).sum();
            return "Doações recebidas: " + format(donated) + "\nEstoque atual: " + format(stock) + "\nItens entregues: " + format(delivered) + "\nHoras voluntárias: " + hours;
        }
        private String format(Map<String,Long> totals) {
            if(totals.isEmpty()) return "Nenhum item cadastrado";
            return totals.entrySet().stream().map(e->e.getValue()+" "+e.getKey()).collect(Collectors.joining("; "));
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
