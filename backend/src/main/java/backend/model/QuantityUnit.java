package backend.model;

/**
 * Enum representing units of quantity.
 */
public enum QuantityUnit {
    GRAM("Gramm", "GRAM"),
    KILOGRAM("Kilogramm", "KILOGRAM"),
    MILLIGRAM("Milligramm", "MILLIGRAM"),
    MILLILITER("Milliliter", "MILLILITER"),
    LITER("Liter", "LITER"),
    CUP("Tasse", "CUP"),
    TABLESPOON("Esslöffel", "TABLESPOON"),
    TEASPOON("Teelöffel", "TEASPOON"),
    PIECE("Stück", "PIECE"),
    DOZEN("Dutzend", "DOZEN");

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
        return name; // Gibt den deutschen Namen zurück
    }
}
