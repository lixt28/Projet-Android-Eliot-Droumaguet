package com.droumaguet.projet


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeRegisterButton()
        initializeGoToLoginButton()
    }

    private fun initializeRegisterButton()
    {
        val registerButton = findViewById<Button>(R.id.btnRegister)

        registerButton.setOnClickListener {
            register()
        }
    }

    private fun register()
    {
        val registerData = getRegisteringData()
        Api().post<RegisterData>("https://polyhome.lesmoulinsdudev.com/api/users/register", registerData, ::registerSuccess)
    }

    private fun getRegisteringData(): RegisterData
    {
        val loginField = findViewById<EditText>(R.id.txtRegisterLogin)
        val passwordField = findViewById<EditText>(R.id.txtRegisterPassword)

        val login = loginField.text.toString()
        val password = passwordField.text.toString()

        return RegisterData(login, password)
    }

    private fun registerSuccess(responseCode: Int)
    {
        if(responseCode == 200) {
            Messager(this).display("Le compte a bien été créé.")
            finish()
        } else if(responseCode == 409) {
            Messager(this).display("Le login est déjà utilisé par un autre compte.")
        } else if(responseCode == 500) {
            Messager(this).display("Une erreur s’est produite au niveau du serveur.")
        }
    }

    private fun initializeGoToLoginButton()
    {
        val goToLoginButton = findViewById<Button>(R.id.btnGoToLogin)

        goToLoginButton.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToLogin()
    {
        finish();
    }
}