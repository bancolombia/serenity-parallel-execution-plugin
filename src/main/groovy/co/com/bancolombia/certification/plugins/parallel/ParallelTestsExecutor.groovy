package co.com.bancolombia.certification.plugins.parallel


import co.com.bancolombia.certification.plugins.exceptions.OperativeSystemNotFoundException
import co.com.bancolombia.certification.plugins.exceptions.ParallelTestsExecutorException
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject

import static co.com.bancolombia.certification.plugins.exceptions.ParallelTestsExecutorException.WRONG_EXECUTOR_MESSAGE
import static co.com.bancolombia.certification.plugins.exceptions.OperativeSystemNotFoundException.OS_NOT_FOUND_MESSAGE
import static org.apache.commons.io.FilenameUtils.getBaseName

class ParallelTestsExecutor implements Runnable {

    private static Logger logger
    private File propertiesFile
    private String srcDirName
    private String documentationBaseDir

    @Inject
    ParallelTestsExecutor(File propertiesFile, String srcDirName, String documentationBaseDir) {
        this.propertiesFile = propertiesFile
        this.srcDirName = srcDirName
        this.documentationBaseDir = documentationBaseDir
        logger = LoggerFactory.getLogger(ParallelTestsExecutor.class.getSimpleName())
    }

    @Override
    void run() {
        String propertiesFileName = getBaseName(propertiesFile.path)
        String documentationDir = documentationBaseDir+File.separator+srcDirName+File.separator+propertiesFileName
        String binResultsDir = documentationBaseDir+File.separator+'binary'+File.separator+propertiesFileName
        String command = getCommand(documentationDir, binResultsDir)
        logger.info("Command to execute: " + command)
        try {
            def process = command.execute()
            StringWriter output = new StringWriter()
            process.waitForProcessOutput(output, new StringWriter())
            writeIntoLogFile(propertiesFileName, output)
        } catch(IOException e) {
            throw new ParallelTestsExecutorException(WRONG_EXECUTOR_MESSAGE, e)
        }
    }

    private String getCommand(String documentationDir, String binResultsDir) {
        String testsToRun = getTestsToRun()
        if(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            return "./gradlew test -Dproperties=$propertiesFile $testsToRun -Dserenity.outputDirectory=$documentationDir -DbinResultsDir=$binResultsDir aggregate --stacktrace"
        } else if(SystemUtils.IS_OS_WINDOWS) {
            return "cmd /c gradlew test -Dproperties=$propertiesFile $testsToRun -Dserenity.outputDirectory=$documentationDir aggregate -DbinResultsDir=$binResultsDir --stacktrace"
        } else {
            throw new OperativeSystemNotFoundException(OS_NOT_FOUND_MESSAGE)
        }
    }

    private void writeIntoLogFile(String propertiesFileName, StringWriter out) {
        def logFileName = propertiesFileName + ".log"
        File logsDir = new File("$Project.DEFAULT_BUILD_DIR_NAME$File.separator$srcDirName")
        if (!logsDir.exists()) {
            logsDir.mkdirs()
        }
        FileWriter log = new FileWriter("$logsDir$File.separator$logFileName")
        log.write(out.toString())
        log.close()
    }

    private String getTestsToRun(){
        String key = "testsToRun"
        String commandToExecute = ""
        Properties properties = new Properties()
        properties.load(this.propertiesFile.newReader())
        if(properties.containsKey(key)){
            commandToExecute = "--tests=" + properties.get(key)
        }
        return commandToExecute
    }

}
