// import java.io.BufferedInputStream;
// import java.io.BufferedOutputStream;
// import java.io.DataInputStream;
// import java.io.EOFException;
// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.net.Socket;

// public class SimpleFileClient {

//     public static final int SOCKET_PORT = 13267;
//     public static final String SERVER = "127.0.0.1";

//     public static final String RECEIVED_FOLDER =
//         "C:\\Users\\mayur\\Desktop\\Shareit\\Received File";
//     public static void main(String[] args) {

//         System.out.println("Connecting to server...");

//         File folder = new File(RECEIVED_FOLDER);

//         if (!folder.exists() && !folder.mkdirs()) {
//             System.out.println("Could not create received folder.");
//             return;
//         }

//         try (
//             Socket socket = new Socket(SERVER, SOCKET_PORT);

//             DataInputStream dis = new DataInputStream(
//                 new BufferedInputStream(socket.getInputStream())
//             )
//         ) {

//             System.out.println("Connected successfully!");

//             // Read the total number of files.
//             int totalFiles = dis.readInt();

//             if (totalFiles < 1 || totalFiles > 1000) {
//                 throw new IOException("Invalid number of files received.");
//             }

//             System.out.println("\nTotal Files: " + totalFiles);

//             long totalDownloaded = 0;

//             for (int fileNumber = 1;
//                  fileNumber <= totalFiles;
//                  fileNumber++) {

//                 // Read file information.
//                 String fileName = dis.readUTF();
//                 long fileSize = dis.readLong();

//                 if (fileSize < 0) {
//                     throw new IOException("Invalid file size.");
//                 }

//                 // Prevent directory paths in received filenames.
//                 if (fileName.isEmpty() ||
//                     fileName.contains("/") ||
//                     fileName.contains("\\") ||
//                     fileName.equals(".") ||
//                     fileName.equals("..")) {

//                     throw new IOException("Invalid filename received.");
//                 }

//                 // Generate a unique filename.
//                 File outputFile = getUniqueFile(folder, fileName);

//                 System.out.println("\n--------------------------------");
//                 System.out.println(
//                     "Receiving File " + fileNumber + " of " + totalFiles
//                 );

//                 System.out.println("File Name: " + fileName);
//                 System.out.println("File Size: " + formatFileSize(fileSize));
//                 System.out.println("Saving As: " + outputFile.getName());

//                 long downloaded = 0;
//                 long remaining = fileSize;

//                 try (
//                     BufferedOutputStream bos =
//                         new BufferedOutputStream(
//                             new FileOutputStream(outputFile)
//                         )
//                 ) {

//                     byte[] buffer = new byte[4096];

//                     while (remaining > 0) {

//                         int bytesRead = dis.read(
//                             buffer,
//                             0,
//                             (int) Math.min(buffer.length, remaining)
//                         );

//                         if (bytesRead == -1) {
//                             throw new EOFException(
//                                 "Connection ended before file completed."
//                             );
//                         }

//                         bos.write(buffer, 0, bytesRead);

//                         downloaded += bytesRead;
//                         remaining -= bytesRead;

//                         updateProgress(downloaded, fileSize);
//                     }

//                     bos.flush();

//                 } catch (IOException e) {

//                     // Delete incomplete file after a failed transfer.
//                     outputFile.delete();
//                     throw e;
//                 }

//                 updateProgress(fileSize, fileSize);

//                 totalDownloaded++;

//                 System.out.println("\nFile Received Successfully!");
//                 System.out.println(
//                     "Saved At: " + outputFile.getAbsolutePath()
//                 );
//             }

//             System.out.println("\n================================");
//             System.out.println("ALL FILES RECEIVED SUCCESSFULLY!");
//             System.out.println("Total Files: " + totalFiles);
//             System.out.println("================================");

//         } catch (IOException e) {

//             System.out.println("\nFile Transfer Failed!");
//             System.out.println("Error: " + e.getMessage());
//         }
//     }

//     // Prevent duplicate filenames.
//     private static File getUniqueFile(File folder, String fileName) {

//         File file = new File(folder, fileName);

//         if (!file.exists()) {
//             return file;
//         }

//         int dotIndex = fileName.lastIndexOf('.');

//         String name;
//         String extension;

//         if (dotIndex > 0) {
//             name = fileName.substring(0, dotIndex);
//             extension = fileName.substring(dotIndex);
//         } else {
//             name = fileName;
//             extension = "";
//         }

//         int count = 1;

//         while (file.exists()) {

//             String newName =
//                 name + " (" + count + ")" + extension;

//             file = new File(folder, newName);
//             count++;
//         }

//         return file;
//     }

//     private static void updateProgress(long current, long total) {

//         int percent = total == 0
//             ? 100
//             : (int) (current * 100.0 / total);

//         String bar = createProgressBar(percent);

//         System.out.print(
//             "\rDownloading: " + percent + "% [" + bar + "] " +
//             formatFileSize(current) + " / " +
//             formatFileSize(total)
//         );

//         System.out.flush();
//     }

//     private static String createProgressBar(int percent) {

//         int width = 40;
//         int filled = percent * width / 100;

//         StringBuilder bar = new StringBuilder();

//         for (int i = 0; i < width; i++) {
//             bar.append(i < filled ? "#" : "-");
//         }

//         return bar.toString();
//     }

//     private static String formatFileSize(long bytes) {

//         if (bytes < 1024) {
//             return bytes + " B";
//         }

//         if (bytes < 1024 * 1024) {
//             return String.format("%.2f KB", bytes / 1024.0);
//         }

//         if (bytes < 1024L * 1024 * 1024) {
//             return String.format(
//                 "%.2f MB", bytes / (1024.0 * 1024.0)
//             );
//         }

//         return String.format(
//             "%.2f GB",
//             bytes / (1024.0 * 1024.0 * 1024.0)
//         );
//     }
// }

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleFileClient {

    public static final int SOCKET_PORT = 13267;
    public static final String SERVER = "127.0.0.1";

    public static final String RECEIVED_FOLDER =
        "C:\\Users\\mayur\\Desktop\\Shareit\\Received File";

    public static void main(String[] args) {

        System.out.println("Connecting to server...");

        File folder = new File(RECEIVED_FOLDER);

        if (!folder.exists() && !folder.mkdirs()) {
            System.out.println("Could not create received folder.");
            return;
        }

        AtomicReference<Socket> activeSocket =
            new AtomicReference<>();

        AtomicBoolean cancelled = new AtomicBoolean(false);

        TransferStopControl stopControl = new TransferStopControl(
            "SimpleSHAREit Client",
            "Stop Download",
            activeSocket,
            cancelled
        );

        try (Socket socket = new Socket(SERVER, SOCKET_PORT)) {

            activeSocket.set(socket);
            stopControl.setActive(true);

            try (
                DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream())
                )
            ) {

                System.out.println("Connected successfully!");

                // Read the total number of files.
                int totalFiles = dis.readInt();

                if (totalFiles < 1 || totalFiles > 1000) {
                    throw new IOException(
                        "Invalid number of files received."
                    );
                }

                System.out.println("\nTotal Files: " + totalFiles);

                for (int fileNumber = 1;
                     fileNumber <= totalFiles;
                     fileNumber++) {

                    if (cancelled.get()) {
                        throw new IOException("Download cancelled.");
                    }

                    // Read file information.
                    String fileName = dis.readUTF();
                    long fileSize = dis.readLong();

                    if (fileSize < 0) {
                        throw new IOException("Invalid file size.");
                    }

                    // Validate received filename.
                    if (fileName.isEmpty() ||
                        fileName.contains("/") ||
                        fileName.contains("\\") ||
                        fileName.equals(".") ||
                        fileName.equals("..")) {

                        throw new IOException(
                            "Invalid filename received."
                        );
                    }

                    // Generate a unique filename.
                    File outputFile = getUniqueFile(folder, fileName);

                    System.out.println("\n--------------------------------");
                    System.out.println(
                        "Receiving File " + fileNumber +
                        " of " + totalFiles
                    );

                    System.out.println("File Name: " + fileName);
                    System.out.println(
                        "File Size: " + formatFileSize(fileSize)
                    );
                    System.out.println(
                        "Saving As: " + outputFile.getName()
                    );

                    long downloaded = 0;
                    long remaining = fileSize;

                    try (
                        BufferedOutputStream bos =
                            new BufferedOutputStream(
                                new FileOutputStream(outputFile)
                            )
                    ) {

                        byte[] buffer = new byte[4096];

                        while (remaining > 0) {

                            if (cancelled.get()) {
                                throw new IOException(
                                    "Download cancelled."
                                );
                            }

                            int bytesRead = dis.read(
                                buffer,
                                0,
                                (int) Math.min(
                                    buffer.length,
                                    remaining
                                )
                            );

                            if (bytesRead == -1) {
                                throw new EOFException(
                                    "Connection ended before file completed."
                                );
                            }

                            bos.write(buffer, 0, bytesRead);

                            downloaded += bytesRead;
                            remaining -= bytesRead;

                            updateProgress(downloaded, fileSize);
                        }

                        bos.flush();

                    } catch (IOException e) {

                        // Delete incomplete file.
                        if (outputFile.exists() && !outputFile.delete()) {
                            System.out.println(
                                "Warning: Could not delete incomplete file: "
                                + outputFile.getAbsolutePath()
                            );
                        }

                        throw e;
                    }

                    if (cancelled.get()) {
                        throw new IOException("Download cancelled.");
                    }

                    updateProgress(fileSize, fileSize);

                    System.out.println("\nFile Received Successfully!");
                    System.out.println(
                        "Saved At: " + outputFile.getAbsolutePath()
                    );
                }

                System.out.println("\n================================");
                System.out.println("ALL FILES RECEIVED SUCCESSFULLY!");
                System.out.println("Total Files: " + totalFiles);
                System.out.println("================================");
            }

        } catch (IOException e) {

            if (cancelled.get()) {
                System.out.println("\nDownload cancelled by user!");
            } else {
                System.out.println("\nFile Transfer Failed!");
                System.out.println("Error: " + e.getMessage());
            }

        } finally {

            activeSocket.set(null);
            stopControl.setActive(false);
            stopControl.close();
        }
    }

    // Prevent duplicate filenames.
    private static File getUniqueFile(File folder, String fileName) {

        File file = new File(folder, fileName);

        if (!file.exists()) {
            return file;
        }

        int dotIndex = fileName.lastIndexOf('.');

        String name;
        String extension;

        if (dotIndex > 0) {
            name = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        } else {
            name = fileName;
            extension = "";
        }

        int count = 1;

        while (file.exists()) {

            String newName =
                name + " (" + count + ")" + extension;

            file = new File(folder, newName);
            count++;
        }

        return file;
    }

    private static void updateProgress(long current, long total) {

        int percent = total == 0
            ? 100
            : (int) (current * 100.0 / total);

        String bar = createProgressBar(percent);

        System.out.print(
            "\rDownloading: " + percent + "% [" + bar + "] " +
            formatFileSize(current) + " / " +
            formatFileSize(total)
        );

        System.out.flush();
    }

    private static String createProgressBar(int percent) {

        int width = 40;
        int filled = percent * width / 100;

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < width; i++) {
            bar.append(i < filled ? "#" : "-");
        }

        return bar.toString();
    }

    private static String formatFileSize(long bytes) {

        if (bytes < 1024) {
            return bytes + " B";
        }

        if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        }

        if (bytes < 1024L * 1024 * 1024) {
            return String.format(
                "%.2f MB", bytes / (1024.0 * 1024.0)
            );
        }

        return String.format(
            "%.2f GB",
            bytes / (1024.0 * 1024.0 * 1024.0)
        );
    }
}
