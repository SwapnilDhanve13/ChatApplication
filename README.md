# Chat Application

This is a Java-based chat application that provides real-time communication features, including private messaging, emoji support, and file sharing capabilities. The application consists of both client-side and server-side components and provides a user-friendly graphical interface.

## Features
* Real-Time Messaging: Supports public chat between users.
* Private Messaging: Send direct messages to specific users using the command /msg [username] [message].
* Emoji Support: Use a built-in emoji panel to add emojis to your messages.
* File Sharing: Send files directly to other users within the chat.
* User List: Displays the list of active users in real-time.
* User-Friendly Interface: A clean and intuitive GUI built using Swing.

## How It Works
### Server
### The server (ChatServer) handles the following tasks:

* Accepts incoming client connections.
* Manages connected users and broadcasts messages to all connected clients.
* Supports private messaging and file transfer between clients.
* Updates the user list and notifies all clients when users join or leave.

### Client
### The client (ChatClientGUI) provides the user interface and handles:

* Connecting to the server and joining the chat with a user-provided name.
* Sending and receiving public and private messages.
* Using an emoji panel for expressive communication.
* Sending files to other users through a file chooser dialog.
* Disconnecting gracefully and notifying the server when the user exits.

## Installation and Setup
***Prerequisites***
* Java Development Kit (JDK) installed (version 8 or above)
* Internet connection (or local network) for server-client communication
* Running the Server
* Compile and run ChatServer.java:

> bash

> Copy code
```
>>> javac ChatServer.java
>>> java ChatServer
```
The server will start and listen for connections on port 5000.

### Running the Client
* Compile and run ChatClientGUI.java:

> bash

> Copy code

```
>>> javac ChatClientGUI.java
>>> java ChatClientGUI
```
***Enter your username when prompted, and start chatting!***

### File Structure
* ChatServer.java: Handles server-side logic for managing client connections and broadcasting messages.
* ChatClient.java: Provides communication logic for the client.
* ChatClientGUI.java: Builds the graphical user interface and integrates client logic.
* com/swapnil/chatapplication/: Package containing the server and client files.
Usage Guide
Public Chat: Type your message in the text field and press Enter to send it to all users.
Private Chat: Use the command /msg [username] [message] to send a message to a specific user.
Sending Files: Click the Send File button, select the file, and share it with others.
Emojis: Click the Emojis button to open the emoji panel and add emojis to your message.
Exit: Use the Exit button to leave the chat gracefully.

### Screenshots
* Turned the Server On:
![# Server On](https://github.com/SwapnilDhanve13/ChatApplication/blob/main/src/images/Chat%20Application%20srcs1.png)
* User name getting window:
  
![# Server On](https://github.com/SwapnilDhanve13/ChatApplication/blob/main/src/images/Chat%20Application%20srcs2.png)
* User Interface
  
![# Server On](https://github.com/SwapnilDhanve13/ChatApplication/blob/main/src/images/Chat%20Application%20srcs3.png)
* User List
  
![# Server On](https://github.com/SwapnilDhanve13/ChatApplication/blob/main/src/images/Chat%20Application%20srcs4.png)
* Emojies Option (click on Emojies Button to Open & Close)
  
![# Server On](https://github.com/SwapnilDhanve13/ChatApplication/blob/main/src/images/Chat%20Application%20srcs5.png)
* Sending Files
  
![# Server On](https://github.com/SwapnilDhanve13/ChatApplication/blob/main/src/images/Chat%20Application%20srcs6.png)

### Future Enhancements
* File Preview: Show file previews for certain file types before downloading.
* Encryption: Add end-to-end encryption for messages and file transfers.
* Group Chat: Extend the application to support group-specific conversations.
* User Authentication: Implement user login and registration for better identity management.

### Contributing
**If you wish to contribute to this project:**

* Fork the repository
* Create a new branch (feature/your-feature)
* Commit your changes and push to your branch
* Open a pull request for review

### License
This project is licensed under the MIT License.

### Acknowledgements
* Java Swing for GUI development
* Java I/O for handling file transfers
* Multithreading for concurrent client management
