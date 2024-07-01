package com.mygdx.engine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TileMap(
    val width: Int,
    val height: Int,
    @SerialName("tilewidth")
    val tileWidth: Int,
    @SerialName("tileheight")
    val tileHeight: Int,
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
        val image: String,
        val tiles: List<Tile>,
    ) {
        @Serializable
        data class Tile(
            val id: Int,
            val properties: List<Property>,
        ) {
            @Serializable
            data class Property(
                val name: String,
                val value: Boolean, // TODO: parse as map is other type than bool
            )
        }
    }
}
