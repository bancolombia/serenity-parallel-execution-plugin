package co.com.bancolombia.certification.plugins


import co.com.bancolombia.certification.plugins.parallel.ParallelModule
import org.gradle.api.Plugin
import org.gradle.api.Project

class ParallelPlugin implements Plugin<Project>{

    void apply(Project project) {
        ParallelModule.load(project)
    }
}
