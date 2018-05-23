package com.ug.eon.android.tv.channels;

/**
 * Created by nemanja.todoric on 1/27/2018.
 */

public class EonProgram {

    private static final String TAG = "EonProgram";

    private long id;
    private String title;
    private String description;
    private int bgImageId;
    private int cardImageId;
    private String studio;
    private String category;
    private String deepLink;
    private long programId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBgImageId() {
        return bgImageId;
    }

    public void setBgImageId(int bgImageId) {
        this.bgImageId = bgImageId;
    }

    public int getCardImageId() {
        return cardImageId;
    }

    public void setCardImageId(int cardImageId) {
        this.cardImageId = cardImageId;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }
}
