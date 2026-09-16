package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="ShiftRow")
@Table(name="shifts")
public class ShiftRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="campaign_id", nullable=false)
    public long campaignId;
    @Column(name="title", nullable=false)
    public String title;
    @Column(name="starts_at", nullable=false)
    public LocalDateTime startsAt;
    @Column(name="ends_at", nullable=false)
    public LocalDateTime endsAt;
    @Column(name="capacity", nullable=false)
    public int capacity;
    @Column(name="status", nullable=false)
    public String status;
}
