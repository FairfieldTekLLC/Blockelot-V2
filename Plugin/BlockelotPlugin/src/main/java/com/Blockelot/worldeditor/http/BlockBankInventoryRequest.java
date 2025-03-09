package com.Blockelot.worldeditor.http;

/**
 *
 * @author geev
 */
public class BlockBankInventoryRequest {

    private String Wid;
    private String Uuid;
    private String Auth;
    private String SearchCriteria;

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
    
    public String getSearchCriteria(){
        return SearchCriteria;
    }
    
    public void SetSearchCriteria(String criteria){
        SearchCriteria = criteria;
    }
    
}
