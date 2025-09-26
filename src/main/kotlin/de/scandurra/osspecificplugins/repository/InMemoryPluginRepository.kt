package de.scandurra.osspecificplugins.repository

import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.Plugin.PluginId
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryPluginRepository : PluginRepository {
    private val map = ConcurrentHashMap<PluginId, Plugin>()

    override fun findById(id: PluginId) = map[id]
    override fun findAll() = map.values.toList()
    override fun upsert(plugin: Plugin) { map[plugin.id] = plugin }
}
