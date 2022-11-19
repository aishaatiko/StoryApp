package com.alicea.storyappsubmission.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.alicea.storyappsubmission.R
import com.alicea.storyappsubmission.data.Result
import com.alicea.storyappsubmission.databinding.ActivityLoginBinding
import com.alicea.storyappsubmission.preference.UserPreference
import com.alicea.storyappsubmission.ui.ViewModelFactory
import com.alicea.storyappsubmission.ui.main.MainActivity
import com.alicea.storyappsubmission.utils.EmailValidator.isValidEmail

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupView() {

        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[LoginViewModel::class.java]

        loginViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {

        binding.loginButton.setOnClickListener {


            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.email_empty_error)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.setError(getString(R.string.password_empty_error), null)
                }
                !isValidEmail(email) -> {
                    binding.edLoginEmail.error = getString(R.string.email_format_error)
                }
                password.length < 6 -> {
                    binding.edLoginPassword.setError(getString(R.string.password_length_error), null)
                }
                else -> {
                    loginViewModel.loginRepo(email, password).observe(this) {
                        result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {}
                                is Result.Success -> {
                                    Toast.makeText(this@LoginActivity, getString(R.string.login_message), Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                is Result.Error -> {
                                    when(result.error) {
                                        "Unable to resolve host \"story-api.dicoding.dev\": No address associated with hostname" -> {
                                            Toast.makeText(this@LoginActivity, getString(R.string.on_failure_message), Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            Toast.makeText(this@LoginActivity, getString(R.string.wrong_email_password), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, email, emailEdit, password, passwordEdit, login)
            start()
        }
    }
}