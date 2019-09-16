package codes.biscuit.genbucket.event;

import codes.biscuit.genbucket.utils.Bucket;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class GenBucketPlaceEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final Bucket bucket;
    private final Block block;
    private final BlockFace direction;
    private final ItemStack removeItem;
    private boolean cancelled;

    public GenBucketPlaceEvent(Player player, Bucket bucket, Block block, BlockFace direction, ItemStack removeItem) {
        this.player = player;
        this.bucket = bucket;
        this.block = block;
        this.direction = direction;
        this.removeItem = removeItem;
    }

    public Player getPlayer() {
        return player;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public Block getBlock() {
        return block;
    }

    public BlockFace getDirection() {
        return direction;
    }

    public ItemStack getRemoveItem() {
        return removeItem;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
