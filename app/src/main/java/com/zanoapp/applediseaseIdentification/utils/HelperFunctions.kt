package com.zanoapp.applediseaseIdentification.utils


import java.nio.FloatBuffer

operator fun ClosedRange<FloatBuffer>.iterator() =
    object : Iterator<FloatBuffer> {
        var current = start
        override fun hasNext(): Boolean {
            return current < endInclusive
        }

        override fun next(): FloatBuffer {
            return current.inc(current)
        }
    }

private operator fun FloatBuffer.inc(input: FloatBuffer): FloatBuffer {
    return inc(this)
}

fun <T> Iterator<T>.customEach(action: (T) -> Unit) {
    for (element: T in this)
        action(element)
}








