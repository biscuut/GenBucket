package codes.biscuit.simplegenbuckets.hooks;

import com.massivecraft.factions.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

// Also works for SavageFactions
class FactionsUUIDHook {

    boolean hasFaction(Player p) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(p);
        return fPlayer.hasFaction();
    }

    boolean isWilderness(Location loc) {
        FLocation fLoc = new FLocation(loc);
        Faction fLocFaction = Board.getInstance().getFactionAt(fLoc);
        return fLocFaction.isWilderness();
    }

    boolean locationIsFactionClaim(Location loc, Player p) {
        Faction locFaction = Board.getInstance().getFactionAt(new FLocation(loc));
        Faction pFaction = FPlayers.getInstance().getByPlayer(p).getFaction();
        return locFaction.equals(pFaction);
    }
}
