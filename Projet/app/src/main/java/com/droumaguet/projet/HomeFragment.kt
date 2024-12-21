package com.droumaguet.projet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    private var token: String? = ""
    private var houseId: Int = -1

    private var allDevices: ArrayList<DeviceData> = ArrayList()
    private lateinit var deviceAdapter: DeviceRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString("token")
            houseId = it.getInt("houseId")
        }

        getDevices()
    }

    companion object {
        fun newInstance(token: String, houseId: Int): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("token", token)
            args.putInt("houseId", houseId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initializeCloseShuttersButton(view)
        initializeOpenShuttersButton(view)
        initializeTurnOffLightsButton(view)
        initializeTurnOnLightsButton(view)
        initializeTurnOnAllButton(view)
        initializeTurnOffAllButton(view)
        initializeQuitButton(view)
        initializeDevicesList(view)

        return view
    }

    private fun initializeDevicesList(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.deviceRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        deviceAdapter = DeviceRecyclerAdapter(
            requireContext(),
            allDevices,
            houseId,
            token
        )
        recyclerView.adapter = deviceAdapter
    }

    private fun getDevices() {
        val token = token
        val houseId = houseId

        Api().get<DeviceResponseData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::onDevicesSuccess,
            token
        )
    }

    private fun onDevicesSuccess(responseCode: Int, devicesResponse: DeviceResponseData?) {
        if (responseCode == 200 && devicesResponse?.devices != null) {
            activity?.runOnUiThread {
                allDevices.clear()
                allDevices.addAll(devicesResponse.devices)

                deviceAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initializeCloseShuttersButton(view: View) {
        val closeShuttersButton = view.findViewById<Button>(R.id.btnCloseShutters)
        closeShuttersButton.setOnClickListener { closeShutters() }
    }

    private fun initializeOpenShuttersButton(view: View) {
        val openShuttersButton = view.findViewById<Button>(R.id.btnOpenShutters)
        openShuttersButton.setOnClickListener { openShutters() }
    }

    private fun initializeTurnOffLightsButton(view: View) {
        val turnOffLightsButton = view.findViewById<Button>(R.id.btnTurnOffLights)
        turnOffLightsButton.setOnClickListener { TurnOffLights() }
    }

    private fun initializeTurnOnLightsButton(view: View) {
        val turnOnLightsButton = view.findViewById<Button>(R.id.btnTurnOnLights)
        turnOnLightsButton.setOnClickListener { TurnOnLights() }
    }

    private fun initializeTurnOnAllButton(view: View) {
        val turnOnAllButton = view.findViewById<Button>(R.id.btnTurnOnAll)
        turnOnAllButton.setOnClickListener { TurnOnAll() }
    }

    private fun initializeTurnOffAllButton(view: View) {
        val turnOffAllButton= view.findViewById<Button>(R.id.btnTurnOffAll)
        turnOffAllButton.setOnClickListener { TurnOffAll() }
    }

    private fun initializeQuitButton(view: View) {
        val quitButton = view.findViewById<Button>(R.id.btnQuit)
        quitButton.setOnClickListener {
            activity?.finish()
        }
    }

    private fun closeShutters() {
        for (device in allDevices) {
            if (device.type == "rolling shutter" || device.type == "garage door") {
                commandDevice(device, "CLOSE")
            }
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun openShutters() {
        for (device in allDevices) {
            if (device.type == "rolling shutter" || device.type == "garage door") {
                commandDevice(device, "OPEN")
            }
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun TurnOffLights() {
        for (device in allDevices) {
            if (device.type == "light") {
                commandDevice(device, "TURN OFF")
            }
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun TurnOnLights() {
        for (device in allDevices) {
            if (device.type == "light") {
                commandDevice(device, "TURN ON")
            }
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun TurnOnAll() {
        for (device in allDevices) {
            if (device.type == "rolling shutter" || device.type == "garage door") {
                commandDevice(device, "OPEN")
            }
            if (device.type == "light") {
                commandDevice(device, "TURN ON")
            }
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun TurnOffAll() {
        for (device in allDevices) {
            if (device.type == "rolling shutter" || device.type == "garage door") {
                commandDevice(device, "CLOSE")
            }
            if (device.type == "light") {
                commandDevice(device, "TURN OFF")
            }
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun commandDevice(device: DeviceData, command: String) {
        Api().post<CommandData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/${device.id}/command",
            CommandData(command),
            ::onCommandSuccess,
            token.orEmpty()
        )
    }

    private fun onCommandSuccess(responseCode: Int) {
        if(responseCode == 200) {
            //Messager(AppCompatActivity()).display("Requête acceptée.")
        } else if(responseCode == 403) {
            //Messager(AppCompatActivity()).display("Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison ou à un tiers ayant accès).")
        } else if(responseCode == 500) {
            //Messager(AppCompatActivity()).display("Une erreur s’est produite au niveau du serveur.")
        }
    }
}
