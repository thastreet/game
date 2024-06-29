package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
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

    private var targetWalkPosition: Vector2? = null

    init {
        setPosition(initialPosition)

        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(2000)
                move(Direction.entries.random())
            }
        }
    }

    private fun move(direction: Direction) {
        if (targetWalkPosition != null) return

        val diff = 8f
        targetWalkPosition = Vector2(
            x + when (direction) {
                LEFT -> -diff
                RIGHT -> diff
                else -> 0f
            },
            y + when (direction) {
                DOWN -> -diff
                UP -> diff
                else -> 0f
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
            targetWalkPosition = null
            return
        }

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
            targetWalkPosition = null
        }
    }
}