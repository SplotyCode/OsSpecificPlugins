package de.scandurra.osspecificplugins.repository

import de.scandurra.osspecificplugins.model.PlatformSet
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import de.scandurra.osspecificplugins.model.Plugin
import de.scandurra.osspecificplugins.model.PluginArtifact
import de.scandurra.osspecificplugins.model.PluginVersion
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Profile("dev")
class DevSeeder(private val repo: InMemoryPluginRepository) {
    @PostConstruct
    fun seed() {
        repo.upsert(
            Plugin(
                id = Plugin.PluginId("wsl"),
                name = "Windows Subsystem Helper",
                versions = listOf(
                    PluginVersion(
                        PluginVersion.SemVer("2.1.0"),
                        Instant.parse("2024-12-04T08:07:00Z"),
                        listOf(
                            PluginArtifact(
                                targets = PlatformSet.fromPredicate { it.operationSystem == OperationSystem.WINDOWS },
                                url = "https://cdn.example/wsl-2.1.0.zip",
                                checksum = "sha256:111111111111..."
                            )
                        )
                    ),
                    PluginVersion(
                        PluginVersion.SemVer("2.0.0"),
                        Instant.parse("2020-12-04T08:07:00Z"),
                        listOf(
                            PluginArtifact(
                                targets = PlatformSet.all(),
                                url = "https://cdn.example/wsl-2.0.0.zip",
                                checksum = "sha256:222222222222..."
                            )
                        )
                    )
                )
            )
        )
        repo.upsert(
            Plugin(
                id = Plugin.PluginId("maconly"),
                name = "Mac Only Plugin",
                versions = listOf(
                    PluginVersion(
                        PluginVersion.SemVer("1.0.0"),
                        Instant.parse("2022-01-01T00:00:00Z"),
                        listOf(
                            PluginArtifact(
                                targets = PlatformSet.fromPredicate { it.operationSystem == OperationSystem.MACOS },
                                url = "https://cdn.example/maconly-1.0.0.zip",
                                checksum = "sha256:aaaaaaaaaaaa..."
                            )
                        )
                    )
                )
            )
        )
    }
}
