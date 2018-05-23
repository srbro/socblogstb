package com.ug.eon.android.tv.prefs;

/**
 * Created by nemanja.todoric on 3/29/2018.
 */

public class ServiceProviderPrefs {

    private int id;
    private String identifier;
    private String name;
    private String certificateUrlFairplay;
    private String licenseServerUrlFairplay;
    private String licenseServerUrlPlayready;
    private String licenseServerUrlWidewine;
    private String supportPhoneNumber;
    private String supportWebAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertificateUrlFairplay() {
        return certificateUrlFairplay;
    }

    public void setCertificateUrlFairplay(String certificateUrlFairplay) {
        this.certificateUrlFairplay = certificateUrlFairplay;
    }

    public String getLicenseServerUrlFairplay() {
        return licenseServerUrlFairplay;
    }

    public void setLicenseServerUrlFairplay(String licenseServerUrlFairplay) {
        this.licenseServerUrlFairplay = licenseServerUrlFairplay;
    }

    public String getLicenseServerUrlPlayready() {
        return licenseServerUrlPlayready;
    }

    public void setLicenseServerUrlPlayready(String licenseServerUrlPlayready) {
        this.licenseServerUrlPlayready = licenseServerUrlPlayready;
    }

    public String getLicenseServerUrlWidewine() {
        return licenseServerUrlWidewine;
    }

    public void setLicenseServerUrlWidewine(String licenseServerUrlWidewine) {
        this.licenseServerUrlWidewine = licenseServerUrlWidewine;
    }

    public String getSupportPhoneNumber() {
        return supportPhoneNumber;
    }

    public void setSupportPhoneNumber(String supportPhoneNumber) {
        this.supportPhoneNumber = supportPhoneNumber;
    }

    public String getSupportWebAddress() {
        return supportWebAddress;
    }

    public void setSupportWebAddress(String supportWebAddress) {
        this.supportWebAddress = supportWebAddress;
    }
}
