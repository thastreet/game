package com.mygdx.game

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP

class Player(initialPosition: Vector2, private val canMove: (Character, Rectangle) -> Boolean) : Character() {
    private val img = Texture("RSE Protags 01.png")
    private var keyDown: Int? = null

    override val idleSprites = IdleSprites(
        up = Sprite(img, 24, 96, 24, 32),
        left = Sprite(img, 24, 32, 24, 32),
        down = Sprite(img, 24, 0, 24, 32),
        right = Sprite(img, 24, 64, 24, 32),
    )

    init {
        setPosition(initialPosition)

        addListener(object : InputListener() {
            override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                if (keycode in setOf(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN)) {
                    keyDown = keycode
                    direction = when (keycode) {
                        Keys.UP -> UP
                        Keys.LEFT -> LEFT
                        Keys.DOWN -> DOWN
                        Keys.RIGHT -> RIGHT
                        else -> throw IllegalStateException()
                    }
                    return true
                }
                return super.keyDown(event, keycode)
            }

            override fun keyUp(event: InputEvent, keycode: Int): Boolean {
                if (keyDown == keycode) {
                    keyDown = null
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
        keyDown?.let {
            val targetPosition = Vector2(
                x + delta * when (it) {
                    Keys.RIGHT -> MOVEMENT_DISTANCE
                    Keys.LEFT -> -MOVEMENT_DISTANCE
                    else -> 0
                }, y + delta * when (it) {
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