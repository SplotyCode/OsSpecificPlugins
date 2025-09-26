package de.scandurra.osspecificplugins.model

import java.time.Instant

class PluginVersion(
    val version: SemVer,
    val releaseDate: Instant,
    val pluginArtifacts: List<PluginArtifact>
) {
    @JvmInline value class SemVer(val value: String)
}