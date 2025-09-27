package de.scandurra.osspecificplugins.service

import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.PluginArtifact
import de.scandurra.osspecificplugins.model.PluginVersion
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ResolverService {
    private val logger = LoggerFactory.getLogger(ResolverService::class.java)

    fun resolveBest(plugin: Plugin, platform: Platform): ResolvedArtifact? {
        val result = plugin.versions
            .sortedByDescending(PluginVersion::releaseDate)
            .firstNotNullOfOrNull { version ->
                version.pluginArtifacts
                    .firstOrNull { it.targets.contains(platform.operationSystem, platform.arch) }
                    ?.let { ResolvedArtifact(version, it) }
            }
        if (result == null && logger.isDebugEnabled) {
            logger.debug(
                "No compatible artifact found for plugin id={}, name={} on platform os={}, arch={}",
                plugin.id,
                plugin.name,
                platform.operationSystem,
                platform.arch,
            )
        }
        return result
    }

    data class ResolvedArtifact(val version: PluginVersion, val artifact: PluginArtifact)
}
