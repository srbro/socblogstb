package com.ug.eon.android.tv.prefs;

/**
 * Created by nemanja.todoric on 3/27/2018.
 */

public class AuthPrefs {
    private String access_token;
    private String refresh_token;
    private String device_number;
    private int comm_id;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getDevice_number() {
        return device_number;
    }

    public void setDevice_number(String device_number) {
        this.device_number = device_number;
    }

    public int getComm_id() {
        return comm_id;
    }

    public void setComm_id(int community_id) {
        this.comm_id = community_id;
    }
}
