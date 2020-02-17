package co.com.bancolombia.certification.plugins.parallel


import co.com.bancolombia.certification.plugins.exceptions.OperativeSystemNotFoundException
import co.com.bancolombia.certification.plugins.exceptions.ParallelTestsExecutorException
import org.apache.commons.lang3.SystemUtils
import org.codehaus.groovy.runtime.ProcessGroovyMethods
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.hamcrest.io.FileMatchers.aFileNamed
import static org.hamcrest.io.FileMatchers.anExistingFile
import static org.mockito.Mockito.mock
import static org.powermock.api.mockito.PowerMockito.mockStatic
import static org.powermock.api.mockito.PowerMockito.when

@RunWith(PowerMockRunner.class)
@PrepareForTest([ProcessGroovyMethods.class, SystemUtils.class])
class ParallelTestsExecutorTest {

    private File propertiesFile
    private String srcDirName = "props"
    private String documentationBaseDir = "resources"
    private Process process
    private ParallelTestsExecutor parallelTestsExecutor

    @Before
    void setUp() {
        process = mock(Process.class)
        propertiesFile = new File(getClass().getClassLoader().getResource("props/properties.properties").getFile())
        parallelTestsExecutor = new ParallelTestsExecutor(propertiesFile, srcDirName, documentationBaseDir)
    }

    @Test
    void mustCreateANewLogFileWhenPropertiesIsRead() {
        mockStatic(ProcessGroovyMethods.class)
        when(ProcessGroovyMethods.execute(Mockito.anyString())).thenReturn(process)
        parallelTestsExecutor.run()
        File file = new File("build/props/properties.log")
        assertThat(file, anExistingFile())
        assertThat(file, aFileNamed(equalToIgnoringCase("properties.log")))
    }

    @Test(expected = ParallelTestsExecutorException.class)
    void mustThrowExceptionIfCommandCantBeExecutedProperly() {
        mockStatic(ProcessGroovyMethods.class)
        when(ProcessGroovyMethods.execute(Mockito.anyString())).thenThrow(new IOException())
        parallelTestsExecutor.run()
    }

    @Test
    void mustReturnEmptyStringIfPropertiesFileDoesNotContainsTestsToRunProperty() {
        String command = Whitebox.invokeMethod(parallelTestsExecutor, "getTestsToRun")
        assertThat(command, is(equalTo("")))
    }

    @Test
    void mustReturnNewCommandIfPropertiesFileContainsTestsToRunProperty() {
        propertiesFile = new File(getClass().getClassLoader().getResource("props/propertiesWithTestKey.properties").getFile())
        ParallelTestsExecutor parallelTestsExecutor = new ParallelTestsExecutor(propertiesFile, srcDirName, documentationBaseDir)
        String command = Whitebox.invokeMethod(parallelTestsExecutor, "getTestsToRun")
        assertThat(command, is(equalTo("--tests=Authentication")))
    }

    @Test
    void mustReturnAppropriateCommandForMac() {
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_MAC"), true)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_LINUX"), false)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_WINDOWS"), false)
        String expectedCommand = "./gradlew test -Dproperties=$propertiesFile  -Dserenity.outputDirectory=$documentationBaseDir -DbinResultsDir=$srcDirName aggregate --stacktrace"
        String command = Whitebox.invokeMethod(parallelTestsExecutor, "getCommand", documentationBaseDir, srcDirName)
        assertThat(command, is(equalTo(expectedCommand)))
    }

    @Test
    void mustReturnAppropriateCommandForLinux() {
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_MAC"), false)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_LINUX"), true)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_WINDOWS"), false)
        String expectedCommand = "./gradlew test -Dproperties=$propertiesFile  -Dserenity.outputDirectory=$documentationBaseDir -DbinResultsDir=$srcDirName aggregate --stacktrace"
        String command = Whitebox.invokeMethod(parallelTestsExecutor, "getCommand", documentationBaseDir, srcDirName)
        assertThat(command, is(equalTo(expectedCommand)))
    }

    @Test
    void mustReturnAppropriateCommandForWindows() {
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_MAC"), false)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_LINUX"), false)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_WINDOWS"), true)
        String expectedCommand = "cmd /c gradlew test -Dproperties=$propertiesFile  -Dserenity.outputDirectory=$documentationBaseDir aggregate -DbinResultsDir=$srcDirName --stacktrace"
        String command = Whitebox.invokeMethod(parallelTestsExecutor, "getCommand", documentationBaseDir, srcDirName)
        assertThat(command, is(equalTo(expectedCommand)))
    }


    @Test(expected = OperativeSystemNotFoundException.class)
    void mustThrowExceptionIfOperativeSystemIsNotSupported() {
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_MAC"), false)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_LINUX"), false)
        setFinalStatic(SystemUtils.class.getDeclaredField("IS_OS_WINDOWS"), false)
        Whitebox.invokeMethod(parallelTestsExecutor, "getCommand", documentationBaseDir, srcDirName)
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true)
        Field modifiersField = Field.class.getDeclaredField("modifiers")
        modifiersField.setAccessible(true)
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL)
        field.set(null, newValue)
    }

    @After
    void tearDown() {
        File dir = new File("build/props/")
        dir.deleteDir()
    }
}
