package feildmaster.module.townychannel;

import com.feildmaster.channelchat.Module;
import org.bukkit.event.*;
import java.util.Map;
import java.util.HashMap;
import com.feildmaster.channelchat.event.channel.*;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;

public class Towny extends Module implements Listener {
    private static Towny plugin;
    private TownyChannel townChan;
    private TownyChannel nationChan;
    private Map<String, TownyChannel> channels = new HashMap<String, TownyChannel>(2);

    public void onEnable() {
        plugin = this;
        reloadConfig();
        registerEvents(this);
    }

    public void onDisable() {
        removeTown();
        removeNation();
    }

    @EventHandler
    public void onReload(ReloadEvent event) {
        reloadConfig();
    }

    @EventHandler
    public void onChannelDelete(ChannelDeleteEvent event) {
        if(event.isCancelled()) return;

        if(event.getChannel() instanceof TownyChannel) {
            event.setCancelled(true);
            event.setCancelReason("You can't delete TownyChannel!");
        }
    }

    public void reloadConfig() {
        super.reloadConfig();

        if(!getConfig().fileExists() || !getConfig().checkDefaults())
            getConfig().saveDefaults();

        // TODO: Clean, Streamline
        if(townChan == null) townChan = new TownyChannel(getConfig().getString("Town.name"));

        townChan.setTag(getConfig().getString("Town.tag"));
        townChan.setListed(getConfig().getBoolean("Town.listed"));
        townChan.setAuto(getConfig().getBoolean("Town.auto"));
        if(!townChan.setAlias(getConfig().getString("Town.alias"))) getServer().getLogger().info("Alias "+getConfig().getString("Town.alias")+" is taken.");

        if(getConfig().getBoolean("Town.enabled")) addTown();
        else removeTown();

        if(nationChan == null) nationChan = new TownyChannel(getConfig().getString("Nation.name"), TownyChannel.TownType.Nation);

        nationChan.setTag(getConfig().getString("Nation.tag"));
        nationChan.setListed(getConfig().getBoolean("Nation.listed"));
        nationChan.setAuto(getConfig().getBoolean("Nation.auto"));
        if(!nationChan.setAlias(getConfig().getString("Nation.alias"))) getServer().getLogger().info("Alias "+getConfig().getString("Nation.alias")+" is taken.");

        if(getConfig().getBoolean("Nation.enabled")) addNation();
        else removeNation();
    }

    private void addTown() {
        if(!getManager().addChannel(townChan)) {
            getServer().getLogger().info("Channel could not be added! (Name {"+townChan.getName()+"} taken?)");
            townChan = null;
        }
    }

    private void addNation() {
        if(!getManager().addChannel(nationChan)) {
            getServer().getLogger().info("Channel could not be added! (Name {"+townChan.getName()+"} taken?)");
            nationChan = null;
        }
    }

    private void removeTown() {
        if(getManager().channelExists(townChan)) {
            townChan.sendMessage("TownyChannel has disabled, or is reloading.");
            getManager().deleteChannel(townChan);
        }

        townChan = null;
    }

    private void removeNation() {
        if(getManager().channelExists(nationChan)) {
            nationChan.sendMessage("TownyChannel has disabled, or is reloading.");
            getManager().deleteChannel(nationChan);
        }
        
        nationChan = null;
    }

    public static Towny getPlugin() {
        return plugin;
    }
}
