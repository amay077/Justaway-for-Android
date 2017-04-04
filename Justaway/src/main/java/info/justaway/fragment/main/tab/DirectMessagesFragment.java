package info.justaway.fragment.main.tab;

import android.os.AsyncTask;
import android.view.View;

import java.util.Collections;
import java.util.Comparator;

import info.justaway.event.model.StreamingDestroyMessageEvent;
import info.justaway.model.Row;
import info.justaway.model.TabManager;
import info.justaway.model.TwitterManager;
import info.justaway.settings.BasicSettings;
import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;

public class DirectMessagesFragment extends BaseFragment {

    /**
     * このタブを表す固有のID、ユーザーリストで正数を使うため負数を使う
     */
    public long getTabId() {
        return TabManager.DIRECT_MESSAGES_TAB_ID;
    }

    /**
     * このタブに表示するツイートの定義
     * @param row ストリーミングAPIから受け取った情報（ツイートやDM）
     * @return trueは表示しない、falseは表示する
     */
    @Override
    protected boolean isSkip(Row row) {
        return !row.isDirectMessage();
    }

    @Override
    protected void taskExecute() {
        new DirectMessagesTask().execute();
    }

    private class DirectMessagesTask extends AsyncTask<Void, Void, ResponseList<DirectMessage>> {
        @Override
        protected ResponseList<DirectMessage> doInBackground(Void... params) {
            try {
                Twitter twitter = TwitterManager.getTwitter();

                // 受信したDM
                Paging directMessagesPaging = new Paging();
                if (mDirectMessagesMaxId > 0 && !mReloading) {
                    directMessagesPaging.setMaxId(mDirectMessagesMaxId - 1);
                    directMessagesPaging.setCount(BasicSettings.getPageCount() / 2);
                } else {
                    directMessagesPaging.setCount(10);
                }
                ResponseList<DirectMessage> directMessages = twitter.getDirectMessages(directMessagesPaging);
                for (DirectMessage directMessage : directMessages) {
                    if (mDirectMessagesMaxId <= 0L || mDirectMessagesMaxId > directMessage.getId()) {
                        mDirectMessagesMaxId = directMessage.getId();
                    }
                }

                // 送信したDM
                Paging sentDirectMessagesPaging = new Paging();
                if (mSentDirectMessagesMaxId > 0 && !mReloading) {
                    sentDirectMessagesPaging.setMaxId(mSentDirectMessagesMaxId - 1);
                    sentDirectMessagesPaging.setCount(BasicSettings.getPageCount() / 2);
                } else {
                    sentDirectMessagesPaging.setCount(10);
                }
                ResponseList<DirectMessage> sentDirectMessages = twitter.getSentDirectMessages(sentDirectMessagesPaging);
                for (DirectMessage directMessage : sentDirectMessages) {
                    if (mSentDirectMessagesMaxId <= 0L || mSentDirectMessagesMaxId > directMessage.getId()) {
                        mSentDirectMessagesMaxId = directMessage.getId();
                    }
                }

                directMessages.addAll(sentDirectMessages);

                // 日付でソート
                Collections.sort(directMessages, new Comparator<DirectMessage>() {

                    @Override
                    public int compare(DirectMessage arg0, DirectMessage arg1) {
                        return arg1.getCreatedAt().compareTo(
                                arg0.getCreatedAt());
                    }
                });
                return directMessages;
            } catch (OutOfMemoryError e) {
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<DirectMessage> statuses) {
            mFooter.setVisibility(View.GONE);
            if (statuses == null || statuses.size() == 0) {
                mReloading = false;
                mPullToRefreshLayout.setRefreshComplete();
                mListView.setVisibility(View.VISIBLE);
                return;
            }
            if (mReloading) {
                clear();
                for (DirectMessage status : statuses) {
                    mAdapter.add(Row.newDirectMessage(status));
                }
                mReloading = false;
            } else {
                for (DirectMessage status : statuses) {
                    mAdapter.extensionAdd(Row.newDirectMessage(status));
                }
                mAutoLoader = true;
                mListView.setVisibility(View.VISIBLE);
            }
            mPullToRefreshLayout.setRefreshComplete();
        }
    }

    /**
     * DM削除通知
     * @param event DMのID
     */
    public void onEventMainThread(StreamingDestroyMessageEvent event) {
        mAdapter.removeDirectMessage(event.getStatusId());
    }
}
