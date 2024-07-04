package com.mygdx.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.Character.Direction.LEFT
import com.mygdx.engine.Character.Direction.RIGHT
import com.mygdx.engine.Character.Direction.UP
import com.mygdx.engine.CharacterStateEnum.IDLE
import com.mygdx.engine.CharacterStateEnum.WALKING
import kotlin.Array
import com.badlogic.gdx.utils.Array as GdxArray

abstract class Character(
    name: String,
    initialPosition: Vector2,
    private val collisionHolder: CollisionHolder,
) : Actor(), Collision.Dynamic {
    enum class Direction {
        UP,
        LEFT,
        DOWN,
        RIGHT,
    }

    var direction = DOWN

    protected abstract val idleState: Idle
    protected open val walkingState: Walking? = null

    private val states: Map<CharacterStateEnum, CharacterState> by lazy {
        buildMap {
            put(IDLE, idleState)

            walkingState?.let { put(WALKING, it) }
        }
    }

    private var stateEnum = IDLE

    private val state: CharacterState
        get() = states.getValue(stateEnum)

    init {
        this.name = name
        setPosition(initialPosition)
    }

    fun onWalkingKeysChanged(walkingKeys: Set<Int>) {
        val direction: Direction? = walkingKeys.firstNotNullOfOrNull { it.asDirection }

        when (stateEnum) {
            WALKING -> (states[WALKING] as Walking).update(direction)
            IDLE -> direction?.let {
                walk(
                    direction = it,
                    keepWalking = true,
                )
            }
        }
    }

    fun walk(direction: Direction, keepWalking: Boolean = false) {
        (states[WALKING] as Walking).enter(
            direction = direction,
            nextDirection = direction.takeIf { keepWalking },
        )
        stateEnum = WALKING
    }

    fun canMove(direction: Direction): Boolean =
        with(collisionHolder) {
            this@Character.canMove(
                calculateHitBox(
                    Vector2(
                        x + when (direction) {
                            LEFT -> -MOVEMENT_DISTANCE
                            RIGHT -> MOVEMENT_DISTANCE
                            else -> 0
                        },
                        y + when (direction) {
                            UP -> MOVEMENT_DISTANCE
                            DOWN -> -MOVEMENT_DISTANCE
                            else -> 0
                        }
                    )
                )
            )
        }

    fun setIsControllable() {
        addListener(ControlListener(this))
    }

    fun log(message: String) {
        if (debug) {
            println("$name state: $message")
        }
    }

    override fun drawDebugBounds(shapes: ShapeRenderer) {
        super.drawDebugBounds(shapes)
        shapes.color = Color.RED

        val hitBox = calculateHitBox()
        shapes.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height)
    }

    override fun act(delta: Float) {
        super.act(delta)
        state.update(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        state.draw(batch)
    }

    override val hitBox: Rectangle
        get() = calculateHitBox()

    override val id = name

    protected fun buildIdleState(idleSprites: Map<Direction, Sprite>) =
        Idle(
            character = this,
            idleSprites = idleSprites,
        )

    protected fun buildWalkingState(animationSprites: Map<Direction, Array<TextureRegion>>) =
        Walking(
            character = this,
            animationSprites = animationSprites.mapValues { GdxArray(it.value) },
            onExit = { stateEnum = IDLE },
        )

    private fun calculateHitBox(position: Vector2 = this.position): Rectangle =
        Rectangle(position.x + MOVEMENT_DISTANCE, position.y, MOVEMENT_DISTANCE.toFloat(), 2f * MOVEMENT_DISTANCE)

    companion object {
        const val WALK_SPEED = 4f
        const val MOVEMENT_DISTANCE = 8
    }
}