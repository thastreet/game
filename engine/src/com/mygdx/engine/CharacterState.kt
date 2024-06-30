package com.mygdx.engine

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

sealed class CharacterState {
    open fun update(delta: Float) {}

    abstract fun draw(batch: Batch)

    abstract val textureRegion: TextureRegion
}