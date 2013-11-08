package net.cyberninjapiggy.apocalyptic;

import net.cyberninjapiggy.apocalyptic.generator.RavagedChunkGenerator;
import net.cyberninjapiggy.apocalyptic.misc.Messages;
import net.cyberninjapiggy.apocalyptic.misc.Updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.cyberninjapiggy.apocalyptic.commands.ApocalypticCommandExecutor;
import net.cyberninjapiggy.apocalyptic.commands.RadiationCommandExecutor;
import net.cyberninjapiggy.apocalyptic.events.MonsterSpawn;
import net.cyberninjapiggy.apocalyptic.events.PlayerChangeWorld;
import net.cyberninjapiggy.apocalyptic.events.PlayerDamaged;
import net.cyberninjapiggy.apocalyptic.events.PlayerEat;
import net.cyberninjapiggy.apocalyptic.events.PlayerJoin;
import net.cyberninjapiggy.apocalyptic.events.PlayerLeave;
import net.cyberninjapiggy.apocalyptic.events.PlayerMove;
import net.cyberninjapiggy.apocalyptic.events.PlayerSpawn;
import net.cyberninjapiggy.apocalyptic.events.StopHazmatCrafting;
import net.cyberninjapiggy.apocalyptic.events.ZombieCombust;
import net.cyberninjapiggy.apocalyptic.events.ZombieTarget;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 *
 * @author Nick
 */
public final class Apocalyptic extends JavaPlugin {
    private static Logger log;
    private Database db;
    public Random rand;
    private Plugin wg;
    private boolean wgEnabled = true;
    
    private static final String texturePack = "https://dl.dropboxusercontent.com/s/qilofl4m4e9uvxh/apocalyptic-1.6.zip?dl=1";
    
    public static final String METADATA_KEY = "radiation";
    
    public static ItemStack hazmatHood = setName(new ItemStack(Material.CHAINMAIL_HELMET, 1), ChatColor.RESET + Messages.getString("Apocalyptic.gasMask"));
    public static ItemStack hazmatSuit = setName(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), ChatColor.RESET + Messages.getString("Apocalyptic.hazmatSuit"));
    public static ItemStack hazmatPants = setName(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), ChatColor.RESET + Messages.getString("Apocalyptic.hazmatPants"));
    public static ItemStack hazmatBoots = setName(new ItemStack(Material.CHAINMAIL_BOOTS, 1), ChatColor.RESET + Messages.getString("Apocalyptic.hazmatBoots"));
    
    
    @Override
    public void onEnable(){
        //acidRain.setCustomName("Acid Rain");
        log = getLogger();
        rand = new Random();
        wg = getWorldGuard();
        Messages.load(this);
        if (wg == null) {
        	wgEnabled = false;
        }
        
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!new File(getDataFolder().getPath() + File.separator + "config.yml").exists()) {
            saveDefaultConfig();
        }
        else {
            if (!getConfig().getString("meta.version").equals(this.getDescription().getVersion())) {
                YamlConfiguration defaults = new YamlConfiguration();
                try {
                    defaults.load(this.getClassLoader().getResourceAsStream("config.yml"));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Apocalyptic.class.getName()).log(Level.SEVERE, null, ex);
                } catch (        IOException | InvalidConfigurationException ex) {
                    Logger.getLogger(Apocalyptic.class.getName()).log(Level.SEVERE, null, ex);
                }
                getConfig().update(defaults);
            }
        }
        db = new SQLite(log, Messages.getString("Apocalyptic.logtitle"), getDataFolder().getAbsolutePath(), Messages.getString("Apocalyptic.dbname"));
        
        if (!db.open()) {
            log.severe(Messages.getString("Apocalyptic.errNotOpenDatabase"));
            this.setEnabled(false);
            return;
        }
        try {
            db.query("CREATE TABLE IF NOT EXISTS radiationLevels ("
                    + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "player VARCHAR(16),"
                    + "level DOUBLE)");
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        
        
        ResultSet result;
        try {
            result = db.query("SELECT * FROM radiationLevels");
            while (result.next()) {
                //radiationLevels.put(result.getString("player"), result.getDouble("level"));
            	Bukkit.getPlayer(result.getString("player")).setMetadata(METADATA_KEY, new FixedMetadataValue(this, result.getDouble("level")));
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        
        db.close();
        
        
        if (getConfig().getBoolean("meta.version-check")) {
        	Updater versionCheck = new Updater(this, 43663, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        	if (versionCheck.getLatestName() != this.getDescription().getName() + " v" + this.getDescription().getVersion()) {
        		if (getConfig().getBoolean("meta.auto-update")) {
        			new Updater(this, 43663, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, getConfig().getBoolean("meta.show-download-progress"));
        		}
        		else {
        			log.info(ChatColor.GREEN + Messages.getString("Apocalyptic.updateAvaliable") + versionCheck.getLatestName() + ChatColor.RESET); //$NON-NLS-3$
        		}
        	}
        }
        
        //CommandExecutors
        getCommand("radiation").setExecutor(new RadiationCommandExecutor(this));
        getCommand("apocalyptic").setExecutor(new ApocalypticCommandExecutor(this));
        
        //Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerSpawn(this), this);
        getServer().getPluginManager().registerEvents(new MonsterSpawn(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEat(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamaged(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChangeWorld(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new ZombieTarget(this), this);
        getServer().getPluginManager().registerEvents(new ZombieCombust(this), this);
        getServer().getPluginManager().registerEvents(new StopHazmatCrafting(this), this);
        
        //Add recipes
        ShapedRecipe hazardHelmetR = new ShapedRecipe(hazmatHood);
        hazardHelmetR.shape("SSS", "S S");
        hazardHelmetR.setIngredient('S', Material.SPONGE);
        
        ShapedRecipe hazardChestR = new ShapedRecipe(hazmatSuit);
        hazardChestR.shape("S S", "SSS", "SSS"); //$NON-NLS-3$
        hazardChestR.setIngredient('S', Material.SPONGE);
        
        ShapedRecipe hazardPantsR = new ShapedRecipe(hazmatPants);
        hazardPantsR.shape("SSS", "S S", "S S"); //$NON-NLS-3$
        hazardPantsR.setIngredient('S', Material.SPONGE);
        
        ShapedRecipe hazardBootsR = new ShapedRecipe(hazmatBoots);
        hazardBootsR.shape("S S", "S S");
        hazardBootsR.setIngredient('S', Material.SPONGE);
        
        getServer().addRecipe(hazardBootsR);
        getServer().addRecipe(hazardPantsR);
        getServer().addRecipe(hazardChestR);
        getServer().addRecipe(hazardHelmetR);
        
        //Schedules      
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (World w : getServer().getWorlds()) {
                	Object regions = null;
                    for (Player p : w.getPlayers()) {
                    	if (wgEnabled) {
                    		regions = ((WorldGuardPlugin)wg).getRegionManager(w).getApplicableRegions(p.getLocation());
                    		
                    	}
                    	if (worldEnabledFallout(w.getName())) {
	                        //Acid Rain
	                        Location l = p.getLocation();
	                        if (p.getEquipment().getHelmet() == null
	                                && p.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) <= l.getBlockY() &&
	                                p.getWorld().hasStorm()) {
	                            p.damage(p.getWorld().getDifficulty().ordinal()*2);
	                        }
	                        //Neurological death syndrome
	                        if (getPlayerRadiation(p) >= 10.0D) {
	                            ArrayList<PotionEffect> pfx = new ArrayList<>();
	                            pfx.add(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 2));
	                            pfx.add(new PotionEffect(PotionEffectType.CONFUSION, 10 * 20, 2));
	                            pfx.add(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 2));
	                            pfx.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10 * 20, 2));
	                            pfx.add(new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 2));
	                            p.addPotionEffects(pfx);
	                        }
	                        //Add radiation
	                        boolean hazmatSuit = playerWearingHazmatSuit(p);
	                        boolean aboveLowPoint = p.getLocation().getBlockY() > getConfig().getWorld(w).getInt("radiationBottom");
	                        boolean belowHighPoint = p.getLocation().getBlockY() < getConfig().getWorld(w).getInt("radiationTop");
	                        boolean random = new Random(p.getWorld().getSeed()).nextInt(4) == 0;
	                        if (!hazmatSuit
	                                && aboveLowPoint
	                                && belowHighPoint
	                                && random) {
	                            addPlayerRadiation(p, (p.getWorld().getEnvironment() == Environment.NETHER ? 0.2 : 0.1) * (Math.round(p.getLevel() / 10)+1));
	                        }
	                    }
                    }
                }
            }
        }, 20 * ((long)10), 20 * ((long)10));
    }
 
    @Override
    public void onDisable() {
        if (!db.open()) {
            log.severe(Messages.getString("Apocalyptic.errNotOpenDatabase"));
            return;
        }
        try {
            for (World w : Bukkit.getWorlds()) {
            	for (Player p : w.getPlayers()) {
	                saveRadiation(p);
            	}
            }
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Apocalyptic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String GenID) {
        return new RavagedChunkGenerator();
    }
    private static ItemStack setName(ItemStack is, String name){
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(name);
        is.setItemMeta(m);
        return is;
    }
    /**
     * 
     * @param name of the world
     * @return whether the named world has fallout enabled
     */
    public boolean worldEnabledFallout(String name) {
        return getConfig().getConfigurationSection("worlds").getKeys(false).contains(name) && getConfig().getBoolean("worlds." + name + ".fallout"); //$NON-NLS-3$
    }
    /**
     * 
     * @param name of the world
     * @return whether the named world has zombie apocalypse enabled
     */
    public boolean worldEnabledZombie(String name) {
        return getConfig().getConfigurationSection("worlds").getKeys(false).contains(name) && getConfig().getBoolean("worlds." + name + ".zombie"); //$NON-NLS-3$
    }
    /**
     * 
     * @param p The player
     * @return whether the player has a full hazmat suit
     */
    public boolean playerWearingHazmatSuit(Player p) {
        EntityEquipment e = p.getEquipment();
        boolean helmet =  e.getHelmet() != null && e.getHelmet().getType() == Material.CHAINMAIL_HELMET && e.getHelmet().getItemMeta().getDisplayName().equals(ChatColor.RESET + Messages.getString("Apocalyptic.gasMask"));
        boolean chest =  e.getChestplate() != null && e.getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE && e.getChestplate().getItemMeta().getDisplayName().equals(ChatColor.RESET + Messages.getString("Apocalyptic.hazmatSuit"));
        boolean legs =  e.getLeggings() != null && e.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS && e.getLeggings().getItemMeta().getDisplayName().equals(ChatColor.RESET + Messages.getString("Apocalyptic.hazmatPants"));
        boolean boots =  e.getBoots() != null && e.getBoots().getType() == Material.CHAINMAIL_BOOTS && e.getBoots().getItemMeta().getDisplayName().equals(ChatColor.RESET + Messages.getString("Apocalyptic.hazmatBoots"));
        return helmet && chest && legs && boots;
    }
    /**
     * 
     * @param p the player which to add radiation to
     * @param level the amount of radiation (in grays) to add to the player
     */
    public void addPlayerRadiation(Player p, double level) {
    	
    	//p.setMetadata(METADATA_KEY, new FixedMetadataValue(p.getMetadata(METADATA_KEY).g));
    	if (p.getMetadata(METADATA_KEY).size() > 0) {
    		p.setMetadata(METADATA_KEY, new FixedMetadataValue(this, p.getMetadata(METADATA_KEY).get(0).asDouble()+level));
    	}
    	else {
    		p.setMetadata(METADATA_KEY, new FixedMetadataValue(this, level));
    	}
        
        if (getPlayerRadiation(p) >= 0.8 && getPlayerRadiation(p) < 1) {
            p.sendMessage(new String[] {
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radiationCriticalWarning") + ChatColor.RESET,
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radBloodWarning") + ChatColor.RESET});
        }
        if (getPlayerRadiation(p) >= 1 && getPlayerRadiation(p) < 6) {
            p.sendMessage(new String[] {
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radDangerLevel") + ChatColor.RESET, 
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radBlood") + ChatColor.RESET,
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.takemoredamage") + ChatColor.RESET
            });
        }
        if (getPlayerRadiation(p) >= 6 && getPlayerRadiation(p) < 10) {
            p.sendMessage(new String[] {
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radiationCritical") + ChatColor.RESET, 
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radBloodStomach") + ChatColor.RESET,
            ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.takeMoreDamageandNoEat") + ChatColor.RESET
            });
        }
        if (getPlayerRadiation(p) >= 10) {
            p.sendMessage(new String[] {
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radDeadly") + ChatColor.RESET, 
                ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radAll") + ChatColor.RESET,
            ChatColor.RED + Messages.getString("Apocalyptic.warning") + ChatColor.GOLD + Messages.getString("Apocalyptic.radAllExplain") + ChatColor.RESET
            });
        }
        
    }
    /**
     * 
     * @param p the player
     * @return the radiation level (in grays) of the specified player
     */
    public double getPlayerRadiation(Player p) {
    	if (p.getMetadata(METADATA_KEY).size() > 0) 
    		return p.getMetadata(METADATA_KEY).get(0).asDouble();
    	else
    		return 0;
    }
    /**
     * 
     * @param p the player which to set the radiation level of
     * @param radiation the level of radiation (in grays) that the player is set to
     */
    public void setPlayerRadiation(Player p, double radiation) {
        addPlayerRadiation(p, getPlayerRadiation(p) * -1);
        addPlayerRadiation(p, radiation);
    }
    
    /**
     * Sends apocalyptic texture pack to a player.
     * @param p the player which to send the texture pack to
     */
    public void sendApocalypticTexturePack(Player p) {
        if (!getConfig().getBoolean("worlds."+p.getWorld().getName() + ".texturepack")) {
            return;
        }
        p.setTexturePack(texturePack);
    }
    public class ApocalypticConfiguration extends YamlConfiguration {
        public void update(YamlConfiguration defaults) {
            Map<String, Object> vals = this.getValues(true);
            saveDefaultConfig();
            for (String s : vals.keySet()) {
            	this.set(s, vals.get(s));
            }
        }
        public ConfigurationSection getWorld(String world) {
            return this.getConfigurationSection("worlds."+world);
        }
        public ConfigurationSection getWorld(World world) {
            return this.getConfigurationSection("worlds."+world.getName());
        }
    }
    @Override
    public ApocalypticConfiguration getConfig() {
        ApocalypticConfiguration config = new ApocalypticConfiguration();
        try {
            config.load(new File(getDataFolder().getPath() + File.separator + "config.yml"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Apocalyptic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(Apocalyptic.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return config;
    }
    /**
     * 
     * @param p a player
     * @param cmd String alias of a command
     * @return whether the specified player can perform the command
     */
    public boolean canDoCommand(CommandSender p, String cmd) {
    	if (p == getServer().getConsoleSender()) {
    		return true;
    	}
    	boolean usePerms = getConfig().getBoolean("meta.permissions");
    	if (usePerms) {
    		return (cmd.equals("radiation.self") && p.hasPermission("apocalyptic.radiation.self")) ||
    				(cmd.equals("radiation.other") && p.hasPermission("apocalyptic.radiation.other")) ||
    				(cmd.equals("radiation.change") && p.hasPermission("apocalyptic.radiation.change.self"))  ||
    				(cmd.equals("apocalyptic.radhelp") && p.hasPermission("apocalyptic.help.radiation")) ||
    				(cmd.equals("apocalyptic.stop") && p.hasPermission("apocalyptic.admin.stop")) ||
    				(cmd.equals("apocalyptic.reload") && p.hasPermission("apocalyptic.admin.reload"));
    	}
    	else {
    		if (cmd.equals("radiation.other") || 
    				cmd.equals("radiation.change") || 
    				cmd.equals("apocalyptic.stop") ||
    				cmd.equals("apocalyptic.reload")) {
    			return ((Player) p).isOp();
    		}
    		return true;
    	}
    }
    private Plugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return plugin;
    }
    public void saveRadiation(Player p) throws SQLException {
    	if (!db.isOpen())
    		db.open();
    	if (db.query("SELECT COUNT(*) AS exists FROM radiationLevels WHERE name=" + p.getName() + "").getInt("exists") > 0) { //$NON-NLS-3$
            db.query("UPDATE radiationLevels SET level="+p.getMetadata(METADATA_KEY).get(0).asDouble()+" WHERE name=" + p.getName());
        }
        else {
            db.query("INSERT INTO radiationLevels (name, level) VALUES (" + p.getMetadata(METADATA_KEY).get(0).asDouble() + ", " + p.getName() + ")"); //$NON-NLS-3$
        }
    	//db.close();
    }
    public void closeDatabase() {
    	db.close();
    }
}