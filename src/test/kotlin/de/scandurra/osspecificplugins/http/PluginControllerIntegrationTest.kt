package de.scandurra.osspecificplugins.http

import de.scandurra.osspecificplugins.model.PlatformSet.CpuArchitecture
import de.scandurra.osspecificplugins.model.PlatformSet.OperationSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class PluginControllerIntegrationTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    @Test
    fun `get plugin - existing returns 200`() {
        mockMvc.get("/api/plugins/wsl")
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            }
    }

    @Test
    fun `get plugin - missing returns 404`() {
        mockMvc.get("/api/plugins/missing")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `resolve - windows prefers latest windows-specific 2_1_0`() {
        mockMvc.get("/api/plugins/wsl/resolve") {
            param("os", OperationSystem.WINDOWS.name)
            param("arch", CpuArchitecture.X86_64.name)
        }.andExpect {
            status { isOk() }
            /* Apparently this value should be quoted (HTTP RFC 7232) */
            header { string("ETag", "\"2.1.0\"") }
            header { string("Cache-Control", "no-cache") }
            jsonPath("$.pluginId") { value("wsl") }
            jsonPath("$.version") { value("2.1.0") }
            jsonPath("$.url") { value("https://cdn.example/wsl-2.1.0.zip") }
            jsonPath("$.checksum") { value("sha256:111111111111...") }
        }
    }

    @Test
    fun `resolve - linux falls back to 2_0_0`() {
        mockMvc.get("/api/plugins/wsl/resolve") {
            param("os", OperationSystem.LINUX.name)
            param("arch", CpuArchitecture.ARM64.name)
        }.andExpect {
            status { isOk() }
            header { string("ETag", "\"2.0.0\"") }
            jsonPath("$.version") { value("2.0.0") }
            jsonPath("$.url") { value("https://cdn.example/wsl-2.0.0.zip") }
            jsonPath("$.checksum") { value("sha256:222222222222...") }
        }
    }

    @Test
    fun `resolve - missing plugin returns 404`() {
        mockMvc.get("/api/plugins/unknown/resolve") {
            param("os", OperationSystem.WINDOWS.name)
            param("arch", CpuArchitecture.X86_64.name)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `resolve - no compatible variant returns 406 with error body`() {
        mockMvc.get("/api/plugins/maconly/resolve") {
            param("os", OperationSystem.WINDOWS.name)
            param("arch", CpuArchitecture.X86_64.name)
        }.andExpect {
            status { isNotAcceptable() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.error") { value("No compatible variant") }
        }
    }
}
