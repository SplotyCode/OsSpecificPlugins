package de.scandurra.osspecificplugins.model

data class PluginArtifact(
    val targets: PlatformSet,
    val url: String,
    val checksum: String,
) {
    init {
        require(targets.isNotEmpty()) { "PlatformSet must contain at least one platform" }
    }
}
