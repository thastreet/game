package com.mygdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP

abstract class Character : Actor() {
    enum class Direction {
        UP,
        LEFT,
        DOWN,
        RIGHT,
    }

    data class IdleSprites(
        val up: Sprite,
        val left: Sprite,
        val down: Sprite,
        val right: Sprite,
    )

    protected abstract val idleSprites: IdleSprites
    abstract val hitBox: Rectangle

    var direction = DOWN

    protected val sprite
        get() = when (direction) {
            UP -> idleSprites.up
            LEFT -> idleSprites.left
            DOWN -> idleSprites.down
            RIGHT -> idleSprites.right
        }

    override fun drawDebugBounds(shapes: ShapeRenderer) {
        super.drawDebugBounds(shapes)
        shapes.color = Color.RED

        val hitBox = hitBox
        shapes.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(sprite, x, y)
    }
}