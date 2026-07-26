package gg.leo.IraqueClan.utils.menu;

public enum MenuType {
    SIMPLE("Simples"),
    PAGINATED("Paginado"),
    CONFIRM("Confirmação");

    private final String displayName;

    MenuType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
