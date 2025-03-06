package com.Blockelot.worldeditor.http;

import org.bukkit.plugin.Plugin;

/**
 *
 * @author geev
 */
public class BlockBankDepositRequest {

    private String Wid;
    private String Uuid;
    private String Auth;
    private BlockBankInventoryItem[] ToDeposit;

    public BlockBankInventoryItem[] getToDeposit() {
        return ToDeposit;
    }

    public void setToDeposit(BlockBankInventoryItem[] itms) {
        ToDeposit = itms;
    }

    public String getUuid() {
        return this.Uuid;
    }

    public void setUuid(String uuid) {
        this.Uuid = uuid;
    }

    public void setAuth(String auth) {
        Auth = auth;
    }

    public String getAuth() {
        return Auth;
    }

    public String getWid() {
        return this.Wid;
    }

    public void SetWid(String wid) {
        this.Wid = wid;
    }

    public void runTaskTimer(Plugin plugin, int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
