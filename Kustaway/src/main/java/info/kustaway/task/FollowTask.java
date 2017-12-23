package info.kustaway.task;

import android.os.AsyncTask;

import info.kustaway.model.TwitterManager;

public class FollowTask extends AsyncTask<Long, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Long... params) {
        try {
            TwitterManager.getTwitter().createFriendship(params[0]);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}