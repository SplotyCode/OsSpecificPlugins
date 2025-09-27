package de.scandurra.osspecificplugins.cli

import de.scandurra.osspecificplugins.model.PlatformSet.CpuArchitecture
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.PlatformSet.Platform
import de.scandurra.osspecificplugins.model.Plugin.PluginId
import de.scandurra.osspecificplugins.repository.PluginRepository
import de.scandurra.osspecificplugins.service.ResolverService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class ResolveCli(
    private val repo: PluginRepository,
    private val resolver: ResolverService,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        if (!args.containsOption("cli")) return

        val args = args.nonOptionArgs
        if (args.size < 3) {
            println("usage: --cli <pluginId> <OS> <ARCH>")
            return
        }
        val p = repo.findById(PluginId(args[0])) ?: return println("plugin ${args[0]} not found")
        val os = OperationSystem.valueOf(args[1])
        val arch = CpuArchitecture.valueOf(args[2])
        val res = resolver.resolveBest(p, Platform(os, arch)) ?: return println("no compatible variant")
        println("Version: ${res.version.version}")
        println("Url: ${res.artifact.url}")
    }
}
