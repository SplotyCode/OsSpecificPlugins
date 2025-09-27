package de.scandurra.osspecificplugins.model

class Plugin (
    val id: PluginId,
    val name: String,
    val versions: List<PluginVersion>,
) {
    @JvmInline value class PluginId(val value: String)
}
