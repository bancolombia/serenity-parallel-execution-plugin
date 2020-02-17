package co.com.bancolombia.certification.plugins.exceptions

class ParallelTestsExecutorException extends RuntimeException {

    static final String WRONG_EXECUTOR_MESSAGE = 'You must have gradle wrapper installed in your project in order to run this plugin correctly'

    ParallelTestsExecutorException(String message, Throwable cause) {
        super(message, cause)
    }

}
