package com.droumaguet.projet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeLoginButton();
        initializeGoToRegister();
    }

    private fun initializeLoginButton(){
        val goToLoginField = findViewById<Button>(R.id.btnConnect)
        goToLoginField.setOnClickListener {
            login()
        }
    }

    private fun login()
    {
        val loginData = getLoginData()
        Api().post<LoginData, TokenData>("https://polyhome.lesmoulinsdudev.com/api/users/auth", loginData, ::loginSuccess)
    }

    private fun getLoginData(): LoginData
    {
        val loginField = findViewById<EditText>(R.id.txtLogin)
        val passwordField = findViewById<EditText>(R.id.txtPassword)

        val login = loginField.text.toString()
        val password = passwordField.text.toString()

        return LoginData(login, password)
    }

    private fun loginSuccess(responseCode: Int, token: TokenData?)
    {
        if(responseCode == 200 && token != null) {

            val intentToHouseAccessActivity = Intent(
                this,
                HouseAccessActivity::class.java
            );
            intentToHouseAccessActivity.putExtra("token", token.token);
            startActivity(intentToHouseAccessActivity);

            Messager(this).display("Connexion réussie.")

        } else if(responseCode == 404) {
            Messager(this).display("Aucun utilisateur ne correspond aux identifiants donnés.")
        } else if(responseCode == 500) {
            Messager(this).display("Une erreur s’est produite au niveau du serveur.")
        }
    }

    private fun initializeGoToRegister()
    {
        val goToRegisterField = findViewById<Button>(R.id.btnGoToRegister)
        goToRegisterField.setOnClickListener {
            goToRegister()
        }
    }

    private fun goToRegister()
    {
        val intentToRegisterActivity = Intent(
            this,
            RegisterActivity::class.java);
        startActivity(intentToRegisterActivity);
    }
}