package de.scandurra.osspecificplugins.model

import de.scandurra.osspecificplugins.model.PlatformSet.CpuArchitecture
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import org.junit.jupiter.api.Test
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class PlatformSetTest {
    val allPlatforms = OperationSystem.entries.flatMap { os ->
        CpuArchitecture.entries.map { arch -> Platform(os, arch) }
    }

    @Test
    fun `empty set`() {
        val set = PlatformSet.fromIterable(emptyList())
        set.toPlatforms().shouldBeEmpty()
        set.contains(OperationSystem.WINDOWS, CpuArchitecture.X86_64) shouldBe false
        set.contains(OperationSystem.LINUX, CpuArchitecture.ARM64) shouldBe false
        set.isEmpty() shouldBe true
        set.isNotEmpty() shouldBe false
    }

    @Test
    fun `single platform`() {
        val p = Platform(OperationSystem.LINUX, CpuArchitecture.X86_64)
        val set = PlatformSet.fromIterable(listOf(p))
        allPlatforms.forEach { (os, arch) ->
            val expected = (os == p.operationSystem && arch == p.arch)
            set.contains(os, arch) shouldBe expected
        }
        set.toPlatforms() shouldContainExactlyInAnyOrder listOf(p)
        set.isEmpty() shouldBe false
        set.isNotEmpty() shouldBe true
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
            Platform(OperationSystem.MACOS, CpuArchitecture.ARM64),
            Platform(OperationSystem.WINDOWS, CpuArchitecture.X86_64)
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
