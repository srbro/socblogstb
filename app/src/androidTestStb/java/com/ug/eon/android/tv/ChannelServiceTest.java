package com.ug.eon.android.tv;

import com.ug.eon.android.tv.channels.EonProgram;
import com.ug.eon.android.tv.channels.services.ChannelManagementService;
import com.ug.eon.android.tv.channels.services.ProgramManagementService;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Created by nemanja.todoric on 2/1/2018.
 */

public class ChannelServiceTest {

    @Test
    public void addChannelTest() {
        ChannelManagementService service = new ChannelManagementService();
        service.setChannelChangedListener(new ChannelManagementService.ChannelChangedListener() {
            @Override
            public void onChannelAdded(long channelId) {
                assertTrue(channelId == 0);
            }
        });
    }

    @Test
    public void addProgramsTest() {
        ProgramManagementService service = new ProgramManagementService();
        service.setProgramChangedListener(new ProgramManagementService.ProgramChangedListener() {
            @Override
            public void onProgramsAdded(long channelId, List<EonProgram> programs) {
                assertTrue(channelId == 0);
            }
        });

        int i = 0;
    }
}
