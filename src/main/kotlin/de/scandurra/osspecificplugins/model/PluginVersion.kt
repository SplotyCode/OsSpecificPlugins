package de.scandurra.osspecificplugins.model

class PluginVersion(
    val version: SemVer,
    val pluginArtifacts: List<PluginArtifact>
) {
    @JvmInline value class SemVer(val value: String)
}