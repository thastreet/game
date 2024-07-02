package com.mygdx.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.mygdx.engine.TileMap.Tileset
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class StageMap(file: FileHandle) {
    private val tileMap = json.decodeFromString<TileMap>(file.readString())

    private val regions: Map<String, Array<Array<TextureRegion>>> =
        tileMap.tilesets
            .associate { it.name to TextureRegion.split(Texture(Gdx.files.internal(it.image)), it.tilewidth, it.tileheight) }

    private val collisionGlobalIds: Set<Int> =
        tileMap.tilesets
            .map { tileset ->
                tileset.tiles
                    .filter { it.collision }
                    .map { it.getGlobalId(tileset) }
            }
            .flatten()
            .toSet()

    private val Tileset.Tile.collision: Boolean
        get() = properties
            .firstOrNull { it.getValue("name").jsonPrimitive.content == "collision" }
            ?.getValue("value")
            ?.jsonPrimitive
            ?.boolean == true

    private val Tileset.Tile.zIndex: Int
        get() = properties
            .firstOrNull { it.getValue("name").jsonPrimitive.content == "zindex" }
            ?.getValue("value")
            ?.jsonPrimitive
            ?.int ?: 0

    private fun Tileset.Tile.getGlobalId(tileset: Tileset): Int =
        id + tileset.firstgid

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

    val actors: Map<Int, Actor> =
        tileMap.layers
            .map { layer ->
                layer.data
                    .mapIndexedNotNull { tileIndex, globalId ->
                        if (globalId == 0) {
                            return@mapIndexedNotNull null
                        }

                        val tileset = getTileset(globalId)

                        MapActor.Tile(
                            position = getTilePosition(tileIndex, tileset),
                            region = getRegion(globalId, tileset),
                            zIndex = tileset.findTile(globalId)?.zIndex ?: 0
                        )
                    }
            }
            .flatten()
            .groupBy { it.zIndex }
            .mapValues { MapActor(it.value, collisions) }

    private fun Tileset.findTile(globalId: Int): Tileset.Tile? =
        tiles.firstOrNull { it.getGlobalId(this) == globalId }

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
        get() = this@StageMap.regions.getValue(name)

    private fun getHitBox(tileIndex: Int, tileset: Tileset): Rectangle =
        getTilePosition(tileIndex, tileset).let { position ->
            Rectangle(position.x, position.y, tileset.tilewidth.toFloat(), tileset.tileheight.toFloat())
        }
}