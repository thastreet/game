package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.engine.Character
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.Character.Direction.LEFT
import com.mygdx.engine.Character.Direction.RIGHT
import com.mygdx.engine.Character.Direction.UP

class Npc(initialPosition: Vector2, canMove: Character.(Rectangle) -> Boolean) : Character("Npc", initialPosition, canMove) {
    private val img = Texture("RSE Peds 04.png")

    override val idleState = buildIdleState(
        idleSprites = mapOf(
            UP to Sprite(img, 24, 224, 24, 32),
            LEFT to Sprite(img, 24, 160, 24, 32),
            DOWN to Sprite(img, 24, 128, 24, 32),
            RIGHT to Sprite(img, 24, 192, 24, 32),
        ),
    )
}