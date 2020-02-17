package co.com.bancolombia.certification.plugins.parallel

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class ParallelModule {

    static void load(Project project) {
        project.ext.ParallelTests = ParallelTests

        project.plugins.apply('base')

        project.tasks.withType(Test) {
            reports.html.enabled = false
            reports.junitXml.enabled = false
            systemProperty 'properties', System.getProperty('properties')
            systemProperty 'serenity.outputDirectory', System.getProperty('serenity.outputDirectory')
            systemProperty 'binResultsDir', System.getProperty('binResultsDir')
            binResultsDir = new File(System.getProperty('binResultsDir').toString())
        }

        project.clean.doFirst {
            delete 'target'
        }

    }
}
