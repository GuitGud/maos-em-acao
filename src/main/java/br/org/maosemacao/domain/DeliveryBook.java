package br.org.maosemacao.domain;
import java.util.*;
/** Snapshot imutável; cada chamada a iterator() inicia uma travessia independente. */
public class DeliveryBook implements Iterable<Delivery> {
    private final List<Delivery> entries;
    public DeliveryBook(List<Delivery> entries) { this.entries=List.copyOf(entries); }
    public Iterator<Delivery> iterator() {
        return new Iterator<>() {
            private int index;
            public boolean hasNext() { return index < entries.size(); }
            public Delivery next() {
                if(!hasNext()) throw new NoSuchElementException();
                return entries.get(index++);
            }
        };
    }
}
