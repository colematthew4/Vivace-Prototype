package cole.matthew.vivace.Models;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
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
     *
     * @param context The context to attach the display to.
     *
     * @exception IOException
     * @exception XmlPullParserException
     */
    public OpenSourceSoftwareContent(Context context)
            throws IOException, XmlPullParserException
    {
        _openSourceSoftware = new ArrayList<>();
        loadSoftwareFromResource(context);
    }

    /**
     * Gets the third party libraries used to build Vivace.
     *
     * @return A {@link List} of {@link OpenSourceSoftware}.
     */
    public List<OpenSourceSoftware> getOpenSourceSoftware() {
        return _openSourceSoftware;
    }

    /**
     * Parses the third party libraries used by Vivace from an xml resource file.
     *
     * @param context Context to get the xml resource from.
     *
     * @exception IOException
     * @exception XmlPullParserException
     */
    private void loadSoftwareFromResource(Context context)
            throws IOException, XmlPullParserException
    {
        try (XmlResourceParser xmlParser = context.getResources().getXml(R.xml.oss)) {
            int xmlTagType;
            while ((xmlTagType = xmlParser.next()) != XmlResourceParser.END_DOCUMENT && xmlTagType != XmlResourceParser.START_TAG) {
                // parse until start tag is found
            }

            String openTagName = xmlParser.getName();
            if (!"oss".equals(openTagName)) {
                throw new XmlPullParserException("XML Document must start with <oss> tag; found " + openTagName + " at " + xmlParser.getPositionDescription());
            }

            final int outerDepth = xmlParser.getDepth();
            while ((xmlTagType = xmlParser.next()) != XmlResourceParser.END_DOCUMENT && (xmlTagType != XmlResourceParser.END_TAG || xmlParser.getDepth() > outerDepth)) {
                if (xmlTagType != XmlResourceParser.END_TAG && xmlTagType != XmlResourceParser.TEXT) {
                    String nodeName = xmlParser.getName();
                    if ("item".equals(nodeName)) {
                        String softwareName = null;
                        String softwareUrl = null;
                        String license = null;

                        final int innerDepth = xmlParser.getDepth();
                        while ((xmlTagType = xmlParser.next()) != XmlResourceParser.END_DOCUMENT &&
                               (xmlTagType != XmlResourceParser.END_TAG || xmlParser.getDepth() > innerDepth)) {
                            if (xmlTagType != XmlResourceParser.END_TAG && xmlTagType != XmlResourceParser.TEXT) {
                                String innerNodeName = xmlParser.getName();
                                if ("name".equals(innerNodeName)) {
                                    if (xmlParser.next() != XmlResourceParser.TEXT) {
                                        continue;
                                    }

                                    softwareName = xmlParser.getText();
                                } else if ("url".equals(innerNodeName)) {
                                    if (xmlParser.next() != XmlResourceParser.TEXT) {
                                        continue;
                                    }

                                    softwareUrl = xmlParser.getText();
                                } else if ("license".equals(innerNodeName)) {
                                    if (xmlParser.next() != XmlResourceParser.TEXT) {
                                        continue;
                                    }

                                    String licenseName = xmlParser.getText();
                                    Parser mdParser = Parser.builder().build();
                                    HtmlRenderer mdRenderer = HtmlRenderer.builder().build();
                                    try (InputStream licenseStream = context.getAssets()
                                                                            .open("Licenses/" + licenseName + ".md"); InputStreamReader licenseReader = new InputStreamReader(
                                            licenseStream); BufferedReader reader = new BufferedReader(licenseReader)) {
                                        Node licenseDocument = mdParser.parseReader(reader);
                                        license = mdRenderer.render(licenseDocument);
                                    }
                                } else {
                                    skipCurrentTag(xmlParser);
                                }
                            }
                        }

                        _openSourceSoftware.add(new OpenSourceSoftware(softwareName, softwareUrl, license));
                    } else {
                        skipCurrentTag(xmlParser);
                    }
                }
            }
        } catch (IllegalArgumentException | XmlPullParserException | IOException e) {
            // TODO: Implement logging and exception handling
            Log.e(this.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * Skips forward to the next XML tag.
     *
     * @param parser the parser to skip forward in.
     *
     * @exception IOException
     * @exception XmlPullParserException
     */
    private void skipCurrentTag(XmlResourceParser parser)
            throws IOException, XmlPullParserException
    {
        int outerDepth = parser.getDepth();
        int type;

        while ((type = parser.next()) != XmlResourceParser.END_DOCUMENT && (type != XmlResourceParser.END_TAG || parser.getDepth() > outerDepth)) {
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
         *
         * @param name    The name of the library.
         * @param url     The url where the library is hosted.
         * @param license The license the library is issued under.
         *
         * @exception IllegalArgumentException When the name, url or license is null or empty.
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
}
