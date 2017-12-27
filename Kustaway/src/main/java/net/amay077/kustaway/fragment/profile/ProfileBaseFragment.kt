package net.amay077.kustaway.fragment.profile

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.amay077.kustaway.adapter.DividerItemDecoration
import net.amay077.kustaway.adapter.ProfileItemAdapter
import net.amay077.kustaway.databinding.PullToRefreshList2Binding
import net.amay077.kustaway.extensions.addOnPagingListener
import net.amay077.kustaway.viewmodel.ProfileBaseFragmentViewModel
import twitter4j.TwitterResponse
import twitter4j.User

/**
 * プロフィール画面の「ユーザータイムライン」「フォロー一覧」「フォロワー一覧」「リストユーザー一覧」「お気に入り一覧」のベースとなる Fragment
 *
 * TViewItem - RecyleView に表示する行の型
 * TDataItem - API等から読み込んだデータ1行の型
 * TViewModel - Fragment に対応させる ViewModel の型
 */
abstract class ProfileBaseFragment<
        TViewItem,
        TDataItem : TwitterResponse?,
        TViewModel : ProfileBaseFragmentViewModel<TDataItem>>
    : Fragment() {

    /*** 実装クラスで、 Fragment 用の ViewModel を生成する */
    abstract fun createViewModel(user: User): TViewModel

    /*** 実装クラスで、RecyclerView に設定する Adapter を生成する */
    abstract fun createAdapter() : ProfileItemAdapter<TViewItem>

    /*** 実装クラスで、モデル側の型からView用の型へ変換する */
    abstract fun convertDataToViewItem(dataItem:TDataItem): TViewItem

    protected lateinit var adapter : ProfileItemAdapter<TViewItem>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bin = inflater?.let { inf -> PullToRefreshList2Binding.inflate(inf, container, false) }
        if (bin == null) {
            return null
        }
        val binding = bin

        val user = arguments.getSerializable("user") as User
        val viewModel = createViewModel(user)

        // RecyclerView の設定
        binding.recyclerView.visibility = View.GONE
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context)) // 罫線付ける

        // コンテキストメニューを使える様にする為の指定、但しデフォルトではロングタップで開く
        registerForContextMenu(binding.recyclerView)

        // Status(ツイート)をViewに描写するアダプター
        adapter = createAdapter()
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnPagingListener {
            // ページング処理(追加読み込み)
            viewModel.loadListItems(true)
        }

        // Pull to Refresh の開始
        binding.ptrLayout.setOnRefreshListener {
            // 洗い替え
            viewModel.loadListItems(false)
        }

        // ViewModel の監視

        // 追加読み込みの Progress
        viewModel.isVisibleBottomProgress.observe(this, Observer { isVisible ->
            binding.guruguru.visibility = if (isVisible ?: false) View.VISIBLE else View.GONE
        })

        // Pull to Refresh の Progress
        viewModel.isVisiblePullProgress.observe(this, Observer { isVisible ->
            binding.ptrLayout.isRefreshing = isVisible ?: false
        })

        // リストビューの Visible
        viewModel.isVisibleListView.observe(this, Observer { isVisible ->
            binding.recyclerView.visibility = if (isVisible ?: false) View.VISIBLE else View.GONE
        })

        // 読み込んだデータを RecyclerView のアダプタに適用
        viewModel.listItems.observe(this, Observer { data ->
            if (data == null) {
                return@Observer
            }

            // 追加でなかったら全消し
            if (!data.isAdditional) {
                adapter.clear()
            }

            for (dataItem in data.items) {
                adapter.add(convertDataToViewItem(dataItem))
            }
            adapter.notifyDataSetChanged()
        })

        // 初回のデータ読み込み(ViewModel の init でやるべき？)
        viewModel.loadListItems(false)

        return binding.root
    }
}
