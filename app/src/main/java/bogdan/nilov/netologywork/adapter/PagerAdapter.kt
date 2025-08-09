package bogdan.nilov.netologywork.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import bogdan.nilov.netologywork.activity.JobsFragment
import bogdan.nilov.netologywork.activity.feed.PostFeedFragment
import bogdan.nilov.netologywork.util.AppConst

class PagerAdapter(fragment: Fragment, private val userId: Long?) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                PostFeedFragment().apply {
                    arguments = bundleOf(AppConst.USER_ID to userId)
                }
            }

            1 -> {
                JobsFragment.newInstance().apply {
                    arguments = bundleOf(AppConst.USER_ID to userId)
                }
            }

            else -> {
                error("Unknown position")
            }
        }
    }
}