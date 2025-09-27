package de.scandurra.osspecificplugins.model

import de.scandurra.osspecificplugins.model.PlatformSet.CpuArchitecture
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import io.kotest.matchers.shouldBe

class PluginArtifactTest {

    @Test
    fun `empty PlatformSet throws IllegalArgumentException`() {
        val empty = PlatformSet.fromIterable(emptyList())

        val ex = assertThrows<IllegalArgumentException> {
            PluginArtifact(
                targets = empty,
                url = "https://example.com/plugin.jar",
                checksum = "deadbeef"
            )
        }
        ex.message shouldBe "PlatformSet must contain at least one platform"
    }

    @Test
    fun `non-empty PlatformSet succeeds`() {
        val onePlatform = Platform(OperationSystem.LINUX, CpuArchitecture.X86_64)
        val set = PlatformSet.fromIterable(listOf(onePlatform))

        val artifact = PluginArtifact(
            targets = set,
            url = "https://example.com/plugin.jar",
            checksum = "deadbeef"
        )

        artifact.targets.isNotEmpty() shouldBe true
    }
}
