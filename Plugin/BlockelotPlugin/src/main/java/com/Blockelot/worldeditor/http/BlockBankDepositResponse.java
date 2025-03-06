package com.Blockelot.worldeditor.http;

/**
 *
 * @author geev
 */
public class BlockBankDepositResponse {

    private String Uuid;
    private String Auth;
    private Boolean Success;

    public Boolean getSuccess() {
        return Success;
    }

    public void setSuccess(Boolean f) {
        Success = f;
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

}
