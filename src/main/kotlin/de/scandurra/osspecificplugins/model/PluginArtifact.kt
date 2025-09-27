package de.scandurra.osspecificplugins.model

data class PluginArtifact(
    val targets: PlatformSet,
    val url: String,
    val checksum: String,
)
