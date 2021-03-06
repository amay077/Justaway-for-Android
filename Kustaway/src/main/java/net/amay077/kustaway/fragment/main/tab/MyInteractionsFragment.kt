package net.amay077.kustaway.fragment.main.tab

import android.arch.lifecycle.ViewModelProviders
import net.amay077.kustaway.extensions.getTwitterRepo
import net.amay077.kustaway.fragment.common.TweetListBasedFragment
import net.amay077.kustaway.viewmodel.MyInteractionsFragmentViewModel

class MyInteractionsFragment : TweetListBasedFragment<MyInteractionsFragmentViewModel>() {

    override fun createViewModel(dummy: Unit): MyInteractionsFragmentViewModel =
            ViewModelProviders
                    .of(this, MyInteractionsFragmentViewModel.Factory(
                            this.getTwitterRepo()
                    ))
                    .get(MyInteractionsFragmentViewModel::class.java)
}