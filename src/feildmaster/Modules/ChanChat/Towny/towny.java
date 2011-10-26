package feildmaster.Modules.ChanChat.Towny;

import com.feildmaster.chanchat.Chat;
import com.feildmaster.chanchat.Util.ModuleConfiguration;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.blockface.bukkitstats.CallHome;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class towny extends JavaPlugin {
    public static TownyUniverse towny;
    private static towny plugin;


    private TownyChannel channel;

    private ModuleConfiguration config;

    // Configuration
    private boolean town_enabled;
    private boolean town_listed;
    private String town_name;
    private String town_tag;
    private String town_alias;
    private ChatColor town_color;

    public void onEnable() {
        plugin = this;
        CallHome.load(this);

        setupConfig();

        try {
            towny = ((Towny)getServer().getPluginManager().getPlugin("Towny")).getTownyUniverse();
        } catch (Exception e) {
            getServer().getLogger().info(String.format("[%1$s] %2$s", getDescription().getName(), "Towny not found, channel not created."));
        }


        reloadConfig();

        if(town_enabled)
            Chat.getChannelManager().addChannel(channel);

        getServer().getLogger().info(String.format("[%1$s] v%2$s Enabled!", getDescription().getName(), getDescription().getVersion()));
    }

    public void onDisable() {
        Chat.getChannelManager().delChannel(channel.getName());
        getServer().getLogger().info(String.format("[%1$s] Disabled!", getDescription().getName()));
    }

    private void setupConfig() {
        config = new ModuleConfiguration(this);
        if(!config.exists())
            config.saveDefaults();
    }

    private void loadConfig() {
        config.load();

        town_enabled = config.getBoolean("Town.enabled");
        town_name = config.getString("Town.name");
        town_tag = config.getString("Town.tag");
        town_alias = config.getString("Town.alias");
        town_listed = config.getBoolean("Town.listed");
        town_color = config.getChatColor("Town.color");
    }

    public void reloadConfig() {
        loadConfig();
        if(channel == null) channel = new TownyChannel(town_name);

        channel.setTag(town_tag);
        channel.setListed(town_listed);
        channel.setChatColor(town_color);
        channel.setAlias(town_alias);
    }

    public static towny getPlugin() {
        return plugin;
    }
}
