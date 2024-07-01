package com.mygdx.engine

import com.badlogic.gdx.math.Rectangle

interface CollisionHolder {
    fun Collision.Dynamic.canMove(hitBox: Rectangle): Boolean
}