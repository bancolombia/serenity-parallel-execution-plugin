package co.com.bancolombia.certification.plugins

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.FAILED

class ParallelModuleTest extends Specification {

    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    private File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'co.com.bancolombia.certificacion.serenity-parallel-execution-plugin'
            }
        """
    }

    private BuildResult gradle(boolean isSuccessExpected, String[] arguments = ['tasks']) {
        arguments += '--stacktrace'
        def runner = GradleRunner.create()
                .withArguments(arguments)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withDebug(true)
        return isSuccessExpected ? runner.build() : runner.buildAndFail()
    }

    private BuildResult gradle(String[] arguments = ['tasks']) {
        gradle(true, arguments)
    }

    def "build must pass if clean task is called even if there is no source to delete"() {
        when:
        def result = gradle('clean')

        then:
        assert result.task(":clean").outcome != FAILED
    }

}
