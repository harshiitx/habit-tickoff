package com.harshiitx.habittickoff.domain.quotes

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuotePickerTest {

    @Test
    fun `never repeats the excluded index across many draws`() {
        repeat(200) { seed ->
            val next = pickNextQuoteIndex(size = 10, excludingIndex = 3, random = Random(seed))
            assertTrue(next != 3)
        }
    }

    @Test
    fun `a single-element list always returns index zero`() {
        assertEquals(0, pickNextQuoteIndex(size = 1, excludingIndex = 0))
    }

    @Test
    fun `the bundled quotes list has at least 100 entries`() {
        assertTrue(QUOTES.size >= 100)
    }

    @Test
    fun `the bundled quotes list has no blank or duplicate entries`() {
        assertTrue(QUOTES.all { it.isNotBlank() })
        assertEquals(QUOTES.size, QUOTES.toSet().size)
    }
}
