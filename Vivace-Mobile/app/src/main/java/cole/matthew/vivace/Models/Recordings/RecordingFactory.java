package cole.matthew.vivace.Models.Recordings;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import cole.matthew.vivace.Exceptions.InvalidFileException;
import cole.matthew.vivace.Helpers.FileExtension;

import static android.icu.text.DateFormat.getDateTimeInstance;

public final class RecordingFactory {
    public RecordingFactory() {
    }

    public IRecording getRecording(File file)
            throws FileNotFoundException, InvalidFileException
    {
        if (file == null || !file.exists())
            throw new FileNotFoundException("This file does not exist.");

        String name = file.getName();
        String filePath = file.getPath();
        String lastModified = getDateTimeInstance().format(file.lastModified());
        return getRecording(name, filePath, lastModified, FileExtension.getExtension(name));
    }

    public IRecording getRecording(String name, String filePath, LocalDateTime lastModified, String fileExtension)
            throws InvalidFileException
    {
        return getRecording(name, filePath, getDateTimeInstance().format(lastModified), fileExtension);
    }

    public IRecording getRecording(String name, String filePath, String lastModified, String fileExtension)
            throws InvalidFileException
    {
        switch (fileExtension) {
            case "musicxml":
            case "xml":
            case "mxl":
                return new MusicXmlRecording(name, filePath, lastModified, RecordingType.MUSICXML);
            case "midi":
                return new MidiRecording(name, filePath, lastModified, RecordingType.MIDI);
            case "mp3":
                // TODO
            case "wav":
                // TODO
            default:
                throw new InvalidFileException(fileExtension);
        }
    }
}
