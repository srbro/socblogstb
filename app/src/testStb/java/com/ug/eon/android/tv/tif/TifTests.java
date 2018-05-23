package com.ug.eon.android.tv.tif;

import com.ug.eon.android.tv.tif.parser.DvbChannelParser;

import org.json.JSONException;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by milan.adamovic on 3/13/18.
 */
public class TifTests {

    /**
     * Tests that sha1 function returns the same hax value for the same string value.
     */
    @Test
    public void hashSha1Test() throws JSONException {
        String channelList = "[{\"id\":2,\"position\":2,\"name\":\"RTS 1\",\"shortName\":\"RTS 1\",\"images\":" +
                "[{\"path\":\"/2017/07/21/16/00/04/rts_1_160x90.png\",\"width\":160,\"height\":90,\"size\":\"S\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/07/21/16/00/04/rts_1_288x162.png\",\"width\":288,\"height\":162,\"size\":\"M\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/07/21/16/00/05/rts_1_384x216.png\",\"width\":384,\"height\":216,\"size\":\"L\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/07/21/16/00/05/rts_1_480x270.png\",\"width\":480,\"height\":270,\"size\":\"XL\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/09/01/15/01/04/stb_fhd_rts_1_480x270.png\",\"width\":300,\"height\":168,\"size\":\"STB_FHD\",\"type\":\"LOGO_16_9\"," +
                "\"mode\":null}],\"ageRating\":null,\"categories\":[{\"id\":1,\"primary\":false},{\"id\":10,\"primary\":true}],\"publishingPoint\":" +
                "[{\"publishingPoint\":\"rts1-sd-i\",\"audioLanguage\":\"srp\",\"subtitleLanguage\":\"\",\"profileIds\":[16,13,14,15]}],\"cutvDelay\":604800000," +
                "\"subscribed\":true,\"errorMessage\":null,\"dvbInfo\":{\"originalNetworkId\":511,\"serviceId\":null,\"transportStreamId\":null,\"casProtected\"" +
                ":false,\"modulation\":null,\"frequency\":null,\"symbolRate\":null},\"drmRequired\":false,\"liveEnabled\":true,\"startOverEnabled\":true," +
                "\"airplayEnabled\":true,\"castEnabled\":true,\"cutvEnabled\":true,\"tamperedDeviceEnabled\":true}]";

        DvbChannelParser parser = new DvbChannelParser(channelList, null);
        String channelSha1 = parser.parse().getString("hash");
        DvbChannelParser parser2 = new DvbChannelParser(channelList, null);
        String channelSha1Same = parser2.parse().getString("hash");

        System.out.println("Same sha1: " + channelSha1);
        System.out.println("Same sha1: " + channelSha1Same);

        // Sha values should be the same.
        assertEquals(channelSha1, channelSha1Same);
    }

    @Test
    public void parserTest() {
        String channelList = "[{\"id\":2,\"position\":2,\"name\":\"RTS 1\",\"shortName\":\"RTS 1\",\"images\":" +
                "[{\"path\":\"/2017/07/21/16/00/04/rts_1_160x90.png\",\"width\":160,\"height\":90,\"size\":\"S\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/07/21/16/00/04/rts_1_288x162.png\",\"width\":288,\"height\":162,\"size\":\"M\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/07/21/16/00/05/rts_1_384x216.png\",\"width\":384,\"height\":216,\"size\":\"L\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/07/21/16/00/05/rts_1_480x270.png\",\"width\":480,\"height\":270,\"size\":\"XL\",\"type\":\"LOGO_16_9\",\"mode\":null}," +
                "{\"path\":\"/2017/09/01/15/01/04/stb_fhd_rts_1_480x270.png\",\"width\":300,\"height\":168,\"size\":\"STB_FHD\",\"type\":\"LOGO_16_9\"," +
                "\"mode\":null}],\"ageRating\":null,\"categories\":[{\"id\":1,\"primary\":false},{\"id\":10,\"primary\":true}],\"publishingPoint\":" +
                "[{\"publishingPoint\":\"rts1-sd-i\",\"audioLanguage\":\"srp\",\"subtitleLanguage\":\"\",\"profileIds\":[16,13,14,15]}],\"cutvDelay\":604800000," +
                "\"subscribed\":true,\"errorMessage\":null,\"dvbInfo\":{\"originalNetworkId\":511,\"serviceId\":null,\"transportStreamId\":null,\"casProtected\"" +
                ":false,\"modulation\":null,\"frequency\":null,\"symbolRate\":null},\"drmRequired\":false,\"liveEnabled\":true,\"startOverEnabled\":true," +
                "\"airplayEnabled\":true,\"castEnabled\":true,\"cutvEnabled\":true,\"tamperedDeviceEnabled\":true}]";

        String radioChannels = "[{\"id\":207,\"position\":700,\"name\":\"Radio S1\",\"shortName\":\"Radio S1\",\"images\":[{\"path\":\"" +
                "/2017/07/21/16/00/09/radio_s1_160x90.png\",\"width\":160,\"height\":90,\"size\":\"S\",\"type\":\"LOGO_16_9\",\"mode\":null}" +
                ",{\"path\":\"/2017/07/21/16/00/09/radio_s1_288x162.png\",\"width\":288,\"height\":162,\"size\":\"M\",\"type\":\"LOGO_16_9\"" +
                ",\"mode\":null},{\"path\":\"/2017/07/21/16/00/10/radio_s1_384x216.png\",\"width\":384,\"height\":216,\"size\":\"L\",\"type\":" +
                "\"LOGO_16_9\",\"mode\":null},{\"path\":\"/2017/07/21/16/00/10/radio_s1_480x270.png\",\"width\":480,\"height\":270,\"size\":\"XL\"" +
                ",\"type\":\"LOGO_16_9\",\"mode\":null},{\"path\":\"/2017/09/01/15/08/40/stb_fhd_radio_s1_480x270.png\",\"width\":300,\"height\":168" +
                ",\"size\":\"STB_FHD\",\"type\":\"LOGO_16_9\",\"mode\":null}],\"ageRating\":null,\"categories\":[{\"id\":104,\"primary\":true},{\"id\":101," +
                "\"primary\":false}],\"publishingPoint\":[{\"publishingPoint\":\"s-radio\",\"audioLanguage\":\"srp\",\"subtitleLanguage\":\"\"" +
                ",\"profileIds\":[17]}],\"cutvDelay\":604800000,\"subscribed\":true,\"errorMessage\":null,\"dvbInfo\":{\"originalNetworkId\":511" +
                ",\"serviceId\":null,\"transportStreamId\":null,\"casProtected\":false,\"modulation\":null,\"frequency\":null,\"symbolRate\":null}" +
                ",\"drmRequired\":false,\"liveEnabled\":true,\"startOverEnabled\":true,\"airplayEnabled\":true,\"castEnabled\":true,\"cutvEnabled\"" +
                ":true,\"tamperedDeviceEnabled\":true}]";

        // Both lists passed.
        String parserExpectedResult = "{\"channels\":[{\"symbolRate\":0,\"radioChannel\":false,\"modulation\":\"256-QAM\",\"name\":\"RTS 1\"," +
                "\"originalNetworkId\":0,\"position\":2,\"casProtected\":false,\"frequency\":640000},{\"symbolRate\":0,\"radioChannel\":true," +
                "\"modulation\":\"256-QAM\",\"name\":\"Radio S1\",\"originalNetworkId\":0,\"position\":700,\"casProtected\":false,\"frequency" +
                "\":640000}],\"doc-type\":\"SBB-Channels\",\"hash\":\"8914e4beb3633323579792cc58b50e9527f56f7\"}";

        DvbChannelParser parser = new DvbChannelParser(channelList, radioChannels);
        assertEquals(parserExpectedResult, parser.parse().toString());

        // Radio list is null.
        String parser2ExpectedResult = "{\"channels\":[{\"symbolRate\":0,\"radioChannel\":false,\"modulation\":\"256-QAM\",\"name\":\"RTS 1\"," +
                "\"originalNetworkId\":0,\"position\":2,\"casProtected\":false,\"frequency\":640000}],\"doc-type\":\"SBB-Channels\",\"hash\":\"" +
                "5336aac9457104335a3e9cfc47e914497d58f5\"}";

        DvbChannelParser parser2 = new DvbChannelParser(channelList, null);
        assertEquals(parser2ExpectedResult, parser2.parse().toString());
    }
}