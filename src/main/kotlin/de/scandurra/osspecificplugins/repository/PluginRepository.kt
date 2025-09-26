package de.scandurra.osspecificplugins.repository

import de.scandurra.osspecificplugins.model.Plugin

interface PluginRepository {
    fun findById(id: Plugin.PluginId): Plugin?
    fun findAll(): List<Plugin>
    fun upsert(plugin: Plugin)
}