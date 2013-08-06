package net.cyberninjapiggy.apocalyptic.commands;

import org.bukkit.command.CommandExecutor;
import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Nick
 */
public class ApocalypticCommandExecutor implements CommandExecutor {
    private final Apocalyptic a;
    private String[] radiationHelp;

    public ApocalypticCommandExecutor(Apocalyptic aThis) {
        this.a = aThis;
        radiationHelp = new String[] {
                ChatColor.DARK_BLUE + " ======= " + ChatColor.AQUA + "RADIATION" + ChatColor.DARK_BLUE + " ======= " + ChatColor.RESET,
                "Radioactive gamma and beta particles permeate the world,",
                "damaging your skin and destroying your brain.",
                "Check your radiation with the command \"/radiation\".",
                ChatColor.RED + "Harmful effects of Radiation:" + ChatColor.RESET,
                "If you have a radiation level above 1 Gray, you will take",
                "twice as much damage.",
                "If you have a radiation level above 5 Grays, you will not",
                "be able to eat, and instead barf up anything you attempt to.",
                "If you have a radiation level above 10 Grays, your brain will",
                "start dying.",
                "You can wash off your radiation by standing in water.",
                "If you have more than " + a.getConfig().getString("maxRadiationWashable") + " Grays, you will not be able to wash and",
                "require a " + (a.getConfig().getBoolean("requireNotchApple") ? "Notch's " : "") + "golden apple to heal yourself."
    };
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if ("apocalyptic".equals(label) && args.length==1) {
            switch (args[0]) {
                case "reload":
                	if (!a.canDoCommand(cs, "apocalyptic.reload")) {
                		cs.sendMessage(ChatColor.RED + "You don't have permission to do that!" + ChatColor.RESET);
                		return true;
                	}
                    a.reloadConfig();
                    cs.sendMessage("Config reloaded.");
                    return true;
                case "stop":
                	if (!a.canDoCommand(cs, "apocalyptic.stop")) {
                		cs.sendMessage(ChatColor.RED + "You don't have permission to do that!" + ChatColor.RESET);
                		return true;
                	}
                    cs.sendMessage("Stopping plugin...");
                    a.getServer().getPluginManager().disablePlugin(a);
                    return true;
                case "radiation":
                	if (!a.canDoCommand(cs, "apocalyptic.radhelp")) {
                		cs.sendMessage(ChatColor.RED + "You don't have permission to do that!" + ChatColor.RESET);
                		return true;
                	}
                    cs.sendMessage(radiationHelp);
                    return true;
            }
        }
        return false;
    }
    
}
