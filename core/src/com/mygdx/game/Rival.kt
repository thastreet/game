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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Rival(initialPosition: Vector2, collisionHolder: CollisionHolder) : Character("Rival", initialPosition, collisionHolder) {
    private val img = Texture("RSE Protags 02.png")
    private val srcWidth = 3 * MOVEMENT_DISTANCE
    private val srcHeight = 4 * MOVEMENT_DISTANCE

    override val idleState = buildIdleState(
        idleSprites = mapOf(
            UP to Sprite(img, srcWidth, 3 * srcHeight, srcWidth, srcHeight),
            LEFT to Sprite(img, srcWidth, 1 * srcHeight, srcWidth, srcHeight),
            DOWN to Sprite(img, srcWidth, 0 * srcHeight, srcWidth, srcHeight),
            RIGHT to Sprite(img, srcWidth, 2 * srcHeight, srcWidth, srcHeight),
        ),
    )

    override val walkingState = buildWalkingState(
        animationSprites = mapOf(
            UP to TextureRegion(img, 0, 3 * srcHeight, 3 * srcWidth, srcHeight).split(srcWidth, srcHeight)[0],
            LEFT to TextureRegion(img, 0, 1 * srcHeight, 3 * srcWidth, srcHeight).split(srcWidth, srcHeight)[0],
            DOWN to TextureRegion(img, 0, 0 * srcHeight, 3 * srcWidth, srcHeight).split(srcWidth, srcHeight)[0],
            RIGHT to TextureRegion(img, 0, 2 * srcHeight, 3 * srcWidth, srcHeight).split(srcWidth, srcHeight)[0],
        ),
    )

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(2000)
                walk(Direction.entries.random())
            }
        }
    }
}