package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.mygdx.game.Character.Companion.MOVEMENT_DISTANCE

class MainScreen : ScreenAdapter() {
    private val stage = Stage(ScalingViewport(Scaling.stretch, Consts.SCREEN_WIDTH.toFloat(), Consts.SCREEN_HEIGHT.toFloat()))
    private val characters = mutableSetOf<Character>()

    init {
        Gdx.input.inputProcessor = stage

        val rival = Rival(
            initialPosition = Vector2(MOVEMENT_DISTANCE * 2f, MOVEMENT_DISTANCE * 2f),
            canMove = { canMove(it) },
        )
        stage.addActor(rival)
        characters.add(rival)

        val npc = Npc(
            initialPosition = Vector2(MOVEMENT_DISTANCE * 3f, MOVEMENT_DISTANCE * 3f),
            canMove = { canMove(it) }
        )
        stage.addActor(npc)
        characters.add(npc)

        val player = Player(
            initialPosition = Vector2(0f, 0f),
            onPositionChanged = { sortActors() },
            canMove = { canMove(it) },
        )
        stage.addActor(player)
        characters.add(player)

        stage.keyboardFocus = player

        stage.isDebugAll = false
    }

    private fun Character.canMove(hitBox: Rectangle): Boolean =
        characters
            .filterNot { it == this }
            .none { it.calculateHitBox().overlaps(hitBox) }

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