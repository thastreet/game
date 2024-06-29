package com.mygdx.game

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP

class Player(initialPosition: Vector2, private val onPositionChanged: Player.() -> Unit, canMove: Character.(Rectangle) -> Boolean) : Character(canMove) {
    private val img = Texture("RSE Protags 01.png")
    private var keyDown: Int? = null

    private var walkAnimation: Animation<TextureRegion>? = null
    private var walkAnimationTime = 0f
    private var walking = false

    override val idleSprites = mapOf(
        UP to Sprite(img, 24, 96, 24, 32),
        LEFT to Sprite(img, 24, 32, 24, 32),
        DOWN to Sprite(img, 24, 0, 24, 32),
        RIGHT to Sprite(img, 24, 64, 24, 32),
    )

    private val walkFrames = mapOf(
        UP to TextureRegion(img, 0, 96, 3 * 24, 1 * 32).split(24, 32)[0],
        LEFT to TextureRegion(img, 0, 32, 3 * 24, 1 * 32).split(24, 32)[0],
        DOWN to TextureRegion(img, 0, 0, 3 * 24, 1 * 32).split(24, 32)[0],
        RIGHT to TextureRegion(img, 0, 64, 3 * 24, 1 * 32).split(24, 32)[0],
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
                    walking = true
                    walkAnimation = Animation(0.2f, com.badlogic.gdx.utils.Array(walkFrames[direction]), LOOP_PINGPONG)
                    return true
                }
                return super.keyDown(event, keycode)
            }

            override fun keyUp(event: InputEvent, keycode: Int): Boolean {
                if (keyDown == keycode) {
                    keyDown = null
                    walking = false
                    walkAnimationTime = 0f
                    return true
                }
                return super.keyUp(event, keycode)
            }
        })
    }

    override val hitBox: Rectangle
        get() = calculateHitBox(position)

    private fun calculateHitBox(position: Vector2): Rectangle =
        Rectangle(position.x + sprite.width / 2 - 5, position.y + 2, 10f, 10f)

    override fun act(delta: Float) {
        super.act(delta)

        walkAnimationTime += delta

        keyDown?.let { key ->
            key.asDirection?.let { direction ->
                val targetPosition = calculateWalkTargetPosition(delta, direction)

                if (canMove(calculateHitBox(targetPosition))) {
                    setPosition(targetPosition)
                }
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val localWalkAnimation = walkAnimation

        if (walking && localWalkAnimation != null) {
            val currentFrame = localWalkAnimation.getKeyFrame(walkAnimationTime, true)
            batch.draw(currentFrame, x, y)
        } else {
            batch.draw(sprite, x, y)
        }
    }

    override fun positionChanged() = onPositionChanged()
}