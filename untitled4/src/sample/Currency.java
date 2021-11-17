package sample;

public class Currency {

    String name;
    String code;
    Double value;

    public Currency(String name, String code, Double value) {
        this.name = name;
        this.code = code;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Double getValue() {
        return value;
    }
}