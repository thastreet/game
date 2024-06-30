package com.mygdx.engine

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.mygdx.engine.Character.Direction

class Idle(private val character: Character, private val idleSprites: Map<Direction, Sprite>) : CharacterState() {
    override fun draw(batch: Batch) {
        batch.draw(textureRegion, character.x, character.y)
    }

    override val textureRegion
        get() = idleSprites.getValue(character.direction)
}