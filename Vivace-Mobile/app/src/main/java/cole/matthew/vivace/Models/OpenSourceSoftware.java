package cole.matthew.vivace.Models;

/**
 * An object representing a third party library used by Vivace.
 */
public class OpenSourceSoftware {
    private final String _name;
    private final String _url;
    private final String _license;

    /**
     * Creates an instance of a {@link OpenSourceSoftware} object.
     *
     * @param name    The name of the library.
     * @param url     The url where the library is hosted.
     * @param license The license the library is issued under.
     *
     * @exception IllegalArgumentException When the name, url or license is null or empty.
     */
    public OpenSourceSoftware(String name, String url, String license)
            throws IllegalArgumentException
    {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Third party libraries have a name, but one was not provided.");
        } else if (url == null || url.equals("")) {
            throw new IllegalArgumentException("Third party libraries are hosted somewhere, but a url was not provided.");
        } else if (license == null || license.equals("")) {
            throw new IllegalArgumentException("Third party libraries have some kind of license, but one was not provided.");
        }

        _name = name;
        _url = url;
        _license = license;
    }

    /**
     * Gets the name of the third party library.
     *
     * @return The name of the library.
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the url where the third party library is hosted.
     *
     * @return The url where the library is hosted.
     */
    public String getUrl() {
        return _url;
    }

    /**
     * Gets the name of the license the third party library utilizes.
     *
     * @return The name of the license.
     */
    public String getLicense() {
        return _license;
    }
}