package codes.biscuit.genbucket.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface MinecraftAbstraction {

    void setBlockData(Block block, byte data);

    void clearOffhand(Player p);
}
