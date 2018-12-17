package cole.matthew.vivace.Helpers;

import org.jetbrains.annotations.Contract;

public final class FileExtension {
    /**
     * Gets the extension of a fileName.
     * <p>
     * This method returns the textual part of the fileName after the last dot.
     * There must be no directory separator after the dot.
     * <pre>
     * foo.txt      --&gt; "txt"
     * a/b/c.jpg    --&gt; "jpg"
     * a/b.txt/c    --&gt; ""
     * a/b/c        --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on, with the
     * exception of a possible {@link IllegalArgumentException} on Windows (see below).
     * </p>
     * <p>
     * <b>Note:</b> This method used to have a hidden problem for names like "foo.exe:bar.txt".
     * In this case, the name wouldn't be the name of a file, but the identifier of an
     * alternate data stream (bar.txt) on the file foo.exe. The method used to return
     * ".txt" here, which would be misleading. Commons IO 2.7, and later versions, are throwing
     * an {@link IllegalArgumentException} for names like this.
     *
     * @param fileName the fileName to retrieve the extension of.
     *
     * @return the extension of the file or an empty string if none exists or {@code null}
     * if the fileName is {@code null}.
     *
     * @exception IllegalArgumentException <b>Windows only:</b> The fileName parameter is, in fact,
     *                                     the identifier of an Alternate Data Stream, for example "foo.exe:bar.txt".
     */
    @Contract("null -> null")
    public static String getExtension(final String fileName)
            throws IllegalArgumentException {
        if (fileName == null)
            return null;

        final int index = indexOfExtension(fileName);
        if (index == -1)
            return "";

        return fileName.substring(index + 1);
    }

    /**
     * Returns the index of the last extension separator character, which is a dot.
     * <p>
     * This method also checks that there is no directory separator after the last dot. To do this it uses
     * {@link #indexOfLastSeparator(String)} which will handle a file in either Unix or Windows format.
     * </p>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on, with the
     * exception of a possible {@link IllegalArgumentException} on Windows (see below).
     * </p>
     * <b>Note:</b> This method used to have a hidden problem for names like "foo.exe:bar.txt".
     * In this case, the name wouldn't be the name of a file, but the identifier of an
     * alternate data stream (bar.txt) on the file foo.exe. The method used to return
     * ".txt" here, which would be misleading. Commons IO 2.7, and later versions, are throwing
     * an {@link IllegalArgumentException} for names like this.
     *
     * @param fileName the fileName to find the last extension separator in, null returns -1
     *
     * @return the index of the last extension separator character, or -1 if there is no such character
     *
     * @exception IllegalArgumentException <b>Windows only:</b> The fileName parameter is, in fact,
     *                                     the identifier of an Alternate Data Stream, for example "foo.exe:bar.txt".
     */
    private static int indexOfExtension(final String fileName)
            throws IllegalArgumentException {
        if (fileName == null)
            return -1;

        final int extensionPos = fileName.lastIndexOf('.');
        final int lastSeparator = indexOfLastSeparator(fileName);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    /**
     * Returns the index of the last directory separator character.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * The position of the last forward or backslash is returned.
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param fileName the fileName to find the last path separator in, null returns -1
     *
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    private static int indexOfLastSeparator(final String fileName) {
        if (fileName == null)
            return -1;

        return fileName.lastIndexOf('/');
    }
}
