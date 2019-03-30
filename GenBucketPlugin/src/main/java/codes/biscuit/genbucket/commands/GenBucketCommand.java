package codes.biscuit.genbucket.commands;

import codes.biscuit.genbucket.GenBucket;
import codes.biscuit.genbucket.utils.Bucket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GenBucketCommand implements CommandExecutor {

    private GenBucket main;

    public GenBucketCommand(GenBucket main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("genbucket.shop")) {
                Player p = (Player)sender;
                if (main.getConfigValues().isGUIEnabled()) {
                    if (!p.getOpenInventory().getTitle().contains(main.getConfigValues().getGUITitle())) {
                        Inventory confirmInv = Bukkit.createInventory(null, 9 * main.getConfigValues().getGUIRows(), main.getConfigValues().getGUITitle());
                        ItemStack exitItem = main.getConfigValues().getExitItemStack();
                        if (!exitItem.getType().equals(Material.AIR)) {
                            ItemMeta cancelItemMeta = exitItem.getItemMeta();
                            cancelItemMeta.setDisplayName(main.getConfigValues().getExitName());
                            cancelItemMeta.setLore(main.getConfigValues().getExitLore());
                            exitItem.setItemMeta(cancelItemMeta);
                            if (main.getConfigValues().isExitGlowing()) {
                                exitItem = main.getUtils().addGlow(exitItem);
                            }
                        }
                        ItemStack fillItem = main.getConfigValues().getFillItemStack();
                        if (!fillItem.getType().equals(Material.AIR)) {
                            ItemMeta fillItemMeta = fillItem.getItemMeta();
                            fillItemMeta.setDisplayName(main.getConfigValues().getFillName());
                            fillItemMeta.setLore(main.getConfigValues().getFillLore());
                            fillItem.setItemMeta(fillItemMeta);
                            if (main.getConfigValues().isFillGlowing()) {
                                fillItem = main.getUtils().addGlow(fillItem);
                            }
                        }
                        for (int i = 0; i < 9 * main.getConfigValues().getGUIRows(); i++) {
                            Bucket bucket = main.getBucketManager().fromSlot(i);
                            if (bucket != null) {
                                ItemStack bucketItem = bucket.getGuiItem();
                                confirmInv.setItem(i, bucketItem);
                            } else if (main.getConfigValues().getExitSlots().contains(i)) {
                                confirmInv.setItem(i, exitItem);
                            } else {
                                confirmInv.setItem(i, fillItem);
                            }
                        }
                        p.openInventory(confirmInv);
                    }
                }
            } else {
                if (!main.getConfigValues().getNoPermissionCommandMessage().equals("")) {
                    sender.sendMessage(main.getConfigValues().getNoPermissionCommandMessage());
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You can only use this command in-game!");
        }
        return true;
    }
}
