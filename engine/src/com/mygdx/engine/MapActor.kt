package com.mygdx.engine

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

class MapActor(texture: Texture, tileMap: TileMap) : Actor() {
    private data class Tile(
        val position: Vector2,
        val region: TextureRegion,
    )

    private val regions = TextureRegion.split(texture, tileMap.tileWidth, tileMap.tileHeight)

    private val tiles: List<Tile> =
        tileMap.layers
            .first()
            .data
            .mapIndexed { tileIndex, textureIndex1 ->
                val textureIndex = textureIndex1 - 1

                val tileJ = tileIndex / tileMap.width
                val tileI = tileIndex - (tileJ * tileMap.width)
                val x = tileI.toFloat() * tileMap.tileWidth
                val y = (tileMap.height - 1) * tileMap.tileHeight - tileJ.toFloat() * tileMap.tileHeight
                val position = Vector2(x, y)

                val columns = regions[0].size
                val textureJ = textureIndex / columns
                val textureI = textureIndex - (textureJ * columns)
                val region = regions[textureJ][textureI]

                Tile(position, region)
            }

    override fun draw(batch: Batch, parentAlpha: Float) {
        tiles.forEach {
            batch.draw(it.region, it.position.x, it.position.y)
        }
    }
}