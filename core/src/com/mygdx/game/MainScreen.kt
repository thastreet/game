package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.mygdx.engine.BaseStage
import com.mygdx.engine.Character.Companion.MOVEMENT_DISTANCE

class MainScreen : ScreenAdapter() {
    private val stage = BaseStage(ScalingViewport(Scaling.stretch, Consts.SCREEN_WIDTH.toFloat(), Consts.SCREEN_HEIGHT.toFloat()))

    init {
        Gdx.input.inputProcessor = stage

        stage.addCollision(
            Rival(
                initialPosition = Vector2(MOVEMENT_DISTANCE * 2f, MOVEMENT_DISTANCE * 2f),
                collisionHolder = stage,
            )
        )

        stage.addCollision(
            Npc(
                initialPosition = Vector2(MOVEMENT_DISTANCE * 4f, MOVEMENT_DISTANCE * 4f),
                collisionHolder = stage,
            )
        )

        val player = Player(
            initialPosition = Vector2(0f, 0f),
            onPositionChanged = { sortActors() },
            collisionHolder = stage,
        )
        stage.addCollision(player)

        stage.keyboardFocus = player

        stage.isDebugAll = false
    }

    private fun Player.sortActors() =
        this@MainScreen.stage.actors.sort { a, b ->
            when {
                a !is Player -> if (a.y > y) -1 else 1
                b !is Player -> if (b.y > y) 1 else -1
                else -> 0
            }
        }

    override fun render(delta: Float) {
        ScreenUtils.clear(1f, 1f, 0f, 0f)

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }
}