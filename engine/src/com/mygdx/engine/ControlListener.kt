package com.mygdx.engine

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.Character.Direction.LEFT
import com.mygdx.engine.Character.Direction.RIGHT
import com.mygdx.engine.Character.Direction.UP

internal class ControlListener(private val character: Character) : InputListener() {
    private val walkingKeys = setOf(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN)
    var keysDown: MutableSet<Int> = mutableSetOf()

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        keysDown.add(keycode)

        if (keycode in walkingKeys) {
            val direction = when (keycode) {
                Keys.UP -> UP
                Keys.LEFT -> LEFT
                Keys.DOWN -> DOWN
                Keys.RIGHT -> RIGHT
                else -> throw IllegalStateException()
            }
            character.walk(direction)
            return true
        }
        return super.keyDown(event, keycode)
    }

    override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
        keysDown.remove(keycode)
        return super.keyUp(event, keycode)
    }
}