package co.com.bancolombia.certification.plugins

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ParallelTestsTest extends Specification {

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

    def "build must pass if source directory is empty"() {
        File emptyFolder = new File('src/functionalTest/resources/noProps')
        emptyFolder.mkdirs()
        buildFile << """
            task parallel(type: ParallelTests) {
                srcDirName = 'src/functionalTest/resources/noProps'
            }
        """
        when:
        def result = gradle('parallel')

        then:
        assert result.task(":parallel").outcome == SUCCESS
    }

    def "build must pass if source directory is not empty and property file is ok"() {
        buildFile << """
            task parallel(type: ParallelTests) {
                srcDirName = 'src/functionalTest/resources/props'
            }
        """
        when:
        def result = gradle('parallel')

        then:
        assert result.task(":parallel").outcome == SUCCESS
    }

    def "build must create a new folder in build directory if source directory is not empty and property file is ok"() {
        buildFile << """
            task parallel(type: ParallelTests) {
                srcDirName = 'src/functionalTest/resources/props'
            }
        """
        when:
        gradle('parallel')
        def result = new File('build/src/functionalTest/resources/props/galaxyC5.log')

        then:
        assert result.exists()
    }

    def cleanup() {
        def dirToDelete = new File('build/src')
        if(dirToDelete.exists()) {
            dirToDelete.deleteDir()
        }
    }
}
