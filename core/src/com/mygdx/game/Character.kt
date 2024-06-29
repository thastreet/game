package com.mygdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP

abstract class Character(protected val canMove: Character.(Rectangle) -> Boolean) : Actor() {
    enum class Direction {
        UP,
        LEFT,
        DOWN,
        RIGHT,
    }

    protected abstract val idleSprites: Map<Direction, Sprite>

    abstract val hitBox: Rectangle

    protected var direction = DOWN

    protected val sprite
        get() = idleSprites.getValue(direction)

    override fun drawDebugBounds(shapes: ShapeRenderer) {
        super.drawDebugBounds(shapes)
        shapes.color = Color.RED

        val hitBox = hitBox
        shapes.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(sprite, x, y)
    }

    protected fun calculateHitBox(position: Vector2): Rectangle =
        Rectangle(position.x + sprite.width / 2 - 5, position.y + 2, 10f, 10f)

    protected fun calculateWalkDeltaPosition(delta: Float, direction: Direction): Vector2 =
        Vector2(
            x + delta * when (direction) {
                RIGHT -> MOVEMENT_DISTANCE
                LEFT -> -MOVEMENT_DISTANCE
                else -> 0
            } * WALK_SPEED, y + delta * when (direction) {
                UP -> MOVEMENT_DISTANCE
                DOWN -> -MOVEMENT_DISTANCE
                else -> 0
            } * WALK_SPEED
        )

    companion object {
        const val WALK_SPEED = 1.5f
        const val MOVEMENT_DISTANCE = 16
    }
}