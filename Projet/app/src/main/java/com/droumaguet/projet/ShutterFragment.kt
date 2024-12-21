package com.droumaguet.projet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShutterFragment : Fragment() {
    private var token: String? = ""
    private var houseId: Int = -1

    private var shutterDevices: ArrayList<DeviceData> = ArrayList()
    private lateinit var shutterAdapter: DeviceRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString("token")
            houseId = it.getInt("houseId")
        }

        getDevices()
    }

    companion object {
        fun newInstance(token: String, houseId: Int): ShutterFragment {
            val fragment = ShutterFragment()
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
        val view = inflater.inflate(R.layout.fragment_shutter, container, false)

        initializeDevicesList(view)
        initializeQuitButton(view)

        return view
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
                shutterDevices.clear()
                shutterDevices.addAll(devicesResponse.devices.filter { it.type == "rolling shutter" })

                shutterAdapter.notifyDataSetChanged()
            }
            //Messager(requireActivity()).display("Requête acceptée.")
        } else if (responseCode == 403) {
            //Messager(requireActivity()).display("Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison ou à un tiers ayant accès).")
        } else if (responseCode == 500) {
            //Messager(requireActivity()).display("Une erreur s’est produite au niveau du serveur.")
        }
    }

    private fun initializeDevicesList(view: View) {
        val shutterRecyclerField = view.findViewById<RecyclerView>(R.id.shutterRecyclerView)
        shutterRecyclerField.layoutManager = LinearLayoutManager(requireContext())

        shutterAdapter = DeviceRecyclerAdapter(
            requireContext(),
            shutterDevices,
            houseId,
            token
        )
        shutterRecyclerField.adapter = shutterAdapter
    }

    private fun initializeQuitButton(view: View) {
        val quitField = view.findViewById<Button>(R.id.btnQuit4)
        quitField.setOnClickListener {
            activity?.finish()
        }
    }
}