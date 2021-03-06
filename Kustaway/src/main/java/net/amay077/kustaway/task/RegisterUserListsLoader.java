package net.amay077.kustaway.task;

import android.content.Context;

import java.util.ArrayList;

import net.amay077.kustaway.KustawayApplication;
import net.amay077.kustaway.model.AccessTokenManager;
import net.amay077.kustaway.model.TwitterManager;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.UserList;

public class RegisterUserListsLoader extends AbstractAsyncTaskLoader<ArrayList<ResponseList<UserList>>> {

    private long mUserId;

    public RegisterUserListsLoader(Context context, long userId) {
        super(context);
        this.mUserId = userId;
    }

    @Override
    public ArrayList<ResponseList<UserList>> loadInBackground() {
        try {
            ArrayList<ResponseList<UserList>> responseLists = new ArrayList<ResponseList<UserList>>();
            responseLists.add(TwitterManager.getTwitter().getUserListsOwnerships(AccessTokenManager.getUserId(), 200, -1));
            responseLists.add(TwitterManager.getTwitter().getUserListMemberships(mUserId, -1, true));

            return responseLists;
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
