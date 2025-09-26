package de.scandurra.osspecificplugins.service

import de.scandurra.osspecificplugins.model.PlatformSet
import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.PluginArtifact
import de.scandurra.osspecificplugins.model.PluginVersion
import org.springframework.stereotype.Service

@Service
class ResolverService {
    fun resolveBest(plugin: Plugin, input: PlatformSet.Platform): ResolvedArtifact? {
        val sorted = plugin.versions.sortedByDescending { it.releaseDate }
        for (version in sorted) {
            val match = version.pluginArtifacts.firstOrNull { it.targets.contains(input.operationSystem, input.arch) }
            if (match != null) return ResolvedArtifact(version, match)
        }
        return null
    }

    data class ResolvedArtifact(val version: PluginVersion, val artifact: PluginArtifact)
}
