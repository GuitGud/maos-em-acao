package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="RegistrationRow")
@Table(name="registrations")
public class RegistrationRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="shift_id", nullable=false)
    public long shiftId;
    @Column(name="volunteer_id", nullable=false)
    public long volunteerId;
    @Column(name="status", nullable=false)
    public String status;
    @Column(name="hours", nullable=false)
    public double hours;
}
