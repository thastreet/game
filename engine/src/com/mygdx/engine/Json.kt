package com.mygdx.engine

import kotlinx.serialization.json.Json

internal val json = Json {
    ignoreUnknownKeys = true
}