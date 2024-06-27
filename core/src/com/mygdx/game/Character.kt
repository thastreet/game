package com.mygdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor

abstract class Character : Actor() {
    abstract val sprite: Sprite
    abstract val hitBox: Rectangle

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