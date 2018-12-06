package codes.biscuit.simplegenbuckets.utils;

import codes.biscuit.simplegenbuckets.SimpleGenBuckets;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Utils {

    private SimpleGenBuckets main;

    public Utils(SimpleGenBuckets main) {
        this.main = main;
    }

    public String matchBucket(ItemStack item) {
        if (main.getConfigValues().getBucketMaterialList().keySet().contains(item.getType()) && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            Set<Map.Entry<Material, String>> materialList = main.getConfigValues().getBucketMaterialList().entrySet();
            for (Map.Entry<Material, String> material : materialList) {
                if (material.getKey().equals(item.getType())) {
                    if (main.getConfigValues().getBucketItemName(material.getValue()).equals(item.getItemMeta().getDisplayName())) {
                        return material.getValue();
                    }
                }
            }
        }
        return null;
    }

    public ItemStack addGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public void checkUpdates(Player p) {
        try {
            URL url = new URL("https://github.com/biscuut/"+main.getDescription().getName()+"/blob/master/pom.xml");
            URLConnection connection = url.openConnection();
            connection.setReadTimeout(5000);
            connection.addRequestProperty("User-Agent", "SimpleGenBuckets update checker");
            connection.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String currentLine;
            String newestVersion = "";
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("<version>")) {
                    String[] newestVersionSplit = currentLine.split(Pattern.quote("<version>"));
                    newestVersionSplit = newestVersionSplit[1].split(Pattern.quote("</version>"));
                    newestVersion = newestVersionSplit[0];
                    break;
                }
            }
            reader.close();
            ArrayList<Integer> newestVersionNumbers = new ArrayList<>();
            ArrayList<Integer> thisVersionNumbers = new ArrayList<>();
            try {
                for (String s : newestVersion.split(Pattern.quote("."))) {
                    newestVersionNumbers.add(Integer.parseInt(s));
                }
                for (String s : main.getDescription().getVersion().split(Pattern.quote("."))) {
                    thisVersionNumbers.add(Integer.parseInt(s));
                }
            } catch (Exception ex) {
                return;
            }
            for (int i = 0; i < 3; i++) {
                if (newestVersionNumbers.get(i) != null && thisVersionNumbers.get(i) != null) {
                    if (newestVersionNumbers.get(i) > thisVersionNumbers.get(i)) {
                        TextComponent newVersion = new TextComponent("A new version of "+main.getDescription().getName()+", " + newestVersion + " is available. Download it by clicking here.");
                        newVersion.setColor(ChatColor.RED);
                        newVersion.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/biscuut/"+main.getDescription().getName()+"/releases")); //TODO Change this to the spigot page when I post it.
                        p.spigot().sendMessage(newVersion);
                    } else if (thisVersionNumbers.get(i) > newestVersionNumbers.get(i)) {
                        p.sendMessage(ChatColor.RED + "You are running a development version of "+main.getDescription().getName()+", " + main.getDescription().getVersion() + ". The latest online version is " + newestVersion + ".");
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
