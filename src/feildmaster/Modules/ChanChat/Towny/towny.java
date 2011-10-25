package feildmaster.Modules.ChanChat.Towny;

import com.feildmaster.chanchat.Chat;
import com.feildmaster.chanchat.Util.ModuleConfiguration;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.blockface.bukkitstats.CallHome;
import org.bukkit.plugin.java.JavaPlugin;

public class towny extends JavaPlugin {
    public static TownyUniverse plugin;

    private TownyChannel channel;

    private ModuleConfiguration config;

    // Configuration
    public boolean town_enabled;
    public boolean town_listed;
    public String town_name;
    public String town_tag;
    public String town_alias;

    public void onEnable() {
        CallHome.load(this);

        setupConfig();

        try {
            plugin = ((Towny)getServer().getPluginManager().getPlugin("Towny")).getTownyUniverse();
        } catch (Exception e) {
            getServer().getLogger().info(String.format("[%1$s] %2$s", getDescription().getName(), "Towny not found, channel not created."));
        }

        if(town_enabled) {
            channel = new TownyChannel(town_name);
            channel.setTag(town_tag);
            channel.setListed(town_listed);
            Chat.getChannelManager().addChannel(channel);
        }

        getServer().getLogger().info(String.format("[%1$s] v%2$s Enabled!", getDescription().getName(), getDescription().getVersion()));
    }

    public void onDisable() {
        //config.closeStream(); // Not necissary after my various tests...
        Chat.getChannelManager().delChannel(channel.getName());
        getServer().getLogger().info(String.format("[%1$s] Disabled!", getDescription().getName()));
    }

    private void setupConfig() {
        config = new ModuleConfiguration(this);
        if(!config.exists())
            config.saveDefaults();

        load_config();
    }

    private void load_config() {
        config.load();

        town_enabled = config.getBoolean("Town.enabled");
        town_name = config.getString("Town.name");
        town_tag = config.getString("Town.tag");
        town_listed = config.getBoolean("Town.listed");
    }
}
