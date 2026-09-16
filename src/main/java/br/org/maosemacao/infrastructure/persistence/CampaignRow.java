package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="CampaignRow")
@Table(name="campaigns")
public class CampaignRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="name", nullable=false)
    public String name;
    @Column(name="description", nullable=false, length=2000)
    public String description;
    @Column(name="status", nullable=false)
    public String status;
}
