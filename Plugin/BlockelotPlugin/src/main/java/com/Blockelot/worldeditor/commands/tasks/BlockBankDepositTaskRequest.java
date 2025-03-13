package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.google.gson.Gson;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.MiscUtil;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.Blockelot.worldeditor.http.BlockBankInventoryItem;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author geev
 *
 * This class handles serializing all of the inventory and sending it to the
 * backend for storage.
 *
 */
public class BlockBankDepositTaskRequest extends HttpRequestor {

    PlayerInfo PlayerInfo;
    String Mat = null;
    int Amount = 0;
    Material Material = null;
    boolean DepositAllBlocks = false;
    ArrayList<ItemStack> ToRemove = new ArrayList<>();
    ArrayList<ItemStack> Partials = new ArrayList<>();
    ArrayList<BlockBankInventoryItem> ToDeposit = new ArrayList<>();
    boolean DepItems = false;

    public BlockBankDepositTaskRequest(PlayerInfo pi, String mat, int amount) {
        this.PlayerInfo = pi;
        Amount = amount;
        if ("all".equals(mat.trim().toLowerCase())) {
            DepositAllBlocks = true;
        } else {
            Material = Material.getMaterial(mat);
        }
    }

    public BlockBankDepositTaskRequest(PlayerInfo pi, ArrayList<BlockBankInventoryItem> itms) {
        this.PlayerInfo = pi;
        ToDeposit = itms;
        DepItems = true;
    }

    private int GetItemStackTotal(Material mat, int AmountLeftToFetch) {

        final Inventory inventory = PlayerInfo.getPlayer().getInventory();

        for (ItemStack itm : inventory.getContents()) {
            if (itm != null) {
                if (itm.getType() == mat) {
                    if (AmountLeftToFetch > itm.getAmount()) {
                        AmountLeftToFetch = AmountLeftToFetch - itm.getAmount();
                        ToRemove.add(itm);
                    } else {

                        ItemStack clone = itm.clone();
                        clone.setAmount(itm.getAmount() - AmountLeftToFetch);
                        Partials.add(clone);
                        ToRemove.add(itm);
                        AmountLeftToFetch = 0;
                    }
                }
            }
        }
        return Amount - AmountLeftToFetch;
    }

    @Override
    public void run() {
        try {
            PlayerInfo.setIsProcessing(true, "Block Bank Deposit");
            PlayerInfo.getPlayer().sendMessage(ChatColor.YELLOW + "Contacting Bank....");

            final Gson gson = new Gson();
            com.Blockelot.worldeditor.http.BlockBankDepositRequest request = new com.Blockelot.worldeditor.http.BlockBankDepositRequest();
            request.setUuid(PlayerInfo.getUUID());
            request.SetWid(PluginManager.getWorldId());
            request.setAuth(PlayerInfo.getLastAuth());
            ArrayList<BlockBankInventoryItem> toDeposit = new ArrayList<>();

            if (DepItems) {
                BlockBankInventoryItem[] t = new BlockBankInventoryItem[ToDeposit.size()];
                for (int i = 0; i < ToDeposit.size(); i++) {
                    t[i] = ToDeposit.get(i);
                }
                request.setToDeposit(t);

            } else {
                if (!DepositAllBlocks) {
                    int realDepositAmount = GetItemStackTotal(Material, Amount);
                    toDeposit.add(new BlockBankInventoryItem(Material, realDepositAmount));
                } else {
                    final Inventory inventory = PlayerInfo.getPlayer().getInventory();
                    for (ItemStack itm : inventory.getContents()) {
                        if (itm == null) {
                            continue;
                        }
                        if (MiscUtil.CanBeDeposited(itm.getType())) {
                            toDeposit.add(new BlockBankInventoryItem(itm.getType(), itm.getAmount()));
                            ToRemove.add(itm);
                        }
                    }
                }
                BlockBankInventoryItem[] itms = new BlockBankInventoryItem[toDeposit.size()];
                itms = toDeposit.toArray(itms);
                request.setToDeposit(itms);
            }

            String hr = RequestHttp(Configuration.BaseUri + "BBDR", gson.toJson(request));
            com.Blockelot.worldeditor.http.BlockBankDepositResponse response = gson.fromJson(hr, com.Blockelot.worldeditor.http.BlockBankDepositResponse.class);
            PlayerInfo.setLastAuth(response.getAuth());

            ArrayList<String> lines = new ArrayList<>();
            lines.add(ChatColor.BLUE + "------------BLOCKELOT BANK DEPOSIT SLIP--------------");
            lines.add(ChatColor.GOLD + "###############################################");

            if (response.getSuccess() == true) {
                ToRemove.forEach(itm -> {
                    PlayerInfo.getPlayer().getInventory().remove(itm);
                });
                Partials.forEach(itm -> {
                    PlayerInfo.getPlayer().getInventory().addItem(itm);
                });
                for (BlockBankInventoryItem itm : request.getToDeposit()) {
                    lines.add(itm.getMaterialName() + " (" + itm.getCount() + ") deposited, thank you.");

                }

            } else {
                for (BlockBankInventoryItem itm : request.getToDeposit()) {
                    lines.add(itm.getMaterialName() + " (" + itm.getCount() + ") was not  deposited, thank you.");
                }
            }

            PlayerInfo.SendBankMessageHeader(lines, true);

            PlayerInfo.setIsProcessing(false, "Block Bank Deposit");

            this.cancel();

        } catch (JsonSyntaxException | IllegalStateException e) {
            PlayerInfo.setIsProcessing(false, "Block Bank Deposit");
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
            ServerUtil.consoleLog(e);
            this.cancel();
        }
    }
}
