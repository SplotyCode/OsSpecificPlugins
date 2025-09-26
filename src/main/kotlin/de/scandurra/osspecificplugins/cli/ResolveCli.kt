package de.scandurra.osspecificplugins.cli

import de.scandurra.osspecificplugins.model.PlatformSet.CpuArchitecture
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import de.scandurra.osspecificplugins.model.Plugin.PluginId
import de.scandurra.osspecificplugins.repository.PluginRepository
import de.scandurra.osspecificplugins.service.ResolverService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ResolveCli(
    private val repo: PluginRepository,
    private val resolver: ResolverService
) : CommandLineRunner {
    override fun run(args: Array<String>) {
        if (args.firstOrNull() != "--cli" || args.size != 4) return
        val p = repo.findById(PluginId(args[1])) ?: return println("plugin not found")
        val os = OperationSystem.valueOf(args[2]);
        val arch = CpuArchitecture.valueOf(args[3])
        val res = resolver.resolveBest(p, Platform(os, arch))
        println(res ?: "no compatible variant")
    }
}
