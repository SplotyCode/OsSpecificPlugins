package de.scandurra.osspecificplugins.model

class PlatformSet private constructor(private val bits: Long) {
    enum class OperationSystem { WINDOWS, LINUX, MACOS }
    enum class CpuArchitecture { X86_64, ARM64 }
    data class Platform(val operationSystem: OperationSystem, val arch: CpuArchitecture)

    fun contains(operationSystem: OperationSystem, arch: CpuArchitecture): Boolean = (bits and (1L shl bitOf(operationSystem, arch))) != 0L

    fun toPlatforms(): List<Platform> {
        val out = ArrayList<Platform>()
        for (os in OperationSystem.entries) {
            for (arch in CpuArchitecture.entries) {
                val bit = 1L shl bitOf(os, arch)
                if ((bits and bit) != 0L) out += Platform(os, arch)
            }
        }
        return out
    }

    companion object {
        private fun bitOf(operationSystem: OperationSystem, arch: CpuArchitecture): Int = operationSystem.ordinal * CpuArchitecture.entries.size + arch.ordinal

        fun fromIterable(targets: Iterable<Platform>): PlatformSet {
            var acc = 0L
            for (t in targets) {
                val idx = bitOf(t.operationSystem, t.arch)
                acc = acc or (1L shl idx)
            }
            return PlatformSet(acc)
        }
    }
}