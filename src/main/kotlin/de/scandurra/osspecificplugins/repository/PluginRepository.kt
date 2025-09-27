package de.scandurra.osspecificplugins.repository

import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.Plugin.PluginId

interface PluginRepository {
    fun findById(id: PluginId): Plugin?
    fun findAll(): List<Plugin>
    fun upsert(plugin: Plugin)
}