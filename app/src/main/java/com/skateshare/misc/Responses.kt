package com.skateshare.misc

open class EventResponse(val response: Int, var success: Boolean = false)
open class ExceptionResponse(val status: String?, val success: Boolean)
open class RecyclerItemResponse(val viewIndex: Int, val message: String?, val enabled: Boolean)