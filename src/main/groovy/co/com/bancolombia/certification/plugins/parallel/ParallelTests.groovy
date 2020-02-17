package co.com.bancolombia.certification.plugins.parallel


import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject

class ParallelTests extends DefaultTask {

    @Input
    final WorkerExecutor workerExecutor

    @Input
    String srcDirName

    @Inject
    ParallelTests(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor
    }

    @TaskAction
    void parallelTestsExecution() {
        def scrDir = new File(srcDirName)
        def documentationDirectory = "target"
        scrDir.eachFileRecurse(FileType.FILES) { file ->
            workerExecutor.submit(ParallelTestsExecutor.class) { WorkerConfiguration config ->
                config.isolationMode = IsolationMode.NONE
                config.params file, srcDirName, documentationDirectory
            }
        }
    }
}
