package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Need(Long id, long campaignId, long itemId, int target) {

}
