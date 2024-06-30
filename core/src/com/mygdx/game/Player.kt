package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.mygdx.engine.Character
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.Character.Direction.LEFT
import com.mygdx.engine.Character.Direction.RIGHT
import com.mygdx.engine.Character.Direction.UP
import com.mygdx.engine.CollisionHolder

class Player(initialPosition: Vector2, private val onPositionChanged: Player.() -> Unit, collisionHolder: CollisionHolder) : Character("Player", initialPosition, collisionHolder) {
    private val img = Texture("RSE Protags 01.png")

    override val idleState = buildIdleState(
        idleSprites = mapOf(
            UP to Sprite(img, 24, 96, 24, 32),
            LEFT to Sprite(img, 24, 32, 24, 32),
            DOWN to Sprite(img, 24, 0, 24, 32),
            RIGHT to Sprite(img, 24, 64, 24, 32),
        ),
    )

    override val walkingState = buildWalkingState(
        animationSprites = mapOf(
            UP to TextureRegion(img, 0, 96, 3 * 24, 1 * 32).split(24, 32)[0],
            LEFT to TextureRegion(img, 0, 32, 3 * 24, 1 * 32).split(24, 32)[0],
            DOWN to TextureRegion(img, 0, 0, 3 * 24, 1 * 32).split(24, 32)[0],
            RIGHT to TextureRegion(img, 0, 64, 3 * 24, 1 * 32).split(24, 32)[0],
        ),
    )

    override fun positionChanged() = onPositionChanged()
}