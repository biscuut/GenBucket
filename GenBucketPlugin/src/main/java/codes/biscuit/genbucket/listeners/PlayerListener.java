package codes.biscuit.genbucket.listeners;

import codes.biscuit.genbucket.GenBucket;
import codes.biscuit.genbucket.timers.GenningTimer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        if (main.getUtils().matchBucket(e.getItemInHand()) != null) { // Sorry, you can't use it with offhand atm.
            e.setCancelled(true);
            startGenBucket(main.getUtils().matchBucket(e.getItemInHand()), e.getPlayer(), e.getBlock(), e.getBlockAgainst().getFace(e.getBlock()), e.getItemInHand());
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
                e.getItem().getType().equals(Material.WATER_BUCKET)) && main.getUtils().matchBucket(e.getItem()) != null) {
            e.setCancelled(true);
            startGenBucket(main.getUtils().matchBucket(e.getItem()), e.getPlayer(), e.getClickedBlock().getRelative(e.getBlockFace()), e.getBlockFace(), e.getItem());
        }
    }

    private boolean noPlayersNearby(Player p) {
        int radius = main.getConfigValues().getPlayerCheckRadius();
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
            String bucket = main.getUtils().matchBucket(e.getItemDrop().getItemStack());
            if (bucket != null && main.getConfigValues().isBucketInfinite(bucket)) {
                e.getItemDrop().remove();
            }
        }
    }

    private void startGenBucket(String bucket, Player p, Block block, BlockFace direction, ItemStack removeItem) {
        if (main.getHookUtils().canPlaceHere(p, block.getLocation()) && noPlayersNearby(p) && main.getHookUtils().takeBucketPlaceCost(p, bucket)) {
            if ((main.getConfigValues().getBucketDirection(bucket).equals("HORIZONTAL") || main.getConfigValues().getBucketDirection(bucket).equals("HORIZONTAL_CHUNK")) &&
                    (direction.equals(BlockFace.UP) || direction.equals(BlockFace.DOWN))) {
                if (!main.getConfigValues().getWrongDirectionMessage().equals("")) {
                    p.sendMessage(main.getConfigValues().getWrongDirectionMessage());
                }
                return;
            }
            boolean chunkLimited = false;
            switch (main.getConfigValues().getBucketDirection(bucket)) {
                case "UPWARDS":
                    direction = BlockFace.UP;
                    break;
                case "DOWNWARDS":
                    direction = BlockFace.DOWN;
                    break;
                case "HORIZONTAL_CHUNK": case "OMNIDIRECTIONAL_CHUNK":
                    chunkLimited = true;
                    break;
            }
            int limit = main.getConfigValues().getVerticalTravel();
            if (direction != BlockFace.UP && direction != BlockFace.DOWN) {
                limit = main.getConfigValues().getHorizontalTravel();
            }
            new GenningTimer(p, main.getConfigValues().getBucketBlockMaterial(bucket), block,
                    direction, main, limit, chunkLimited).runTaskTimer(main, 0L, main.getConfigValues().getBlockSpeedDelay());
            if (main.getConfigValues().isNotInfinite(bucket)) {
                if (!main.getHookUtils().getBypassPlayers().contains(p)) {
                    if (removeItem.getAmount() == 1) {
                        removeItem.setType(Material.AIR);
                    } else {
                        removeItem.setAmount(removeItem.getAmount() - 1);
                    }
                }
                if (!main.getConfigValues().getPlaceNormalMessage(main.getConfigValues().getBucketPlaceCost(bucket)).equals("")) {
                    p.sendMessage(main.getConfigValues().getPlaceNormalMessage(main.getConfigValues().getBucketPlaceCost(bucket)));
                }
            } else {
                if (!main.getConfigValues().getPlaceInfiniteMessage(main.getConfigValues().getBucketPlaceCost(bucket)).equals("")) {
                    p.sendMessage(main.getConfigValues().getPlaceInfiniteMessage(main.getConfigValues().getBucketPlaceCost(bucket)));
                }
            }
        }
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null &&
                e.getClickedInventory().getName().equals(main.getConfigValues().getGUITitle())) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName())
                if (main.getConfigValues().getBucketFromShopName(e.getCurrentItem().getItemMeta().getDisplayName()) != null) {
                    String bucket = main.getConfigValues().getBucketFromShopName(e.getCurrentItem().getItemMeta().getDisplayName());
                    double price = main.getConfigValues().getBucketBuyPrice(bucket);
                    int amount = 1;
                    if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                        amount = 16;
                        price *= main.getConfigValues().getBulkBuyAmount();
                    }
                    if (main.getHookUtils().takeShopMoney(p, price)) {
                        ItemStack item = main.getUtils().getBucketItemStack(bucket, amount);
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
                            int itemCount = ((ItemStack)excessItem).getAmount();
                            while (itemCount > 64) {
                                ((ItemStack) excessItem).setAmount(64);
                                p.getWorld().dropItemNaturally(p.getLocation(), (ItemStack)excessItem);
                                itemCount = itemCount - 64;
                            }
                            if (itemCount > 0) {
                                ((ItemStack) excessItem).setAmount(itemCount);
                                p.getWorld().dropItemNaturally(p.getLocation(), (ItemStack)excessItem);
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

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (main.getConfigValues().showUpdateMessage() && e.getPlayer().isOp()) {
            main.getUtils().checkUpdates(e.getPlayer());
        }
    }
}
