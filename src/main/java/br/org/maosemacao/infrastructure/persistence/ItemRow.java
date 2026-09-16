package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="ItemRow")
@Table(name="items")
public class ItemRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="name", nullable=false)
    public String name;
    @Column(name="unit", nullable=false)
    public String unit;
    @Column(name="balance", nullable=false)
    public int balance;
}
