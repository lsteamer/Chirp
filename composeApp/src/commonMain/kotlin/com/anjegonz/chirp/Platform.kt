package com.anjegonz.chirp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform