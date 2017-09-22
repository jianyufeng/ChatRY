/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sanbafule.sharelock.chatting.help;


public enum ChattingRowType {


    /**
     * Display text of message received
     */
    DESCRIPTION_ROW_RECEIVED("C1001R", Integer.valueOf(7)),

    /**
     * Display text of message transmitted
     */
    DESCRIPTION_ROW_TRANSMIT("C1001T", Integer.valueOf(8)),

    /**
     * display a voice of message received
     */
    VOICE_ROW_RECEIVED("C1002R", Integer.valueOf(5)),

    /**
     * display a voice of message transmitted
     */
    VOICE_ROW_TRANSMIT("C1002T", Integer.valueOf(6)),


    /**
     * display a image of message received
     */
    IMAGE_ROW_RECEIVED("C1003R", Integer.valueOf(1)),

    /**
     * display a image of message transmitted
     */
    IMAGE_ROW_TRANSMIT("C1003T", Integer.valueOf(2)),


    /**
     * display a file of message received
     */
    FILE_ROW_RECEIVED("C1004R", Integer.valueOf(3)),

    /**
     * display a file of message transmitted
     */
    FILE_ROW_TRANSMIT("C1004T", Integer.valueOf(4)),

    /**
     * Display lacation of message received
     */
    LOCATION_ROW_RECEIVED("C1005R", Integer.valueOf(10)),

    /**
     * Display lacation of message transmitted
     */
    LOCATION_ROW_TRANSMIT("C1005T", Integer.valueOf(11)),

    /**
     * Display gif of message received
     */
    GIF_ROW_RECEIVED("C1006R", Integer.valueOf(12)),

    /**
     * Display gif of message transmitted
     */
    GIF_ROW_TRANSMIT("C1006T", Integer.valueOf(13)),

    /**
     * chatting item for system .such as time
     */
    CHATTING_SYSTEM_RECEIVED("C186R", Integer.valueOf(9)),

    CHATTING_SYSTEM_TRANSMIT("C186T", Integer.valueOf(9)),

    VIDEO_ROW_RECEIVED("C10014R", Integer.valueOf(14)),
    VIDEO_ROW_TRANSMIT("C10014T", Integer.valueOf(15));


//    CALL_ROW_TRANSMIT("C2400T" , Integer.valueOf(13));


    private final Integer mId;
    private final Object mDefaultValue;

    /**
     * Constructor of <code>ChattingRowType</code>.
     *
     * @param id           The unique identifier of the setting
     * @param defaultValue The default value of the setting
     */
    private ChattingRowType(Object defaultValue, Integer id) {
        this.mId = id;
        this.mDefaultValue = defaultValue;
    }

    /**
     * Method that returns the unique identifier of the setting.
     *
     * @return the mId
     */
    public Integer getId() {
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
     * Method that returns an instance of {@link } from its.
     * unique identifier
     *
     * @param value The unique identifier
     * @return CCPPreferenceSettings The navigation sort mode
     * c1001R
     */
    public static ChattingRowType fromValue(String value) {
        ChattingRowType[] values = values();
        int cc = values.length;
        for (int i = 0; i < cc; i++) {
            if (values[i].mDefaultValue.equals(value)) {
                return values[i];
            }
        }
        return null;
    }

}
