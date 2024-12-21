package com.droumaguet.projet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HouseManagementActivity : AppCompatActivity() {
    private var token: String? = ""
    private var houseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_management)

        token = intent.getStringExtra("token")
        houseId = intent.getIntExtra("houseId", -1)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_view, HomeFragment.newInstance(token ?: "", houseId))
            .commit()

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment.newInstance(token ?: "", houseId)
                R.id.nav_lightbulb -> LightFragment.newInstance(token ?: "", houseId)
                R.id.nav_shutter -> ShutterFragment.newInstance(token ?: "", houseId)
                R.id.nav_garage -> GarageFragment.newInstance(token ?: "", houseId)
                else -> HomeFragment.newInstance(token ?: "", houseId)
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.container_view, selectedFragment)
                .commit()
            true
        }
    }
}
