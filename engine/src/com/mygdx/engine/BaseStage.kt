package com.mygdx.engine

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.engine.Collision.Dynamic
import com.mygdx.engine.Collision.Static

class BaseStage(viewport: Viewport, init: BaseStageInitializer.(CollisionHolder) -> Unit) : Stage(viewport), CollisionHolder {
    private val collisions = mutableSetOf<Collision>()
    private val characters = mutableSetOf<Character>()
    private val maps = mutableSetOf<StageMap>()

    inner class BaseStageInitializer {
        fun addCharacter(character: Character) {
            collisions.add(character)
            characters.add(character)
        }

        fun addMap(map: StageMap) {
            map.collisions.forEach { collisions.add(it) }
            maps.add(map)
        }

        fun Character.setHasControl() {
            setIsControllable()
            keyboardFocus = this
        }
    }

    init {
        BaseStageInitializer().init(this)

        maps.forEach { addActor(it.actors[0]) }

        characters.forEach { addActor(it) }

        maps.map { map ->
            map.actors
                .toSortedMap()
                .filterKeys { it > 0 }
                .forEach { addActor(it.value) }
        }
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
}