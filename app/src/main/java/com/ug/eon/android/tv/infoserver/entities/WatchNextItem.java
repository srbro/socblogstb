package com.ug.eon.android.tv.infoserver.entities;

import java.util.List;

public class WatchNextItem {
    private String type;
    private WatchNextItemPayload payload;

    public String getType() {
        return type;
    }

    public WatchNextItemPayload getPayload() {
        return payload;
    }

    public class WatchNextItemPayload {
        private int id;
        private int channelId;
        private String title;
        private String shortDescription;
        private String startTime;
        private String endTime;
        private List<Image> images;
        private List<Image> channelLogos;

        public int getId() {
            return id;
        }

        public int getChannelId() {
            return channelId;
        }

        public String getTitle() {
            return title;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public List<Image> getImages() {
            return images;
        }

        public List<Image> getChannelLogos() {
            return channelLogos;
        }
    }
}
