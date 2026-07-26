package gg.leo.IraqueClan.clan.role;

public enum ClanRole {
    LIDER("Líder", "Lider", 100),
    SUB_LIDER("Sub-Líder", "SubLider", 60),
    MEMBRO("Membro", "Membro", 20);

    private final String displayName;
    private final String configKey;
    private final int powerLevel;

    ClanRole(String displayName, String configKey, int powerLevel) {
        this.displayName = displayName;
        this.configKey = configKey;
        this.powerLevel = powerLevel;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public int getPowerLevel() {
        return this.powerLevel;
    }

    public boolean isHigherThan(ClanRole other) {
        return this.powerLevel > other.powerLevel;
    }

    public boolean isSameOrHigher(ClanRole other) {
        return this.powerLevel >= other.powerLevel;
    }

    public static ClanRole fromConfigKey(String key) {
        for (ClanRole role : values()) {
            if (role.configKey.equalsIgnoreCase(key)) {
                return role;
            }
        }
        return MEMBRO;
    }
}
