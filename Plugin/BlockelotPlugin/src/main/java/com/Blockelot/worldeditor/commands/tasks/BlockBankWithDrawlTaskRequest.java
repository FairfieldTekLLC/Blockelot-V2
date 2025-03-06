package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.google.gson.Gson;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author geev
 */
public class BlockBankWithDrawlTaskRequest extends HttpRequestor {

    PlayerInfo PlayerInfo;
    Material Material = null;
    int Amount = 0;

    public BlockBankWithDrawlTaskRequest(PlayerInfo pi, Material mat, int amount) {
        this.PlayerInfo = pi;
        Material = mat;
        Amount = amount;
    }

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "Block Bank Deposit");
            PlayerInfo.getPlayer().sendMessage(ChatColor.YELLOW + "Contacting Bank....");
            Gson gson = new Gson();
            com.Blockelot.worldeditor.http.BlockBankWithdrawlRequest request = new com.Blockelot.worldeditor.http.BlockBankWithdrawlRequest();
            request.setUuid(PlayerInfo.getUUID());
            request.SetWid(PluginManager.getWorldId());
            request.setAmount(Amount);
            request.setMaterial(Material.name());
            request.setAuth(PlayerInfo.getLastAuth());

            String hr = RequestHttp(PluginManager.Config.BaseUri + "BBWR", gson.toJson(request));
            com.Blockelot.worldeditor.http.BlockBankWithdrawlResponse response = gson.fromJson(hr, com.Blockelot.worldeditor.http.BlockBankWithdrawlResponse.class);
            PlayerInfo.setLastAuth(response.getAuth());

            ArrayList<String> lines = new ArrayList<>();
            lines.add(ChatColor.BLUE + "-----------BLOCKELOT BANK WITHDRAWL SLIP-------------");
            lines.add(ChatColor.GOLD + "###############################################");

            if (response.getSuccess() == true) {
                Material mat = Material.getMaterial(response.getMaterial());
                if (mat != null) {
                    PlayerInfo.getPlayer().getInventory().addItem(new ItemStack(mat, response.getAmount()));
                }
                lines.add(Material.name() + " (" + response.getAmount() + ") withdrawn, thank you.");

            } else {
                lines.add(Material.name() + " (" + response.getAmount() + ") was not deposited.");
            }
            PlayerInfo.SendBankMessageHeader(lines, true);

            PlayerInfo.setIsProcessing(false, "Block Bank Deposit");
            this.cancel();

        } catch (Exception e) {
            PlayerInfo.setIsProcessing(false, "Block Bank Deposit");
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
            ServerUtil.consoleLog(e);
            this.cancel();
        }
    }
}
