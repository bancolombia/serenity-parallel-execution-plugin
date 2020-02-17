package co.com.bancolombia.certification.plugins


import co.com.bancolombia.certification.plugins.parallel.ParallelModule
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

import static org.powermock.api.mockito.PowerMockito.mockStatic

@RunWith(PowerMockRunner.class)
@PrepareForTest(ParallelModule.class)
class ParallelPluginTest {

    @Test
    void mustLoadTheProjectIntoParallelModule() {
        mockStatic(ParallelModule.class)
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'co.com.bancolombia.certificacion.serenity-parallel-execution-plugin'
        PowerMockito.verifyStatic(ParallelModule.class, Mockito.times(1))
        ParallelModule.load(project)
    }
}
