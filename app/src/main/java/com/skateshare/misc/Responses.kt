package com.skateshare.misc

open class EventResponse(val response: Int, var success: Boolean = false)
open class ExceptionResponse(
    val message: String?,
    val isSuccessful: Boolean,
    val isEnabled: Boolean = true)
