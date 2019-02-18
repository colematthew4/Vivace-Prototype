package cole.matthew.vivace.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cole.matthew.vivace.Activities.SettingsActivity;
import cole.matthew.vivace.BuildConfig;
import cole.matthew.vivace.Models.Exceptions.InsufficientStorageException;
import cole.matthew.vivace.Models.Exceptions.InvalidFileException;
import cole.matthew.vivace.Models.Exceptions.StorageNotReadableException;
import cole.matthew.vivace.Models.Exceptions.StorageNotWritableException;
import cole.matthew.vivace.Models.Recordings.IRecording;
import cole.matthew.vivace.Models.Recordings.RecordingFactory;

public class FileStore {
    private static final String TAG = "FileStore_Tag";
    private Activity _context;

    public FileStore(Activity context) {
        _context = context;
    }

    /**
     * Gets the amount of recordings that have been saved.
     *
     * @return the amount of recordings saved on the device
     */
    public int getRecordingCount() {
        int recordingCount = 0;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        final boolean usePublicStorage = sharedPreferences.getBoolean(SettingsActivity.KEY_FILE_STORAGE_LOCATION, false);

        if (isExternalStorageReadable()) {
            try {
                File storageLocation = usePublicStorage ? getPublicStorageDir() : getPrivateStorageDir();
                recordingCount = storageLocation.list().length;
            } catch (StorageNotReadableException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return recordingCount;
    }

    /**
     * Gets an array of the recordings that have been saved to the device.
     *
     * @return the recordings saved on the device
     */
    public List<IRecording> getRecordings() {
        List<IRecording> recordings = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        final boolean usePublicStorage = sharedPreferences.getBoolean(SettingsActivity.KEY_FILE_STORAGE_LOCATION, false);

        if (isExternalStorageReadable()) {
            try {
                File storageLocation = usePublicStorage ? getPublicStorageDir() : getPrivateStorageDir();
                File[] internalRecordings = storageLocation.listFiles();
                RecordingFactory recordingFactory = new RecordingFactory();
                for (File internalRecording : internalRecordings) {
                    recordings.add(recordingFactory.getRecording(internalRecording));
                }
            } catch (StorageNotReadableException | FileNotFoundException | InvalidFileException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return recordings;
    }

    /**
     * Checks if external storage is available for read and write.
     *
     * @return True if it is writable, false if not.
     */
    public boolean isExternalStorageWritable() {
        boolean result;

        if (VivacePermissions.hasPermission(_context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String state = Environment.getExternalStorageState();
            result = Environment.MEDIA_MOUNTED.equals(state);
        } else {
            result = VivacePermissions.requestPermission(_context, VivacePermissionCodes.WRITE_EXTERNAL_STORAGE);
        }

        return result;
    }

    /**
     * Checks if external storage is available to at least read.
     *
     * @return True if it is readable, false if not.
     */
    public boolean isExternalStorageReadable() {
        boolean result;

        if (VivacePermissions.hasPermission(_context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            String state = Environment.getExternalStorageState();
            result = Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
        } else {
            result = VivacePermissions.requestPermission(_context, VivacePermissionCodes.READ_EXTERNAL_STORAGE);
        }

        return result;
    }

    /**
     * Gets the public external storage directory of the recordings Vivace has saved.
     *
     * @return The public external storage directory.
     */
    public File getPublicStorageDir()
            throws StorageNotReadableException
    {
        if (!isExternalStorageReadable()) {
            throw new StorageNotReadableException("Vivace requires your permission to save recordings to your device.");
        }

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e(TAG, "Directory not created.");
        }

        // Get the directory for the user's public pictures directory
        File file = new File(storageDir, "Recordings");

        if (!file.exists() && !file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }

        return file;
    }

    /**
     * Gets the private external directory of the recordings Vivace has saved.
     *
     * @return The private external storage directory.
     */
    public File getPrivateStorageDir()
            throws StorageNotReadableException
    {
        if (!isExternalStorageReadable()) {
            throw new StorageNotReadableException("Vivace requires your permission to save recordings to your device.");
        }

        File storageDir = _context.getExternalFilesDir(null);
        if (storageDir == null) {
            throw new StorageNotReadableException("No external storage available.");
        }

        // Get the directory for the user's public pictures directory
        File file = new File(storageDir, "Recordings");

        if (!file.exists() && !file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }

        return file;
    }

    /**
     * Copies files from private external storage to public external storage on your device. That
     * way if Vivace is uninstalled the files it saved will remain on the device.
     */
    public void transferStorageToPublic()
            throws InsufficientStorageException, StorageNotReadableException, StorageNotWritableException
    {
        File publicStorageDir = getPublicStorageDir();
        File privateStorageDir = getPrivateStorageDir();

        if (!isExternalStorageWritable()) {
            throw new StorageNotWritableException("Vivace requires your permission to write files to your device.");
        }

        if (publicStorageDir.getFreeSpace() > 0.1 * publicStorageDir.getTotalSpace()) {
            //            new AsyncTask<Object, Object, Object>()
            //            {
            //                @Override
            //                protected Object doInBackground(Object[] objects)
            //                {
            //                    for (File file : publicStorageDir.listFiles())
            //                    {
            //                        try
            //                        {
            //                            InputStream inputStream = new FileInputStream(file);
            //                            OutputStream outputStream = new FileOutputStream(privateStorageDir);
            //
            //                            byte[] buf = new byte[1024];
            //                            int length;
            //
            //                            while ((length = inputStream.read(buf)) > 0)
            //                                outputStream.write(buf, 0, length);
            //
            //                            inputStream.close();
            //                            outputStream.close();
            //                        }
            //                        catch (java.io.IOException e)
            //                        {
            //                            Log.e(TAG, e.getMessage());
            //                        }
            //                    }
            //
            //                    return null;
            //                }
            //            };
            for (File file : privateStorageDir.listFiles()) {
                try {
                    try (InputStream inputStream = new FileInputStream(file); OutputStream outputStream = new FileOutputStream(new File(publicStorageDir, file.getName()))) {
                        byte[] buf = new byte[1024];
                        int length;

                        while ((length = inputStream.read(buf)) > 0)
                            outputStream.write(buf, 0, length);
                    }

                    delete(file, privateStorageDir);
                } catch (java.io.IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            throw new InsufficientStorageException("You do not have enough storage space to transfer files from public to private storage.");
        }
    }

    /**
     * Copies files from public external storage to private external storage on your device. That
     * way if Vivace is uninstalled the files it saved will also be removed.
     */
    public void transferStorageToPrivate()
            throws InsufficientStorageException, StorageNotReadableException, StorageNotWritableException
    {
        final File publicStorageDir = getPublicStorageDir();
        final File privateStorageDir = getPrivateStorageDir();

        if (!isExternalStorageWritable()) {
            throw new StorageNotWritableException("Vivace requires your permission to write files to your device.");
        }

        if (privateStorageDir.getFreeSpace() > 0.1 * privateStorageDir.getTotalSpace()) {
            //            new AsyncTask<Object, Object, Object>()
            //            {
            //                @Override
            //                protected Object doInBackground(Object[] objects)
            //                {
            //                    for (File file : publicStorageDir.listFiles())
            //                    {
            //                        try
            //                        {
            //                            InputStream inputStream = new FileInputStream(file);
            //                            OutputStream outputStream = new FileOutputStream(privateStorageDir);
            //
            //                            byte[] buf = new byte[1024];
            //                            int length;
            //
            //                            while ((length = inputStream.read(buf)) > 0)
            //                                outputStream.write(buf, 0, length);
            //
            //                            inputStream.close();
            //                            outputStream.close();
            //                        }
            //                        catch (java.io.IOException e)
            //                        {
            //                            Log.e(TAG, e.getMessage());
            //                        }
            //                    }
            //
            //                    return null;
            //                }
            //            };
            for (File file : publicStorageDir.listFiles()) {
                try {
                    try (InputStream inputStream = new FileInputStream(file); OutputStream outputStream = new FileOutputStream(new File(privateStorageDir, file.getName()))) {
                        byte[] buf = new byte[1024];
                        int length;

                        while ((length = inputStream.read(buf)) > 0)
                            outputStream.write(buf, 0, length);
                    }

                    delete(file, publicStorageDir);
                } catch (java.io.IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            throw new InsufficientStorageException("You do not have enough storage space to transfer files from public to private storage.");
        }
    }

    /**
     * Permanently deletes a file from the filesystem.
     *
     * @param fileToDelete The file to delete from the filesystem.
     *
     * @exception StorageNotReadableException if the application isn't given permission to read the filesystem.
     * @exception StorageNotWritableException if the application isn't given permission to write to the filesystem.
     */
    public void delete(File fileToDelete)
            throws StorageNotWritableException, StorageNotReadableException
    {
        if (!isExternalStorageWritable()) {
            throw new StorageNotWritableException("Vivace requires your permission to write files to your device.");
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        final boolean usePublicStorage = sharedPreferences.getBoolean(SettingsActivity.KEY_FILE_STORAGE_LOCATION, false);

        File storageDir = usePublicStorage ? getPublicStorageDir() : getPrivateStorageDir();
        delete(fileToDelete, storageDir);
    }

    /**
     * Permanently deletes a file from the filesystem.
     *
     * @param fileToDelete The file to delete from the filesystem.
     * @param storageDir   The storage directory to delete the file from.
     */
    private void delete(File fileToDelete, @NonNull File storageDir) {
        for (File storedFile : storageDir.listFiles()) {
            if (storedFile.compareTo(fileToDelete) == 0) {
                int retries = 0;
                boolean deleted;

                do {
                    // TODO: remove debug check
                    Log.i(TAG, "Deleting " + fileToDelete.getName() + " from the filesystem.");
                    deleted = BuildConfig.DEBUG || fileToDelete.delete();
                } while (retries++ < 3 && !deleted);

                return;
            }
        }
    }
}