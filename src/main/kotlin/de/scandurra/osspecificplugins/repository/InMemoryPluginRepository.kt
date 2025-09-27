package de.scandurra.osspecificplugins.repository

import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.Plugin.PluginId
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory

@Repository
class InMemoryPluginRepository : PluginRepository {
    private val logger = LoggerFactory.getLogger(InMemoryPluginRepository::class.java)
    private val map = ConcurrentHashMap<PluginId, Plugin>()

    override fun findById(id: PluginId) = map[id]
    override fun findAll() = map.values.toList()
    override fun upsert(plugin: Plugin) {
        val previous = map.put(plugin.id, plugin)
        if (logger.isDebugEnabled) {
            if (previous == null) {
                logger.debug("Upsert insert: id={}, name='{}', versions={}", plugin.id.value, plugin.name, plugin.versions.size)
            } else {
                logger.debug("Upsert update: id={}, name='{}', versions={}, previousVersions={}", plugin.id.value, plugin.name, plugin.versions.size, previous.versions.size)
            }
        }
    }
}
