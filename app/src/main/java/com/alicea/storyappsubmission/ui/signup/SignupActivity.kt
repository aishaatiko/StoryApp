package com.alicea.storyappsubmission.ui.signup

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
import com.alicea.storyappsubmission.databinding.ActivitySignupBinding
import com.alicea.storyappsubmission.preference.UserModel
import com.alicea.storyappsubmission.preference.UserPreference
import com.alicea.storyappsubmission.ui.ViewModelFactory
import com.alicea.storyappsubmission.ui.welcome.WelcomeActivity
import com.alicea.storyappsubmission.utils.EmailValidator.isValidEmail

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[SignupViewModel::class.java]

        signupViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            binding.nameEditTextLayout.isErrorEnabled = false
            binding.emailEditTextLayout.isErrorEnabled = false

            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = getString(R.string.name_empty_error)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.email_empty_error)
                }
                !isValidEmail(email) -> {
                    binding.edRegisterEmail.error = getString(R.string.email_format_error)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.setError(getString(R.string.password_empty_error), null)
                }
                password.length < 6 -> {
                    binding.edRegisterPassword.setError(getString(R.string.password_length_error), null)
                }
                else -> {
                    signupViewModel.registerRepo(UserModel(name, email, password)).observe(this) {
                        result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {}
                                is Result.Success -> {
                                    Toast.makeText(this@SignupActivity, getString(R.string.signup_message_popup), Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignupActivity, WelcomeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                                is Result.Error -> {
                                    when(result.error) {
                                        "Unable to resolve host \"story-api.dicoding.dev\": No address associated with hostname" -> {
                                            Toast.makeText(this@SignupActivity, getString(R.string.on_failure_message), Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            binding.emailEditTextLayout.error = getString(R.string.email_taken_error)
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
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, nameEdit, email, emailEdit, password, passwordEdit, signup)
            start()
        }
    }
}