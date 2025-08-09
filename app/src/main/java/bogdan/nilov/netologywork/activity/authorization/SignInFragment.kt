package bogdan.nilov.netologywork.activity.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import bogdan.nilov.netologywork.R
import bogdan.nilov.netologywork.databinding.FragmentSignInBinding
import bogdan.nilov.netologywork.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignInFragment : Fragment() {


    private var login = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val authViewModel by activityViewModels<AuthViewModel>()

        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.loginTextField.addTextChangedListener {
            login = it.toString()
            binding.apply {
                loginLayout.error = null
                buttonSignIn.isChecked = updateStateButtonLogin()
            }
        }
        binding.passwordTextField.addTextChangedListener {
            password = it.toString()
            binding.apply {
                passwordLayout.error = null
                buttonSignIn.isChecked = updateStateButtonLogin()
            }
        }

        binding.buttonSignIn.setOnClickListener {
            when {
                password.isEmpty() && login.isEmpty() -> {
                    binding.apply {
                        loginLayout.error = getString(R.string.empty_login)
                        passwordLayout.error = getString(R.string.passwords_is_empty)
                    }
                }

                password.isEmpty() -> {
                    binding.passwordLayout.error = getString(R.string.passwords_is_empty)
                }

                login.isEmpty() -> {
                    binding.loginLayout.error = getString(R.string.empty_login)
                }

                else -> {
                    authViewModel.login(login, password)
                }
            }
        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        authViewModel.dataAuth.observe(viewLifecycleOwner) { state ->
            val token = state.token.toString()

            if (state.id != 0L && token.isNotEmpty()) {
                findNavController().navigateUp()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun updateStateButtonLogin(): Boolean {
        return login.isNotEmpty() && password.isNotEmpty()
    }


}