package codes.biscuit.genbucket.timers;

import codes.biscuit.genbucket.GenBucket;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GenningTimer extends BukkitRunnable {

    private Player p;
    private Material genMaterial; //TODO all these could be turned into a single bucket class? worth it?
    private Block startingBlock;
    private Block currentBlock;
    private GenBucket main;
    private BlockFace direction;
    private int limit;
    private int blockCounter = 0;
    private int chunkCounter = 0;
    private boolean chunkLimited;
    private Chunk previousChunk = null;

    public GenningTimer(Player p, Material genMaterial, Block startingBlock, BlockFace direction, GenBucket main, int limit, boolean chunkLimited) {
        this.p = p;
        this.genMaterial = genMaterial;
        this.startingBlock = startingBlock;
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
                main.getHookUtils().canGenBlock(p, currentBlock.getLocation(), direction != BlockFace.UP && direction != BlockFace.DOWN) && main.getConfigValues().getIgnoredBlockList().contains(currentBlock.getType())) {
            if (previousChunk == null || !previousChunk.equals(currentBlock.getChunk())) { // Check every chunk only once for efficiency.
                previousChunk = currentBlock.getChunk();
                if (!main.getHookUtils().canGenChunk(p, currentBlock.getChunk())) {
                    cancel();
                    return;
                }
            }
            main.getHookUtils().logBlock(p, currentBlock.getLocation(), currentBlock.getType(), currentBlock.getData());
            currentBlock.setType(genMaterial);
            if (chunkLimited && currentBlock.getChunk() != currentBlock.getRelative(direction).getChunk()) {
                chunkCounter++;
                if (chunkCounter >= main.getConfigValues().getMaxChunks()) cancel();
            }
            currentBlock = currentBlock.getRelative(direction);
            Material wool;
            try {
                wool = Material.valueOf("WOOL");
            } catch (IllegalArgumentException ex) {
                wool = Material.valueOf("LIME_WOOL"); // For 1.13
            }
            p.sendBlockChange(startingBlock.getLocation(), wool, (byte)5);
        } else {
            main.getUtils().getCurrentGens().remove(startingBlock.getLocation());
            p.sendBlockChange(startingBlock.getLocation(), genMaterial, (byte)0);
            cancel();
        }
        blockCounter++;
    }
}
