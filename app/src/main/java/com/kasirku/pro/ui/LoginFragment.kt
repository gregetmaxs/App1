package com.kasirku.pro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kasirku.pro.R
import com.kasirku.pro.databinding.FragmentLoginBinding
import com.kasirku.pro.util.SessionManager
import com.kasirku.pro.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        if (sessionManager.isLoggedIn()) {
            navigateAfterLogin()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                binding.tvError.text = "Email dan password wajib diisi"
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
            binding.tvError.visibility = View.GONE

            viewModel.login(email, password)
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true

            result.onSuccess {
                Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                navigateAfterLogin()
            }

            result.onFailure { e ->
                binding.tvError.text = e.message
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateAfterLogin() {
        if (sessionManager.canAccessPos() || sessionManager.canAccessSettings() || sessionManager.canAccessReport()) {
            findNavController().navigate(R.id.action_login_to_dashboard)
        } else if (sessionManager.canAccessDelivery()) {
            findNavController().navigate(R.id.action_login_to_delivery)
        } else {
            findNavController().navigate(R.id.action_login_to_dashboard)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
