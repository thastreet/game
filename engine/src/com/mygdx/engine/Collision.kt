package com.mygdx.engine

import com.badlogic.gdx.math.Rectangle

interface Collision {
    val id: String
    val hitBox: Rectangle
}