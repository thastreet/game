package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScalingViewport

class MainScreen : ScreenAdapter() {
    private val stage = Stage(ScalingViewport(Scaling.stretch, Consts.SCREEN_WIDTH.toFloat(), Consts.SCREEN_HEIGHT.toFloat()))
    private val characters = mutableSetOf<Character>()

    init {
        Gdx.input.inputProcessor = stage

        val rival = Rival(Vector2(30f, 10f))
        stage.addActor(rival)
        characters.add(rival)

        val player = Player(Vector2(10f, 10f)) { self, targetHitBox ->
            characters
                .filterNot { it == self}
                .none { it.hitBox.overlaps(targetHitBox) }
        }
        stage.addActor(player)
        characters.add(player)

        stage.keyboardFocus = player

        stage.isDebugAll = false
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