package com.mygdx.engine

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.mygdx.engine.Character.Companion.MOVEMENT_DISTANCE
import com.mygdx.engine.Character.Companion.WALK_SPEED
import com.mygdx.engine.Character.Direction
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.Character.Direction.LEFT
import com.mygdx.engine.Character.Direction.RIGHT
import com.mygdx.engine.Character.Direction.UP
import kotlin.math.max
import kotlin.math.min

class Walking(
    private val character: Character,
    private val animationSprites: Map<Direction, Array<TextureRegion>>,
    private val onExit: () -> Unit,
) : CharacterState() {
    private var nextDirection: Direction? = null
    private var targetPosition: Vector2? = null

    private var walkAnimationTime = 0f
    private var walkAnimation: Animation<TextureRegion>? = null

    fun enter(direction: Direction, nextDirection: Direction? = null) {
        if (targetPosition != null) {
            character.log("Already walking")
            return
        }

        this.nextDirection = nextDirection
        walkTowards(direction)
    }

    private fun walkTowards(direction: Direction) {
        character.direction = direction
        walkAnimation = Animation(0.2f, animationSprites[direction], LOOP_PINGPONG)

        if (character.canMove(direction)) {
            targetPosition = Vector2(
                character.x + when (direction) {
                    LEFT -> -MOVEMENT_DISTANCE
                    RIGHT -> MOVEMENT_DISTANCE
                    else -> 0
                },
                character.y + when (direction) {
                    DOWN -> -MOVEMENT_DISTANCE
                    UP -> MOVEMENT_DISTANCE
                    else -> 0
                }
            )
        }
    }

    fun update(direction: Direction?) {
        nextDirection = direction
    }

    private fun exit() {
        walkAnimationTime = 0f

        targetPosition = null
        walkAnimation = null
    }

    override fun update(delta: Float) {
        walkAnimationTime += delta

        targetPosition?.let { targetPosition ->
            val direction = when {
                targetPosition.y > character.y -> UP
                targetPosition.y < character.y -> DOWN
                targetPosition.x > character.x -> RIGHT
                targetPosition.x < character.x -> LEFT
                else -> throw IllegalStateException("Character is already at target position")
            }

            val walkDeltaPosition = calculateWalkDeltaPosition(delta, direction)

            val walkDeltaDiff: Float
            val availableDiff: Float
            when (direction) {
                UP, DOWN -> {
                    walkDeltaDiff = walkDeltaPosition.y - character.y
                    availableDiff = targetPosition.y - character.y
                }

                LEFT, RIGHT -> {
                    walkDeltaDiff = walkDeltaPosition.x - character.x
                    availableDiff = targetPosition.x - character.x
                }
            }

            val coercedDiff = when (direction) {
                RIGHT, UP -> min(walkDeltaDiff, availableDiff)
                LEFT, DOWN -> max(walkDeltaDiff, availableDiff)
            }

            character.log("walkDeltaDiff: $walkDeltaDiff, availableDiff: $availableDiff, coercedDiff (final diff): $coercedDiff, targetPosition: $targetPosition")

            when (direction) {
                UP, DOWN -> character.y += coercedDiff
                LEFT, RIGHT -> character.x += coercedDiff
            }
        }

        val targetReached = character.position == targetPosition
        val animationTimeout = targetPosition == null && nextDirection == null && walkAnimationTime > walkAnimation!!.animationDuration / 2

        if (targetReached || animationTimeout) {
            onTargetPositionReached()
        }
    }

    private fun calculateWalkDeltaPosition(delta: Float, direction: Direction): Vector2 =
        Vector2(
            character.x + delta * when (direction) {
                RIGHT -> MOVEMENT_DISTANCE
                LEFT -> -MOVEMENT_DISTANCE
                else -> 0
            } * WALK_SPEED, character.y + delta * when (direction) {
                UP -> MOVEMENT_DISTANCE
                DOWN -> -MOVEMENT_DISTANCE
                else -> 0
            } * WALK_SPEED
        )

    private fun onTargetPositionReached() {
        character.log("onTargetPositionReached")
        targetPosition = null

        nextDirection.let {
            if (it == null) {
                character.log("exit")
                exit()
                onExit()
            } else {
                character.log("continueWalking")
                walkTowards(it)
            }
        }
    }

    override fun draw(batch: Batch) {
        val currentFrame = walkAnimation!!.getKeyFrame(walkAnimationTime, true)
        batch.draw(currentFrame, character.x, character.y)
    }

    override val textureRegion
        get() = walkAnimation!!.getKeyFrame(walkAnimationTime)!!
}