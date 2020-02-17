package co.com.bancolombia.certification.plugins.parallel

import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.MatcherAssert.assertThat

class ParallelModuleTest {

    private Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'co.com.bancolombia.certificacion.serenity-parallel-execution-plugin'
        ParallelModule.load(project)
    }

    @Test
    void mustSetUpProjectExtensions() {
        assertThat(project.ext.ParallelTests, is(equalTo(ParallelTests)))
    }

    @Test
    void mustSetUpProjectPlugins() {
        assertThat(project.plugins.findPlugin('base').getClass(), is(equalTo(BasePlugin.class)))
    }
}
