package com.droumaguet.projet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GuestManagementActivity: AppCompatActivity() {
    private var guests = ArrayList<String>()
    private lateinit var adapter: GuestAdapter
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_management)

        adapter = GuestAdapter(this, guests)

        getToken()
        getHouseId()

        initializeAddNewGuestButton()
        initializeGuestsList()
        initializeGoToAccessButton()
        loadGuestList()
    }

    private fun getToken(): String?
    {
        val token = intent.getStringExtra("token")
        return token
    }

    private fun getHouseId(): Int
    {
        val houseId = intent.getIntExtra("houseId", -1)
        return houseId
    }

    private fun initializeAddNewGuestButton()
    {
        val addNewGuestField = findViewById<Button>(R.id.btnAddNewGuest)
        addNewGuestField.setOnClickListener {
            addGuest()
        }
    }

    private fun addGuest()
    {
        val houseId = getHouseId()
        val guestData = getGuestData()
        Api().post<GuestData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", guestData, ::AddGuestSuccess, getToken())
    }

    private fun getGuestData(): GuestData
    {
        val loginUserField = findViewById<EditText>(R.id.txtUserLogin)
        val loginUser = loginUserField.text.toString()

        return GuestData(loginUser)
    }

    private fun AddGuestSuccess(responseCode: Int)
    {
        if(responseCode == 200) {
            runOnUiThread {
                val loginField = findViewById<EditText>(R.id.txtUserLogin)
                val login = loginField.text.toString()
                guests.add(login)
                adapter.notifyDataSetChanged()
                saveGuestsList()
            }
            Messager(this).display("Accès accordé.")
        } else if(responseCode == 403) {
            Messager(this).display("Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison).")
        } else if(responseCode == 500) {
            Messager(this).display("Une erreur s’est produite au niveau du serveur.")
        }
    }

    private fun initializeGuestsList()
    {
        val guestList = findViewById<ListView>(R.id.guestList)
        guestList.adapter = adapter
    }

    private fun saveGuestsList()
    {
        val guestStorage = GuestStorage(this)
        mainScope.launch {
            guestStorage.write(guests)
        }
    }

    private fun loadGuestList() {
        val guestStorage = GuestStorage(this)
        mainScope.launch {
            val loadedGuests = guestStorage.read()
            guests.clear()
            if (loadedGuests != null && loadedGuests.isNotEmpty()) {
                guests.addAll(loadedGuests)
            }
            adapter.notifyDataSetChanged()
        }
    }


    fun deleteGuest(guestLogin: String)
    {
        val guestStorage = GuestStorage(this)
        val houseId = getHouseId()
        val guestData = GuestData(guestLogin)
        Api().delete<GuestData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", guestData ,::onDeleteGuestSuccess, getToken())
        guests.remove(guestLogin)
        adapter.notifyDataSetChanged()
        mainScope.launch {
            guestStorage.remove(guestLogin)
        }
    }

    private fun onDeleteGuestSuccess(responseCode: Int)
    {
        if(responseCode == 200) {
            Messager(this).display("Suppression réalisée.")
        } else if(responseCode == 403) {
            Messager(this).display("Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison).")
        } else if(responseCode == 500) {
            Messager(this).display("Une erreur s’est produite au niveau du serveur.")
        }
    }

    private fun initializeGoToAccessButton()
    {
        val goToAccess = findViewById<Button>(R.id.btnQuit2)
        goToAccess.setOnClickListener {
            finish()
        }
    }
}