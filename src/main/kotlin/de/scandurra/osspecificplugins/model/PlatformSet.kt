package de.scandurra.osspecificplugins.model

class PlatformSet private constructor(private val bits: Long) {
    enum class OS { WINDOWS, LINUX, MACOS }
    enum class Arch { X86_64, ARM64 }
    data class Platform(val os: OS, val arch: Arch)

    fun contains(os: OS, arch: Arch): Boolean = (bits and (1L shl bitOf(os, arch))) != 0L

    fun toPlatforms(): List<Platform> {
        val out = ArrayList<Platform>()
        val archCount = Arch.entries.size
        for (os in OS.entries) {
            for (arch in Arch.entries) {
                val bit = 1L shl (os.ordinal * archCount + arch.ordinal)
                if ((bits and bit) != 0L) out += Platform(os, arch)
            }
        }
        return out
    }

    companion object {
        private fun bitOf(os: OS, arch: Arch): Int = os.ordinal * Arch.entries.size + arch.ordinal

        fun fromIterable(targets: Iterable<Platform>): PlatformSet {
            var acc = 0L
            val archCount = Arch.entries.size
            for (t in targets) {
                val idx = t.os.ordinal * archCount + t.arch.ordinal
                acc = acc or (1L shl idx)
            }
            return PlatformSet(acc)
        }
    }
}