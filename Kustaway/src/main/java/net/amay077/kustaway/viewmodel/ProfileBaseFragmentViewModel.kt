package net.amay077.kustaway.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import net.amay077.kustaway.model.PagedResponseList
import twitter4j.TwitterResponse
import twitter4j.User

/**
 * プロフィール画面の各フラグメント用の共通ViewModel
 */
abstract class ProfileBaseFragmentViewModel<T : TwitterResponse?>(
        private val user: User
) : ViewModel() {

    /** List用のデータを非同期で読む */
    protected suspend abstract fun loadListItemsAsync(userId:Long, cursor: Long) : PagedResponseList<T>

    /** List最下段のプログレスの表示ON/OFF */
    private val _isVisibleBottomProgress = MutableLiveData<Boolean>()
    val isVisibleBottomProgress : LiveData<Boolean> = _isVisibleBottomProgress

    /** ListView自体のプログレスの表示ON/OFF */
    private val _isVisibleListView = MutableLiveData<Boolean>()
    val isVisibleListView : LiveData<Boolean> = _isVisibleListView

    /** Pull to Refresh のプログレスの表示ON/OFF */
    private val _isVisiblePullProgress = MutableLiveData<Boolean>()
    val isVisiblePullProgress : LiveData<Boolean> = _isVisiblePullProgress

    /** ListView にバインドするデータ */
    private val _listItems = MutableLiveData<ProfileItemList<T>>()
    val listItems: LiveData<ProfileItemList<T>> = _listItems

    // 追加読み込みを有効とするか？
    private var isEnabledAdditionalLoading = false
    private var cursor: Long = -1

    /** ListView にバインドするデータをロードして listItems に通知する TODO いずれ RxCommand にする */
    fun loadListItems(isAdditional:Boolean) {
        launch (UI) {
            if (isAdditional) {
                if (!isEnabledAdditionalLoading) {
                    return@launch
                }

                // 追加読み込みの場合は下部プログレスを表示ON
                _isVisibleBottomProgress.postValue(true)
            } else {
                cursor = -1
            }

            isEnabledAdditionalLoading = false

            // データを非同期で読む
            val res = loadListItemsAsync(user.id, cursor);

            cursor = res.nextCursor
            isEnabledAdditionalLoading = res.hasNext

            // 読んだデータを通知してリストを表示ON
            _listItems.postValue(ProfileItemList(res.items, isAdditional))
            _isVisibleListView.postValue(true)

            // プログレス類は表示OFF
            _isVisiblePullProgress.postValue(false)
            _isVisibleBottomProgress.postValue(false)
        }
    }
}