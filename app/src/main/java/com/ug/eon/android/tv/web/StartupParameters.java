package com.ug.eon.android.tv.web;

/**
 * Created by petar.stefanovic on 02/04/2018.
 */

public class StartupParameters {

    private String startupAction;
    private String startupActionData;
    private String startupMode;

    public String getStartupAction() {
        return startupAction != null ? startupAction : "";
    }

    public void setStartupAction(String startupAction) {
        this.startupAction = startupAction;
    }

    public String getStartupActionData() { return startupActionData != null ? startupActionData : ""; }

    public void setStartupActionData(String startupActionData) { this.startupActionData = startupActionData; }

    public String getStartupMode() {
        return startupMode != null ? startupMode : "";
    }

    public void setStartupMode(String mode) {
        this.startupMode = mode;
    }
}
