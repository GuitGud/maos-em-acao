package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Shift(Long id, long campaignId, String title, LocalDateTime startsAt, LocalDateTime endsAt, int capacity, String status) {

    public void canEnroll(int occupied, LocalDateTime now) {
        Rule.require(status.equals("ABERTO") && startsAt.isAfter(now), "Turno indisponível para inscrição");
        Rule.require(occupied < capacity, "Turno lotado");
    }
    public static void validate(LocalDateTime start, LocalDateTime end, int capacity, LocalDateTime now) {
        Rule.require(start != null && end != null && start.isAfter(now) && end.isAfter(start), "Datas do turno inválidas");
        Rule.require(capacity > 0 && capacity <= 1000, "Capacidade deve estar entre 1 e 1000");
        Rule.require(java.time.Duration.between(start, end).toHours() <= 24, "Turno deve durar até 24 horas");
    }

}
