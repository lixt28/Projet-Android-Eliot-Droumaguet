package com.droumaguet.projet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceRecyclerAdapter(
    private val context: Context,
    private val devices: List<DeviceData>,
    private val houseId: Int,
    private val token: String?
) : RecyclerView.Adapter<DeviceRecyclerAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceId: TextView = itemView.findViewById(R.id.deviceId)
        val deviceType: TextView = itemView.findViewById(R.id.deviceType)
        val deviceState: TextView = itemView.findViewById(R.id.deviceState)
        val buttonContainer: LinearLayout = itemView.findViewById(R.id.btnContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.device_item, parent, false)
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        holder.deviceId.text = device.id
        holder.deviceType.text = device.type

        when (device.type) {
            "garage door", "rolling shutter" -> holder.deviceState.text =
                if (device.openingMode == 0) "Ouvert" else "Fermé"
            "light" -> holder.deviceState.text =
                if (device.power == 1) "Allumée" else "Eteinte"
        }

        holder.buttonContainer.removeAllViews()
        for (command in device.availableCommands) {
            val button = Button(context).apply {
                text = command
                setOnClickListener {
                    handleCommand(device, CommandData(command), houseId, token.orEmpty())
                }
            }
            holder.buttonContainer.addView(button)
        }
    }

    override fun getItemCount(): Int = devices.size

    private fun handleCommand(device: DeviceData, command: CommandData, houseId: Int, token: String) {
        if (command.command == "OPEN") {
            commandDevice(device, command.command, houseId, token)
            device.openingMode = 0
        } else if (command.command == "CLOSE") {
            commandDevice(device, command.command, houseId, token)
            device.openingMode = 1
        } else if (command.command == "STOP") {
            commandDevice(device, command.command, houseId, token)
        } else if (command.command == "TURN ON") {
            commandDevice(device, command.command, houseId, token)
            device.power = 1
        } else if (command.command == "TURN OFF") {
            commandDevice(device, command.command, houseId, token)
            device.power = 0
        }
        notifyDataSetChanged()
    }

    private fun commandDevice(device: DeviceData, command: String, houseId: Int, token: String) {
        val deviceId = device.id
        Api().post<CommandData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", CommandData(command), ::onCommandSuccess, token)
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
