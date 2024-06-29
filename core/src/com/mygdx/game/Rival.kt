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
import kotlin.math.min

class Rival(initialPosition: Vector2, canMove: Character.(Rectangle) -> Boolean) : Character(canMove) {
    private val img = Texture("RSE Protags 02.png")

    override val idleSprites = mapOf(
        UP to Sprite(img, 24, 96, 24, 32),
        LEFT to Sprite(img, 24, 32, 24, 32),
        DOWN to Sprite(img, 24, 0, 24, 32),
        RIGHT to Sprite(img, 24, 64, 24, 32),
    )

    private var targetPosition: Vector2? = null

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
        if (targetPosition != null) return
        targetPosition = Vector2(x + 8f, y)
    }

    override val hitBox: Rectangle
        get() = Rectangle(x + sprite.width / 2 - 5, y + 2, 10f, 10f)

    override fun act(delta: Float) {
        super.act(delta)

        targetPosition?.let {
            val toMove = calculateWalkTargetPosition(delta, RIGHT).x - position.x
            val remainingToMove = it.x - x
            val diff = min(toMove, remainingToMove)
            println("x: $x, toMove: $toMove, remainingToMove: $remainingToMove, diff: $diff")
            x += diff

            if (position == it) {
                targetPosition = null
            }
        }
    }
}