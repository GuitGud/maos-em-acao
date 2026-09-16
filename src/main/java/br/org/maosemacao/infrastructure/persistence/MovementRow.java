package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="MovementRow")
@Table(name="movements")
public class MovementRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="item_id", nullable=false)
    public long itemId;
    @Column(name="campaign_id", nullable=false)
    public long campaignId;
    @Column(name="quantity", nullable=false)
    public int quantity;
    @Column(name="kind", nullable=false)
    public String kind;
    @Column(name="reason", nullable=false, length=2000)
    public String reason;
    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;
    @Column(name="actor_id", nullable=false)
    public long actorId;
}
