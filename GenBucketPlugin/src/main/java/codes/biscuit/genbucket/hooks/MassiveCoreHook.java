package codes.biscuit.genbucket.hooks;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class MassiveCoreHook {

    boolean hasNoFaction(Player p) {
        return !MPlayer.get(p).hasFaction();
    }

    boolean isWilderness(Location loc) {
        return BoardColl.get().getFactionAt(PS.valueOf(loc)).isNone();
    }

    boolean locationIsNotFaction(Location loc, Player p) {
        Faction locFaction = BoardColl.get().getFactionAt(PS.valueOf(loc));
        Faction pFaction = MPlayer.get(p).getFaction();
        return !locFaction.equals(pFaction);
    }

    boolean isFriendlyPlayer(Player p, Player otherP) {
        MPlayer mPlayer = MPlayer.get(p);
        MPlayer otherMPlayer = MPlayer.get(otherP);
        Rel relation = mPlayer.getRelationTo(otherMPlayer);
        return relation == Rel.MEMBER || relation == Rel.ALLY || relation == Rel.TRUCE;
    }
}
