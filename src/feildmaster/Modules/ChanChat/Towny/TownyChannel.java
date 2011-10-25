package feildmaster.Modules.ChanChat.Towny;

import com.feildmaster.chanchat.Chan.CustomChannel;
import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class TownyChannel extends CustomChannel {
    private final TownType type;

    private Set<String> members;
    private String nametag;

    public TownyChannel(String name) {
        super(name);
        type = TownType.Town;
    }

    // TODO: Check if player has a town/nation before allowing to join!
    public void sendJoinMessage(Player player) {
        if(type == TownType.Town && (getResident(player) != null && getResident(player).hasTown())) {
            nametag = getTown(getResident(player)).getName();
        }

        super.sendJoinMessage(player);

        nametag = null;
    }

    public Boolean isMember(Player player) {
        if(members == null) return super.isMember(player);
        return super.isMember(player) && members.contains(player.getName());
    }

    public String getDisplayName() {
        return super.getDisplayName().replaceAll("(?i)\\{name}", nametag == null?getName():nametag);
    }

    public void handleEvent(PlayerChatEvent event) {
        List<Resident> list = null;

        try {
            Resident res = towny.plugin.getResident(event.getPlayer().getName());
            if(!res.hasTown()) {
                event.getPlayer().sendMessage(format(" You do not have a town."));
                event.setCancelled(true);
                return;
            }
            Town town = res.getTown();
            if(type.equals(TownType.Town)) {
                list = town.getResidents();
                nametag = town.getName();
            }else if(town.hasNation()) {
                list = town.getNation().getResidents();
                nametag = town.getNation().getName();
            } else {
                // Your town does not belong to a nation.
                event.isCancelled();
                return;
            }
        } catch (NotRegisteredException e) {
            event.getPlayer().sendMessage(format(" Not registered with towny."));
            event.setCancelled(true);
            return;
        }

        members = new HashSet<String>();

        for(Resident r : list)
            members.add(r.getName());

        super.handleEvent(event);

        members = null;
        nametag = null;
    }

    public static enum TownType {
        Town,
        Nation;
    }

    private Resident getResident(Player player) {
        Resident resident = null;
        try {
            resident = towny.plugin.getResident(player.getName());
        } catch (NotRegisteredException ex) {}
        return resident;
    }

    private Town getTown(Resident player) {
        try {
            return player.getTown();
        } catch (NotRegisteredException ex) {
            return null;
        }
    }

    private Nation getNation(Town town) {
        try {
            return town.getNation();
        } catch (NotRegisteredException ex) {
            return null;
        }
    }
}
