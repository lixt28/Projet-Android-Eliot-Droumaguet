package com.droumaguet.projet

data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int? = null,
    var openingMode: Int? = null,
    var power: Int? = null
)