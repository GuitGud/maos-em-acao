package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="ReportRow")
@Table(name="reports")
public class ReportRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="title", nullable=false)
    public String title;
    @Column(name="content", nullable=false, length=12000)
    public String content;
    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;
    @Column(name="actor_id", nullable=false)
    public long actorId;
}
