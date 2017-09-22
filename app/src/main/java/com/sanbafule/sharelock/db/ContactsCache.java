package com.sanbafule.sharelock.db;

import android.content.Intent;
import android.os.AsyncTask;

import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.modle.Contact;


import java.util.ArrayList;


/**
 *
 */
public class ContactsCache {


    private static ContactsCache instance;

    private ArrayList<Contact> contacts;

    private LoadingTask asyncTask;

    private ContactsCache() {

    }

    public static ContactsCache getInstance() {
        if (instance == null) {
            instance = new ContactsCache();
        }

        return instance;
    }

    private class LoadingTask extends AsyncTask<Void, Void, ArrayList<Contact> > {
        public LoadingTask() {
        }
        @Override
        protected ArrayList<Contact>  doInBackground(Void... intents) {

            return  contacts = (ArrayList<Contact>) ContactSqlManager.getAllContacts();

        }
        @Override
        protected void onPostExecute(ArrayList<Contact>  result) {
            if (result != null) {
                contacts=result;
                Intent intent = new Intent(ReceiveAction.ACTION_CONTACTS_CHANGE);
                ShareLockManager.getInstance().getContext().sendBroadcast(intent);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * 异步获取联系人缓存
     */
    public synchronized void load() {
        try {
            if (asyncTask == null) {
                asyncTask = new LoadingTask();
            }
            asyncTask.execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            stop();
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (asyncTask != null && !asyncTask.isCancelled()) {
                asyncTask.cancel(true);
                asyncTask = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the contacts
     */
    public synchronized ArrayList<Contact> getContacts() {
        return contacts;
    }

}
