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

class MapActor(tileMap: TileMap) : Actor() {
    private data class Tile(
        val position: Vector2,
        val region: TextureRegion,
    )

    private val regions: Map<String, Array<Array<TextureRegion>>> = tileMap.tilesets.associate {
        val texture = Texture(Gdx.files.internal(it.image))
        it.name to TextureRegion.split(texture, it.tilewidth, it.tileheight)
    }
    private val columns = tileMap.tilesets.first().columns

    private val collisionGlobalIds: Set<Int> =
        tileMap.tilesets
            .map { tileset ->
                tileset.tiles
                    .filter { tile ->
                        tile.properties.any { property -> property.name == "collision" && property.value }
                    }
                    .map { it.id + tileset.firstgid }
            }
            .flatten()
            .toSet()

    private val tiles: List<Tile> =
        tileMap.layers
            .map { layer ->
                layer.data
                    .mapIndexedNotNull { tileIndex, globalId ->
                        if (globalId == 0) {
                            return@mapIndexedNotNull null
                        }

                        val tileset = tileMap.tilesets.first { globalId in it.firstgid..it.firstgid + it.tilecount }
                        val textureIndex = globalId - tileset.firstgid


                        val tileJ = tileIndex / tileMap.width
                        val tileI = tileIndex - tileJ * tileMap.width
                        val x = tileI * tileset.tilewidth
                        val y = (tileMap.height - 1) * tileset.tileheight - tileJ * tileset.tileheight
                        val position = Vector2(x.toFloat(), y.toFloat())

                        val textureJ = textureIndex / columns
                        val textureI = textureIndex - (textureJ * columns)
                        val region = regions.getValue(tileset.name)[textureJ][textureI]

                        Tile(position, region)
                    }
            }
            .flatten()

    val collisions: List<Collision> =
        tileMap.layers
            .map { layer ->
                layer.data
                    .mapIndexedNotNull { tileIndex, globalId ->
                        if (globalId in collisionGlobalIds) {
                            val tileset = tileMap.tilesets.first { globalId in it.firstgid..it.firstgid + it.tilecount }
                            val tileJ = tileIndex / tileMap.width
                            val tileI = tileIndex - tileJ * tileMap.width
                            val x = tileI * tileset.tilewidth
                            val y = (tileMap.height - 1) * tileset.tileheight - tileJ * tileset.tileheight

                            Collision.Static(Rectangle(x.toFloat(), y.toFloat(), tileset.tilewidth.toFloat(), tileset.tileheight.toFloat()))
                        } else {
                            null
                        }
                    }
            }
            .flatten()

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