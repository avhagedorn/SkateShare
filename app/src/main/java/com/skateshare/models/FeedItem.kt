package com.skateshare.models

import com.google.firebase.Timestamp
import java.util.*

open class FeedItem() {
    open var description: String = ""
    open var datePosted: Timestamp = Timestamp.now()
    open var isCurrentUser: Boolean = false
    open var posterId: String = ""
    open var postProfilePictureUrl: String = ""
    open var posterUsername: String = ""
}
class LoadingItem : FeedItem()