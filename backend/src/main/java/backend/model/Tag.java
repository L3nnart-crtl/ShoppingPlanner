package backend.model;



public enum Tag {
    // Ernährungsbasierte Tags
    VEGETARIAN("Vegetarisch"),
    VEGAN("Vegan"),
    GLUTEN_FREE("Glutenfrei"),
    LACTOSE_FREE("Laktosefrei"),
    KETO("Keto"),
    PALEO("Paleo"),
    LOW_CARB("Low Carb"),
    HIGH_PROTEIN("High Protein"),
    DIABETIC_FRIENDLY("Diabetikerfreundlich"),
    LOW_FAT("Low Fat"),

    // Besondere Diätanforderungen
    HALAL("Halal"),
    KOSHER("Koscher"),
    EXCLUSION_DIET("Ausschlussdiät"),
    QUICK("Schnell"),
    MEAL_PREP("Meal Prep"),
    KIDS_MEAL("Mahlzeit für Kinder"),

    // Geschmack und Zubereitung
    SWEET("Süß"),
    SAVORY("Herzhaft"),
    SPICY("Scharf"),
    MILD("Mild"),
    EXOTIC("Exotisch"),
    SEASONAL("Saisonale Rezepte"),
    GRILL("Grillrezepte"),

    // Für bestimmte Mahlzeiten
    BREAKFAST("Frühstück"),
    LUNCH("Mittagessen"),
    DINNER("Abendessen"),
    SNACKS("Snacks"),
    SOUPS("Suppen"),
    SALADS("Salate"),

    // Zutatenbasierte Tags
    SUGAR_FREE("Zuckerfrei"),
    FISH("Fisch"),
    MEAT("Fleisch"),
    NUTS("Nüsse"),
    WHOLEGRAIN("Vollkorn"),
    LEGUMES("Hülsenfrüchte"),
    PASTA("Nudeln"),
    RICE("Reis"),

    // Weitere praktische Tags
    BUDGET_FRIENDLY("Budgetfreundlich"),
    FOR_2_PEOPLE("Für 2 Personen"),
    FAMILY_FRIENDLY("Familientauglich"),
    FOR_BEGGINERS("Für Anfänger"),
    GOURMET("Gourmet"),
    STORAGE("Lagerung"),
    DESSERT("Nachtisch"),

    // Zubereitungsmethoden
    QUICK_PREP("Schnell"),
    SLOW_COOK("Langsame Zubereitung"),
    ONE_POT("Ein-Pfannen-Gerichte"),
    FRYING("Frittieren"),
    BAKING("Backen"),

    // Gesundheits- und Fitnessorientierte Tags
    LOW_CALORIE("Low Calorie"),
    ANTI_AGEING("Anti-Aging"),
    DETOX("Entgiftend"),
    HEART_HEALTH("Herzgesund");

    private final String label;

    Tag(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
