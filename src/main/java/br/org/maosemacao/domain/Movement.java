package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Movement(Long id, long itemId, long campaignId, int quantity, String kind, String reason, LocalDateTime createdAt, long actorId) {

}
