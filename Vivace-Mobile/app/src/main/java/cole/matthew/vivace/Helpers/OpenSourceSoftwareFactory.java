package cole.matthew.vivace.Helpers;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cole.matthew.vivace.Models.OpenSourceSoftware;
import cole.matthew.vivace.R;

/**
 * Helper factory for providing the open source libraries used to build Vivace.
 */
public final class OpenSourceSoftwareFactory {
    private Context _context;
    private Map<String, String> _softwareLicenseCache;
    private List<OpenSourceSoftware> _openSourceSoftware;

    /**
     * Creates an instance of a {@link OpenSourceSoftwareFactory} object.
     *
     * @param context The context to get the xml resources from.
     */
    public OpenSourceSoftwareFactory(Context context) {
        _context = context;
        _softwareLicenseCache = new HashMap<>();
        _openSourceSoftware = new ArrayList<>();
    }

    /**
     * Gets the third party libraries used to build Vivace.
     *
     * @return A {@link List} of {@link OpenSourceSoftware}.
     */
    public List<OpenSourceSoftware> getOpenSourceSoftware() {
        if (_openSourceSoftware.isEmpty()) {
            try {
                loadSoftwareFromResource();
            } catch (IOException | XmlPullParserException e) {
                // TODO: Implement logging and exception handling
                Log.e(this.getClass().getName(), e.getMessage());
            }
        }

        return _openSourceSoftware;
    }

    /**
     * Parses the third party libraries used by Vivace from an xml resource file.
     *
     * @throws IOException
     * @throws XmlPullParserException
     * @throws IllegalArgumentException
     */
    private void loadSoftwareFromResource()
            throws IOException, XmlPullParserException, IllegalArgumentException
    {
        try (XmlResourceParser xmlParser = _context.getResources().getXml(R.xml.oss)) {
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
                                    license = getSoftwareLicense(licenseName);
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
     * @throws IOException
     * @throws XmlPullParserException
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
     * Gets the license content for the given license.
     *
     * @param licenseName The name of the license to retrieve.
     * @return The license as an HTML document.
     * @throws IOException When the license doesn't exist as an asset in the application.
     * @throws IllegalArgumentException When the license name is null or empty.
     */
    private String getSoftwareLicense(String licenseName)
            throws IOException, IllegalArgumentException
    {
        if (licenseName == null || licenseName.equals("")) {
            throw new IllegalArgumentException("The license cannot be null or empty.");
        }

        String renderedLicense = _softwareLicenseCache.get(licenseName);

        if (renderedLicense == null || renderedLicense.equals("")) {
            renderedLicense = getRenderedLicense(licenseName);
            _softwareLicenseCache.put(licenseName, renderedLicense);
        }

        return renderedLicense;
    }

    /**
     * Retrieves the content of the license and renders it as HTML.
     *
     * @param licenseName The name of the license to render.
     *
     * @return An HTML document as a string.
     * @throws IOException When the license doesn't exist as an asset in the application.
     */
    private String getRenderedLicense(String licenseName)
            throws IOException
    {
        Parser parser = Parser.builder().build();
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        try (InputStream licenseStream = _context.getAssets().open("Licenses/" + licenseName + ".md");
             InputStreamReader licenseStreamReader = new InputStreamReader(licenseStream);
             BufferedReader licenseReader = new BufferedReader(licenseStreamReader))
        {
            Node licenseDocument = parser.parseReader(licenseReader);
            return htmlRenderer.render(licenseDocument);
        }
    }
}
