package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.Blockelot.worldeditor.http.BlockBankInventoryItem;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import com.Blockelot.Util.MiscUtil;
import com.google.gson.JsonSyntaxException;

/**
 * This class is used to request the current inventory of the players
 * block bank.
 * @author geev
 */
public class BlockBankInventoryTaskRequest extends HttpRequestor {

    PlayerInfo PlayerInfo;

    public BlockBankInventoryTaskRequest(PlayerInfo pi,String SearchString) {
        this.PlayerInfo = pi;
        this.SearchString = SearchString;
    }
    
    public String SearchString;

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "Block Bank Inventory");

            Gson gson = new Gson();

            com.Blockelot.worldeditor.http.BlockBankInventoryRequest request = new com.Blockelot.worldeditor.http.BlockBankInventoryRequest();
            request.setUuid(PlayerInfo.getUUID());
            request.SetWid(PluginManager.getWorldId());
            request.setAuth(PlayerInfo.getLastAuth());
            request.SetSearchCriteria(SearchString);

            PlayerInfo.getPlayer().sendMessage(ChatColor.YELLOW + "Contacting Bank....");

            String hr = RequestHttp(Configuration.BaseUri + "BBIRQ", gson.toJson(request));

            com.Blockelot.worldeditor.http.BlockBankInventoryResponse response = gson.fromJson(hr, com.Blockelot.worldeditor.http.BlockBankInventoryResponse.class);

            PlayerInfo.setLastAuth(response.getAuth());

            ArrayList<String> lines = new ArrayList<>();

            lines.add(ChatColor.BLUE + "----------------ACCOUNT BALANCE------------------");
            lines.add(ChatColor.GOLD + "###################################################");

            if (response.getItems().length == 0) {
                lines.add("Your block bank is empty!");
            } else {
                int maxNumOfDigits = 0;
                for (BlockBankInventoryItem itm : response.getItems()) {
                    int c = itm.getCount();
                    if ((c + "").length() > maxNumOfDigits) {
                        maxNumOfDigits = (c + "").length();
                    }
                }
                for (BlockBankInventoryItem itm : response.getItems()) {

                    String c = MiscUtil.padLeft(itm.getCount() + "", maxNumOfDigits, "0");
                    String l = MiscUtil.padRight(itm.getMaterialName(), (45 - maxNumOfDigits), "_");
                    lines.add((l + c).trim());

                }
            }
            PlayerInfo.SendBankMessageHeader(lines, true);
            PlayerInfo.setIsProcessing(false, "Block Bank Inventory");
            this.cancel();

        } catch (JsonSyntaxException | IllegalStateException e) {
            PlayerInfo.setIsProcessing(false, "Block Bank Inventory");
            this.cancel();
        }
    }
}
