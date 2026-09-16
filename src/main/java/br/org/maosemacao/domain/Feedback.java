package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Feedback(Long id, long registrationId, int rating, String comment) {

}
