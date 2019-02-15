package codes.biscuit.genbucket.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WorldGuardHook {

//    boolean checkChunkBreakFlag(Chunk chunk, Player p);

    boolean canBreakBlock(Location block, Player p);
}
