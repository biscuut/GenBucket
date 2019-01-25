package codes.biscuit.genbucket.hooks;

import net.coreprotect.CoreProtect;
import org.bukkit.Location;
import org.bukkit.Material;

import static org.bukkit.Bukkit.getServer;

// CoreProtect API v5
class CoreProtectHook {

    void logBlock(String p, Location loc, Material mat, byte damage) {
        ((CoreProtect)getServer().getPluginManager().getPlugin("CoreProtect")).getAPI().logRemoval(p + " (GenBucket)", loc, mat, damage);
    }
}
