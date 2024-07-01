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
import com.mygdx.engine.TileMap.Tileset

class MapActor(private val tileMap: TileMap) : Actor() {
    private data class Tile(
        val position: Vector2,
        val region: TextureRegion,
    )

    private val regions: Map<String, Array<Array<TextureRegion>>> =
        tileMap.tilesets
            .associate { it.name to TextureRegion.split(Texture(Gdx.files.internal(it.image)), it.tilewidth, it.tileheight) }

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

                        val tileset = getTileset(globalId)

                        Tile(
                            position = getTilePosition(tileIndex, tileset),
                            region = getRegion(globalId, tileset),
                        )
                    }
            }
            .flatten()

    val collisions: List<Collision> =
        tileMap.layers
            .map { layer ->
                layer.data
                    .mapIndexedNotNull { tileIndex, globalId ->
                        if (globalId in collisionGlobalIds) {
                            val tileset = getTileset(globalId)

                            Collision.Static(getHitBox(tileIndex, tileset))
                        } else {
                            null
                        }
                    }
            }
            .flatten()

    private fun getTileset(globalId: Int): Tileset =
        tileMap.tilesets.first { globalId in it.firstgid..it.firstgid + it.tilecount }

    private fun getTilePosition(tileIndex: Int, tileset: Tileset): Vector2 {
        val j = tileIndex / tileMap.width
        val i = tileIndex - j * tileMap.width
        val x = i * tileset.tilewidth
        val y = (tileMap.height - 1) * tileset.tileheight - j * tileset.tileheight

        return Vector2(x.toFloat(), y.toFloat())
    }

    private fun getRegion(globalId: Int, tileset: Tileset): TextureRegion {
        val textureIndex = globalId - tileset.firstgid
        val j = textureIndex / tileset.columns
        val i = textureIndex - j * tileset.columns
        return tileset.regions[j][i]
    }

    private val Tileset.regions: Array<Array<TextureRegion>>
        get() = this@MapActor.regions.getValue(name)

    private fun getHitBox(tileIndex: Int, tileset: Tileset): Rectangle =
        getTilePosition(tileIndex, tileset).let { position ->
            Rectangle(position.x, position.y, tileset.tilewidth.toFloat(), tileset.tileheight.toFloat())
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