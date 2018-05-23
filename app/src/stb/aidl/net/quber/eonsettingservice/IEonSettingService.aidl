// IEonSettingService.aidl
package net.quber.eonsettingservice;

// Declare any non-default types here with import statements

interface IEonSettingService {

    /**
     * get video output standard
     *
     * @return resolution video output standard enum DISPLAY_MODE,
     *            DISPLAY_MODE_720P50HZ           = 0,
     *            DISPLAY_MODE_720P               = 1,
     *            DISPLAY_MODE_1080I50HZ          = 2,
     *            DISPLAY_MODE_1080I              = 3,
     *            DISPLAY_MODE_1080P50HZ          = 4,
     *            DISPLAY_MODE_1080P              = 5,
     *            DISPLAY_MODE_720P_3DOU_AS       = 6,
     *            DISPLAY_MODE_1080P24HZ          = 7,
     *            DISPLAY_MODE_1080P24HZ_3DOU_AS  = 8,
     *            DISPLAY_MODE_3840x2160P30HZ     = 9,
     *            DISPLAY_MODE_3840x2160P60HZ     = 10,
     *
     */
    int getDisplayResolution();

    /**
     * set video output standard
     *
     * @param displayMode resolution video output standard enum DISPLAY_MODE,
     *            DISPLAY_MODE_AUTO               = 0,
     *            DISPLAY_MODE_720P50HZ           = 1,
     *            DISPLAY_MODE_720P               = 2,
     *            DISPLAY_MODE_1080I50HZ          = 3,
     *            DISPLAY_MODE_1080I              = 4,
     *            DISPLAY_MODE_1080P50HZ          = 5,
     *            DISPLAY_MODE_1080P              = 6,
     *            DISPLAY_MODE_720P_3DOU_AS       = 7,
     *            DISPLAY_MODE_1080P24HZ          = 8,
     *            DISPLAY_MODE_1080P24HZ_3DOU_AS  = 9,
     *            DISPLAY_MODE_3840x2160P30HZ     = 10,
     *            DISPLAY_MODE_3840x2160P60HZ     = 11,
     *
     * @return true, if success
     */
    boolean setDisplayResolution(int displayMode);

    /**
     * get Video Scaling Mode
     *
     * @return video scaling mode enum SCALING_MODE,
     * 	SCALING_MODE_NONE = 0;
     * 	SCALING_MODE_LETTERBOX = 1;
     * 	SCALING_MODE_PANSCAN = 2;
     * 	SCALING_MODE_PILLARBOX = 3;
     * 	SCALING_MODE_STRETCHED = 4;
     * 	SCALING_MODE_ZOOM = 5;
     *
     */
    int getVideoScalingMode();

    /**
     * set Video Scaling Mode
     *
     * @param scalingMode video scaling mode enum SCALING_MODE,
     *  SCALING_MODE_NONE = 0;
     * 	SCALING_MODE_LETTERBOX = 1;
     * 	SCALING_MODE_PANSCAN = 2;
     * 	SCALING_MODE_PILLARBOX = 3;
     * 	SCALING_MODE_STRETCHED = 4;
     * 	SCALING_MODE_ZOOM = 5;
     *
     *
     * @return true, if success
     */
    boolean setVideoScalingMode(int scalingMode);

    /**
     * get Video Aspect Ratio
     *
     * @return video aspect ratio enum ASPECT_RATIO
     *  ASPECT_RATIO_FULL = 0
     * 	ASPECT_RATIO_4X3 = 1;
     * 	ASPECT_RATIO_16X9 = 2;
     */
    int getVideoAspectRatio();

    /**
     * set Video Aspect Ratio
     *
     * @param aspectRatio video aspect ratio enum ASPECT_RATIO,
     * 	ASPECT_RATIO_4X3 = 0;
     * 	ASPECT_RATIO_16X9 = 1;
     *
     * @return true, if success
     */
    boolean setVideoAspectRatio(int aspectRatio);

    /**
     * get AudioOutput Mode
     *
     * @return enum AUDIO_OUTPUT_MODE,
     *   AUDIO_OUTPUT_MODE_AUTO = 0;
     *   AUDIO_OUTPUT_MODE_HDMI = 1;
     *   AUDIO_OUTPUT_MODE_SPDIF = 2;
     *
     */
    int getAudioOutputMode();

    /**
     * set AudioOutput Mode
     *
     * @param mode enum AUDIO_OUTPUT_MODE,
     *   AUDIO_OUTPUT_MODE_AUTO = 0;
     *   AUDIO_OUTPUT_MODE_HDMI = 1;
     *   AUDIO_OUTPUT_MODE_SPDIF = 2;
     *
     * @return true, if success
     */
    boolean setAudioOutputMode(int mode);

    /**
     * get HDMI Audio Mode
     *
     * @return enum HDMI_AUDIO_MODE, HDMI_AUDIO_MODE_PCM for PCM, HDMI_AUDIO_MODE_STEREO for STEREO
     *   HDMI_AUDIO_MODE_PCM = 0;
     *   HDMI_AUDIO_MODE_PASSTHROUGH = 1;
     *
     */
    int getHDMIAudioMode();

    /**
     * set HDMI Audio Mode
     *
     * @param mode enum HDMI_AUDIO_MODE,
     *   HDMI_AUDIO_MODE_PCM = 0;
     *   HDMI_AUDIO_MODE_PASSTHROUGH = 1;
     *
     * @return true, if success
     */
    boolean setHDMIAudioMode(int mode);

    /**
     * set graphics setting according to video offset settings.
     *
     *
     * @return true, if success
     */
    boolean setAdjustScreenOffset(int left, int top, int right, int bottom);

    /**
     * getSystemUpdateState
     *
     * @return String -- version / update date
     */
    String getSystemUpdateState();

    /**
     * start Google OTA Update
     * @return
     */
    boolean startSystemUpdate();

    /**
     * getAppVersionSate
     * @param packageName
     * @return String -- versionCode / versionName
     */
    String getAppVersionState(String packageName);

    /**
     * startAppUpdate
     * @return
     */
    boolean startAppUpdate();

    /**
     * Clear APP Data
     * @param packageName
     * @return
     */
    boolean clearAppData(String packageName);

    /**
     * Read currently lock status
     * @return If is locked 1, or not 0.
     */
    int readTunerLockStatus();

    /**
     * Read currently strength of signal
     * @return
     */
    float readTunerStrength();

    /**
     * Read currently quality of signal
     * @return
     */
    float readTunerQuality();

    /**
     * Update channel data
     * @return true, if success channel update
     */
    boolean updateDtvChannels(String json_channel_source);

    /**
     * Check Frequency
     * Note that the response may take a long time
     *
     * @param freq frequency KHz
     * @param symbol symbolrate Kbd
     * @param mod modulation (64QAM / 256QAM)
     * @return success tuning, true
     */
    boolean checkDvbcTuning(int freq, int symbol, String mod);

    /**
     * get
     * pair RCU List
     *
     * @return pair RCU device name List
     */
    List<String> getPairDevice();

    /**
     * unpair device
     *
     * @param String target RCU device name
     * @return success unpair, true
     */
    boolean unpairDevice(String deviceName);

    /**
     * get RCU device battery
     *
     * @param String target RCU device name
     * @return battery level(percent), -1 : false
     */
    int getBatteryDevice(String deviceName);

    /**
     * get STB Screen lock / unlock
     *
     * @param boolean true : stb lock, false : stb unlock
     */
    void setStbLock(boolean lock);
}
