package de.scandurra.osspecificplugins.model

class PlatformSet private constructor(private val bits: Long) {
    enum class OperationSystem { WINDOWS, LINUX, MACOS }
    enum class CpuArchitecture { X86_64, ARM64 }
    data class Platform(val operationSystem: OperationSystem, val arch: CpuArchitecture)

    fun contains(operationSystem: OperationSystem, arch: CpuArchitecture) =
        (bits and (1L shl bitOf(operationSystem, arch))) != 0L

    fun isEmpty(): Boolean = bits == 0L
    fun isNotEmpty(): Boolean = !isEmpty()

    fun toPlatforms() = allPlatforms.filter { contains(it.operationSystem, it.arch) }

    companion object {
        private fun bitOf(operationSystem: OperationSystem, arch: CpuArchitecture) =
            operationSystem.ordinal * CpuArchitecture.entries.size + arch.ordinal

        private val allPlatforms = OperationSystem.entries.flatMap { os ->
            CpuArchitecture.entries.map { arch -> Platform(os, arch) }
        }

        fun fromIterable(targets: Iterable<Platform>): PlatformSet {
            var acc = 0L
            for (t in targets) {
                val idx = bitOf(t.operationSystem, t.arch)
                acc = acc or (1L shl idx)
            }
            return PlatformSet(acc)
        }

        fun fromPredicate(predicate: (Platform) -> Boolean) = fromIterable(allPlatforms.filter(predicate))

        fun all() = fromIterable(allPlatforms)
    }
}