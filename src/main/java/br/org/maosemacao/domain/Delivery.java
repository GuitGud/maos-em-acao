package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Delivery(Long id, long campaignId, long itemId, int quantity, String beneficiary, int vulnerability, int members, int priority, String status, LocalDateTime createdAt, long actorId, String reason) {

}
