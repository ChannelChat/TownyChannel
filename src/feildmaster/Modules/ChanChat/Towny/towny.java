package feildmaster.modules.chanchat.towny;

import com.feildmaster.channelchat.event.channel.*;
import com.feildmaster.channelchat.configuration.ModuleConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;

public class Towny extends JavaPlugin {
    private static Towny plugin;
    private TownyChannel townChan;
    private ModuleConfiguration config;

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
        removeChannel();
        getServer().getLogger().info(String.format("[%1$s] Disabled!", getDescription().getName()));
    }

    private void setupConfig() {
        config = new ModuleConfiguration(this);
        if(!config.exists())
            config.saveDefaults();
    }

    // Reserved for configuration updates
    private void loadConfig() {
        config.load();
    }

    public void reloadConfig() {
        loadConfig();
        if(townChan == null) townChan = new TownyChannel(config.getString("Town.name"));

        townChan.setTag(config.getString("Town.tag"));
        townChan.setListed(config.getBoolean("Town.listed"));
        townChan.setAuto(config.getBoolean("Town.auto"));

        String town_alias = config.getString("Town.alias");
        if(!townChan.setAlias(town_alias)) getServer().getLogger().info("Alias "+town_alias+" is taken.");

        if(config.getBoolean("Town.enabled")) addChannel();
        else removeChannel();
    }

    private void addChannel() {
        if(!getManager().addChannel(townChan)) {
            getServer().getLogger().info("Channel could not be added! (Name {"+townChan.getName()+"} taken)");
            townChan = null;
        }
    }

    private void removeChannel() {
        townChan.sendMessage("TownyChannel has disabled, or is reloading.");
        getManager().delChannel(townChan);
        townChan = null;
    }

    public static Towny getPlugin() {
        return plugin;
    }
}
