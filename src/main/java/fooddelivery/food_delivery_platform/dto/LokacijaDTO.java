package fooddelivery.food_delivery_platform.dto;

public class LokacijaDTO {

    private Double latitude;
    private Double longitude;

    public LokacijaDTO() {
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
}