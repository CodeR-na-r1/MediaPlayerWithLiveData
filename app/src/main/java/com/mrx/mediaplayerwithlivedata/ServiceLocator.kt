package com.mrx.mediaplayerwithlivedata

import android.content.Context

object ServiceLocator {
    private var myMediaPlayerService: MyMediaPlayerService? = null

    fun getMediaPlayerService(__context: Context): MyMediaPlayerService {
        if (myMediaPlayerService == null) {
            myMediaPlayerService = MyMediaPlayerService(__context)
        }

        return myMediaPlayerService!!
    }
}