package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Registration(Long id, long shiftId, long volunteerId, String status, double hours) {

}
