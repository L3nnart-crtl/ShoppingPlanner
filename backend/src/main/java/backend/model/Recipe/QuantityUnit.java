package backend.model.Recipe;
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
        switch (this) {
            case GRAM: return "g";
            case KILOGRAM: return "kg";
            case MILLIGRAM: return "mg";
            case MILLILITER: return "ml";
            case LITER: return "l";
            case CUP: return "Tasse(n)";
            case TABLESPOON: return "EL";
            case TEASPOON: return "TL";
            case PIECE: return "Stück";
            case DOZEN: return "Dutzend";
            default: return name; // Fallback
        }
    }
}
