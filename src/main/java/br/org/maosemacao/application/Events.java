package br.org.maosemacao.application;
import java.util.*;
/** Observer síncrono: a caixa de entrada participa da mesma transação do comando. */
public class Events {
    public record Change(List<Long> recipients, String message) { }
    public interface Observer { void on(Change event); }
    private final List<Observer> observers;
    public Events(Observer... observers) { this.observers=List.of(observers); }
    public void publish(Change event) { observers.forEach(observer -> observer.on(event)); }
}
