package com.skateshare.models

import java.util.*
import kotlin.random.Random

open class SimpleFeedItem(
    open val id: Long = 0L,
    open val item_type: Int = 0
)

class SimpleLoadingItem(
    override val id: Long = Random.nextLong(),
    override val item_type: Int = 1,
) : SimpleFeedItem()