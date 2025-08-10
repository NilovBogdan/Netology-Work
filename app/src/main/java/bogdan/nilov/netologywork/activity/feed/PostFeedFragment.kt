package bogdan.nilov.netologywork.activity.feed
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.filter
import bogdan.nilov.netologywork.R
import bogdan.nilov.netologywork.adapter.OnInteractionListener
import bogdan.nilov.netologywork.adapter.PostAdapter
import bogdan.nilov.netologywork.adapter.PostViewHolder
import bogdan.nilov.netologywork.databinding.FragmentFeedPostBinding
import bogdan.nilov.netologywork.dto.FeedItem
import bogdan.nilov.netologywork.dto.Post
import bogdan.nilov.netologywork.dto.UserResponse
import bogdan.nilov.netologywork.model.AuthModel
import bogdan.nilov.netologywork.model.InvolvedItemType
import bogdan.nilov.netologywork.util.AppConst
import bogdan.nilov.netologywork.viewModel.AuthViewModel
import bogdan.nilov.netologywork.viewModel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.getValue


@AndroidEntryPoint
class PostFeedFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val postViewModel by activityViewModels<PostViewModel>()
        val authViewModel by activityViewModels<AuthViewModel>()
        val binding = FragmentFeedPostBinding.inflate(inflater, container, false)
        val parentNavController = parentFragment?.parentFragment?.findNavController()

        var token: AuthModel? = null
        authViewModel.dataAuth.observe(viewLifecycleOwner) { state ->
            token = state
        }

        val userId = arguments?.getLong(AppConst.USER_ID)

        val postAdapter = PostAdapter(object : OnInteractionListener {
            override fun like(feedItem: FeedItem) {
                if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                    postViewModel.like(feedItem as Post)
                } else {
                    parentNavController?.navigate(R.id.action_mainFragment_to_signInFragment)
                }
            }

            override fun share(content: String) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, "Share post")
                startActivity(shareIntent)
            }

            override fun delete(feedItem: FeedItem) {
                postViewModel.deletePost(feedItem as Post)
            }

            override fun edit(feedItem: FeedItem) {
                feedItem as Post
                postViewModel.edit(feedItem)
                parentNavController?.navigate(
                    R.id.action_mainFragment_to_newPostFragment,
                    bundleOf(AppConst.EDIT_POST to feedItem.content)
                )
            }

            override fun selectUser(userResponse: UserResponse) {}

            override fun openCard(feedItem: FeedItem) {
                postViewModel.openPost(feedItem as Post)
                lifecycleScope.launch {
                    postViewModel.getInvolved(feedItem.likeOwnerIds, InvolvedItemType.LIKERS)
                    postViewModel.getInvolved(feedItem.mentionIds, InvolvedItemType.MENTIONED)
                }
                parentNavController?.navigate(R.id.action_mainFragment_to_detailPostFragment)
            }
        })

        binding.recyclerViewPost.adapter = postAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.data.collectLatest {
                    if (userId != null) {
                        postAdapter.submitData(it.filter { feedItem ->
                            feedItem is Post && feedItem.authorId == userId
                        })
                    } else {
                        postAdapter.submitData(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            postAdapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing =
                    it.refresh is LoadState.Loading
                if (it.append is LoadState.Error
                    || it.prepend is LoadState.Error
                    || it.refresh is LoadState.Error
                ) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.connection_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                suspendCancellableCoroutine {
                    it.invokeOnCancellation {
                        (0..<binding.recyclerViewPost.childCount)
                            .map(binding.recyclerViewPost::getChildAt)
                            .map(binding.recyclerViewPost::getChildViewHolder)
                            .filterIsInstance<PostViewHolder>()
                            .onEach(PostViewHolder::stopPlayer)
                    }
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            postAdapter.refresh()
        }

        binding.buttonNewPost.isVisible = userId == null
        binding.buttonNewPost.setOnClickListener {
            if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                parentNavController?.navigate(R.id.action_mainFragment_to_newPostFragment)
            } else {
                parentNavController?.navigate(R.id.action_mainFragment_to_signInFragment)
            }
        }

        return binding.root
    }
}
