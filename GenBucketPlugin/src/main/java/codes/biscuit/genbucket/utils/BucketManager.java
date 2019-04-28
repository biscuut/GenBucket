package codes.biscuit.genbucket.utils;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BucketManager {

    private Map<String, Bucket> buckets = new HashMap<>(); // id --> bucket

    public Map<String, Bucket> getBuckets() {
        return buckets;
    }

    Bucket createBucket(String id) {
        Bucket newBucket = new Bucket(id);
        buckets.put(id, newBucket);
        return newBucket;
    }

    public Bucket getBucket(String id) {
        return buckets.get(id);
    }

    public Bucket matchBucket(ItemStack item) {
        for (Map.Entry<String, Bucket> entry : buckets.entrySet()) {
            Bucket bucket = getBucket(entry.getKey());
            ItemStack otherItem = bucket.getItem();
            if (otherItem.getType() == item.getType() && ((item.hasItemMeta() && otherItem.getItemMeta().hasDisplayName()) || (!item.hasItemMeta() && !otherItem.getItemMeta().hasDisplayName()))
                    && otherItem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()) && ((!otherItem.getItemMeta().hasLore() && !item.getItemMeta().hasLore()) || (otherItem.getItemMeta().getLore().containsAll(item.getItemMeta().getLore())))) {
                return bucket;
            }
        }
        return null;
    }

    public Bucket fromSlot(int slot) {
        for (Map.Entry<String, Bucket> entry : buckets.entrySet()) {
            Bucket bucket = getBucket(entry.getKey());
            if (bucket.getSlot() == slot) {
                return bucket;
            }
        }
        return null;
    }

    public Bucket fromShopName(String name) {
        for (Map.Entry<String, Bucket> entry : buckets.entrySet()) {
            Bucket bucket = getBucket(entry.getKey());
            if (bucket.getGuiItem().getItemMeta().getDisplayName().equals(name)) {
                return bucket;
            }
        }
        return null;
    }
}
