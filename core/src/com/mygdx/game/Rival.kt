package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.Character.Direction.DOWN
import com.mygdx.game.Character.Direction.LEFT
import com.mygdx.game.Character.Direction.RIGHT
import com.mygdx.game.Character.Direction.UP

class Rival(initialPosition: Vector2) : Character() {
    private val img = Texture("RSE Protags 02.png")

    override val idleSprites = mapOf(
        UP to Sprite(img, 24, 96, 24, 32),
        LEFT to Sprite(img, 24, 32, 24, 32),
        DOWN to Sprite(img, 24, 0, 24, 32),
        RIGHT to Sprite(img, 24, 64, 24, 32),
    )

    init {
        setPosition(initialPosition)
    }

    override val hitBox: Rectangle
        get() = Rectangle(x + sprite.width / 4, y + 2, sprite.width / 2, sprite.height / 3)
}