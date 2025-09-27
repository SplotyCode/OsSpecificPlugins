package de.scandurra.osspecificplugins.model

import de.scandurra.osspecificplugins.model.PlatformSet.CpuArchitecture
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import io.kotest.matchers.string.shouldContain
import java.time.Instant

class PluginVersionTest {

    @Test
    fun `non-overlapping artifacts succeed`() {
        val linuxX64 = Platform(OperationSystem.LINUX, CpuArchitecture.X86_64)
        val macArm = Platform(OperationSystem.MACOS, CpuArchitecture.ARM64)

        val artifact1 = PluginArtifact(
            targets = PlatformSet.fromIterable(listOf(linuxX64)),
            url = "https://example.com/a1.jar",
            checksum = "a1"
        )
        val artifact2 = PluginArtifact(
            targets = PlatformSet.fromIterable(listOf(macArm)),
            url = "https://example.com/a2.jar",
            checksum = "a2"
        )

        PluginVersion(
            version = PluginVersion.SemVer("1.0.0"),
            releaseDate = Instant.now(),
            pluginArtifacts = listOf(artifact1, artifact2)
        )
    }

    @Test
    fun `overlapping artifacts throw IllegalArgumentException`() {
        val linuxX64 = Platform(OperationSystem.LINUX, CpuArchitecture.X86_64)

        val artifact1 = PluginArtifact(
            targets = PlatformSet.fromIterable(listOf(linuxX64)),
            url = "https://example.com/a1.jar",
            checksum = "a1"
        )
        val artifact2 = PluginArtifact(
            targets = PlatformSet.fromIterable(listOf(linuxX64)),
            url = "https://example.com/a2.jar",
            checksum = "a2"
        )

        val ex = assertThrows<IllegalArgumentException> {
            PluginVersion(
                version = PluginVersion.SemVer("1.0.0"),
                releaseDate = Instant.now(),
                pluginArtifacts = listOf(artifact1, artifact2)
            )
        }

        ex.message!!.shouldContain("Multiple PluginArtifacts target the same platform")
        ex.message!!.shouldContain("1.0.0")
    }
}
