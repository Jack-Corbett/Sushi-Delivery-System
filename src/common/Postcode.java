package common;

public class Postcode extends Model {

    private String code;
    private Integer distance;

    public Postcode(String postcode, Integer distance) {
        code = postcode;
        this.distance = distance;
        setName(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        String oldCode = this.code;
        this.code = code;
        notifyUpdate("code", oldCode, this.code);
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        Integer oldDistance = this.distance;
        this.distance = distance;
        notifyUpdate("distance", oldDistance, this.distance);
    }

    @Override
    public String getName() {
        return name;
    }
}
