package cole.matthew.vivace.Models;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cole.matthew.vivace.R;

/**
 * Helper class for providing the open source libraries used to build Vivace.
 */
public final class OpenSourceSoftwareContent {
    private List<OpenSourceSoftware> _openSourceSoftware;

    /**
     * Creates an instance of a {@link OpenSourceSoftwareContent} object.
     * @param context The context to attach the display to.
     * @throws IOException
     * @throws XmlPullParserException
     */
    public OpenSourceSoftwareContent(Context context)
            throws IOException, XmlPullParserException
    {
        _openSourceSoftware = new ArrayList<>();
        loadSoftwareFromResource(context);
    }

    /**
     * Gets the third party libraries used to build Vivace.
     * @return A {@link List} of {@link OpenSourceSoftware}.
     */
    public List<OpenSourceSoftware> getOpenSourceSoftware() {
        return _openSourceSoftware;
    }

    /**
     * Parses the third party libraries used by Vivace from an xml resource file.
     * @param context Context to get the xml resource from.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private void loadSoftwareFromResource(Context context)
            throws IOException, XmlPullParserException
    {
        try (XmlResourceParser parser = context.getResources().getXml(R.xml.oss)) {
            int xmlTagType;
            while ((xmlTagType = parser.next()) != XmlResourceParser.END_DOCUMENT &&
                   xmlTagType != XmlResourceParser.START_TAG) {
                // parse until start tag is found
            }

            String openTagName = parser.getName();
            if (!"oss".equals(openTagName)) {
                throw new XmlPullParserException("XML Document must start with <oss> tag; found " +
                                                 openTagName + " at " + parser.getPositionDescription());
            }

            final int outerDepth = parser.getDepth();
            while ((xmlTagType = parser.next()) != XmlResourceParser.END_DOCUMENT && (xmlTagType != XmlResourceParser.END_TAG || parser.getDepth() > outerDepth)) {
                if (xmlTagType != XmlResourceParser.END_TAG && xmlTagType != XmlResourceParser.TEXT) {
                    String nodeName = parser.getName();
                    if ("item".equals(nodeName)) {
                        String softwareName = null;
                        String softwareUrl = null;
                        StringBuilder licenseBuffer = new StringBuilder();

                        final int innerDepth = parser.getDepth();
                        while ((xmlTagType = parser.next()) != XmlResourceParser.END_DOCUMENT && (xmlTagType != XmlResourceParser.END_TAG || parser.getDepth() > innerDepth)) {
                            if (xmlTagType != XmlResourceParser.END_TAG && xmlTagType != XmlResourceParser.TEXT) {
                                String innerNodeName = parser.getName();
                                if ("name".equals(innerNodeName)) {
                                    if (parser.next() != XmlResourceParser.TEXT)
                                        continue;

                                    softwareName = parser.getText();
                                } else if ("url".equals(innerNodeName)) {
                                    if (parser.next() != XmlResourceParser.TEXT)
                                        continue;

                                    softwareUrl = parser.getText();
                                } else if ("license".equals(innerNodeName)) {
                                    if (parser.next() != XmlResourceParser.TEXT)
                                        continue;

                                    String licenseName = parser.getText();
                                    try (InputStream licenseStream = context.getAssets().open("Licenses/" + licenseName + ".txt");
                                         InputStreamReader licenseReader = new InputStreamReader(licenseStream);
                                         BufferedReader reader = new BufferedReader(licenseReader))
                                    {
                                        int charCode;
                                        while ((charCode = reader.read()) >= 0) {
                                            licenseBuffer.append((char)charCode);
                                        }
                                    }
                                } else {
                                    skipCurrentTag(parser);
                                }
                            }
                        }

                        _openSourceSoftware.add(new OpenSourceSoftware(softwareName, softwareUrl, licenseBuffer.toString()));
                    } else {
                        skipCurrentTag(parser);
                    }
                }
            }
        } catch (IllegalArgumentException | XmlPullParserException | IOException e) {
            // TODO: Implement logging and exception handling
            Log.e("", e.getMessage());
            throw e;
        }
    }

    /**
     * Skips forward to the next XML tag.
     * @param parser the parser to skip forward in.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private void skipCurrentTag(XmlResourceParser parser)
            throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        int type;

        while ((type = parser.next()) != XmlResourceParser.END_DOCUMENT &&
               (type != XmlResourceParser.END_TAG || parser.getDepth() > outerDepth)) {
            //skip all other nodes
        }
    }

    /**
     * An object representing a third party library used by Vivace.
     */
    public static class OpenSourceSoftware {
        private final String _name;
        private final String _url;
        private final String _license;

        /**
         * Creates an instance of a {@link OpenSourceSoftware} object.
         * @param name The name of the library.
         * @param url The url where the library is hosted.
         * @param license The license the library is issued under.
         * @throws IllegalArgumentException When the name, url or license is null or empty.
         */
        OpenSourceSoftware(String name, String url, String license)
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
         * @return The name of the library.
         */
        public String getName() {
            return _name;
        }

        /**
         * Gets the url where the third party library is hosted.
         * @return The url where the library is hosted.
         */
        public String getUrl() {
            return _url;
        }

        /**
         * Gets the name of the license the third party library utilizes.
         * @return The name of the license.
         */
        public String getLicense() {
            return _license;
        }
    }
}
