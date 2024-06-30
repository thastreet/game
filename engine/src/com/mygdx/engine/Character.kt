package com.mygdx.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.mygdx.engine.Character.CharacterStateEnum.IDLE
import com.mygdx.engine.Character.CharacterStateEnum.WALKING
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.CharacterState.Idle
import com.mygdx.engine.CharacterState.Walking
import kotlin.Array
import kotlin.Boolean
import kotlin.Float
import kotlin.String
import kotlin.getValue
import kotlin.lazy
import kotlin.let
import com.badlogic.gdx.utils.Array as GdxArray

abstract class Character(
    name: String,
    initialPosition: Vector2,
    private val canMove: Character.(Rectangle) -> Boolean
) : Actor() {
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

    private enum class CharacterStateEnum {
        IDLE,
        WALKING,
    }

    init {
        this.name = name
        setPosition(initialPosition)
    }

    protected fun walk(direction: Direction) {
        if (stateEnum == WALKING)
            return

        states[WALKING]?.let {
            (it as Walking).enter(direction)
        }
        stateEnum = WALKING
    }

    protected fun buildIdleState(idleSprites: Map<Direction, Sprite>) =
        Idle(
            character = this,
            idleSprites = idleSprites,
        )

    protected fun buildWalkingState(animationSprites: Map<Direction, Array<TextureRegion>>, continueWalking: () -> Direction? = { null }) =
        Walking(
            character = this,
            canMove = { canMove(it) },
            animationSprites = animationSprites.mapValues { GdxArray(it.value) },
            onExit = { stateEnum = IDLE },
            continueWalking = continueWalking,
        )

    fun calculateHitBox(position: Vector2 = this.position): Rectangle =
        Rectangle(position.x + state.textureRegion.regionWidth / 2 - 5, position.y + 2, 10f, 10f)

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

    companion object {
        const val WALK_SPEED = 3f
        const val MOVEMENT_DISTANCE = 8
    }
}