package codes.biscuit.genbucket.hooks;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WorldGuardHook {

    boolean checkLocationBreakFlag(Chunk chunk, Player p);

    boolean checkLocationBreakFlag(Location block, Player p);
}
