package com.mygdx.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class TileMap(
    val width: Int,
    val height: Int,
    val layers: List<Layer>,
    val tilesets: List<Tileset>,
) {
    @Serializable
    data class Layer(
        val data: List<Int>,
    )

    @Serializable
    data class Tileset(
        val columns: Int,
        val firstgid: Int,
        val tilecount: Int,
        val image: String,
        val tilewidth: Int,
        val tileheight: Int,
        val name: String,
        val tiles: List<Tile>,
    ) {
        @Serializable
        data class Tile(
            val id: Int,
            val properties: List<JsonObject>,
        )
    }
}
