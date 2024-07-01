package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.mygdx.engine.BaseStage
import com.mygdx.engine.Character.Companion.MOVEMENT_DISTANCE
import com.mygdx.engine.MapActor
import com.mygdx.engine.TileMap
import com.mygdx.engine.json

class MainScreen : ScreenAdapter() {
    private val stage = BaseStage(ScalingViewport(Scaling.stretch, Consts.SCREEN_WIDTH.toFloat(), Consts.SCREEN_HEIGHT.toFloat()))

    init {
        Gdx.input.inputProcessor = stage

        stage.addActor(
            MapActor(
                texture = Texture(Gdx.files.internal("RS Outside_A2.png")),
                tileMap = json.decodeFromString<TileMap>(Gdx.files.internal("map1.tmj").readString())
            )
        )

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

        stage.addCollision(
            Player(
                initialPosition = Vector2(0f, 0f),
                collisionHolder = stage,
            ).also {
                with(stage) { it.setHasControl() }
            }
        )

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