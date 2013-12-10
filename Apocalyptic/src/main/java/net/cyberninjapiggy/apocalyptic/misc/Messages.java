package net.cyberninjapiggy.apocalyptic.misc;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
 
public class Messages {
	private Plugin plugin = null;
	 
	private File configFile = null;
	private FileConfiguration config = null;
	 
	public Messages(Plugin plugin)
	{
		this.plugin = plugin;
		this.configFile = new File(this.plugin.getDataFolder(), "lang.yml");
		 
		this.reload();
		this.saveDefault();
	}
	 
	public void reload()
	{
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		 
		InputStream defaultConfigStream = this.plugin.getResource("lang.yml");
		if (defaultConfigStream != null)
		{
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
			this.config.setDefaults(defaultConfig);
		}
	}
	 
	public void saveDefault()
	{
		if (!this.configFile.exists())
		{
			this.plugin.saveResource("lang.yml", false);
		}
	}
	 
	public String getCaption(String name)
	{
		return this.getCaption(name, false);
	}
	 
	public String getCaption(String name, boolean color)
	{
		String caption = this.config.getString(name);
		if (caption == null)
		{
		this.plugin.getLogger().warning("Missing caption: " + name);
		caption = "&c[missing caption]";
		}
		 
		if (color)
		{
			caption = ChatColor.translateAlternateColorCodes('&', caption);
		}
		return caption;
	}
	public String getString(String msg) {
		return getCaption(msg);
	}
}