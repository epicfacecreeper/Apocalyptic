package net.cyberninjapiggy.apocalyptic;

import net.cyberninjapiggy.apocalyptic.generator.RavagedChunkGenerator;
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
import java.util.Set;
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
import net.cyberninjapiggy.apocalyptic.events.PlayerMove;
import net.cyberninjapiggy.apocalyptic.events.PlayerSpawn;
import net.cyberninjapiggy.apocalyptic.events.ZombieTarget;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Nick
 */
public final class Apocalyptic extends JavaPlugin {
    private Map<String, Double> radiationLevels = new HashMap<>();
    private static Logger log;
    private Database db;
    public static Random rand = new Random();
    
    public static final String texturePack = "https://dl.dropboxusercontent.com/s/qilofl4m4e9uvxh/apocalyptic-1.6.zip?dl=1";
    
    //private Villager acidRain = ((Villager) getServer().getWorlds().get(0).spawnEntity(new Location(getServer().getWorlds().get(0), 0, -128, 0), EntityType.VILLAGER));
    
    public static ItemStack hazmatHood = setName(new ItemStack(Material.CHAINMAIL_HELMET, 1), "§rGas Mask");
    public static ItemStack hazmatSuit = setName(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), "§rHazmat Suit");
    public static ItemStack hazmatPants = setName(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), "§rHazmat Pants");
    public static ItemStack hazmatBoots = setName(new ItemStack(Material.CHAINMAIL_BOOTS, 1), "§rHazmat Boots");
    
    @Override
    public void onEnable(){
        //acidRain.setCustomName("Acid Rain");
        log = getLogger();
        
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
        db = new SQLite(log, "[Apocalyptic] ", getDataFolder().getAbsolutePath(), "apocalyptic");
        
        if (!db.open()) {
            log.severe("Could not open database");
            this.setEnabled(false);
            return;
        }
        try {
            db.query("CREATE TABLE IF NOT EXISTS radiationLevels ("
                    + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "player VARCHAR(16),"
                    + "level DOUBLE)");
        } catch (SQLException ex) {
            Logger.getLogger(Apocalyptic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        ResultSet result;
        try {
            result = db.query("SELECT * FROM radiationLevels");
            while (result.next()) {
                radiationLevels.put(result.getString("player"), result.getDouble("level"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Apocalyptic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        db.close();
        if (getConfig().getBoolean("meta.version-check")) {
        	Updater versionCheck = new Updater(this, "apocalyptic", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        	if (versionCheck.getLatestVersionString() != this.getDescription().getName() + " v" + this.getDescription().getVersion()) {
        		if (getConfig().getBoolean("meta.auto-update")) {
        			Updater updater = new Updater(this, "apocalyptic", this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, getConfig().getBoolean("meta.show-download-progress"));
        		}
        		else {
        			log.info("An update is available: " + versionCheck.getLatestVersionString() + "(" + versionCheck.getFileSize() + " bytes)");
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
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new ZombieTarget(this), this);
        
        //Add recipes
        ShapedRecipe hazardHelmetR = new ShapedRecipe(hazmatHood);
        hazardHelmetR.shape("SSS", "S S");
        hazardHelmetR.setIngredient('S', Material.SPONGE);
        
        ShapedRecipe hazardChestR = new ShapedRecipe(hazmatSuit);
        hazardChestR.shape("S S", "SSS", "SSS");
        hazardChestR.setIngredient('S', Material.SPONGE);
        
        ShapedRecipe hazardPantsR = new ShapedRecipe(hazmatPants);
        hazardPantsR.shape("SSS", "S S", "S S");
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
                    if (worldEnabledFallout(w.getName())) {
                        for (Player p : w.getPlayers()) {
                            //Acid Rain
                            Location l = p.getLocation();
                            if (p.getEquipment().getHelmet() == null
                                    && p.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()) <= l.getBlockY() &&
                                    p.getWorld().hasStorm()) {
                                p.damage(p.getWorld().getDifficulty().getValue()*2);
                            }
                            //Neurological death syndrome
                            if (getPlayerRadiation(p) >= 10) {
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
                            boolean random = new Random(p.getWorld().getSeed()).nextInt(6) == 0;
                            if (!hazmatSuit
                                    && aboveLowPoint
                                    && belowHighPoint
                                    && random) {
                                addPlayerRadiation(p, (p.getWorld().getEnvironment() == Environment.NETHER ? 0.2 : 0.1) * Math.round(p.getLevel() / 10));
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
            log.severe("Could not open database!");
            return;
        }
        try {
            for (Entry<String, Double> entry : radiationLevels.entrySet()) {
                if (db.query("SELECT COUNT(*) AS exists FROM radiationLevels WHERE name=" + entry.getKey() + "").getInt("exists") > 0) {
                    db.query("UPDATE radiationLevels SET level="+entry.getValue()+" WHERE name=" + entry.getKey());
                }
                else {
                    db.query("INSERT INTO radiationLevels (name, level) VALUES (" + entry.getValue() + ", " + entry.getKey() + ")");
                }
            }
            
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
    public boolean worldEnabledFallout(String name) {
        return getConfig().getConfigurationSection("worlds").getKeys(false).contains(name) && getConfig().getBoolean("worlds." + name + ".fallout");
    }
    public boolean worldEnabledZombie(String name) {
        return getConfig().getConfigurationSection("worlds").getKeys(false).contains(name) && getConfig().getBoolean("worlds." + name + ".zombie");
    }
    public boolean playerWearingHazmatSuit(Player p) {
        EntityEquipment e = p.getEquipment();
        boolean helmet =  e.getHelmet() != null && e.getHelmet().getType() == Material.CHAINMAIL_HELMET;
        boolean chest =  e.getChestplate() != null && e.getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE;
        boolean legs =  e.getLeggings() != null && e.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS;
        boolean boots =  e.getBoots() != null && e.getBoots().getType() == Material.CHAINMAIL_BOOTS;
        return helmet && chest && legs && boots;
    }
    public void addPlayerRadiation(Player p, double level) {
        
        if (getPlayerRadiation(p) >= 0.8 && getPlayerRadiation(p) < 1) {
            p.sendMessage(new String[] {
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Your radiation level is reaching a critical level." + ChatColor.RESET,
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Haematopoiesis will soon set in." + ChatColor.RESET});
        }
        if (getPlayerRadiation(p) >= 1 && getPlayerRadiation(p) < 6) {
            p.sendMessage(new String[] {
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Your radiation has reached a dangerous level." + ChatColor.RESET, 
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Haematopoiesis has set in." + ChatColor.RESET,
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "You will take more damage when attacked." + ChatColor.RESET
            });
        }
        if (getPlayerRadiation(p) >= 6 && getPlayerRadiation(p) < 10) {
            p.sendMessage(new String[] {
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Your radiation has reached a critical level." + ChatColor.RESET, 
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Your symptoms now include Haematopoiesis and Gastrointestinal Dysfunction." + ChatColor.RESET,
            ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "You will take more damage when attacked and will not be able to eat." + ChatColor.RESET
            });
        }
        if (getPlayerRadiation(p) >= 10) {
            p.sendMessage(new String[] {
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Your radiation has reached a deadly level." + ChatColor.RESET, 
                ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "Your symptoms now include Haematopoiesis, Gastrointestinal Dysfunction, and Neurological Death." + ChatColor.RESET,
            ChatColor.RED + "[WARNING] " + ChatColor.GOLD + "You will take more damage when attacked, will not be able to eat, and will have far increased damage, dizziness, and a decreased level of consciousness." + ChatColor.RESET
            });
        }
        
    }
    public double getPlayerRadiation(Player p) {
        if (radiationLevels.containsKey(p.getName())) {
            return radiationLevels.get(p.getName());
        }
        return 0;
    }
    public void setPlayerRadiation(Player p, double radiation) {
        addPlayerRadiation(p, getPlayerRadiation(p) * -1);
        addPlayerRadiation(p, radiation);
    }
    
    
    public void sendRadiationMessage(CommandSender s, double radiation) {
        ChatColor color = ChatColor.GREEN;
        if (radiation >= 0.8 && radiation < 1.0) {
            color = ChatColor.YELLOW;
        }
        else if (radiation >= 1.0 && radiation < 5.0) {
            color = ChatColor.RED;
        }
        else if (radiation >= 5.0 && radiation < 6.0) {
            color = ChatColor.DARK_RED;
        }
        else if (radiation >= 6.0 && radiation < 9.0) {
            color = ChatColor.LIGHT_PURPLE;
        }
        else if (radiation >= 9.0 && radiation < 10.0) {
            color = ChatColor.DARK_PURPLE;
        }
        else if (radiation >= 10.0) {
            color = ChatColor.BLACK;
        }
        
        s.sendMessage("" + color + radiation + " Grays" + ChatColor.RESET);
    }
    public void sendApocalypticTexturePack(Player p) {
        /*if (!getConfig().getBoolean("worlds."+p.getWorld().getName() + ".texturepack")) {
            return;
        }
        p.setTexturePack(texturePack);*/
    }
    public class ApocalypticConfiguration extends YamlConfiguration {
        public void update(YamlConfiguration defaults) {
            Set<String> keys = defaults.getKeys(true);
            for (String key : keys) {
                if (this.get(key) == null) {
                    this.set(key, defaults.get(key));
                }
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
}