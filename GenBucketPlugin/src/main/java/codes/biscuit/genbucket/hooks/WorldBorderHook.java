package codes.biscuit.genbucket.hooks;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import org.bukkit.Location;

class WorldBorderHook {

    boolean isInsideBorder(Location loc) {
        BorderData border = Config.Border(loc.getWorld().getName());
        if (border != null) {
            return border.insideBorder(loc);
        } else {
            return true;
        }
    }
}
