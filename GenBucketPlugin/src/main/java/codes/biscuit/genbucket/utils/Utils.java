package codes.biscuit.genbucket.utils;

import codes.biscuit.genbucket.GenBucket;
import codes.biscuit.genbucket.timers.GenningTimer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Pattern;

public class Utils {

    private GenBucket main;
    private List<Recipe> recipeList = new ArrayList<>();
    private Map<Location, GenningTimer> currentGens = new HashMap<>();

    public Utils(GenBucket main) {
        this.main = main;
    }

    static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    static List<String> colorLore(List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, color(lore.get(i)));
        }
        return lore; // For convenience
    }

    ItemStack itemFromString(String rawItem) {
        Material material;
        String[] rawSplit;
        if (rawItem.contains(":")) {
            rawSplit = rawItem.split(":");
        } else {
            rawSplit = new String[] {rawItem};
        }
        try {
            material = Material.valueOf(rawSplit[0]);
        } catch (IllegalArgumentException ex) {
            material = Material.DIRT;
        }
        short damage = 0;
        if (rawSplit.length > 1) {
            try {
                damage = Short.valueOf(rawSplit[1]);
            } catch (IllegalArgumentException ignored) {}
        }
        return new ItemStack(material, 1, damage);
    }

    Material materialFromString(String rawMaterial) {
        Material material;
        String[] rawSplit;
        if (rawMaterial.contains(":")) {
            rawSplit = rawMaterial.split(":");
        } else {
            rawSplit = new String[] {rawMaterial};
        }
        try {
            material = Material.valueOf(rawSplit[0]);
        } catch (IllegalArgumentException ex) {
            material = Material.DIRT;
        }
        return material;
    }

    public ItemStack addGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public void registerRecipes() {
        if (main.getConfigValues().getRecipeBuckets() != null) {
            for (String bucketID : main.getConfigValues().getRecipeBuckets()) {
                Bucket bucket = main.getBucketManager().getBucket(bucketID);
                ItemStack item = bucket.getItem();
                item.setAmount(main.getConfigValues().getRecipeAmount(bucket));
                ShapedRecipe newRecipe = new ShapedRecipe(item);
                if (main.getConfigValues().getRecipeShape(bucket) != null) {
                    newRecipe.shape(main.getConfigValues().getRecipeShape(bucket).get(0), main.getConfigValues().getRecipeShape(bucket).get(1), main.getConfigValues().getRecipeShape(bucket).get(2));
                } else {
                    continue;
                }
                if (main.getConfigValues().getIngredients(bucket) != null) {
                    for (Map.Entry<Character, Map<Material, Short>> ingredient : main.getConfigValues().getIngredients(bucket).entrySet()) {
                        Material mat = null;
                        for (Material loopMat : ingredient.getValue().keySet()) mat = loopMat;
                        short data;
                        if (mat != null) {
                            data = ingredient.getValue().get(mat);
                        } else {
                            continue;
                        }
                        if (data != 1) {
                            newRecipe.setIngredient(ingredient.getKey(), new ItemStack(mat, 1, data).getData());
                        } else {
                            newRecipe.setIngredient(ingredient.getKey(), mat);
                        }
                    }
                } else {
                    continue;
                }
                main.getServer().addRecipe(newRecipe);
                recipeList.add(newRecipe);
            }
        }
    }

    public void reloadRecipes() {
        List<Recipe> oldRecipes = new ArrayList<>();
        {
            Iterator<Recipe> allRecipes = main.getServer().recipeIterator();
            while(allRecipes.hasNext()){
                Recipe recipe = allRecipes.next();
                if(!recipeList.contains(recipe)) {
                    oldRecipes.add(recipe);
                }
            }
        }
        main.getServer().clearRecipes();
        recipeList.clear();
        for (Recipe recipe : oldRecipes) main.getServer().addRecipe(recipe);
        registerRecipes();
    }

    public void updateConfig() {
        if (main.getConfigValues().getConfigVersion() < 1.2) {
            Map<String, Object> oldValues = new HashMap<>();
            for (String oldKey : main.getConfig().getKeys(true)) {
                oldValues.put(oldKey, main.getConfig().get(oldKey));
            }
            main.saveResource("config.yml", true);
            main.reloadConfig();
            for (String newKey : main.getConfig().getKeys(true)) {
                if (oldValues.containsKey(newKey)) {
                    main.getConfig().set(newKey, oldValues.get(newKey));
                }
            }
            main.getConfig().set("config-version", 1.2);
            main.saveConfig();
        }
    }

    public void checkUpdates(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=63651");
                    URLConnection connection = url.openConnection();
                    connection.setReadTimeout(5000);
                    connection.addRequestProperty("User-Agent", "GenBucket update checker");
                    connection.setDoOutput(true);
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String newestVersion = reader.readLine();
                    reader.close();
                    List<Integer> newestVersionNumbers = new ArrayList<>();
                    List<Integer> thisVersionNumbers = new ArrayList<>();
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
                                newVersion.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/genbucket-1-8-1-13-turn-any-block-into-a-wall-or-floor.63651/"));
                                p.spigot().sendMessage(newVersion);
                                return;
                            } else if (thisVersionNumbers.get(i) > newestVersionNumbers.get(i)) {
                                p.sendMessage(ChatColor.RED + "You are running a development version of "+main.getDescription().getName()+", " + main.getDescription().getVersion() + ". The latest online version is " + newestVersion + ".");
                                return;
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
        }.runTask(main);
    }

    public Map<Location, GenningTimer> getCurrentGens() {
        return currentGens;
    }
}
