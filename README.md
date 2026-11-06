# SimpleSHAREit Master

A Java-based file-sharing application developed using Socket Programming. It allows users to transfer multiple files between a server and a client over a network.

## Features

- Multiple file selection
- Multiple file transfer
- File name and file size detection
- Real-time transfer progress bar
- Stop or cancel file transfer
- Duplicate file name protection
- Automatic creation of received files folder

## Technologies Used

- Java
- Java Socket Programming
- TCP/IP
- Java Swing
- InputStream and OutputStream

## Project Structure

```text
SimpleSHAREit-Master/
├── SimpleFileServer.java
├── SimpleFileClient.java
├── TransferStopControl.java
├── Transfer File/
└── Received File/
```

## Requirements

- Java JDK
- Visual Studio Code (or another Java IDE)

## How to Run

### 1. Compile the project

```bash
javac SimpleFileServer.java SimpleFileClient.java TransferStopControl.java
```

### 2. Start the server

```bash
java SimpleFileServer
```

Select the files you want to share.

### 3. Start the client

Open another terminal and execute:

```bash
java SimpleFileClient
```

The selected files will be received in the `Received File` folder.

## Default Configuration

- Port: `13267`
- Server IP: `127.0.0.1` (same computer)

For transferring files between different computers, configure the client with the server computer's IP address.

## Author

Mayur Patil