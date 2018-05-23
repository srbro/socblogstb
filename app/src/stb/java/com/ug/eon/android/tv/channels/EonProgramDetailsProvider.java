package com.ug.eon.android.tv.channels;

import com.ug.eon.android.tv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemanja.todoric on 1/27/2018.
 */

public class EonProgramDetailsProvider {

    private static final String TAG = "EonProgramDetails";

    public static List<EonProgram> getProgramsForMainChannel() {
        List<EonProgram> mainChannelPrograms = new ArrayList<>();

        EonProgram liveTVProgram = new EonProgram();
        liveTVProgram.setId(1);
        liveTVProgram.setTitle("Live TV");
        liveTVProgram.setDescription("Watch live TV shows");
        liveTVProgram.setCategory("Live TV channel category");
        liveTVProgram.setDeepLink("eon://app/livetv");
        liveTVProgram.setCardImageId(R.drawable.eon_live_tv_card_01);
        liveTVProgram.setBgImageId(R.drawable.eon_live_tv_card_01);

        mainChannelPrograms.add(liveTVProgram);

        EonProgram tvGuideProgram = new EonProgram();
        tvGuideProgram.setId(1);
        tvGuideProgram.setTitle("TV Guide");
        tvGuideProgram.setDescription("Choose channel to watch");
        tvGuideProgram.setCategory("Live TV channel category");
        tvGuideProgram.setDeepLink("eon://app/guide");
        tvGuideProgram.setCardImageId(R.drawable.eon_tv_guide_card_02);
        tvGuideProgram.setBgImageId(R.drawable.eon_tv_guide_card_02);

        mainChannelPrograms.add(tvGuideProgram);

        EonProgram nowOnTvProgram = new EonProgram();
        nowOnTvProgram.setId(1);
        nowOnTvProgram.setTitle("Now on TV");
        nowOnTvProgram.setDescription("See what's now on TV");
        nowOnTvProgram.setCategory("Live TV channel category");
        nowOnTvProgram.setDeepLink("eon://app/nowontv");
        nowOnTvProgram.setCardImageId(R.drawable.eon_now_on_tv_card_03);
        nowOnTvProgram.setBgImageId(R.drawable.eon_now_on_tv_card_03);

        mainChannelPrograms.add(nowOnTvProgram);

        EonProgram onDemandProgram = new EonProgram();
        onDemandProgram.setId(1);
        onDemandProgram.setTitle("Video On Demand");
        onDemandProgram.setDescription("Watch Videos on Demand");
        onDemandProgram.setCategory("Live TV channel category");
        onDemandProgram.setDeepLink("eon://app/vod");
        onDemandProgram.setCardImageId(R.drawable.eon_on_demand_card_04);
        onDemandProgram.setBgImageId(R.drawable.eon_on_demand_card_04);

        mainChannelPrograms.add(onDemandProgram);

        EonProgram radioProgram = new EonProgram();
        radioProgram.setId(1);
        radioProgram.setTitle("Radio stations");
        radioProgram.setDescription("Listen to your favorite radio stations");
        radioProgram.setCategory("Live TV channel category");
        radioProgram.setDeepLink("eon://app/radio");
        radioProgram.setCardImageId(R.drawable.eon_radio_card_05);
        radioProgram.setBgImageId(R.drawable.eon_radio_card_05);

        mainChannelPrograms.add(radioProgram);

        return mainChannelPrograms;
    }

}
