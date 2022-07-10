package ru.apmgor.forecast.data.errors

sealed class Errors(errorMessage: String) : Throwable(errorMessage) {
    object InternetConnectionError : Errors("Неполадки с интернет подключением")
    data class APIError(val code: Int) : Errors("Ошибка удаленного API: $code")
    object GPSError : Errors("Не включен GPS")
    object LocationPermissionError : Errors("Разрешение не предоставлено")
}