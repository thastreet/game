package com.mygdx.engine

import com.badlogic.gdx.math.Rectangle

sealed interface Collision {
    val hitBox: Rectangle

    data class Static(override val hitBox: Rectangle) : Collision

    interface Dynamic : Collision {
        val id: String
    }
}