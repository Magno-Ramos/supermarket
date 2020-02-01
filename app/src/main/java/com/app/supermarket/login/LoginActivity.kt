package com.app.supermarket.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.app.supermarket.MarketApplication
import com.app.supermarket.R
import com.app.supermarket.main.MainActivity
import com.app.supermarket.model.User
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: LoginViewModel

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MarketApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel.getUserLiveData().observe(this, Observer { user ->
            if (user != null) {
                openMainActivity(user)
            }
        })

        viewModel.getSignInState().observe(this, Observer { state ->
            state?.let {
                when {
                    it.isLoading() -> showProgress()
                    it.isSuccess() -> hideProgress()
                    it.isError() -> {
                        hideProgress()
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        btn_sign.setOnClickListener {
            clearErrors()

            val email = edt_email?.text
            val pass = edt_password?.text

            when {
                email.isNullOrBlank() -> {
                    edt_email?.error = getString(R.string.error_email)
                    edt_email?.requestFocus()
                }
                pass.isNullOrBlank() -> {
                    edt_password?.error = getString(R.string.error_password)
                    edt_password?.requestFocus()
                }
                else -> viewModel.signIn(email.toString(), pass.toString())
            }
        }
    }

    private fun clearErrors() {
        edt_email?.error = null
        edt_password?.error = null
    }

    private fun openMainActivity(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.INTENT_KEY_USER, user)
        startActivity(intent)
    }

    private fun hideProgress() {
        dialog?.dismiss()
    }

    private fun showProgress() {
        dialog = AlertDialog.Builder(this)
            .setTitle("")
            .setMessage(R.string.loading)
            .show()
    }
}