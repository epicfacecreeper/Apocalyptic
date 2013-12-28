/*
    Copyright 2013 Nick Schatz

    This file is part of Apocalyptic.

    Apocalyptic is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Apocalyptic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Apocalyptic. If not, see <http://www.gnu.org/licenses/>.

*/

package net.cyberninjapiggy.apocalyptic;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.cyberninjapiggy.apocalyptic.commands.ApocalypticCommandExecutor;
import net.cyberninjapiggy.apocalyptic.commands.RadiationCommandExecutor;
import net.cyberninjapiggy.apocalyptic.events.*;
import net.cyberninjapiggy.apocalyptic.generator.RavagedChunkGenerator;
import net.cyberninjapiggy.apocalyptic.misc.ApocalypticConfiguration;
import net.cyberninjapiggy.apocalyptic.misc.Messages;
import net.cyberninjapiggy.apocalyptic.misc.Updater;
import net.cyberninjapiggy.apocalyptic.misc.Util;
import org.bukkit.*;
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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Messages messages;

    private static final int dboId = 43663;
    private static final String texturePack = "https://www.dropbox.com/s/waiut1qz722uojh/apocalyptic%20texture%20pack.zip?dl=1";
    
    public static final String METADATA_KEY = "radiation";
    
    public static ItemStack hazmatHood;
    public static ItemStack hazmatSuit;
    public static ItemStack hazmatPants;
    public static ItemStack hazmatBoots;

    private ApocalypticConfiguration cachedConfig;
    private boolean recacheConfig;
    
    
    @Override
    public void onEnable(){
    	messages = new Messages(this);
        //acidRain.setCustomName("Acid Rain");
        log = getLogger();
        rand = new Random();
        wg = getWorldGuard();
        
        hazmatHood = setName(new ItemStack(Material.CHAINMAIL_HELMET, 1), ChatColor.RESET + getMessages().getCaption("gasMask"));
        hazmatSuit = setName(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), ChatColor.RESET + getMessages().getCaption("hazmatSuit"));
        hazmatPants = setName(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), ChatColor.RESET + getMessages().getCaption("hazmatPants"));
        hazmatBoots = setName(new ItemStack(Material.CHAINMAIL_BOOTS, 1), ChatColor.RESET + getMessages().getCaption("hazmatBoots"));
        
        if (wg == null) {
        	wgEnabled = false;
        }
        
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        messages.saveDefault();
        if (!new File(getDataFolder().getPath() + File.separator + "config.yml").exists()) {
            saveDefaultConfig();
        }
        else {
            if (!getConfig().getString("meta.version").equals(this.getDescription().getVersion())) {
                getConfig().update(this);
            }
        }
        db = new SQLite(log, getMessages().getCaption("logtitle"), getDataFolder().getAbsolutePath(), "apocalyptic");

        if (!db.open()) {
            log.severe(getMessages().getCaption("errNotOpenDatabase"));
            this.setEnabled(false);
            return;
        }
        try {
            db.query("CREATE TABLE IF NOT EXISTS radiationLevels ("
                    + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "player VARCHAR(16),"
                    + "level DOUBLE);");
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        
       db.close();
        
        
        if (getConfig().getBoolean("meta.version-check")) {
        	Updater versionCheck = new Updater(this, dboId, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        	if (!versionCheck.getLatestName().equals(this.getDescription().getName() + " v" + this.getDescription().getVersion())) {
        		if (getConfig().getBoolean("meta.auto-update")) {
        			new Updater(this, dboId, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, getConfig().getBoolean("meta.show-download-progress"));
        		}
        		else {
        			log.info(ChatColor.GREEN + getMessages().getCaption("updateAvaliable") + versionCheck.getLatestName()); //$NON-NLS-3$
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
        int start = 306;
        //Loop through iron/diamond/gold
        for (int i=0;i<=3;i++) {
            //Loop through pieces of the armor in the set
            for (int j=0;j<=3;j++) {
                int mId = start+(i*4)+j;
                Material mat = Material.getMaterial(mId);
                ShapelessRecipe recipe = new ShapelessRecipe(setName(new ItemStack(mat), ChatColor.RESET+messages.getCaption("hazmat")+" "+ Util.title(mat.name().replace("_", " ").toLowerCase())));
                recipe.addIngredient(mat);
                Material chain = Material.getMaterial(start-4+j);
                recipe.addIngredient(chain);

                getServer().addRecipe(recipe);
            }
        }
        start = 298;
        for (int j=0;j<=3;j++) {
            int mId = start+j;
            Material mat = Material.getMaterial(mId);
            ShapelessRecipe recipe = new ShapelessRecipe(setName(new ItemStack(mat), ChatColor.RESET+messages.getCaption("hazmat")+" " + Util.title(mat.name().replace("_", " ").toLowerCase())));
            recipe.addIngredient(mat);
            Material chain = Material.getMaterial(mId+4);
            recipe.addIngredient(chain);
            //log.info(chain.name() + " " + mat.name());

            getServer().addRecipe(recipe);
        }
        
        getServer().addRecipe(hazardBootsR);
        getServer().addRecipe(hazardPantsR);
        getServer().addRecipe(hazardChestR);
        getServer().addRecipe(hazardHelmetR);
        
        //Schedules      
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (World w : getServer().getWorlds()) {
                	Object regions;
                    for (Player p : w.getPlayers()) {
                    	boolean noFallout = false;
                    	boolean forceFallout = false;
                    	if (wgEnabled) {
                    		regions = ((WorldGuardPlugin)wg).getRegionManager(w).getApplicableRegions(p.getLocation());
                            for (ProtectedRegion next : ((ApplicableRegionSet) regions)) {
                                for (String s : getConfig().getStringList("regions.fallout")) {
                                    if (next.getId().equals(s)) {
                                        forceFallout = true;
                                        break;
                                    }
                                }
                                for (String s : getConfig().getStringList("regions.noFallout")) {
                                    if (next.getId().equals(s)) {
                                        noFallout = true;
                                        break;
                                    }
                                }
                            }
                    	}
                    	if (!noFallout && (worldEnabledFallout(w.getName()) || forceFallout)) {
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
	                        boolean random = rand.nextInt(4) == 0;
	                        if (!hazmatSuit
	                                && aboveLowPoint
	                                && belowHighPoint
	                                && random) {
	                            addPlayerRadiation(p, (p.getWorld().getEnvironment() == Environment.NETHER ? getConfig().getWorld(w).getDouble("radiationRate")*2 : getConfig().getWorld(w).getDouble("radiationRate")) * (Math.round(p.getLevel() / 10)+1));
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
            log.severe(getMessages().getCaption("errNotOpenDatabase"));
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
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String genID) {
        return new RavagedChunkGenerator(this, genID);
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
        boolean helmet =  e.getHelmet() != null && (e.getHelmet().getItemMeta().getDisplayName().startsWith(ChatColor.RESET + getMessages().getCaption("gasMask")) || e.getHelmet().getItemMeta().getDisplayName().startsWith(ChatColor.RESET + getMessages().getCaption("hazmat")));
        boolean chest =  e.getChestplate() != null && e.getChestplate().getItemMeta().getDisplayName().startsWith(ChatColor.RESET + getMessages().getCaption("hazmat"));
        boolean legs =  e.getLeggings() != null && e.getLeggings().getItemMeta().getDisplayName().startsWith(ChatColor.RESET + getMessages().getCaption("hazmat"));
        boolean boots =  e.getBoots() != null && e.getBoots().getItemMeta().getDisplayName().startsWith(ChatColor.RESET + getMessages().getCaption("hazmat"));
        return helmet && chest && legs && boots;
    }
    /**
     * 
     * @param p the player which to add radiation to
     * @param level the amount of radiation (in grays) to add to the player
     */
    public void addPlayerRadiation(Player p, double level) {
    	
    	//p.setMetadata(METADATA_KEY, new FixedMetadataValue(p.getMetadata(METADATA_KEY).g));
        double oldRadiation = 0;
    	if (p.getMetadata(METADATA_KEY).size() > 0) {
            oldRadiation = p.getMetadata(METADATA_KEY).get(0).asDouble();
    		p.setMetadata(METADATA_KEY, new FixedMetadataValue(this, oldRadiation+level));
    	}
    	else {
    		p.setMetadata(METADATA_KEY, new FixedMetadataValue(this, level));
    	}
        
        if (getPlayerRadiation(p) >= 0.8 && getPlayerRadiation(p) < 1.0) {
            p.sendMessage(new String[] {
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radiationCriticalWarning") ,
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radBloodWarning") });
        }
        if (oldRadiation < 1.0 && getPlayerRadiation(p) >= 1.0 && getPlayerRadiation(p) < 6.0) {
            p.sendMessage(new String[] {
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radDangerLevel") ,
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radBlood") ,
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("takemoredamage")
            });
        }
        if (oldRadiation < 6.0 && getPlayerRadiation(p) >= 6.0 && getPlayerRadiation(p) < 10.0) {
            p.sendMessage(new String[] {
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radiationCritical") ,
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radBloodStomach") ,
            ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("takeMoreDamageandNoEat")
            });
        }
        if (oldRadiation < 10.0 && getPlayerRadiation(p) >= 10) {
            p.sendMessage(new String[] {
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radDeadly") ,
                ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radAll") ,
            ChatColor.RED + getMessages().getCaption("warning") +" "+ ChatColor.GOLD + getMessages().getCaption("radAllExplain")
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
        p.setResourcePack(texturePack);
    }
    @Override
    public ApocalypticConfiguration getConfig() {
        if (cachedConfig == null || recacheConfig) {
            recacheConfig = false;
            ApocalypticConfiguration config = new ApocalypticConfiguration();
            try {
                config.load(new File(getDataFolder().getPath() + File.separator + "config.yml"));
            } catch (IOException | InvalidConfigurationException ex) {
                ex.printStackTrace();
            }
            cachedConfig = config;
            return config;
        }
        else {
            return cachedConfig;
        }
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
            return !(cmd.equals("radiation.other") || cmd.equals("radiation.change") || cmd.equals("apocalyptic.stop") || cmd.equals("apocalyptic.reload")) || p.isOp();
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
    	db.open();
        if (!p.getMetadata(METADATA_KEY).isEmpty()) {
            if (db.query("SELECT COUNT(*) AS \"exists\" FROM radiationLevels WHERE player=\"" + p.getName() + "\";").getInt("exists") > 0) { //$NON-NLS-3$
                db.query("UPDATE radiationLevels SET level="+p.getMetadata(METADATA_KEY).get(0).asDouble()+" WHERE player=\"" + p.getName()+"\";");
            }
            else {
                db.query("INSERT INTO radiationLevels (player, level) VALUES (\"" + p.getName() + "\", " + p.getMetadata(METADATA_KEY).get(0).asDouble() + ");"); //$NON-NLS-3$
            }
        }
    	db.close();
    }
    public void loadRadiation(Player p) {

        db.open();
        ResultSet result;
        try {
            result = db.query("SELECT * FROM radiationLevels WHERE player=\""+p.getName()+"\"");
            while (result.next()) {
                p.setMetadata(Apocalyptic.METADATA_KEY, new FixedMetadataValue(this, result.getDouble("level")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

	public Messages getMessages() {
		return messages;
	}
    public void reloadConfig() {
        recacheConfig = true;
    }

}
