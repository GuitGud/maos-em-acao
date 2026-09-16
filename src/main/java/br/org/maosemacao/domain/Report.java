package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Report(Long id, String title, String content, LocalDateTime createdAt, long actorId) {

}
