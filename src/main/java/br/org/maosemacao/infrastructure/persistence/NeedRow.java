package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="NeedRow")
@Table(name="needs")
public class NeedRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="campaign_id", nullable=false)
    public long campaignId;
    @Column(name="item_id", nullable=false)
    public long itemId;
    @Column(name="target", nullable=false)
    public int target;
}
