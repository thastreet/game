package com.mygdx.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

// TODO: handle multiple tilesets and layers
class MapActor(tileMap: TileMap) : Actor() {
    private data class Tile(
        val position: Vector2,
        val region: TextureRegion,
    )

    private val texture = Texture(Gdx.files.internal(tileMap.tilesets.first().image))
    private val regions = TextureRegion.split(texture, tileMap.tileWidth, tileMap.tileHeight)
    private val columns = tileMap.tilesets.first().columns

    private val collisionGlobalIds: Set<Int> =
        tileMap.tilesets
            .first()
            .tiles
            .filter { tile ->
                tile.properties.any { property -> property.name == "collision" && property.value }
            }
            .map { it.id + tileMap.tilesets.first().firstgid }
            .toSet()

    private val tiles: List<Tile> =
        tileMap.layers
            .first()
            .data
            .mapIndexedNotNull { tileIndex, globalId ->
                val textureIndex = globalId - tileMap.tilesets.first().firstgid

                if (textureIndex == -1)
                    return@mapIndexedNotNull null

                val tileJ = tileIndex / tileMap.width
                val tileI = tileIndex - tileJ * tileMap.width
                val x = tileI * tileMap.tileWidth
                val y = (tileMap.height - 1) * tileMap.tileHeight - tileJ * tileMap.tileHeight
                val position = Vector2(x.toFloat(), y.toFloat())

                val textureJ = textureIndex / columns
                val textureI = textureIndex - (textureJ * columns)
                val region = regions[textureJ][textureI]

                Tile(position, region)
            }

    val collisions: List<Collision> =
        tileMap.layers
            .first()
            .data
            .mapIndexedNotNull { tileIndex, globalId ->
                if (globalId in collisionGlobalIds) {
                    val tileJ = tileIndex / tileMap.width
                    val tileI = tileIndex - tileJ * tileMap.width
                    val x = tileI * tileMap.tileWidth
                    val y = (tileMap.height - 1) * tileMap.tileHeight - tileJ * tileMap.tileHeight

                    Collision.Static(Rectangle(x.toFloat(), y.toFloat(), tileMap.tileWidth.toFloat(), tileMap.tileHeight.toFloat()))
                } else {
                    null
                }
            }

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