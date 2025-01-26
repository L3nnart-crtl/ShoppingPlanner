package backend.model.Recipe;
public enum QuantityUnit {
    GRAM("Gramm", "GRAM"),
    KILOGRAM("Kilogramm", "KILOGRAM"),
    MILLIGRAM("Milligramm", "MILLIGRAM");


    private final String name;
    private final String value;

    QuantityUnit(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case GRAM: return "g";
            case KILOGRAM: return "kg";
            case MILLIGRAM: return "mg";
            default: return name; // Fallback
        }
    }
}
