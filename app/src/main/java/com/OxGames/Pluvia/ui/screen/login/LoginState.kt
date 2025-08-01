package com.OxGames.Pluvia.ui.screen.login

import com.OxGames.Pluvia.enums.LoginResult
import com.OxGames.Pluvia.enums.LoginScreen

data class LoginState(
    val username: String = "",
    val password: String = "",
    val rememberSession: Boolean = false,
    val twoFactorCode: String = "",

    val isSteamConnected: Boolean = false,
    val isLoggingIn: Boolean = false,

    val loginResult: LoginResult = LoginResult.Failed,
    val loginScreen: LoginScreen = LoginScreen.CREDENTIAL,

    val previousCodeIncorrect: Boolean = false,

    val email: String? = null,

    val qrCode: String? = null,
    val isQrFailed: Boolean = false,
) {
    override fun toString(): String {
        return "UserLoginState(" +
            "username='$username', " +
            "password='$password', " +
            "rememberSession=$rememberSession, " +
            "twoFactorCode='$twoFactorCode', " +
            "isSteamConnected=$isSteamConnected, " +
            "isLoggingIn=$isLoggingIn, " +
            "loginResult=$loginResult, " +
            "loginScreen=$loginScreen, " +
            "previousCodeIncorrect=$previousCodeIncorrect, " +
            "email=$email, " +
            "qrCode=$qrCode, " +
            "isQrFailed=$isQrFailed" +
            ")"
    }
}
