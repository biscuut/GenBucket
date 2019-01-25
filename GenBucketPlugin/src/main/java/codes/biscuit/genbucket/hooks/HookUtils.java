package codes.biscuit.genbucket.hooks;

import codes.biscuit.genbucket.GenBucket;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class HookUtils {

    private GenBucket main;
    private Set<Hooks> enabledHooks = EnumSet.noneOf(Hooks.class);
    private MassiveCoreHook massiveCoreHook;
    private FactionsUUIDHook factionsUUIDHook;
    private WorldBorderHook worldBorderHook;
    private WorldGuardHook worldGuardHook;
    private CoreProtectHook coreProtectHook;
    private Economy economy;
    private Set<UUID> bypassPlayers = new HashSet<>();

    public HookUtils(GenBucket main) {
        this.main = main;
        economy = main.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        PluginManager pm = main.getServer().getPluginManager();
        if (main.getConfigValues().isFactionsHookEnabled() && pm.getPlugin("MassiveCore") != null &&
                pm.getPlugin("Factions") != null &&
                pm.getPlugin("Factions").getDescription().getDepend().contains("MassiveCore")) {
            main.getLogger().info("Hooked into MassiveCore factions");
            enabledHooks.add(Hooks.MASSIVECOREFACTIONS);
            massiveCoreHook = new MassiveCoreHook();
        } else if (main.getConfigValues().isFactionsHookEnabled() && pm.getPlugin("Factions") != null) {
            main.getLogger().info("Hooked into FactionsUUID/SavageFactions");
            enabledHooks.add(Hooks.FACTIONSUUID);
            factionsUUIDHook = new FactionsUUIDHook();
        }
        if (main.getConfigValues().isWorldGuardHookEnabled() && pm.getPlugin("WorldGuard") != null) {
            String pluginVersion = main.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
            if (pluginVersion.startsWith("7") && pm.getPlugin("WorldEdit") != null) {
                main.getLogger().info("Hooked into WorldGuard 7");
                enabledHooks.add(Hooks.WORLDGUARD);
                worldGuardHook = new WorldGuard_7();
            } else if (pluginVersion.startsWith("6")) {
                main.getLogger().info("Hooked into WorldGuard 6");
                enabledHooks.add(Hooks.WORLDGUARD);
                worldGuardHook = new WorldGuard_6();
            }
        }
        if (main.getConfigValues().isWorldBorderHookEnabled() && pm.getPlugin("WorldBorder") != null) {
            main.getLogger().info("Hooked into WorldBorder");
            enabledHooks.add(Hooks.WORLDBORDER);
            worldBorderHook = new WorldBorderHook();
        }
        if (main.getConfigValues().isCoreProtectHookEnabled() && pm.getPlugin("CoreProtect") != null) {
            main.getLogger().info("Hooked into CoreProtect");
            enabledHooks.add(Hooks.COREPROTECT);
            coreProtectHook = new CoreProtectHook();
        }
    }

    public boolean canBePlacedHere(Player p, Location loc, boolean silent) {
        if (bypassPlayers.contains(p.getUniqueId())) {
            return true;
        }
        if (!p.hasPermission("genbucket.use")) {
            if (!silent)cannotPlaceNoPermission(p);
            return false;
        }
        if (loc.getBlockY() > main.getConfigValues().getMaxY()) {
            if (!silent)cannotPlaceYLevel(p);
            return false;
        }
        if (enabledHooks.contains(Hooks.MASSIVECOREFACTIONS)) {
            if (main.getConfigValues().needsFaction() && !massiveCoreHook.hasFaction(p)) {
                if (!silent)cannotPlaceNoFaction(p);
                return false;
            }
            if (!massiveCoreHook.isWilderness(loc) && !massiveCoreHook.locationIsFactionClaim(loc, p)) {
                if (!silent)cannotPlaceWilderness(p);
                return false;
            }
            if (main.getConfigValues().cantPlaceWilderness() && massiveCoreHook.isWilderness(loc)) {
                if (!silent)cannotPlaceWilderness(p);
                return false;
            }
        } else if (enabledHooks.contains(Hooks.FACTIONSUUID)) {
            if (main.getConfigValues().needsFaction() && !factionsUUIDHook.hasFaction(p)) {
                if (!silent)cannotPlaceNoFaction(p);
                return false;
            }
            if (!factionsUUIDHook.isWilderness(loc) && !factionsUUIDHook.locationIsFactionClaim(loc, p)) {
                if (!silent)cannotPlaceWilderness(p);
                return false;
            }
            if (main.getConfigValues().cantPlaceWilderness() && factionsUUIDHook.isWilderness(loc)) {
                if (!silent)cannotPlaceWilderness(p);
                return false;
            }
        }
        if (enabledHooks.contains(Hooks.WORLDGUARD)) {
            if (worldGuardHook.checkLocationBreakFlag(loc.getChunk(), p)) {
                if (!silent)cannotPlaceRegion(p);
                return false;
            }
        }
        if (enabledHooks.contains(Hooks.WORLDBORDER)) {
            if (!worldBorderHook.isInsideBorder(loc)) {
                if (!silent)cannotPlaceRegion(p);
                return false;
            }
        }
        WorldBorder border = p.getWorld().getWorldBorder();
        double radius = border.getSize()/2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return !(x >= radius || -x > radius) || (z >= radius || -z > radius);
    }

    private void cannotPlaceNoFaction(Player p) {
        if (!main.getConfigValues().getNoFactionMessage().equals("")) {
            p.sendMessage(main.getConfigValues().getNoFactionMessage());
        }
    }

    private void cannotPlaceWilderness(Player p) {
        if (!main.getConfigValues().getOnlyClaimMessage().equals("")) {
            p.sendMessage(main.getConfigValues().getOnlyClaimMessage());
        }
    }

    private void cannotPlaceRegion(Player p) {
        if (!main.getConfigValues().getRegionProtectedMessage().equals("")) {
            p.sendMessage(main.getConfigValues().getRegionProtectedMessage());
        }
    }

    private void cannotPlaceNoPermission(Player p) {
        if (!main.getConfigValues().getNoPermissionPlaceMessage().equals("")) {
            p.sendMessage(main.getConfigValues().getNoPermissionPlaceMessage());
        }
    }

    private void cannotPlaceYLevel(Player p) {
        if (!main.getConfigValues().getCannotPlaceYLevelMessage().equals("")) {
            p.sendMessage(main.getConfigValues().getCannotPlaceYLevelMessage());
        }
    }

    public boolean takeBucketPlaceCost(Player p, String bucket) {
        if (bypassPlayers.contains(p.getUniqueId())) {
            return true;
        }
        if (main.getConfigValues().isBucketInfinite(bucket)) {
            if (hasMoney(p, main.getConfigValues().getBucketPlaceCost(bucket))) {
                removeMoney(p, main.getConfigValues().getBucketPlaceCost(bucket));
                return true;
            } else {
                if (!main.getConfigValues().notEnoughMoneyPlaceMessage(main.getConfigValues().getBucketPlaceCost(bucket)).equals("")) {
                    p.sendMessage(main.getConfigValues().notEnoughMoneyPlaceMessage(main.getConfigValues().getBucketPlaceCost(bucket)));
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean takeShopMoney(Player p, double amount) {
        if (bypassPlayers.contains(p.getUniqueId())) {
            return true;
        }
        if (hasMoney(p, amount)) {
            removeMoney(p, amount);
            return true;
        } else {
            if (!main.getConfigValues().notEnoughMoneyBuyMessage(amount).equals("")) {
                p.sendMessage(main.getConfigValues().notEnoughMoneyBuyMessage(amount));
            }
            return false;
        }
    }

    private boolean hasMoney(Player p, double money) {
        return economy.has(p, money);
    }

    private void removeMoney(Player p, double money) {
        economy.withdrawPlayer(p, money);
    }

    public void logBlock(Player p, Location loc, Material mat, byte damage) {
        if (enabledHooks.contains(Hooks.COREPROTECT)) {
            coreProtectHook.logBlock(p.getName(), loc, mat, damage);
        }
    }

    public Set<UUID> getBypassPlayers() {
        return bypassPlayers;
    }

    enum Hooks {
        FACTIONSUUID,
        MASSIVECOREFACTIONS,
        COREPROTECT,
        WORLDGUARD,
        WORLDBORDER
    }
}
