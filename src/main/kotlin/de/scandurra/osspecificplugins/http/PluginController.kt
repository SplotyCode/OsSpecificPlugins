package de.scandurra.osspecificplugins.http

import de.scandurra.osspecificplugins.model.PlatformSet
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.repository.PluginRepository
import de.scandurra.osspecificplugins.service.ResolverService
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PluginController(
    private val repo: PluginRepository,
    private val resolver: ResolverService,
) {
    @GetMapping("/plugins/{id}")
    fun getPlugin(@PathVariable id: String): ResponseEntity<Any> {
        val plugin = repo.findById(Plugin.PluginId(id)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(plugin)
    }

    @GetMapping("/plugins/{id}/resolve")
    fun resolve(
        @PathVariable id: String,
        @RequestParam os: OperationSystem,
        @RequestParam arch: PlatformSet.CpuArchitecture,
    ): ResponseEntity<Any> {
        val plugin = repo.findById(Plugin.PluginId(id)) ?: return ResponseEntity.notFound().build()
        val res = resolver.resolveBest(plugin, PlatformSet.Platform(os, arch))
            ?: return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(mapOf("error" to "No compatible variant"))
        return ResponseEntity.ok()
            .eTag(res.version.version.value)
            .cacheControl(CacheControl.noCache())
            .body(ResolveResponse(plugin.id.value, res.version.version.value, res.artifact.url, res.artifact.checksum))
    }

    data class ResolveResponse(val pluginId: String, val version: String, val url: String, val checksum: String)
}