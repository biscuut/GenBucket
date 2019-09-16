package codes.biscuit.genbucket.listeners;

import codes.biscuit.genbucket.GenBucket;
import codes.biscuit.genbucket.event.GenBucketPlaceEvent;
import codes.biscuit.genbucket.timers.GenningTimer;
import codes.biscuit.genbucket.utils.Bucket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PlayerListener implements Listener {

    private GenBucket main;

    public PlayerListener(GenBucket main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) {
            if (!main.getHookUtils().getBypassPlayers().contains(e.getPlayer())) {
                return;
            }
        }
        Bucket matchedBucket = main.getBucketManager().matchBucket(e.getItemInHand());
        if (matchedBucket != null) { // Sorry, you can't use it with offhand atm.
            e.setCancelled(true);
            startGenBucket(matchedBucket, e.getPlayer(), e.getBlock(), e.getBlockAgainst().getFace(e.getBlock()), e.getItemInHand());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketPlace(PlayerInteractEvent e) {
        if (e.isCancelled()) {
            if (!main.getHookUtils().getBypassPlayers().contains(e.getPlayer())) {
                return;
            }
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getItem() != null && (e.getItem().getType().equals(Material.LAVA_BUCKET) ||
                e.getItem().getType().equals(Material.WATER_BUCKET))) {
            Bucket bucket = main.getBucketManager().matchBucket(e.getItem());
            if (bucket != null) {
                e.setCancelled(true);
                startGenBucket(bucket, e.getPlayer(), e.getClickedBlock().getRelative(e.getBlockFace()), e.getBlockFace(), e.getItem());
            }
        }
    }

    private boolean noPlayersNearby(Player p) {
        double radius = main.getConfigValues().getPlayerCheckRadius();
        if (radius != 0) {
            for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof Player) {
                    if (!main.getHookUtils().isFriendlyPlayer(p, (Player)entity)) {
                        if (!main.getConfigValues().getNearbyPlayerMessage().equals("")) {
                            p.sendMessage(main.getConfigValues().getNearbyPlayerMessage());
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent e) {
        if (main.getConfigValues().bucketsDisappearDrop()) {
            Bucket bucket = main.getBucketManager().matchBucket(e.getItemDrop().getItemStack());
            if (bucket != null && bucket.isInfinite()) {
                e.getItemDrop().remove();
            }
        }
    }

    private void startGenBucket(Bucket bucket, Player p, Block block, BlockFace direction, ItemStack removeItem) {
        if (main.getHookUtils().canPlaceHere(p, block.getLocation()) && noPlayersNearby(p) && main.getHookUtils().takeBucketPlaceCost(p, bucket)) {
            if (bucket.getDirection() == Bucket.Direction.HORIZONTAL &&
                    (direction.equals(BlockFace.UP) || direction.equals(BlockFace.DOWN))) {
                if (!main.getConfigValues().getWrongDirectionMessage().equals("")) {
                    p.sendMessage(main.getConfigValues().getWrongDirectionMessage());
                }
                return;
            }
            GenBucketPlaceEvent event = new GenBucketPlaceEvent(p, bucket, block, direction, removeItem);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            switch (bucket.getDirection()) {
                case UPWARDS:
                    direction = BlockFace.UP;
                    break;
                case DOWNWARDS:
                    direction = BlockFace.DOWN;
                    break;
            }
            int limit = main.getConfigValues().getVerticalTravel();
            if (direction != BlockFace.UP && direction != BlockFace.DOWN) {
                limit = main.getConfigValues().getHorizontalTravel();
            }
            GenningTimer genningTimer = new GenningTimer(p, bucket, block, direction, main, limit);
            genningTimer.runTaskTimer(main, 0L, main.getConfigValues().getBlockSpeedDelay());
            if (main.getConfigValues().cancellingEnabled()) {
                main.getUtils().getCurrentGens().put(block.getLocation(), genningTimer);
            }
            if (!bucket.isInfinite()) {
                if (!main.getHookUtils().getBypassPlayers().contains(p)) {
                    if (removeItem.getAmount() <= 1) {
                        p.setItemInHand(null);
                    } else {
                        removeItem.setAmount(removeItem.getAmount() - 1);
                    }
                }
                if (!main.getConfigValues().getPlaceNormalMessage(bucket.getPlacePrice()).equals("") && bucket.getPlacePrice() > 0) {
                    p.sendMessage(main.getConfigValues().getPlaceNormalMessage(bucket.getPlacePrice()));
                }
            } else {
                if (!main.getConfigValues().getPlaceInfiniteMessage(bucket.getPlacePrice()).equals("") && bucket.getPlacePrice() > 0) {
                    p.sendMessage(main.getConfigValues().getPlaceInfiniteMessage(bucket.getPlacePrice()));
                }
            }
        }
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getView().getTitle() != null &&
                e.getView().getTitle().equals(main.getConfigValues().getGUITitle())) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                Bucket bucket = main.getBucketManager().fromShopName(e.getCurrentItem().getItemMeta().getDisplayName());
                if (bucket != null) {
                    double price = bucket.getBuyPrice();
                    int amount = 1;
                    if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                        amount = 16;
                        price *= main.getConfigValues().getBulkBuyAmount();
                    }
                    if (main.getHookUtils().takeShopMoney(p, price)) {
                        ItemStack item = bucket.getItem();
                        item.setAmount(amount);
                        Map excessItems;
                        if (!main.getConfigValues().shopShouldDropItem()) {
                            if (p.getInventory().firstEmpty() == -1) {
                                if (!main.getConfigValues().getNoSpaceBuyMessage().equals("")) {
                                    p.sendMessage(main.getConfigValues().getNoSpaceBuyMessage());
                                }
                                return;
                            }
                        }
                        excessItems = p.getInventory().addItem(item);
                        for (Object excessItem : excessItems.values()) {
                            int itemCount = ((ItemStack) excessItem).getAmount();
                            while (itemCount > 64) {
                                ((ItemStack) excessItem).setAmount(64);
                                p.getWorld().dropItemNaturally(p.getLocation(), (ItemStack) excessItem);
                                itemCount = itemCount - 64;
                            }
                            if (itemCount > 0) {
                                ((ItemStack) excessItem).setAmount(itemCount);
                                p.getWorld().dropItemNaturally(p.getLocation(), (ItemStack) excessItem);
                            }
                        }
                        if (!main.getConfigValues().getBuyConfirmationMessage(amount).equals("")) {
                            p.sendMessage(main.getConfigValues().getBuyConfirmationMessage(amount));
                        }
                    }
                } else if (main.getConfigValues().getExitName().equals(e.getCurrentItem().getItemMeta().getDisplayName())) {
                    p.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (main.getConfigValues().showUpdateMessage() && e.getPlayer().isOp()) {
            main.getUtils().checkUpdates(e.getPlayer());
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        if (main.getConfigValues().cancellingEnabled()) {
            Location b = e.getBlock().getLocation();
            if (main.getUtils().getCurrentGens().containsKey(b)) {
                main.getUtils().getCurrentGens().get(b).cancel();
                main.getUtils().getCurrentGens().remove(b);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (main.getConfigValues().cancellingEnabled()) {
            Location b = e.getBlock().getLocation();
            if (main.getUtils().getCurrentGens().containsKey(b)) {
                main.getUtils().getCurrentGens().get(b).cancel();
                main.getUtils().getCurrentGens().remove(b);
            }
        }
    }
}
