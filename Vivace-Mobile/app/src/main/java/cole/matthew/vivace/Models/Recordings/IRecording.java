package cole.matthew.vivace.Models.Recordings;

public interface IRecording {
    String getName();
    String getPath();
    String getLastModified();
    RecordingType getRecordingType();
}
