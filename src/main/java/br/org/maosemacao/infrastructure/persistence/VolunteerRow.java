package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="VolunteerRow")
@Table(name="volunteers")
public class VolunteerRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="name", nullable=false)
    public String name;
    @Column(name="email", nullable=false)
    public String email;
    @Column(name="password_hash", nullable=false)
    public String passwordHash;
    @Column(name="approved", nullable=false)
    public boolean approved;
    @Column(name="role", nullable=false)
    public String role;
}
