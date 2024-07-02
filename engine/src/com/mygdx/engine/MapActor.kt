package com.mygdx.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

class MapActor(private val tiles: List<Tile>, private val collisions: List<Collision>) : Actor() {
    data class Tile(
        val position: Vector2,
        val region: TextureRegion,
        val zIndex: Int,
    )

    override fun draw(batch: Batch, parentAlpha: Float) {
        tiles.forEach {
            batch.draw(it.region, it.position.x, it.position.y)
        }
    }

    override fun drawDebugBounds(shapes: ShapeRenderer) {
        super.drawDebugBounds(shapes)
        shapes.color = Color.RED

        collisions
            .map { it.hitBox }
            .forEach { hitBox -> shapes.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height) }
    }
}