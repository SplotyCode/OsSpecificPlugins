package de.scandurra.osspecificplugins.model

import java.time.Instant

data class PluginVersion(
    val version: SemVer,
    val releaseDate: Instant,
    val pluginArtifacts: List<PluginArtifact>,
) {
    init {
        var seen = PlatformSet.empty()
        for (artifact in pluginArtifacts) {
            val targets = artifact.targets
            val overlap = targets intersect seen
            require(overlap.isEmpty()) {
                "Multiple PluginArtifacts target the same platform " +
                        "(${overlap.toPlatforms()}) in version ${version.value}"
            }
            seen = seen union targets
        }
    }

    @JvmInline value class SemVer(val value: String)
}