package feildmaster.module.townychannel;

import java.util.Map;
import java.util.HashMap;
import com.feildmaster.channelchat.event.channel.*;
import com.feildmaster.channelchat.configuration.ModuleConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;

public class Towny extends JavaPlugin {
    private static Towny plugin;
    private TownyChannel townChan;
    private TownyChannel nationChan;
    private ModuleConfiguration config;
    private Map<String, TownyChannel> channels = new HashMap<String, TownyChannel>(2);

    public void onEnable() {
        plugin = this;

        setupConfig();
        reloadConfig();

        ChannelListener listener = new ChannelListener() {
            public void onReload(ReloadEvent event) {
                reloadConfig();
            }
            public void onChannelDelete(ChannelDeleteEvent event) {
                if(event.isCancelled()) return;

                if(event.getChannel() instanceof TownyChannel) {
                    event.setCancelled(true);
                    event.setCancelReason("You can't delete TownyChannel!");
                }
            }
        };

        getManager().registerEvent(ChannelEvent.Type.RELOAD, listener, ChannelEvent.Priority.Highest, plugin);
        getManager().registerEvent(ChannelEvent.Type.DELETE, listener, ChannelEvent.Priority.Highest, plugin);

        getServer().getLogger().info(String.format("[%1$s] v%2$s Enabled!", getDescription().getName(), getDescription().getVersion()));
    }

    public void onDisable() {
        removeTown();
        removeNation();
        getServer().getLogger().info(String.format("[%1$s] Disabled!", getDescription().getName()));
    }

    private void setupConfig() {
        config = new ModuleConfiguration(this);

        if(!config.exists())
            config.saveDefaults();

        config.checkDefaults();
    }

    public void reloadConfig() {
        config.load();

        // TODO: Clean, Streamline
        if(townChan == null) townChan = new TownyChannel(config.getString("Town.name"));

        townChan.setTag(config.getString("Town.tag"));
        townChan.setListed(config.getBoolean("Town.listed"));
        townChan.setAuto(config.getBoolean("Town.auto"));

        String alias = config.getString("Town.alias");
        if(!townChan.setAlias(alias)) getServer().getLogger().info("Alias "+alias+" is taken.");

        if(config.getBoolean("Town.enabled")) addTown();
        else removeTown();

        if(nationChan == null) nationChan = new TownyChannel(config.getString("Nation.name"), TownyChannel.TownType.Nation);

        nationChan.setTag(config.getString("Nation.tag"));
        nationChan.setListed(config.getBoolean("Nation.listed"));
        nationChan.setAuto(config.getBoolean("Nation.auto"));

        String alias2 = config.getString("Nation.alias");
        if(!nationChan.setAlias(alias2)) getServer().getLogger().info("Alias "+alias2+" is taken.");

        if(config.getBoolean("Nation.enabled")) addNation();
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
            getManager().delChannel(townChan);
        }

        townChan = null;
    }

    private void removeNation() {
        if(getManager().channelExists(nationChan)) {
            nationChan.sendMessage("TownyChannel has disabled, or is reloading.");
            getManager().delChannel(nationChan);
        }
        
        nationChan = null;
    }

    public static Towny getPlugin() {
        return plugin;
    }
}
