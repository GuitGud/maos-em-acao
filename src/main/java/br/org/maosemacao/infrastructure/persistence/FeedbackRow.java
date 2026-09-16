package br.org.maosemacao.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity(name="FeedbackRow")
@Table(name="feedbacks")
public class FeedbackRow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @Column(name="registration_id", nullable=false)
    public long registrationId;
    @Column(name="rating", nullable=false)
    public int rating;
    @Column(name="comment", nullable=false, length=2000)
    public String comment;
}
