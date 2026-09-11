package com.harshiitx.habittickoff.domain.quotes

import kotlin.random.Random

/** Picks a random index into a list of the given [size], never repeating [excludingIndex]. */
fun pickNextQuoteIndex(size: Int, excludingIndex: Int, random: Random = Random): Int {
    if (size <= 1) return 0
    var index: Int
    do {
        index = random.nextInt(size)
    } while (index == excludingIndex)
    return index
}
