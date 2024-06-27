package com.mygdx.game

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

class Player(initialPosition: Vector2, private val canMove: (Character, Rectangle) -> Boolean) : Character() {
    private val img = Texture("RSE Protags 01.png")
    private var direction: Int? = null

    override val sprite = Sprite(img, 24, 0, 24, 32)

    init {
        setPosition(initialPosition)

        addListener(object : InputListener() {
            override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                if (keycode in setOf(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN)) {
                    direction = keycode
                    return true
                }
                return super.keyDown(event, keycode)
            }

            override fun keyUp(event: InputEvent, keycode: Int): Boolean {
                if (direction == keycode) {
                    direction = null
                    return true
                }
                return super.keyUp(event, keycode)
            }
        })
    }

    override val hitBox: Rectangle
        get() = calculateHitBox(Vector2(x, y))

    private fun calculateHitBox(position: Vector2): Rectangle =
        Rectangle(position.x + sprite.width / 4, position.y + 2, sprite.width / 2, sprite.height / 3)

    override fun act(delta: Float) {
        super.act(delta)
        direction?.let {
            val targetPosition = Vector2(
                x + delta * when (direction) {
                    Keys.RIGHT -> MOVEMENT_DISTANCE
                    Keys.LEFT -> -MOVEMENT_DISTANCE
                    else -> 0
                }, y + delta * when (direction) {
                    Keys.UP -> MOVEMENT_DISTANCE
                    Keys.DOWN -> -MOVEMENT_DISTANCE
                    else -> 0
                }
            )

            if (canMove(this, calculateHitBox(targetPosition))) {
                setPosition(targetPosition)
            }
        }
    }

    companion object {
        private const val MOVEMENT_DISTANCE = 16
    }
}