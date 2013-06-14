package net.cyberninjapiggy.apocalyptic.commands;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Nick
 */
public class RadiationCommandExecutor implements CommandExecutor {
    private final Apocalyptic a;
    public RadiationCommandExecutor(Apocalyptic a) {
        this.a = a;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
	if (cmd.getName().equalsIgnoreCase("radiation")) {
            if (sender == a.getServer().getConsoleSender()) {
                if (args.length == 0) {
                    sender.sendMessage("Cannot use this command and no arguments from console.");
                }
                if (args.length == 1) {
                    if (a.getServer().getPlayer(args[0]).isOnline()) {
                        a.sendRadiationMessage(sender, a.getPlayerRadiation(a.getServer().getPlayer(args[0])));
                    }
                    else {
                        sender.sendMessage("Cannot find player \"" + args[0] + "\"");
                    }
                }
                if (args.length == 2) {
                    if (a.getServer().getPlayer(args[0]).isOnline()) {
                        if (isNumeric(args[1])) {
                            a.setPlayerRadiation(a.getServer().getPlayer(args[0]), Double.parseDouble(args[1]));
                        }
                        else {
                            sender.sendMessage(args[0] + " is not a valid number.");
                        }
                    }
                    else {
                        sender.sendMessage("Cannot find player \"" + args[0] + "\"");
                    }
                }
            }
            else {
                if (args.length == 0) {
                    a.sendRadiationMessage(sender, a.getPlayerRadiation((Player) sender));
                }
                if (args.length == 1) {
                    if (a.getServer().getPlayer(args[0]).isOnline()) {
                        a.sendRadiationMessage(sender, a.getPlayerRadiation(a.getServer().getPlayer(args[0])));
                    }
                    else {
                        sender.sendMessage("Cannot find player \"" + args[0] + "\"");
                    }
                }
                if (args.length == 2) {
                    if (a.getServer().getPlayer(args[0]).isOnline()) {
                        if (isNumeric(args[1])) {
                            a.setPlayerRadiation(a.getServer().getPlayer(args[0]), Double.parseDouble(args[1]));
                        }
                        else {
                            sender.sendMessage("" + args[0] + " is not a valid number.");
                        }
                    }
                    else {
                        sender.sendMessage("Cannot find player \"" + args[0] + "\"");
                    }
                }
            }
            return true;
	}
	return false; 
    }
    private static boolean isNumeric(String str) {return str.matches("-?\\d+(\\.\\d+)?");}
}
