package co.com.bancolombia.certification.plugins.exceptions

class OperativeSystemNotFoundException extends Exception {

    static final String OS_NOT_FOUND_MESSAGE = 'The operative system was not recognized, this plugin is available for MAC, LINUX and WINDOWS only, founded: ' + System.getProperty("os.name")

    OperativeSystemNotFoundException(String message) {
        super(message)
    }

}
