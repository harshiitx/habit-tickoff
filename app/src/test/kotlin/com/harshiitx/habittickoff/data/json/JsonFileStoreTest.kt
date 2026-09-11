package com.harshiitx.habittickoff.data.json

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class JsonFileStoreTest {

    @Serializable
    data class Sample(val id: String, val count: Int)

    private lateinit var tempDir: File
    private lateinit var file: File

    @Before
    fun setUp() {
        tempDir = createTempDirectory("json-file-store-test").toFile()
        file = File(tempDir, "sample.json")
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `save then load with a new instance returns the same items`() = runTest {
        val writer = JsonFileStore(file, ListSerializer(Sample.serializer()))
        val items = listOf(Sample("a", 1), Sample("b", 2))

        writer.save(items)

        val reader = JsonFileStore(file, ListSerializer(Sample.serializer()))
        reader.load()

        assertEquals(items, reader.items.value)
    }

    @Test
    fun `load on a missing file leaves the store empty`() = runTest {
        val store = JsonFileStore(file, ListSerializer(Sample.serializer()))

        store.load()

        assertTrue(store.items.value.isEmpty())
    }

    @Test
    fun `save never leaves a stray temp file behind`() = runTest {
        val store = JsonFileStore(file, ListSerializer(Sample.serializer()))

        store.save(listOf(Sample("a", 1)))

        assertTrue(file.exists())
        assertTrue(File(tempDir, "sample.json.tmp").exists().not())
    }
}
