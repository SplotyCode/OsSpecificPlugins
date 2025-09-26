package de.scandurra.osspecificplugins.model

import de.scandurra.osspecificplugins.model.PlatformSet.Arch
import de.scandurra.osspecificplugins.model.PlatformSet.OS
import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import org.junit.jupiter.api.Test
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class PlatformSetTest {
    val allPlatforms = OS.entries.flatMap { os ->
        Arch.entries.map { arch -> Platform(os, arch) }
    }

    @Test
    fun `empty set`() {
        val set = PlatformSet.fromIterable(emptyList())
        set.toPlatforms().shouldBeEmpty()
        set.contains(OS.WINDOWS, Arch.X86_64) shouldBe false
        set.contains(OS.LINUX, Arch.ARM64) shouldBe false
    }

    @Test
    fun `single platform`() {
        val p = Platform(OS.LINUX, Arch.X86_64)
        val set = PlatformSet.fromIterable(listOf(p))
        allPlatforms.forEach { (os, arch) ->
            val expected = (os == p.os && arch == p.arch)
            set.contains(os, arch) shouldBe expected
        }
        set.toPlatforms() shouldContainExactlyInAnyOrder listOf(p)
    }

    @Test
    fun `all platforms`() {
        val set = PlatformSet.fromIterable(allPlatforms)
        allPlatforms.forEach { (os, arch) ->
            set.contains(os, arch) shouldBe true
        }
        set.toPlatforms() shouldContainExactlyInAnyOrder allPlatforms
    }

    @Test
    fun `toPlatforms and fromIterable roundtrip`() {
        val initial = listOf(
            Platform(OS.MACOS, Arch.ARM64),
            Platform(OS.WINDOWS, Arch.X86_64)
        )
        val set = PlatformSet.fromIterable(initial)
        val roundTrip = PlatformSet.fromIterable(set.toPlatforms())
        roundTrip.toPlatforms() shouldContainExactlyInAnyOrder initial
        allPlatforms.forEach { (os, arch) ->
            val before = set.contains(os, arch)
            val after = roundTrip.contains(os, arch)
            after shouldBe before
        }
    }
}
