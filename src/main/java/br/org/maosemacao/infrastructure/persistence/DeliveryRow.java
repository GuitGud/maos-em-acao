package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="DeliveryRow")
@Table(name="deliverys")
public class DeliveryRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="campaign_id", nullable=false)
    public long campaignId;
    @Column(name="item_id", nullable=false)
    public long itemId;
    @Column(name="quantity", nullable=false)
    public int quantity;
    @Column(name="beneficiary", nullable=false)
    public String beneficiary;
    @Column(name="vulnerability", nullable=false)
    public int vulnerability;
    @Column(name="members", nullable=false)
    public int members;
    @Column(name="priority", nullable=false)
    public int priority;
    @Column(name="status", nullable=false)
    public String status;
    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;
    @Column(name="actor_id", nullable=false)
    public long actorId;
    @Column(name="reason", nullable=false, length=2000)
    public String reason;
}
