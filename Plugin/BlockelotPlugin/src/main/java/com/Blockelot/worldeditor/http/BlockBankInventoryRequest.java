package com.Blockelot.worldeditor.http;

/**
 *
 * @author geev
 */
public class BlockBankInventoryRequest {

    private String Wid;
    private String Uuid;
    private String Auth;

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
