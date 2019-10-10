package codes.biscuit.genbucket.hooks;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

// CoreProtect API v5
class CoreProtectHook {

    private CoreProtectAPI api =  ((CoreProtect) Bukkit.getServer().getPluginManager().getPlugin("CoreProtect")).getAPI();

    void logRemoval(String p, Location loc, Material mat, byte damage) {
        api.logRemoval(p + " (GenBucket)", loc, mat, damage);
    }

    void logPlacement(String p, Location loc, Material mat, byte damage) {
        api.logPlacement(p + " (GenBucket)", loc, mat, damage);
    }
}
