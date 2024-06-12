package com.somaiya.summer_project.login.Model

data class LoginResponse(
    var loading:Boolean = false,
    var success:Boolean = false,
    var logindata: LoginData? = null
)