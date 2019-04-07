package codes.biscuit.genbucket.timers;

import codes.biscuit.genbucket.GenBucket;
import codes.biscuit.genbucket.utils.Bucket;
import codes.biscuit.genbucket.utils.ReflectionUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GenningTimer extends BukkitRunnable {

    private Player p;
    private Bucket bucket;
    private Block startingBlock;
    private Block currentBlock;
    private GenBucket main;
    private BlockFace direction;
    private int limit;
    private int blockCounter = 0;
    private int chunkCounter = 0;
    private Chunk previousChunk = null;

    public GenningTimer(Player p, Bucket bucket, Block startingBlock, BlockFace direction, GenBucket main, int limit) {
        this.p = p;
        this.bucket = bucket;
        this.startingBlock = startingBlock;
        this.currentBlock = startingBlock;
        this.direction = direction;
        this.main = main;
        this.limit = limit;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        if (blockCounter < limit && !(currentBlock.getY() > main.getConfigValues().getMaxY()) &&
                main.getHookUtils().canGenBlock(p, currentBlock.getLocation(), direction != BlockFace.UP && direction != BlockFace.DOWN) &&
                (main.getConfigValues().getIgnoredBlockList().contains(currentBlock.getType()) || (bucket.isPatch() && currentBlock.getType() == bucket.getBlockItem().getType()))) {
            if (previousChunk == null || !previousChunk.equals(currentBlock.getChunk())) { // Check every chunk only once for efficiency.
                previousChunk = currentBlock.getChunk();
                if (!main.getHookUtils().canGenChunk(p, currentBlock.getChunk())) {
                    cancel();
                    return;
                }
            }
            main.getHookUtils().logBlock(p, currentBlock.getLocation(), currentBlock.getType(), currentBlock.getData());
            currentBlock.setType(bucket.getBlockItem().getType());
            if (!ReflectionUtils.getVersion().contains("1_13")) {
                Method setData = ReflectionUtils.getMethod(currentBlock.getClass(), "setData", byte.class);
                if (setData != null) {
                    try {
                        setData.invoke(currentBlock, bucket.getBlockItem().getData().getData());
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                }
            }
            if (bucket.isByChunk() && currentBlock.getChunk() != currentBlock.getRelative(direction).getChunk()) {
                chunkCounter++;
                if (chunkCounter >= main.getConfigValues().getMaxChunks()) cancel();
            }
            currentBlock = currentBlock.getRelative(direction);
            if (main.getConfigValues().cancellingEnabled()) {
                Material wool;
                try {
                    wool = Material.valueOf("WOOL");
                } catch (IllegalArgumentException ex) {
                    wool = Material.valueOf("LIME_WOOL"); // For 1.13
                }
                p.sendBlockChange(startingBlock.getLocation(), wool, (byte) 5);
            }
        } else {
            if (main.getConfigValues().cancellingEnabled()) {
                main.getUtils().getCurrentGens().remove(startingBlock.getLocation());
                p.sendBlockChange(startingBlock.getLocation(), startingBlock.getType(), startingBlock.getData());
            }
            cancel();
        }
        blockCounter++;
    }
}
