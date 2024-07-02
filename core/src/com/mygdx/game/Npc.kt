package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.engine.Character
import com.mygdx.engine.Character.Direction.DOWN
import com.mygdx.engine.Character.Direction.LEFT
import com.mygdx.engine.Character.Direction.RIGHT
import com.mygdx.engine.Character.Direction.UP
import com.mygdx.engine.CollisionHolder

class Npc(initialPosition: Vector2, collisionHolder: CollisionHolder) : Character("Npc", initialPosition, collisionHolder) {
    private val img = Texture("RSE Peds 04.png")
    private val srcWidth = 3 * MOVEMENT_DISTANCE
    private val srcHeight = 4 * MOVEMENT_DISTANCE

    override val idleState = buildIdleState(
        idleSprites = mapOf(
            UP to Sprite(img, srcWidth, 128 + 3 * srcHeight, srcWidth, srcHeight),
            LEFT to Sprite(img, srcWidth, 128 + 1 * srcHeight, srcWidth, srcHeight),
            DOWN to Sprite(img, srcWidth, 128 + 0 * srcHeight, srcWidth, srcHeight),
            RIGHT to Sprite(img, srcWidth, 128 + 2 * srcHeight, srcWidth, srcHeight),
        ),
    )
}