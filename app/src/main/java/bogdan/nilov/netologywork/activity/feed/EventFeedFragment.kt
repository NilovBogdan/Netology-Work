package bogdan.nilov.netologywork.activity.feed

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import bogdan.nilov.netologywork.R
import bogdan.nilov.netologywork.adapter.EventAdapter
import bogdan.nilov.netologywork.adapter.EventViewHolder
import bogdan.nilov.netologywork.adapter.OnInteractionListener
import bogdan.nilov.netologywork.databinding.FragmentEventFeedBinding
import bogdan.nilov.netologywork.dto.Event
import bogdan.nilov.netologywork.dto.FeedItem
import bogdan.nilov.netologywork.dto.UserResponse
import bogdan.nilov.netologywork.model.AuthModel
import bogdan.nilov.netologywork.model.InvolvedItemType
import bogdan.nilov.netologywork.util.AppConst
import bogdan.nilov.netologywork.viewModel.AuthViewModel
import bogdan.nilov.netologywork.viewModel.EventViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.getValue


@AndroidEntryPoint
class EventFeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val authViewModel by activityViewModels<AuthViewModel>()
        val eventViewModel by activityViewModels<EventViewModel>()
        val binding = FragmentEventFeedBinding.inflate(inflater, container, false)
        val parentNavController = parentFragment?.parentFragment?.findNavController()

        var token: AuthModel? = null
        authViewModel.dataAuth.observe(viewLifecycleOwner) { state ->
            token = state
        }

        val eventAdapter = EventAdapter(object : OnInteractionListener {
            override fun like(feedItem: FeedItem) {
                if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                    eventViewModel.like(feedItem as Event)
                } else {
                    parentNavController?.navigate(R.id.action_mainFragment_to_signUpFragment)
                }
            }

            override fun share(content: String) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, "Share event")
                startActivity(shareIntent)
            }

            override fun delete(feedItem: FeedItem) {
                eventViewModel.deleteEvent(feedItem as Event)
            }

            override fun edit(feedItem: FeedItem) {
                feedItem as Event
                eventViewModel.edit(feedItem)
                parentNavController?.navigate(
                    R.id.action_mainFragment_to_newEventFragment,
                    bundleOf(AppConst.EDIT_EVENT to feedItem.content)
                )
            }

            override fun selectUser(userResponse: UserResponse) {}

            override fun openCard(feedItem: FeedItem) {
                eventViewModel.openEvent(feedItem as Event)
                lifecycleScope.launch {
                    eventViewModel.getInvolved(
                        feedItem.speakerIds,
                        InvolvedItemType.SPEAKERS
                    )
                    eventViewModel.getInvolved(
                        feedItem.likeOwnerIds,
                        InvolvedItemType.LIKERS
                    )
                    eventViewModel.getInvolved(
                        feedItem.participantsIds,
                        InvolvedItemType.PARTICIPANT
                    )
                }
                parentNavController?.navigate(R.id.action_mainFragment_to_detailEventFragment)
            }
        })

        binding.recyclerViewEvent.adapter = eventAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.data.collectLatest {
                    eventAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            eventAdapter.loadStateFlow.collectLatest {
                binding.swipeRefreshEvent.isRefreshing =
                    it.refresh is LoadState.Loading

                if (it.append is LoadState.Error
                    || it.prepend is LoadState.Error
                    || it.refresh is LoadState.Error
                ) {
                    Snackbar.make(
                        binding.root,
                        R.string.connection_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                suspendCancellableCoroutine {
                    it.invokeOnCancellation {
                        (0..<binding.recyclerViewEvent.childCount)
                            .map(binding.recyclerViewEvent::getChildAt)
                            .map(binding.recyclerViewEvent::getChildViewHolder)
                            .filterIsInstance<EventViewHolder>()
                            .onEach(EventViewHolder::stopPlayer)
                    }
                }
            }
        }

        binding.swipeRefreshEvent.setOnRefreshListener {
            eventAdapter.refresh()
        }

        binding.buttonNewEvent.setOnClickListener {
            if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                parentNavController?.navigate(R.id.action_mainFragment_to_newEventFragment)
            } else {
                parentNavController?.navigate(R.id.action_mainFragment_to_signUpFragment)
            }
        }


        return binding.root
    }

}