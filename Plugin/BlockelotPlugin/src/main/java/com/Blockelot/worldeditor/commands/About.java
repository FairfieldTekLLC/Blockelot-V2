package com.Blockelot.worldeditor.commands;

import com.Blockelot.PluginManager;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author geev
 */
public class About    implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player player1) {
            player = player1;
            ArrayList<String> lines = new ArrayList<>();
            lines.add(ChatColor.BLUE + "-----------------BLOCKELOT-ABOUT---------------------");
            lines.add(ChatColor.YELLOW + "BLOCKELOT");
            lines.add(ChatColor.YELLOW + "Programmed by: Vince Gee a.k.a. ChapleKeep" );
            lines.add(ChatColor.YELLOW + "Website: www.blockelot.com");
            lines.add(ChatColor.YELLOW + "Patreon: https://www.patreon.com/Blockelot");
            lines.add(ChatColor.YELLOW + "Email: Vince@Fairfieldtek.com");
            lines.add(ChatColor.YELLOW + "");
            lines.add(ChatColor.YELLOW + "Blockelot and it's Cloud Storage is provided \"as is\", without warranties of any kind.");
            lines.add(ChatColor.YELLOW + "");
            
            PluginManager.GetPlayerInfo(player.getUniqueId()).SendBankMessageHeader(lines, true, false);
        }
        return true;
    }
}
