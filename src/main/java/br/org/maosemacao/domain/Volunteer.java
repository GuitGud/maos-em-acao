package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Volunteer(Long id, String name, String email, String passwordHash, boolean approved, String role) {

}
