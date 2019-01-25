package codes.biscuit.genbucket.timers;

import codes.biscuit.genbucket.GenBucket;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GenningTimer extends BukkitRunnable {

    private Player p;
    private Material genMaterial;
    private Block currentBlock;
    private GenBucket main;
    private BlockFace direction;
    private int limit;
    private int blockCounter = 0;
    private int chunkCounter = 0;
    private boolean chunkLimited;

    public GenningTimer(Player p, Material genMaterial, Block startingBlock, BlockFace direction, GenBucket main, int limit, boolean chunkLimited) {
        this.p = p;
        this.genMaterial = genMaterial;
        this.currentBlock = startingBlock;
        this.direction = direction;
        this.main = main;
        this.limit = limit;
        this.chunkLimited = chunkLimited;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        if (blockCounter < limit && !(currentBlock.getY() > main.getConfigValues().getMaxY()) &&
                main.getHookUtils().canBePlacedHere(p, currentBlock.getLocation(), true) && main.getConfigValues().getIgnoredBlockList().contains(currentBlock.getType())) {
            main.getHookUtils().logBlock(p, currentBlock.getLocation(), currentBlock.getType(), currentBlock.getData());
            currentBlock.setType(genMaterial);
            if (chunkLimited && currentBlock.getChunk() != currentBlock.getRelative(direction).getChunk()) {
                chunkCounter++;
                if (chunkCounter >= main.getConfigValues().getMaxChunks()) cancel();
            }
            currentBlock = currentBlock.getRelative(direction);
        } else {
            cancel();
        }
        blockCounter++;
    }
}
