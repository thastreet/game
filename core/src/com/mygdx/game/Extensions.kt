package com.mygdx.game

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP

fun Actor.setPosition(position: Vector2) =
    setPosition(position.x, position.y)

val Actor.position: Vector2
    get() = Vector2(x, y)

val Int.asDirection: Character.Direction?
    get() = when (this) {
        Keys.UP -> UP
        Keys.LEFT -> LEFT
        Keys.DOWN -> DOWN
        Keys.RIGHT -> RIGHT
        else -> null
    }