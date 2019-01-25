package codes.biscuit.genbucket.commands;

import codes.biscuit.genbucket.GenBucket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GenBucketAdminCommand implements TabExecutor {

    private GenBucket main;

    public GenBucketAdminCommand(GenBucket main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>(Arrays.asList("give", "reload", "bypass"));
            for (String arg : Arrays.asList("give", "reload", "bypass")) {
                if (!arg.startsWith(args[0].toLowerCase())) {
                    arguments.remove(arg);
                }
            }
            return arguments;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> arguments = new ArrayList<>(main.getConfigValues().getBucketList());
            for (String arg : main.getConfigValues().getBucketList()) {
                if (!arg.startsWith(args[2].toLowerCase())) {
                    arguments.remove(arg);
                }
            }
            return arguments;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "give":
                    if (sender.hasPermission("genbucket.give")) {
                        if (args.length > 1) {
                            Player p = Bukkit.getPlayerExact(args[1]);
                            if (p != null) {
                                if (args.length > 2) {
                                    String bucket = args[2];
                                    if (main.getConfigValues().bucketExists(bucket)) {
                                        int giveAmount = 1;
                                        if (args.length > 3) {
                                            try {
                                                giveAmount = Integer.parseInt(args[3]);
                                            } catch (NumberFormatException ex) {
                                                sender.sendMessage(ChatColor.RED + "This isn't a valid give amount!");
                                                return false;
                                            }
                                        }
                                        ItemStack item = main.getUtils().getBucketItemStack(bucket, giveAmount);
                                        Map excessItems;
                                        if (!main.getConfigValues().giveShouldDropItem()) {
                                            if (giveAmount < 65) {
                                                if (p.getInventory().firstEmpty() == -1) {
                                                    sender.sendMessage(ChatColor.RED + "This player doesn't have any empty slots in their inventory!");
                                                    return true;
                                                }
                                            } else {
                                                sender.sendMessage(ChatColor.RED + "You can only give 64 at a time!");
                                                return true;
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
                                        double price = main.getConfigValues().getBucketShopPrice(bucket) * giveAmount;
                                        if (!main.getConfigValues().getGiveMessage(p, giveAmount, bucket).equals("")) {
                                            sender.sendMessage(main.getConfigValues().getGiveMessage(p, giveAmount, bucket));
                                        }
                                        if (!main.getConfigValues().getReceiveMessage(giveAmount, price, bucket).equals("")) {
                                            p.sendMessage(main.getConfigValues().getReceiveMessage(giveAmount, price, bucket));
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "This bucket does not exist!");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Please specify a bucket!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "This player is not online!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Please specify a player!");
                        }
                    } else {
                        if (!main.getConfigValues().getNoPermissionCommandMessage().equals("")) {
                            sender.sendMessage(main.getConfigValues().getNoPermissionCommandMessage());
                        }
                    }
                    break;
                case "reload":
                    if (sender.hasPermission("genbucket.reload")) {
                        main.reloadConfig();
                        main.getUtils().reloadRecipes();
                        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config. Most values have been instantly updated.");
                    } else {
                        if (!main.getConfigValues().getNoPermissionCommandMessage().equals("")) {
                            sender.sendMessage(main.getConfigValues().getNoPermissionCommandMessage());
                        }
                    }
                    break;
                case "bypass":
                    if (sender instanceof Player) {
                        if (sender.hasPermission("genbucket.bypass")) {
                            if (main.getHookUtils().getBypassPlayers().contains(((Player)sender).getUniqueId())) {
                                sender.sendMessage(ChatColor.RED + "You can no longer place genbuckets anywhere infinitely.");
                                main.getHookUtils().getBypassPlayers().remove(((Player)sender).getUniqueId());
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "You can now place genbuckets anywhere infinitely.");
                                main.getHookUtils().getBypassPlayers().add(((Player)sender).getUniqueId());
                            }
                        } else {
                            if (!main.getConfigValues().getNoPermissionCommandMessage().equals("")) {
                                sender.sendMessage(main.getConfigValues().getNoPermissionCommandMessage());
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You can only use this command in-game!");
                    }
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Invalid argument!");
            }
        } else {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------" + ChatColor.GRAY +"[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " GenBucket " + ChatColor.GRAY + "]" + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "● /gba give <player> <bucket> [amount] " + ChatColor.GRAY + "- Give a player (a) genbucket(s)");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "● /gba reload " + ChatColor.GRAY + "- Reload the config");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "● /gba bypass " + ChatColor.GRAY + "- Buy and place genbuckets anywhere infinitely and for free!");
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "v" + main.getDescription().getVersion() + " by Biscut");
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------------------------------");
        }
        return false;
    }
}
