package fooddelivery.food_delivery_platform.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lokacije_dostavljaca")
public class LokacijaDostavljaca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dostavljac_id")
    private Dostavljac dostavljac;

    private Double latitude;

    private Double longitude;

    private LocalDateTime timestamp;

    public LokacijaDostavljaca() {
    }

    public Long getId() {
        return id;
    }

    public Dostavljac getDostavljac() {
        return dostavljac;
    }

    public void setDostavljac(Dostavljac dostavljac) {
        this.dostavljac = dostavljac;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}