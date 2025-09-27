package de.scandurra.osspecificplugins.service

import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.PluginArtifact
import de.scandurra.osspecificplugins.model.PluginVersion
import org.springframework.stereotype.Service

@Service
class ResolverService {
    fun resolveBest(plugin: Plugin, platform: Platform): ResolvedArtifact? =
        plugin.versions
            .sortedByDescending(PluginVersion::releaseDate)
            .firstNotNullOfOrNull { version ->
                version.pluginArtifacts
                    .firstOrNull { it.targets.contains(platform.operationSystem, platform.arch) }
                    ?.let { ResolvedArtifact(version, it) }
            }

    data class ResolvedArtifact(val version: PluginVersion, val artifact: PluginArtifact)
}
