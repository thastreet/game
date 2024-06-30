package com.mygdx.engine

import com.badlogic.gdx.math.Rectangle

interface CollisionHolder {
    fun Collision.canMove(hitBox: Rectangle): Boolean
}