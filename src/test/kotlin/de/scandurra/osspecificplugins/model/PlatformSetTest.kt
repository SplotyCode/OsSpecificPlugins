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
    fun `empty factory`() {
        val set = PlatformSet.empty()
        set.toPlatforms().shouldBeEmpty()
        set.contains(OperationSystem.WINDOWS, CpuArchitecture.X86_64) shouldBe false
        set.contains(OperationSystem.MACOS, CpuArchitecture.ARM64) shouldBe false
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
    fun `union of sets`() {
        val p1 = Platform(OperationSystem.LINUX, CpuArchitecture.X86_64)
        val p2 = Platform(OperationSystem.MACOS, CpuArchitecture.ARM64)
        val s1 = PlatformSet.fromIterable(listOf(p1))
        val s2 = PlatformSet.fromIterable(listOf(p2))
        val union = s1 union s2
        union.toPlatforms() shouldContainExactlyInAnyOrder listOf(p1, p2)
        allPlatforms.forEach { (os, arch) ->
            val expected = (os == p1.operationSystem && arch == p1.arch) || (os == p2.operationSystem && arch == p2.arch)
            union.contains(os, arch) shouldBe expected
        }
    }

    @Test
    fun `intersection of sets`() {
        val common = Platform(OperationSystem.WINDOWS, CpuArchitecture.X86_64)
        val onlyLeft = Platform(OperationSystem.LINUX, CpuArchitecture.X86_64)
        val onlyRight = Platform(OperationSystem.MACOS, CpuArchitecture.ARM64)

        val left = PlatformSet.fromIterable(listOf(common, onlyLeft))
        val right = PlatformSet.fromIterable(listOf(common, onlyRight))

        val inter = left intersect right
        inter.toPlatforms() shouldContainExactlyInAnyOrder listOf(common)
        inter.isNotEmpty() shouldBe true

        val disjoint = PlatformSet.fromIterable(listOf(onlyLeft)) intersect PlatformSet.fromIterable(listOf(onlyRight))
        disjoint.isEmpty() shouldBe true
        disjoint.toPlatforms().shouldBeEmpty()
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
