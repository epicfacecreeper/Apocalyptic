package net.cyberninjapiggy.apocalyptic.commands;

import net.cyberninjapiggy.apocalyptic.Apocalyptic;
import org.bukkit.ChatColor;
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
                        sendRadiationMessage(sender, a.getPlayerRadiation(a.getServer().getPlayer(args[0])));
                    }
                    else {
                        sender.sendMessage("Cannot find player \"" + args[0] + "\"");
                    }
                }
                if (args.length == 2) {
                    if (a.getServer().getPlayer(args[0]).isOnline()) {
                        if (isNumeric(args[1])) {
                            sender.sendMessage("Set radiation");
                            a.setPlayerRadiation(a.getServer().getPlayer(args[0]), Double.parseDouble(args[1]));
                        }
                        else {
                            sender.sendMessage(args[1] + " is not a valid number.");
                        }
                    }
                    else {
                        sender.sendMessage("Cannot find player \"" + args[0] + "\"");
                    }
                }
            }
            else {
                if (args.length == 0 && a.canDoCommand((Player) sender, "radiation.self")) {
                    sendRadiationMessage(sender, a.getPlayerRadiation((Player) sender));
                }
                if (args.length == 1 && a.canDoCommand((Player) sender, "radiation.other")) {
                    if (a.getServer().getPlayer(args[0]).isOnline()) {
                        sender.sendMessage("Set radiation");
                        sendRadiationMessage(sender, a.getPlayerRadiation(a.getServer().getPlayer(args[0])));
                    }
                    else {
                        sender.sendMessage("Cannot find player \"" + args[0] + "\"");
                    }
                }
                if (args.length == 2 && a.canDoCommand((Player) sender, "radiation.change")) {
                    if (a.getServer().getPlayer(args[0]).isOnline()) {
                        if (isNumeric(args[1])) {
                            a.setPlayerRadiation(a.getServer().getPlayer(args[0]), Double.parseDouble(args[1]));
                        }
                        else {
                            sender.sendMessage("" + args[1] + " is not a valid number.");
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
    private void sendRadiationMessage(CommandSender s, double radiation) {
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
        
        s.sendMessage(color +""+ radiation + " " + a.getMessages().getCaption("grays"));
    }
}
