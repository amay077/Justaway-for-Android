package net.amay077.kustaway.fragment.profile

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.amay077.kustaway.adapter.DividerItemDecoration
import net.amay077.kustaway.adapter.RecyclerUserAdapter
import net.amay077.kustaway.databinding.ListGuruguruBinding
import net.amay077.kustaway.model.TwitterManager
import twitter4j.PagableResponseList
import twitter4j.User
import java.util.*

/**
 * フォロワー一覧
 */
class FollowersListFragment : Fragment() {
    private var mUserId: Long = 0
    private var mCursor: Long = -1
    private var mAutoLoader = false

    private lateinit var binding: ListGuruguruBinding
    private lateinit var mAdapter: RecyclerUserAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bin = inflater?.let { inf -> ListGuruguruBinding.inflate(inf, container, false) }
        if (bin == null) {
            return null
        }
        binding = bin

        val user = arguments.getSerializable("user") as User

        mUserId = user.id

        // RecyclerView の設定
        binding.listView.visibility = View.GONE // ListView は消しておく TODO 完全に RecyclerView 化できたら .xml からも消す
        binding.recyclerView.visibility = View.GONE
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context)) // 罫線付ける

        // コンテキストメニューを使える様にする為の指定、但しデフォルトではロングタップで開く
        registerForContextMenu(binding.recyclerView)

        // Status(ツイート)をViewに描写するアダプター
        mAdapter = RecyclerUserAdapter(activity, ArrayList())
        binding.recyclerView.adapter = mAdapter

        FollowersListTask().execute(mUserId)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(view: RecyclerView?, scrollState: Int) {}

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // see - http://recyclerview.hatenablog.com/entry/2016/11/05/182404
                val totalCount = recyclerView!!.adapter.itemCount //合計のアイテム数
                val childCount = recyclerView.childCount // RecyclerViewに表示されてるアイテム数
                val layoutManager = recyclerView.layoutManager

                if (layoutManager is GridLayoutManager) { // GridLayoutManager
                    val firstPosition = layoutManager.findFirstVisibleItemPosition() // RecyclerViewに表示されている一番上のアイテムポジション
                    if (totalCount == childCount + firstPosition) {
                        // ページング処理
                        // GridLayoutManagerを指定している時のページング処理
                    }
                } else if (layoutManager is LinearLayoutManager) { // LinearLayoutManager
                    val firstPosition = layoutManager.findFirstVisibleItemPosition() // RecyclerViewの一番上に表示されているアイテムのポジション
                    if (totalCount == childCount + firstPosition) {
                        // ページング処理
                        additionalReading()
                    }
                }
            }
        })

        return binding.root
    }

    private fun additionalReading() {
        if (!mAutoLoader) {
            return
        }
        binding.guruguru.visibility = View.VISIBLE
        mAutoLoader = false
        FollowersListTask().execute(mUserId)
    }

    private inner class FollowersListTask : AsyncTask<Long, Void, PagableResponseList<User>>() {
        protected override fun doInBackground(vararg params: Long?): PagableResponseList<User>? {
            try {
                val users = TwitterManager.getTwitter().getFollowersList(params[0] ?: -1, mCursor) // TODO 雑すぎ
                mCursor = users.nextCursor
                return users
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(users: PagableResponseList<User>?) {
            binding.guruguru.visibility = View.GONE
            if (users == null) {
                return
            }
            for (user in users) {
                mAdapter.add(user)
            }
            if (users.hasNext()) {
                mAutoLoader = true
            }
            mAdapter.notifyDataSetChanged()
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
}
