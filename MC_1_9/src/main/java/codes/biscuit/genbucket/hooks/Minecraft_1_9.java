package codes.biscuit.genbucket.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Minecraft_1_9 implements MinecraftAbstraction {

    public void setBlockData(Block block, byte data) {}

    public void clearOffhand(Player p) {
        p.getInventory().setItemInOffHand(null);
    }
}
