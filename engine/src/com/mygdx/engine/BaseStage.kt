package com.mygdx.engine

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.engine.Collision.Dynamic
import com.mygdx.engine.Collision.Static

class BaseStage(viewport: Viewport) : Stage(viewport), CollisionHolder {
    private val collisions = mutableSetOf<Collision>()

    fun addCharacter(character: Character) {
        addCollision(character)
        addActor(character)
    }

    fun addMap(map: MapActor) {
        addActor(map)

        map.collisions.forEach {
            addCollision(it)
        }
    }

    private fun addCollision(collision: Collision) {
        collisions.add(collision)
    }

    override fun Dynamic.canMove(hitBox: Rectangle): Boolean =
        collisions
            .filter { collision ->
                when (collision) {
                    is Dynamic -> collision.id != id
                    is Static -> true
                }
            }
            .none { it.hitBox.overlaps(hitBox) }

    fun Character.setHasControl() {
        setIsControllable()
        keyboardFocus = this
    }
}