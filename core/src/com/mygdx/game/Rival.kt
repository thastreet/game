package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class Rival(initialPosition: Vector2, canMove: Character.(Rectangle) -> Boolean) : Character(canMove) {
    private val img = Texture("RSE Protags 02.png")

    override val idleSprites = mapOf(
        UP to Sprite(img, 24, 96, 24, 32),
        LEFT to Sprite(img, 24, 32, 24, 32),
        DOWN to Sprite(img, 24, 0, 24, 32),
        RIGHT to Sprite(img, 24, 64, 24, 32),
    )

    private var walkAnimation: Animation<TextureRegion>? = null
    private var walkAnimationTime = 0f
    private var walking = false

    private val walkFrames = mapOf(
        UP to TextureRegion(img, 0, 96, 3 * 24, 1 * 32).split(24, 32)[0],
        LEFT to TextureRegion(img, 0, 32, 3 * 24, 1 * 32).split(24, 32)[0],
        DOWN to TextureRegion(img, 0, 0, 3 * 24, 1 * 32).split(24, 32)[0],
        RIGHT to TextureRegion(img, 0, 64, 3 * 24, 1 * 32).split(24, 32)[0],
    )

    private var targetWalkPosition: Vector2? = null

    init {
        setPosition(initialPosition)

        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(2000)
                walk(Direction.entries.random())
            }
        }
    }

    private fun walk(direction: Direction) {
        if (targetWalkPosition != null) return

        this.direction = direction

        walking = true
        walkAnimation = Animation(0.2f, com.badlogic.gdx.utils.Array(walkFrames[direction]), LOOP_PINGPONG)

        targetWalkPosition = Vector2(
            x + when (direction) {
                LEFT -> -MOVEMENT_DISTANCE
                RIGHT -> MOVEMENT_DISTANCE
                else -> 0
            },
            y + when (direction) {
                DOWN -> -MOVEMENT_DISTANCE
                UP -> MOVEMENT_DISTANCE
                else -> 0
            }
        )
    }

    override val hitBox: Rectangle
        get() = Rectangle(x + sprite.width / 2 - 5, y + 2, 10f, 10f)

    override fun act(delta: Float) {
        super.act(delta)

        targetWalkPosition?.let { walk(delta, it) }
    }

    private fun walk(delta: Float, targetPosition: Vector2) {
        val direction = when {
            targetPosition.y > y -> UP
            targetPosition.y < y -> DOWN
            targetPosition.x > x -> RIGHT
            targetPosition.x < x -> LEFT
            else -> return
        }

        val walkDeltaPosition = calculateWalkDeltaPosition(delta, direction)

        if (!canMove(calculateHitBox(walkDeltaPosition))) {
            println("Can't move!")
            stopWalking()
            return
        }

        walkAnimationTime += delta

        val walkDeltaDiff: Float
        val availableDiff: Float
        when (direction) {
            UP, DOWN -> {
                walkDeltaDiff = walkDeltaPosition.y - position.y
                availableDiff = targetPosition.y - y
            }

            LEFT, RIGHT -> {
                walkDeltaDiff = walkDeltaPosition.x - position.x
                availableDiff = targetPosition.x - x
            }
        }

        val coercedDiff = when (direction) {
            RIGHT, UP -> min(walkDeltaDiff, availableDiff)
            LEFT, DOWN -> max(walkDeltaDiff, availableDiff)
        }

        println("walkDeltaDiff: $walkDeltaDiff, availableDiff: $availableDiff, coercedDiff: $coercedDiff")

        when (direction) {
            UP, DOWN -> y += coercedDiff
            LEFT, RIGHT -> x += coercedDiff
        }

        if (position == targetPosition) {
            stopWalking()
        }
    }

    private fun stopWalking() {
        targetWalkPosition = null
        walking = false
        walkAnimationTime = 0f
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
}