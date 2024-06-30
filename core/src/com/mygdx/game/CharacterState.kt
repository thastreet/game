package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.mygdx.game.Character.Companion.MOVEMENT_DISTANCE
import com.mygdx.game.Character.Companion.WALK_SPEED
import com.mygdx.game.Character.Direction
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP
import kotlin.math.max
import kotlin.math.min

sealed class CharacterState(protected val character: Character) {
    open fun update(delta: Float) {}

    abstract fun draw(batch: Batch)

    abstract val textureRegion: TextureRegion

    class Idle(character: Character, private val idleSprites: Map<Direction, Sprite>) : CharacterState(character) {
        override fun draw(batch: Batch) {
            batch.draw(textureRegion, character.x, character.y)
        }

        override val textureRegion
            get() = idleSprites.getValue(character.direction)
    }

    class Walking(
        character: Character,
        val canMove: (Rectangle) -> Boolean,
        private val animationSprites: Map<Direction, Array<TextureRegion>>,
        private val onExit: () -> Unit,
        val continueWalking: () -> Direction? = { null }
    ) :
        CharacterState(character) {
        private var walkAnimationTime = 0f

        private var targetPosition: Vector2? = null
        private var walkAnimation: Animation<TextureRegion>? = null

        fun enter(direction: Direction) {
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
            walkAnimation = Animation(0.2f, animationSprites[direction], LOOP_PINGPONG)
            character.direction = direction
        }

        private fun exit() {
            walkAnimationTime = 0f

            targetPosition = null
            walkAnimation = null
        }

        override fun update(delta: Float) {
            val targetPosition = this.targetPosition ?: return

            val direction = when {
                targetPosition.y > character.y -> UP
                targetPosition.y < character.y -> DOWN
                targetPosition.x > character.x -> RIGHT
                targetPosition.x < character.x -> LEFT
                else -> return
            }

            val walkDeltaPosition = calculateWalkDeltaPosition(delta, direction)

            walkAnimationTime += delta

            if (!canMove(character.calculateHitBox(walkDeltaPosition))) {
                log("Can't move!")
                onTargetPositionReached()
                return
            }

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

            log("walkDeltaDiff: $walkDeltaDiff, availableDiff: $availableDiff, coercedDiff: $coercedDiff")

            when (direction) {
                UP, DOWN -> character.y += coercedDiff
                LEFT, RIGHT -> character.x += coercedDiff
            }

            if (character.position == targetPosition) {
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
            continueWalking()?.let {
                enter(it)
            } ?: run {
                exit()
                onExit()
            }
        }

        private fun log(message: String) {
            if (character.debug) {
                println("${character.name} state: $message")
            }
        }

        override fun draw(batch: Batch) {
            val currentFrame = walkAnimation!!.getKeyFrame(walkAnimationTime, true)
            batch.draw(currentFrame, character.x, character.y)
        }

        override val textureRegion
            get() = walkAnimation!!.getKeyFrame(walkAnimationTime)!!
    }
}