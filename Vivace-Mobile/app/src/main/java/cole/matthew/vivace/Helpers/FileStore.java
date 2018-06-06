package cole.matthew.vivace.Helpers;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import cole.matthew.vivace.Exceptions.InsufficientStorageException;
import cole.matthew.vivace.Exceptions.StorageNotReadableException;

public class FileStore
{
    private static final String TAG = "FileStore_Tag";
    private Activity _context;

    public FileStore(Activity context)
    {
        _context = context;
    }

    /**
     * Checks if external storage is available for read and write.
     * @return True if it is writable, false if not.
     */
    public boolean isExternalStorageWritable()
    {
        boolean result;

        if (VivacePermissions.hasPermission(_context, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            String state = Environment.getExternalStorageState();
            result = Environment.MEDIA_MOUNTED.equals(state);
        }
        else
            result = VivacePermissions.requestPermission(_context, VivacePermissionCodes.WRITE_EXTERNAL_STORAGE);

        return result;
    }

    /**
     * Checks if external storage is available to at least read.
     * @return True if it is readable, false if not.
     */
    public boolean isExternalStorageReadable()
    {
        boolean result;

        if (VivacePermissions.hasPermission(_context, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            String state = Environment.getExternalStorageState();
            result = Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
        }
        else
            result = VivacePermissions.requestPermission(_context, VivacePermissionCodes.READ_EXTERNAL_STORAGE);

        return result;
    }

    /**
     * Gets the public external storage directory of the recordings Vivace has saved.
     * @return The public external storage directory.
     */
    public File getPublicStorageDir()
            throws StorageNotReadableException
    {
        if (!isExternalStorageReadable())
            throw new StorageNotReadableException("Vivace requires your permission to save recordings to your device.");

        File storageDir = Environment.getExternalStoragePublicDirectory(null);
        if (!storageDir.exists() && !storageDir.mkdirs())
            Log.e(TAG, "Directory not created.");

        // Get the directory for the user's public pictures directory
        File file = new File(storageDir, "Recordings");

        if (!file.exists() && !file.mkdirs())
            Log.e(TAG, "Directory not created");

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
        if (!isExternalStorageReadable())
            throw new StorageNotReadableException("Vivace requires your permission to save recordings to your device.");

        File storageDir = _context.getExternalFilesDir(null);
        if (storageDir == null)
            throw new StorageNotReadableException("No external storage available.");

        // Get the directory for the user's public pictures directory
        File file = new File(storageDir, "Recordings");

        if (!file.exists() && !file.mkdirs())
            Log.e(TAG, "Directory not created");

        return file;
    }

    /**
     * Copies files from private external storage to public external storage on your device. That
     * way if Vivace is uninstalled the files it saved will remain on the device.
     */
    public void transferStorageToPublic()
            throws StorageNotReadableException
    {
        File publicStorageDir = getPublicStorageDir();
        File privateStorageDir = getPrivateStorageDir();
    }

    /**
     * Copies files from public external storage to private external storage on your device. That
     * way if Vivace is uninstalled the files it saved will also be removed.
     */
    public void transferStorageToPrivate()
            throws InsufficientStorageException, StorageNotReadableException
    {
        final File publicStorageDir = getPublicStorageDir();
        final File privateStorageDir = getPrivateStorageDir();

        if (privateStorageDir.getFreeSpace() > 0.9 * privateStorageDir.getTotalSpace())
        {
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
            for (File file : publicStorageDir.listFiles())
            {
                try
                {
                    InputStream inputStream = new FileInputStream(file);
                    OutputStream outputStream = new FileOutputStream(privateStorageDir);

                    byte[] buf = new byte[1024];
                    int length;

                    while ((length = inputStream.read(buf)) > 0)
                        outputStream.write(buf, 0, length);

                    inputStream.close();
                    outputStream.close();
                }
                catch (java.io.IOException e)
                {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        else
            throw new InsufficientStorageException("You do not have enough storage space to transfer " +
                                                   "files from public to private storage.");
    }
}