package com.sanbafule.sharelock.comm.sp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.ObjectStringIdentifier;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.util.LogUtil;

import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/10/27.
 */
public class ShareLockPreferences {


    /**
     *
     */
    public static final String SHARELOCK_PREFERENCE = getDefaultSharedPreferencesFileName();


    private ShareLockPreferences() {
        super();
    }


    public static void loadDefaults() {
        //Sets the default preferences if no value is set yet
        try {
            Map<ShareLockPreferenceSettings, Object> defaultPrefs =
                    new HashMap<ShareLockPreferenceSettings, Object>();
            ShareLockPreferenceSettings[] values = ShareLockPreferenceSettings.values();
            int cc = values.length;
            for (int i = 0; i < cc; i++) {
                defaultPrefs.put(values[i], values[i].getDefaultValue());
            }
            savePreferences(defaultPrefs, false, true);
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }



    public static SharedPreferences getSharedPreferences() {
        return SApplication.getInstance().getSharedPreferences(
                getDefaultSharedPreferencesFileName(), Context.MODE_PRIVATE);
    }


    public static String getDefaultSharedPreferencesFileName() {
        return SString.SP_FILE_NAME;
    }


    public static SharedPreferences.Editor getSharedPreferencesEditor() {
        SharedPreferences cCPreferences = getSharedPreferences();
        SharedPreferences.Editor edit = cCPreferences.edit();
        edit.remove("");
        return edit;
    }


    public static void savePreference(ShareLockPreferenceSettings pref, Object value, boolean applied)
            throws InvalidClassException {
        Map<ShareLockPreferenceSettings, Object> prefs =
                new HashMap<ShareLockPreferenceSettings, Object>();
        prefs.put(pref, value);
        savePreferences(prefs, applied);
    }


    public static void savePreferences(Map<ShareLockPreferenceSettings, Object> prefs, boolean applied)
            throws InvalidClassException {
        savePreferences(prefs, true, applied);
    }


    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    private static void savePreferences(
            Map<ShareLockPreferenceSettings, Object> prefs, boolean noSaveIfExists, boolean applied)
            throws InvalidClassException {
        //Get the preferences editor
        SharedPreferences sp = getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();

        //Save all settings
        Iterator<ShareLockPreferenceSettings> it = prefs.keySet().iterator();
        while (it.hasNext()) {
            ShareLockPreferenceSettings pref = it.next();
            if (!noSaveIfExists && sp.contains(pref.getId())) {
                //The preference already has a value
                continue;
            }

            //Known and valid types
            Object value = prefs.get(pref);
        LogUtil.i(value.getClass().getSimpleName());
            if(value == null ) {
                return;
            }
            if (value instanceof Boolean && pref.getDefaultValue() instanceof Boolean) {
                editor.putBoolean(pref.getId(), ((Boolean)value).booleanValue());
            } else if (value instanceof String && pref.getDefaultValue() instanceof String) {
                editor.putString(pref.getId(), (String)value);
            } else if (value instanceof Integer && pref.getDefaultValue() instanceof Integer) {
                editor.putInt(pref.getId(), (Integer)value);
            } else if (value instanceof Long && pref.getDefaultValue() instanceof Long) {
                editor.putLong(pref.getId(), (Long)value);
            } else if (value instanceof Set && pref.getDefaultValue() instanceof Set) {
                //editor.putStringSet(pref.getId(), (Set<String>)value);
            } else if (value instanceof ObjectStringIdentifier
                    && pref.getDefaultValue() instanceof ObjectStringIdentifier) {
                editor.putString(pref.getId(), ((ObjectStringIdentifier)value).getId());
            }   else {
                //The object is not of the appropriate type
                String msg = String.format(
                        "%s: %s",
                        pref.getId(),
                        value.getClass().getName());
                LogUtil.i(String.format(
                        "Configuration error. InvalidClassException: %s",
                        msg));
                throw new InvalidClassException(msg);
            }

        }

        //Commit settings
        editor.commit();

    }
}
