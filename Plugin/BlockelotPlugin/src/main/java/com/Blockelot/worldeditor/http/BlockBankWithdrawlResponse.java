package com.Blockelot.worldeditor.http;

/**
 *
 * @author geev
 */
public class BlockBankWithdrawlResponse {

    private String Wid;
    private String Uuid;
    private String Auth;
    private String Material;
    private int Amount;
    private Boolean Success;

    public Boolean getSuccess() {
        return Success;
    }

    public void setSuccess(Boolean f) {
        Success = f;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amt) {
        Amount = amt;
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
}
