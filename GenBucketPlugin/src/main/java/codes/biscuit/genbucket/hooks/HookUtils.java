package codes.biscuit.genbucket.hooks;

import codes.biscuit.genbucket.GenBucket;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class HookUtils {

    private GenBucket main;
    private Map<Hooks, Object> enabledHooks = new EnumMap<>(Hooks.class);
    private Economy economy;
    private Set<OfflinePlayer> bypassPlayers = new HashSet<>();

    public HookUtils(GenBucket main) {
        this.main = main;
        economy = main.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        PluginManager pm = main.getServer().getPluginManager();
        if (main.getConfigValues().isFactionsHookEnabled() && pm.getPlugin("MassiveCore") != null &&
                pm.getPlugin("Factions") != null &&
                pm.getPlugin("Factions").getDescription().getDepend().contains("MassiveCore")) {
            main.getLogger().info("Hooked into MassiveCore factions");
            enabledHooks.put(Hooks.MASSIVECOREFACTIONS, new MassiveCoreHook());
        } else if (main.getConfigValues().isFactionsHookEnabled() && pm.getPlugin("Factions") != null) {
            main.getLogger().info("Hooked into FactionsUUID/SavageFactions");
            enabledHooks.put(Hooks.FACTIONSUUID, new FactionsUUIDHook());
        }
        if (main.getConfigValues().isWorldGuardHookEnabled() && pm.getPlugin("WorldGuard") != null) {
            String pluginVersion = main.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
            if (pluginVersion.startsWith("7") && pm.getPlugin("WorldEdit") != null) {
                main.getLogger().info("Hooked into WorldGuard 7");
                enabledHooks.put(Hooks.WORLDGUARD, new WorldGuard_7());
            } else if (pluginVersion.startsWith("6")) {
                main.getLogger().info("Hooked into WorldGuard 6");
                enabledHooks.put(Hooks.WORLDGUARD, new WorldGuard_6());
            }
        }
        if (main.getConfigValues().isWorldBorderHookEnabled() && pm.getPlugin("WorldBorder") != null) {
            main.getLogger().info("Hooked into WorldBorder");
            enabledHooks.put(Hooks.WORLDBORDER, new WorldBorderHook());
        }
        if (main.getConfigValues().isCoreProtectHookEnabled() && pm.getPlugin("CoreProtect") != null) {
            main.getLogger().info("Hooked into CoreProtect");
            enabledHooks.put(Hooks.COREPROTECT, new CoreProtectHook());
        }
    }

    public boolean canPlaceHere(Player p, Location loc) {
        if (!p.hasPermission("genbucket.use")) {
            cannotPlaceNoPermission(p);
            return false;
        }
        if (loc.getBlockY() > main.getConfigValues().getMaxY()) {
            cannotPlaceYLevel(p);
            return false;
        }
        if (enabledHooks.containsKey(Hooks.MASSIVECOREFACTIONS)) {
            MassiveCoreHook massiveCoreHook = (MassiveCoreHook)enabledHooks.get(Hooks.MASSIVECOREFACTIONS);
            if (main.getConfigValues().needsFaction() && massiveCoreHook.hasNoFaction(p)) {
                cannotPlaceNoFaction(p);
                return false;
            }
            if (!massiveCoreHook.isWilderness(loc) && massiveCoreHook.locationIsNotFaction(loc, p)) {
                onlyClaim(p);
                return false;
            }
            if (main.getConfigValues().cantPlaceWilderness() && massiveCoreHook.isWilderness(loc)) {
                onlyClaim(p);
                return false;
            }
        } else if (enabledHooks.containsKey(Hooks.FACTIONSUUID)) {
            FactionsUUIDHook factionsUUIDHook = (FactionsUUIDHook) enabledHooks.get(Hooks.FACTIONSUUID);
            if (main.getConfigValues().needsFaction() && !factionsUUIDHook.hasFaction(p)) {
                cannotPlaceNoFaction(p);
                return false;
            }
            if (factionsUUIDHook.isNotWilderness(loc) && !factionsUUIDHook.locationIsFactionClaim(loc, p)) {
                onlyClaim(p);
                return false;
            }
            if (main.getConfigValues().cantPlaceWilderness() && !factionsUUIDHook.isNotWilderness(loc)) {
                onlyClaim(p);
                return false;
            }
        }
        return true;
    }

    public boolean canGenChunk(Player p, Chunk chunk) {
        Location middle = chunk.getBlock(7, 63, 7).getLocation();
        if (enabledHooks.containsKey(Hooks.MASSIVECOREFACTIONS)) {
            MassiveCoreHook massiveCoreHook = (MassiveCoreHook)enabledHooks.get(Hooks.MASSIVECOREFACTIONS);
            if (main.getConfigValues().needsFaction() && massiveCoreHook.hasNoFaction(p)) {
                return false;
            }
            if (!massiveCoreHook.isWilderness(middle) && massiveCoreHook.locationIsNotFaction(middle, p)) {
                return false;
            }
            return !main.getConfigValues().cantPlaceWilderness() || !massiveCoreHook.isWilderness(middle);
        } else if (enabledHooks.containsKey(Hooks.FACTIONSUUID)) {
            FactionsUUIDHook factionsUUIDHook = (FactionsUUIDHook) enabledHooks.get(Hooks.FACTIONSUUID);
            if (main.getConfigValues().needsFaction() && !factionsUUIDHook.hasFaction(p)) {
                return false;
            }
            if (factionsUUIDHook.isNotWilderness(middle) && !factionsUUIDHook.locationIsFactionClaim(middle, p)) {
                return false;
            }
            return !main.getConfigValues().cantPlaceWilderness() || factionsUUIDHook.isNotWilderness(middle);
        }
        return true;
    }

    public boolean canGenBlock(Player p, Location block, boolean horizontal) {
        if (enabledHooks.containsKey(Hooks.WORLDGUARD)) {
            WorldGuardHook worldGuardHook = (WorldGuardHook)enabledHooks.get(Hooks.WORLDGUARD);
            if (!worldGuardHook.canBreakBlock(block, p)) {
                return false;
            }
        }
        if (main.getConfigValues().getSpongeCheckRadius() > 0) {
            double radius = main.getConfigValues().getSpongeCheckRadius();
            for (double x = block.getX() - radius; x < block.getX() + radius; x++) {
                for (double y = block.getY() - radius; y < block.getY() + radius; y++) {
                    for (double z = block.getZ() - radius; z < block.getZ() + radius; z++) {
                        Block b = new Location(block.getWorld(), x, y, z).getBlock();
                        if (!b.getLocation().equals(block) && b.getType() == Material.SPONGE) {
                            return false;
                        }
                    }
                }
            }
        }
        if (horizontal) {
            if (enabledHooks.containsKey(Hooks.WORLDBORDER)) {
                WorldBorderHook worldBorderHook = (WorldBorderHook) enabledHooks.get(Hooks.WORLDBORDER);
                if (!worldBorderHook.isInsideBorder(block)) {
                    return false;
                }
            }
            WorldBorder border = p.getWorld().getWorldBorder();
            double radius = border.getSize() / 2;
            Location center = border.getCenter();
            double x = block.getX() - center.getX(), z = block.getZ() - center.getZ();
            return !(x >= radius || -x > radius) || (z >= radius || -z > radius);
        }
        return true;
    }

    private void cannotPlaceNoFaction(Player p) {
        if (!main.getConfigValues().getNoFactionMessage().equals("")) {
            p.sendMessage(main.getConfigValues().getNoFactionMessage());
        }
    }

    private void onlyClaim(Player p) {
        if (!main.getConfigValues().getOnlyClaimMessage().equals("")) {
            p.sendMessage(main.getConfigValues().getOnlyClaimMessage());
        }
    }

//    private void cannotPlaceRegion(Player p) {
//        if (!main.getConfigValues().getRegionProtectedMessage().equals("")) {
//            p.sendMessage(main.getConfigValues().getRegionProtectedMessage());
//        }
//    }

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
        if (bypassPlayers.contains(p)) {
            return true;
        }
        if (hasMoney(p, main.getConfigValues().getBucketPlaceCost(bucket))) {
            removeMoney(p, main.getConfigValues().getBucketPlaceCost(bucket));
            return true;
        } else {
            if (!main.getConfigValues().notEnoughMoneyPlaceMessage(main.getConfigValues().getBucketPlaceCost(bucket)).equals("")) {
                p.sendMessage(main.getConfigValues().notEnoughMoneyPlaceMessage(main.getConfigValues().getBucketPlaceCost(bucket)));
            }
            return false;
        }
    }

    public boolean takeShopMoney(Player p, double amount) {
        if (bypassPlayers.contains(p)) {
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

    public boolean isFriendlyPlayer(Player p, Player p2) {
        if (enabledHooks.containsKey(Hooks.MASSIVECOREFACTIONS)) {
            MassiveCoreHook massiveCoreHook = (MassiveCoreHook)enabledHooks.get(Hooks.MASSIVECOREFACTIONS);
            return massiveCoreHook.isFriendlyPlayer(p, p2);
        } else if (enabledHooks.containsKey(Hooks.FACTIONSUUID)) {
            FactionsUUIDHook factionsUUIDHook = (FactionsUUIDHook) enabledHooks.get(Hooks.FACTIONSUUID);
            return factionsUUIDHook.isFriendlyPlayer(p, p2);
        } else {
            return false; // Should the default be true or false? Idk what would work best.
        }
    }

    private boolean hasMoney(Player p, double money) {
        return economy.has(p, money);
    }

    private void removeMoney(Player p, double money) {
        economy.withdrawPlayer(p, money);
    }

    public void logBlock(Player p, Location loc, Material mat, byte damage) {
        if (enabledHooks.containsKey(Hooks.COREPROTECT)) {
            CoreProtectHook coreProtectHook = (CoreProtectHook)enabledHooks.get(Hooks.COREPROTECT);
            coreProtectHook.logBlock(p.getName(), loc, mat, damage);
        }
    }

    public Set<OfflinePlayer> getBypassPlayers() {
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
