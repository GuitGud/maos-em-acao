package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Notification(Long id, long volunteerId, String message, boolean seen, LocalDateTime createdAt) {

}
