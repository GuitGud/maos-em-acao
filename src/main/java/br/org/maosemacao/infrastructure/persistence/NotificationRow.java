package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="NotificationRow")
@Table(name="notifications")
public class NotificationRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="volunteer_id", nullable=false)
    public long volunteerId;
    @Column(name="message", nullable=false, length=2000)
    public String message;
    @Column(name="seen", nullable=false)
    public boolean seen;
    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;
}
