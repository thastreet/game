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
) {
    @Serializable
    data class Layer(
        val data: List<Int>,
    )
}
