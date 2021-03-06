package net.amay077.kustaway.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import net.amay077.kustaway.model.PagedResponseList
import net.amay077.kustaway.repository.TwitterRepository
import twitter4j.User

/**
 * フォロー一覧画面の ViewModel
 */
class FollowingListFragmentViewModel (
        private val twitterRepo: TwitterRepository,
        userId: Long
) : ListBasedFragmentViewModel<Long, User, Long>(userId) {

    class Factory(
            private val twitterRepo: TwitterRepository,
            private val userId: Long
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                FollowingListFragmentViewModel(twitterRepo, userId) as T
    }

    suspend override fun loadListItemsAsync(userId:Long, cursor: Long?): PagedResponseList<User, Long> {
        val res = twitterRepo.loadFriendList(userId, cursor ?: -1);
        return PagedResponseList(res, res.hasNext(), res.nextCursor)
    }

}