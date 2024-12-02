package com.capstone.viziaproject.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.viziaproject.R
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.databinding.ActivityLoginBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.home.HomeFragment
import com.capstone.viziaproject.helper.Result
import com.capstone.viziaproject.ui.main.MainActivity
import com.capstone.viziaproject.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupPasswordToggle()
    }

    private fun setupAction() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showErrorDialog("Email atau password tidak boleh kosong.")
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorDialog("Email tidak valid.")
                return@setOnClickListener
            }
            if (password.length < 8) {
                showErrorDialog("Password harus memiliki setidaknya 8 karakter.")
                return@setOnClickListener
            }

            viewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val response = result.data
                        if (response.status == "success") {
                            response.data.let {
                                viewModel.saveSession(UserModel(email, it.token, true))
                                showSuccessDialog(it.name)
                            }
                        } else {
                            showErrorDialog(response.message)
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showErrorDialog("Kesalahan tidak terduga")
                    }
                }
            }
        }
        binding.textView2.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }
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

    private fun showSuccessDialog(name: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Selamat!")
            setMessage("Anda berhasil login, $name.")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
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
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
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
}