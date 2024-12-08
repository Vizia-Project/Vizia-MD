package com.capstone.viziaproject.ui.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityLoginBinding
import com.capstone.viziaproject.databinding.ActivityRegisterBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.helper.Result
import com.capstone.viziaproject.ui.login.LoginActivity
import com.capstone.viziaproject.ui.login.LoginViewModel
import com.capstone.viziaproject.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private val signupViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupPasswordToggle()
        setupPasswordToggle2()
    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.buttonSignup.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val passwordConfirmation = binding.editTextPassword2.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank() || passwordConfirmation.isBlank()) {
                showErrorDialog("Harap isi semua kolom.")
                return@setOnClickListener
            }

            if (password != passwordConfirmation) {
                showErrorDialog("Password dan Konfirmasi Password tidak sesuai.")
                return@setOnClickListener
            }

            signupViewModel.signup(name, email, password, passwordConfirmation).observe(this, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val response = result.data
                        if (response.status == "success") {
                            showSuccessDialog(email)
                        } else {
                            showErrorDialog(response.message ?: "Terjadi kesalahan.")
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showErrorDialog(result.error ?: "Kesalahan tidak terduga.")
                    }

                    else -> Log.d("cekcek", "Error Register Activity")
                }
            })
        }

        binding.textView2.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Success!")
            setMessage("Account with $email has been created. Please log in.")
            setPositiveButton("Proceed") { _, _ ->
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun setupPasswordToggle() {
        var isPasswordVisible = false
        binding.iconEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.editTextPassword.transformationMethod = null
                binding.iconEye.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
            } else {
                binding.editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.iconEye.setColorFilter(ContextCompat.getColor(this, R.color.gray))
            }
            binding.editTextPassword.text?.let { it1 -> binding.editTextPassword.setSelection(it1.length) }
        }
    }

    private fun setupPasswordToggle2() {
        var isPasswordVisible = false
        binding.iconEye2.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.editTextPassword2.transformationMethod = null
                binding.iconEye2.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
            } else {
                binding.editTextPassword2.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.iconEye2.setColorFilter(ContextCompat.getColor(this, R.color.gray))
            }
            binding.editTextPassword2.text?.let { it1 -> binding.editTextPassword2.setSelection(it1.length) }
        }
    }

}