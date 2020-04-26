package com.dy.networkdisk.api.dto.email

data class RemoteLoginEmailDTO(
        var email: String,
        var nickname: String,
        var lastLocation: String,
        var thisLocation: String,
        var loginIP: String
)