
package com.sanbafule.sharelock.comm.sp;


public enum ShareLockPreferenceSettings {



    CLIENTUSER("com.sanbafule.sharelock.clientuser",""),
    // 是否登录
    ISLOGIN("com.sanbafule.sharelock.islogin",Boolean.FALSE),
    // 消息免打扰
    NO_DISTURB("com.sanbafule.sharelock.nodissturb",Boolean.FALSE),
    // 语音消息的听筒模式
    SETTINGS_NEW_MSG_EARPIECE("com.sanbafule.sharelock.message.earpiece",Boolean.FALSE);



    private final String mId;
    private final Object mDefaultValue;

    /**
     * Constructor of <code>CCPPreferenceSettings</code>.
     * @param id
     *            The unique identifier of the setting
     * @param defaultValue
     *            The default value of the setting
     */
    private ShareLockPreferenceSettings(String id, Object defaultValue) {
        this.mId = id;
        this.mDefaultValue = defaultValue;
    }

    /**
     * Method that returns the unique identifier of the setting.
     * @return the mId
     */
    public String getId() {
        return this.mId;
    }

    /**
     * Method that returns the default value of the setting.
     *
     * @return Object The default value of the setting
     */
    public Object getDefaultValue() {
        return this.mDefaultValue;
    }

    /**
     * Method that returns an instance of {@link ShareLockPreferenceSettings} from
     * its. unique identifier
     *
     * @param id
     *            The unique identifier
     * @return CCPPreferenceSettings The navigation sort mode
     */
    public static ShareLockPreferenceSettings fromId(String id) {
        ShareLockPreferenceSettings[] values = values();
        int cc = values.length;
        for (int i = 0; i < cc; i++) {
            if (values[i].mId == id) {
                return values[i];
            }
        }
        return null;
    }
}
