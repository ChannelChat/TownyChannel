package com.feildmaster.module.towny;

import com.feildmaster.channelchat.channel.CustomChannel;
import com.palmergames.bukkit.towny.object.*;
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
        this(name, TownType.Town);
    }

    public TownyChannel(String name, TownType type) {
        super(name);
        this.type = type;
    }

    @Override
    public void sendJoinMessage(Player player) {
        Resident a = getResident(player);

        if(a == null) {
            return;
        }

        members = new HashSet<String>();

        if(type.isTown() && a.hasTown()) {
            for(Resident r : getTown(a).getResidents())
                members.add(r.getName());
            nametag = getTown(a).getName();
        } else if(type.isNation() && a.hasNation()) {
            for(Resident r : getNation(a).getResidents())
                members.add(r.getName());
            nametag = getNation(a).getName();
        } else {
            members = null;
            return;
        }

        super.sendJoinMessage(player);

        members = null;
        nametag = null;
    }

    @Override
    public Boolean isMember(Player player) {
        if(members == null) return super.isMember(player);
        return super.isMember(player) && members.contains(player.getName());
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName().replaceAll("(?i)\\{name}", nametag == null?getName():nametag);
    }

    @Override
    public void handleEvent(PlayerChatEvent event) {
        List<Resident> list;

        Resident res = getResident(event.getPlayer());
        if(!res.hasTown()) {
            event.getPlayer().sendMessage(format(" You do not have a town."));
            event.setCancelled(true);
            return;
        }

        Town town = getTown(res);
        if(type.isTown()) {
            list = town.getResidents();
            nametag = town.getName();
        }else if(type.isNation() && town.hasNation()) {
            Nation nation = getNation(res);
            list = nation.getResidents();
            nametag = nation.getName();
        } else {
            event.getPlayer().sendMessage(format(" You do not have a nation."));
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

    private Resident getResident(Player player) {
        try {
            return TownyUniverse.getDataSource().getResident(player.getName());
        } catch (Exception ex) {
            return null;
        }
    }

    private Town getTown(Resident player) {
        try {
            return player.getTown();
        } catch (Exception ex) {
            return null;
        }
    }

    private Nation getNation(Resident player) {
        try {
            return getTown(player).getNation();
        } catch (Exception ex) {
            return null;
        }
    }

    public void callReload() {
        Towny.getPlugin().reloadConfig();
    }

    public static enum TownType {
        Town,
        Nation;

        public boolean isTown() {
            return this.equals(Town);
        }

        public boolean isNation() {
            return this.equals(Nation);
        }
    }
}
