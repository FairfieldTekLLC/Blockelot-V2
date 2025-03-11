package com.Blockelot.worldeditor.commands;

import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
            ArrayList<String> lines = new ArrayList<>();
            lines.add(ChatColor.BLUE + "-----------------BLOCKELOT-HELP----------------------");
            lines.add(ChatColor.YELLOW + "/b.about - Shows this about information.");
            lines.add(ChatColor.YELLOW + "/b.help - Shows this help information.");
            lines.add(ChatColor.YELLOW + "/b.bbinv [wild card] - Shows the players Blockelot Bank Balance.");
            lines.add(ChatColor.YELLOW + "/b.bbwd [Material] [Amount] - Withdrawl blocks from bank.");
            lines.add(ChatColor.YELLOW + "/b.bbdep [Material] [Amount] - Deposit blocks in bank.");
            lines.add(ChatColor.YELLOW + "/b.bbdep all - Deposit all blocks in bank.");
            lines.add(ChatColor.YELLOW + "/b.we.clear - Clears the Selections and Clipboard.");
            lines.add(ChatColor.YELLOW + "/b.we.clearHistory - Clears Undo buffers.");
            lines.add(ChatColor.YELLOW + "/b.we.size - Shows the dimensions of your selection.");
            lines.add(ChatColor.YELLOW + "/b.we.print - Shows your corner selection points");
            lines.add(ChatColor.YELLOW + "/b.we.select - Selects the specified world position your looking at.");
            lines.add(ChatColor.YELLOW + "/b.we.select [X] [Y] [Z] - Selects the specified world positions");
            lines.add(ChatColor.YELLOW + "/b.we.copy - Copies the blocks in your selection into the clipboard.");
            lines.add(ChatColor.YELLOW + "/b.we.del - Sets blocks in your selection to air.");
            lines.add(ChatColor.YELLOW + "/b.we.delete - Sets blocks in your selection to air.");
            lines.add(ChatColor.YELLOW + "/b.we.distr - Gets the block type Distribution of clipboard.");
            lines.add(ChatColor.YELLOW + "/b.we.paste - Pastes your clipboard where you are pointing.");
            lines.add(ChatColor.YELLOW + "/b.we.paste [Rotational Axis XYZ] [Degrees 90 180 270] - Pastes your clipboard rotating accordingly.");
            lines.add(ChatColor.YELLOW + "/b.we.paste [X] [Y] [Z] - Pastes your clipboard at specified location.");
            lines.add(ChatColor.YELLOW + "/b.we.paste [X] [Y] [Z] [Rotational Axis XYZ] [Degrees 90 180 270] - Pastes your clipboard rotating accordingly.");
            lines.add(ChatColor.YELLOW + "/b.we.stripmine - Clears the chunk and puts all items in chests at bottom.");
            lines.add(ChatColor.YELLOW + "/b.we.stripmine [true] - Clears the chunk, put blocks in bank, chests at bottom for non-blocks.");
            lines.add(ChatColor.YELLOW + "/b.we.undo - Undo you last action.");
            lines.add(ChatColor.YELLOW + "/b.reg [Email Address] - Registers your player with Blockelot.");
            lines.add(ChatColor.YELLOW + "/b.auth - Re-Authorizes your player against Blockelot.");
            lines.add(ChatColor.YELLOW + "/b.ls - Show the current contents of the remote directory.");
            lines.add(ChatColor.YELLOW + "/b.cd [Directory] - Changes to directory.");
            lines.add(ChatColor.YELLOW + "/b.rm [Filename or Foldername] - Removes the file or folder from your storage.");
            lines.add(ChatColor.YELLOW + "/b.mk [Foldername] - Creates a new folder.");
            lines.add(ChatColor.YELLOW + "/b.save [Filename] - Saves the clipboard into the cloud, names file.");
            lines.add(ChatColor.YELLOW + "/b.load [Filename] - Loads the contents into your clipboard.");
            lines.add(ChatColor.YELLOW + "/b.autopickup - Turns autopickup on and off.");
            lines.add(ChatColor.YELLOW + "/b.xpfly - Turns xp flying on and off.");
            PluginManager.GetPlayerInfo(player.getUniqueId()).SendBankMessageHeader(lines, true, false);
        }
        return true;
    }
}
