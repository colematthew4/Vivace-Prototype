package cole.matthew.vivace.Models.Recordings;

public final class MusicXmlRecording implements IRecording {
    private String _name;
    private String _storagePath;
    private String _lastModified;
    private RecordingType _recordingType;

    MusicXmlRecording(String name, String filePath, String lastModified, RecordingType type) {
        this(name, filePath, lastModified);
        _recordingType = type;
    }

    MusicXmlRecording(String name, String filePath, String lastModified) {
        _name = name;
        _storagePath = filePath;
        _lastModified = lastModified;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getPath() {
        return _storagePath;
    }

    @Override
    public String getLastModified() {
        return _lastModified;
    }

    @Override
    public RecordingType getRecordingType() {
        return _recordingType;
    }
}
