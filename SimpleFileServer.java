// import java.io.BufferedInputStream;
// import java.io.BufferedOutputStream;
// import java.io.DataOutputStream;
// import java.io.EOFException;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.net.ServerSocket;
// import java.net.Socket;

// import javax.swing.JFileChooser;
// import javax.swing.UIManager;

// public class SimpleFileServer {

//     public static final int SOCKET_PORT = 13267;

//     public static void main(String[] args) {

//         try {
//             UIManager.setLookAndFeel(
//                 UIManager.getSystemLookAndFeelClassName()
//             );
//         } catch (Exception ignored) {
//         }

//         // Select multiple files using a file chooser.
//         JFileChooser chooser = new JFileChooser();

//         chooser.setDialogTitle("Select Files to Share");
//         chooser.setMultiSelectionEnabled(true);
//         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

//         int result = chooser.showOpenDialog(null);

//         if (result != JFileChooser.APPROVE_OPTION) {
//             System.out.println("No files selected. Server stopped.");
//             return;
//         }

//         File[] selectedFiles = chooser.getSelectedFiles();

//         if (selectedFiles.length == 0) {
//             System.out.println("No files selected.");
//             return;
//         }

//         System.out.println("\nSimpleSHAREit Master Server Started!");
//         System.out.println("Selected Files: " + selectedFiles.length);

//         for (File file : selectedFiles) {
//             System.out.println("- " + file.getName());
//         }

//         try (ServerSocket serverSocket =
//                  new ServerSocket(SOCKET_PORT)) {

//             System.out.println("\nWaiting for client connection...");

//             while (true) {

//                 try (Socket socket = serverSocket.accept()) {

//                     System.out.println(
//                         "\nClient connected: " +
//                         socket.getInetAddress()
//                     );

//                     try (
//                         DataOutputStream dos = new DataOutputStream(
//                             new BufferedOutputStream(
//                                 socket.getOutputStream()
//                             )
//                         )
//                     ) {

//                         // Send the number of files first.
//                         dos.writeInt(selectedFiles.length);
//                         dos.flush();

//                         for (File file : selectedFiles) {

//                             if (!file.isFile() || !file.canRead()) {
//                                 throw new IOException(
//                                     "Cannot read file: " + file.getName()
//                                 );
//                             }

//                             long fileSize = file.length();

//                             System.out.println(
//                                 "\nSending File: " + file.getName()
//                             );

//                             System.out.println(
//                                 "File Size: " + formatFileSize(fileSize)
//                             );

//                             // Send file information.
//                             dos.writeUTF(file.getName());
//                             dos.writeLong(fileSize);
//                             dos.flush();

//                             long sent = 0;
//                             long remaining = fileSize;

//                             try (
//                                 BufferedInputStream bis =
//                                     new BufferedInputStream(
//                                         new FileInputStream(file)
//                                     )
//                             ) {

//                                 byte[] buffer = new byte[4096];

//                                 while (remaining > 0) {

//                                     int bytesRead = bis.read(
//                                         buffer,
//                                         0,
//                                         (int) Math.min(
//                                             buffer.length,
//                                             remaining
//                                         )
//                                     );

//                                     if (bytesRead == -1) {
//                                         throw new EOFException(
//                                             "Source file changed or ended early."
//                                         );
//                                     }

//                                     dos.write(buffer, 0, bytesRead);

//                                     sent += bytesRead;
//                                     remaining -= bytesRead;

//                                     updateProgress(sent, fileSize);
//                                 }
//                             }

//                             dos.flush();

//                             updateProgress(fileSize, fileSize);

//                             System.out.println(
//                                 "\nFile Sent Successfully: " +
//                                 file.getName()
//                             );
//                         }

//                         System.out.println(
//                             "\nAll Files Transferred Successfully!"
//                         );

//                     } catch (IOException e) {
//                         System.out.println(
//                             "\nTransfer Error: " + e.getMessage()
//                         );
//                     }

//                 } catch (IOException e) {
//                     System.out.println(
//                         "\nConnection Error: " + e.getMessage()
//                     );
//                 }

//                 System.out.println("\nWaiting for next client...");
//             }

//         } catch (IOException e) {
//             System.out.println("Server Error: " + e.getMessage());
//         }
//     }

//     private static void updateProgress(long current, long total) {

//         int percent = total == 0
//             ? 100
//             : (int) (current * 100.0 / total);

//         String bar = createProgressBar(percent);

//         System.out.print(
//             "\rSending: " + percent + "% [" + bar + "] " +
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
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class SimpleFileServer {

    public static final int SOCKET_PORT = 13267;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {
        }

        // Select multiple files.
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Select Files to Share");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = chooser.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            System.out.println("No files selected. Server stopped.");
            return;
        }

        File[] selectedFiles = chooser.getSelectedFiles();

        if (selectedFiles.length == 0) {
            System.out.println("No files selected.");
            return;
        }

        // Validate selected files.
        for (File file : selectedFiles) {

            if (!file.isFile() || !file.canRead()) {
                System.out.println(
                    "Cannot read file: " + file.getAbsolutePath()
                );
                return;
            }
        }

        System.out.println("\nSimpleSHAREit Master Server Started!");
        System.out.println("Selected Files: " + selectedFiles.length);

        for (File file : selectedFiles) {
            System.out.println("- " + file.getName());
        }

        AtomicReference<Socket> activeSocket =
            new AtomicReference<>();

        AtomicBoolean cancelled = new AtomicBoolean(false);

        TransferStopControl stopControl = new TransferStopControl(
            "SimpleSHAREit Server",
            "Stop Sharing",
            activeSocket,
            cancelled
        );

        try (ServerSocket serverSocket =
                 new ServerSocket(SOCKET_PORT)) {

            System.out.println("\nWaiting for client connection...");

            while (true) {

                try (Socket socket = serverSocket.accept()) {

                    // Reset cancellation for this new connection.
                    cancelled.set(false);

                    activeSocket.set(socket);
                    stopControl.setActive(true);

                    System.out.println(
                        "\nClient connected: " +
                        socket.getInetAddress()
                    );

                    try (
                        DataOutputStream dos = new DataOutputStream(
                            new BufferedOutputStream(
                                socket.getOutputStream()
                            )
                        )
                    ) {

                        // Send total number of selected files.
                        dos.writeInt(selectedFiles.length);
                        dos.flush();

                        for (File file : selectedFiles) {

                            if (cancelled.get()) {
                                throw new IOException("Transfer cancelled.");
                            }

                            long fileSize = file.length();

                            System.out.println(
                                "\nSending File: " + file.getName()
                            );

                            System.out.println(
                                "File Size: " + formatFileSize(fileSize)
                            );

                            // Send file information.
                            dos.writeUTF(file.getName());
                            dos.writeLong(fileSize);
                            dos.flush();

                            long sent = 0;
                            long remaining = fileSize;

                            try (
                                BufferedInputStream bis =
                                    new BufferedInputStream(
                                        new FileInputStream(file)
                                    )
                            ) {

                                byte[] buffer = new byte[4096];

                                while (remaining > 0) {

                                    if (cancelled.get()) {
                                        throw new IOException(
                                            "Transfer cancelled."
                                        );
                                    }

                                    int bytesRead = bis.read(
                                        buffer,
                                        0,
                                        (int) Math.min(
                                            buffer.length,
                                            remaining
                                        )
                                    );

                                    if (bytesRead == -1) {
                                        throw new EOFException(
                                            "Source file ended unexpectedly."
                                        );
                                    }

                                    dos.write(buffer, 0, bytesRead);

                                    sent += bytesRead;
                                    remaining -= bytesRead;

                                    updateProgress(sent, fileSize);
                                }
                            }

                            dos.flush();

                            if (cancelled.get()) {
                                throw new IOException("Transfer cancelled.");
                            }

                            updateProgress(fileSize, fileSize);

                            System.out.println(
                                "\nFile Sent Successfully: " +
                                file.getName()
                            );
                        }

                        System.out.println(
                            "\nAll Files Transferred Successfully!"
                        );

                    } catch (IOException e) {

                        if (cancelled.get()) {
                            System.out.println(
                                "\nSharing stopped by user!"
                            );
                        } else {
                            System.out.println(
                                "\nTransfer Error: " + e.getMessage()
                            );
                        }
                    }

                } catch (IOException e) {

                    if (cancelled.get()) {
                        System.out.println(
                            "\nSharing stopped by user!"
                        );
                    } else {
                        System.out.println(
                            "\nConnection Error: " + e.getMessage()
                        );
                    }

                } finally {

                    // Clean up after this connection.
                    activeSocket.set(null);
                    stopControl.setActive(false);
                    cancelled.set(false);
                }

                System.out.println("\nWaiting for next client...");
            }

        } catch (IOException e) {

            System.out.println("Server Error: " + e.getMessage());

        } finally {

            stopControl.close();
        }
    }

    private static void updateProgress(long current, long total) {

        int percent = total == 0
            ? 100
            : (int) (current * 100.0 / total);

        String bar = createProgressBar(percent);

        System.out.print(
            "\rSending: " + percent + "% [" + bar + "] " +
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
