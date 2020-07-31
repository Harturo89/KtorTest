package com.itcs.back.model

import java.util.*

data class User(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val image: String,
    val color: String)
