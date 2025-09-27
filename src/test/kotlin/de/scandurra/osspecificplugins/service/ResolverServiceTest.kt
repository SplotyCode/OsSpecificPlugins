package de.scandurra.osspecificplugins.service

import de.scandurra.osspecificplugins.model.PlatformSet
import de.scandurra.osspecificplugins.model.PlatformSet.CpuArchitecture
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.Plugin.PluginId
import de.scandurra.osspecificplugins.model.PluginArtifact
import de.scandurra.osspecificplugins.model.PluginVersion
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

class ResolverServiceTest {

    private val resolver = ResolverService()

    private val linuxX64 = Platform(OperationSystem.LINUX, CpuArchitecture.X86_64)
    private val macArm = Platform(OperationSystem.MACOS, CpuArchitecture.ARM64)
    private val winX64 = Platform(OperationSystem.WINDOWS, CpuArchitecture.X86_64)

    @Test
    fun `returns null when no artifacts match platform`() {
        val v1 = PluginVersion(
            version = PluginVersion.SemVer("1.0.0"),
            releaseDate = Instant.parse("2023-01-01T00:00:00Z"),
            pluginArtifacts = listOf(
                PluginArtifact(
                    targets = PlatformSet.fromIterable(listOf(macArm)),
                    url = "https://example.com/macos-arm64-1.0.0.jar",
                    checksum = "aaa"
                )
            )
        )
        val plugin = Plugin(
            id = PluginId("p1"),
            name = "Plugin One",
            versions = listOf(v1)
        )

        val result = resolver.resolveBest(plugin, winX64)
        result.shouldBeNull()
    }

    @Test
    fun `selects newest version that has a matching artifact`() {
        val oldWithLinux = PluginVersion(
            version = PluginVersion.SemVer("1.0.0"),
            releaseDate = Instant.parse("2023-01-01T00:00:00Z"),
            pluginArtifacts = listOf(
                PluginArtifact(
                    targets = PlatformSet.fromIterable(listOf(linuxX64)),
                    url = "https://example.com/linux-x64-1.0.0.jar",
                    checksum = "l100"
                )
            )
        )
        val newerWithLinux = PluginVersion(
            version = PluginVersion.SemVer("2.0.0"),
            releaseDate = Instant.parse("2024-06-15T12:00:00Z"),
            pluginArtifacts = listOf(
                PluginArtifact(
                    targets = PlatformSet.fromIterable(listOf(linuxX64)),
                    url = "https://example.com/linux-x64-2.0.0.jar",
                    checksum = "l200"
                )
            )
        )
        val newestButNoLinux = PluginVersion(
            version = PluginVersion.SemVer("3.0.0"),
            releaseDate = Instant.parse("2025-01-01T00:00:00Z"),
            pluginArtifacts = listOf(
                PluginArtifact(
                    targets = PlatformSet.fromIterable(listOf(macArm)),
                    url = "https://example.com/macos-arm64-3.0.0.jar",
                    checksum = "m300"
                )
            )
        )

        val plugin = Plugin(
            id = PluginId("p2"),
            name = "Plugin Two",
            versions = listOf(oldWithLinux, newerWithLinux, newestButNoLinux)
        )

        val result = resolver.resolveBest(plugin, linuxX64)!!
        result.version.version.value shouldBe "2.0.0"
        result.artifact.url shouldBe "https://example.com/linux-x64-2.0.0.jar"
    }

    @Test
    fun `selects correct artifact within a version when multiple artifacts present`() {
        val v1 = PluginVersion(
            version = PluginVersion.SemVer("1.2.3"),
            releaseDate = Instant.parse("2024-12-31T23:59:59Z"),
            pluginArtifacts = listOf(
                PluginArtifact(
                    targets = PlatformSet.fromIterable(listOf(macArm)),
                    url = "https://example.com/macos-arm64-1.2.3.jar",
                    checksum = "mac"
                ),
                PluginArtifact(
                    targets = PlatformSet.fromIterable(listOf(winX64)),
                    url = "https://example.com/windows-x64-1.2.3.jar",
                    checksum = "win"
                )
            )
        )

        val plugin = Plugin(
            id = PluginId("p3"),
            name = "Plugin Three",
            versions = listOf(v1)
        )

        val resultWin = resolver.resolveBest(plugin, winX64)!!
        resultWin.version.version.value shouldBe "1.2.3"
        resultWin.artifact.checksum shouldBe "win"

        val resultMac = resolver.resolveBest(plugin, macArm)!!
        resultMac.version.version.value shouldBe "1.2.3"
        resultMac.artifact.checksum shouldBe "mac"
    }
}
