package codes.biscuit.genbucket.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Minecraft_1_8 implements MinecraftAbstraction {

    @SuppressWarnings("deprecation")
    public void setBlockData(Block block, byte data) {
        block.setData(data);
    }

    public void clearOffhand(Player p) {}
}
