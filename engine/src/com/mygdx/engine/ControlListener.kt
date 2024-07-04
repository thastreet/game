package com.mygdx.engine

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

internal class ControlListener(private val character: Character) : InputListener() {
    private val walkingKeys = setOf(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN)
    private var keysDown: MutableSet<Int> = mutableSetOf()

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        keysDown.add(keycode)

        if (keycode in walkingKeys) {
            character.onWalkingKeysChanged(keysDown)
            return true
        }
        return super.keyDown(event, keycode)
    }

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        keysDown.remove(keycode)

        if (keycode in walkingKeys) {
            character.onWalkingKeysChanged(keysDown)
            return true
        }
        return super.keyUp(event, keycode)
    }
}