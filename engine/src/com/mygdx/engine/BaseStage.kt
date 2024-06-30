package com.mygdx.engine

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport

class BaseStage(viewport: Viewport) : Stage(viewport), CollisionHolder {
    private val collisions = mutableSetOf<Collision>()

    fun addCollision(collision: Collision) {
        collisions.add(collision)

        (collision as? Actor)?.let {
            addActor(it)
        }
    }

    override fun Collision.canMove(hitBox: Rectangle): Boolean =
        collisions
            .filterNot { it.id == id }
            .none { it.hitBox.overlaps(hitBox) }
}