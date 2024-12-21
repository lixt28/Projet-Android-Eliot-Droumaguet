package com.droumaguet.projet


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class HouseAccessActivity : AppCompatActivity() {
    private var houseId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_access)

        getToken()
        getHouseId()
        initializeGoToHouseManagementButton()
        initializeManageGuestButton()
        initializeManageAsGuest()
    }

    private fun getToken(): String?
    {
        val token = intent.getStringExtra("token")
        return token
    }

    private fun getHouseId()
    {
        Api().get<List<HouseData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::getHouseIdSuccess, getToken())
    }

    private fun getHouseIdSuccess(responseCode: Int, house: List<HouseData>?)
    {
        if(responseCode == 200 && house != null) {
            val myHouse = house[0]
            this.houseId = myHouse.houseId
            val isOwner = myHouse.owner
            if (isOwner) {
                Messager(this).display("Requête acceptée. Vous êtes propriétaire de la maison $houseId.")
                findViewById<TextView>(R.id.txtId).text = this.houseId.toString()
            }
        } else if(responseCode == 403) {
            Messager(this).display("Accès interdit (token invalide).")
        } else if(responseCode == 500) {
            Messager(this).display("Une erreur s’est produite au niveau du serveur.")
        }
    }

    private fun initializeGoToHouseManagementButton()
    {
        val goToHouseManagementField = findViewById<Button>(R.id.btnGoToManagement)
        goToHouseManagementField.setOnClickListener {
            goToHouseManagement(this.houseId)
        }
    }

    private fun goToHouseManagement(houseId: Int)
    {
        val intentToHouseManagementActivity = Intent(
            this,
            HouseManagementActivity::class.java
        );

        intentToHouseManagementActivity.putExtra("token", getToken());
        intentToHouseManagementActivity.putExtra("houseId", houseId);
        startActivity(intentToHouseManagementActivity);
    }

    private fun initializeManageGuestButton()
    {
        val addGuestField = findViewById<Button>(R.id.btnManageGuest)
        addGuestField.setOnClickListener {
            goToManageGuest()
        }
    }

    private fun goToManageGuest()
    {
        val intentToGuestManagementActivity = Intent(
            this,
            GuestManagementActivity::class.java
        );
        intentToGuestManagementActivity.putExtra("token", getToken());
        intentToGuestManagementActivity.putExtra("houseId", houseId);
        startActivity(intentToGuestManagementActivity);
    }

    private fun initializeManageAsGuest()
    {
        val manageAsGuestField = findViewById<Button>(R.id.btnManageAsGuest)
        val houseIdField = findViewById<EditText>(R.id.txtHouseId)

        manageAsGuestField.setOnClickListener {
            val houseIdText = houseIdField.text.toString()
            val houseId = houseIdText.toIntOrNull()

            if (houseId != null) {
                goToHouseManagement(houseId)
            } else {
                Messager(this).display("Problème détecté. Veuillez choisir un ID valide.")
            }
        }
    }
}