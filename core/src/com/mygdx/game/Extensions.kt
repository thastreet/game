package com.mygdx.game

import com.badlogic.gdx.Input.Keys
import com.mygdx.engine.Character
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.Character.Direction.LEFT
import com.mygdx.engine.Character.Direction.RIGHT
import com.mygdx.engine.Character.Direction.UP

val Int.asDirection: Character.Direction?
    get() = when (this) {
        Keys.UP -> UP
        Keys.LEFT -> LEFT
        Keys.DOWN -> DOWN
        Keys.RIGHT -> RIGHT
        else -> null
    }