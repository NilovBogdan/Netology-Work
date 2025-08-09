package bogdan.nilov.netologywork.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import bogdan.nilov.netologywork.R
import bogdan.nilov.netologywork.databinding.FragmentMainBinding
import bogdan.nilov.netologywork.model.AuthModel
import bogdan.nilov.netologywork.util.AppConst
import bogdan.nilov.netologywork.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

        var token: AuthModel? = null
        authViewModel.dataAuth.observe(viewLifecycleOwner) { state ->
            token = state
        }


        val childNavHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val childNavController = childNavHostFragment.navController
        binding.bottomNavigation.setupWithNavController(childNavController)

        binding.topAppBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.user -> {
                    if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                        findNavController().navigate(
                            R.id.action_mainFragment_to_detailUserFragment,
                            bundleOf(AppConst.USER_ID to token?.id)
                        )
                    } else {
                        findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
                    }
                    true
                }

                else -> false
            }
        }

        return binding.root
    }


}