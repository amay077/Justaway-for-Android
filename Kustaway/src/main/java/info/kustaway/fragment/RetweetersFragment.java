package info.kustaway.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import info.kustaway.R;
import info.kustaway.adapter.UserAdapter;
import info.kustaway.model.TwitterManager;
import info.kustaway.util.MessageUtil;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * リツイートを表示
 *
 * @author aska
 */
public class RetweetersFragment extends DialogFragment {

    private ProgressBar mProgressBar;
    private UserAdapter mAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_retweeters);

        ListView listView = (ListView) dialog.findViewById(R.id.list);
        mProgressBar = (ProgressBar) dialog.findViewById(R.id.guruguru);

        mAdapter = new UserAdapter(activity, R.layout.row_user);
        listView.setAdapter(mAdapter);

        Long statusId = getArguments().getLong("statusId");
        if (statusId > 0) {
            new RetweetsTask().execute(statusId);
        }

        return dialog;
    }

    private class RetweetsTask extends AsyncTask<Long, Void, ResponseList<Status>> {


        public RetweetsTask() {
            super();
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Long... params) {
            try {
                Twitter twitter = TwitterManager.getTwitter();
                return twitter.getRetweets(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
            mProgressBar.setVisibility(View.GONE);
            if (statuses != null) {
                for (twitter4j.Status status : statuses) {
                    mAdapter.add(status.getUser());
                }
                mAdapter.notifyDataSetChanged();
            } else {
                MessageUtil.showToast(R.string.toast_load_data_failure);
            }
        }
    }
}
