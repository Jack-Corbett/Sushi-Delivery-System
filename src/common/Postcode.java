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
        this.code = code;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String getName() {
        return name;
    }
}
