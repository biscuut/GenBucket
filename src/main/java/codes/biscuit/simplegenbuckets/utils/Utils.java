package codes.biscuit.simplegenbuckets.utils;

import codes.biscuit.simplegenbuckets.SimpleGenBuckets;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Set;

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
}
