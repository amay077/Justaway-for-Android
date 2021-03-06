package net.amay077.kustaway.fragment.mute

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.amay077.kustaway.adapter.MuteUser
import net.amay077.kustaway.adapter.RecyclerMuteUserAdapter
import net.amay077.kustaway.databinding.RecyclerListBinding
import net.amay077.kustaway.settings.MuteSettings
import java.util.*

class UserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (inflater == null) {
            return null
        }

        val binding = RecyclerListBinding.inflate(inflater, container, false)

        val adapter = RecyclerMuteUserAdapter(activity, ArrayList())
        binding.recyclerView.adapter = adapter

        val userMap = MuteSettings.getUserMap()
        for (userId in userMap.keys) {
            userMap[userId]?.let { screenName ->
                adapter.add(MuteUser(userId, screenName))
            }
        }

        // 削除ボタンが押されたとき
        adapter.onItemRemoveListener = { user ->
            adapter.remove(user)
            adapter.notifyDataSetChanged()
            MuteSettings.removeUser(user.userId)
            MuteSettings.saveMuteSettings()
            Unit
        }

        return binding.root
    }
}
