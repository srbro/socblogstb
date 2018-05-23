package com.ug.eon.android.tv.channels;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Created by nemanja.todoric on 2/1/2018.
 */

public class ChannelTest {

    @Test
    public void eonChannelBasicTest() {

        String channelName = "Eon Main Channel";
        String channelDesc = "Watch Eon television";
        int channelLogo = 5;

        EonChannel channel = new EonChannel();
        channel.setName(channelName);
        channel.setDescription(channelDesc);
        channel.setChannelLogo(channelLogo);

        assertTrue(channel.getName().equals(channelName));
        assertTrue(channel.getDescription().equals(channelDesc));
        assertTrue(channel.getChannelLogo() == channelLogo);
    }

    @Test
    public void eonProgramBasicTest() {
        String programTitle = "Live TV";
        String programDesc = "Watch Live TV";
        int programId = 1;

        EonProgram program = new EonProgram();
        program.setTitle(programTitle);
        program.setDescription(programDesc);
        program.setId(programId);

        assertTrue(program.getTitle().equals(programTitle));
        assertTrue(program.getDescription().equals(programDesc));
        assertTrue(program.getId() == programId);
    }
}