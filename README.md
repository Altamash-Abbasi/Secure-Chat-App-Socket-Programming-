🔐 Secure Multi-User Chat Application

A secure, multi-client chat application built using Java TCP Socket Programming, featuring end-to-end encryption (RSA + AES) and a Swing-based graphical user interface, supporting both private and group messaging.

This project demonstrates secure communication, concurrent server architecture, cryptographic key exchange, and real-time messaging.


🚀 Features

🔐 End-to-End Encrypted Communication (RSA + AES hybrid encryption)

👥 Multi-client support using thread-per-client architecture

💬 Private one-to-one messaging (username-based routing)

👨‍👩‍👧‍👦 Group chat (broadcast messaging)

🖥 Desktop GUI built with Java Swing

🔑 RSA key exchange for secure AES session key sharing

🛡 Secure session management

⚡ Real-time communication over TCP sockets



🏗 System Architecture
Client (Swing UI)
      │
      ▼
Encrypted TCP Communication (AES)
      │
      ▼
Server (Multi-threaded, RSA Key Exchange)
      │
      ├── Private Message Routing
      └── Group Broadcast Messaging


      
🔹 Server Responsibilities

Accepts multiple client connections

Creates a dedicated thread per client

Maintains active user sessions

Handles RSA key exchange

Routes:

Private messages (direct to specific user)

Group messages (broadcast to all connected clients)



🔹 Client Responsibilities

Connects securely via TCP

Performs RSA-based key exchange

Encrypts/decrypts messages using AES

Provides Swing-based interactive GUI




🔐 Security Model
🔑 Hybrid Encryption Approach

RSA (Asymmetric Encryption)

Used for secure AES key exchange

Prevents key interception during handshake

AES (Symmetric Encryption)

Used for encrypting chat messages

Ensures fast and secure communication



🔒 Communication Flow

Client connects to server

RSA public key exchange

AES session key securely transmitted

All chat messages encrypted using AES

Server securely routes encrypted messages



🧠 Tech Stack

Java

TCP/IP Socket Programming

Multithreading (Thread-per-client model)

Java Swing (GUI)

RSA (Public Key Cryptography)

AES (Symmetric Encryption)

Java Security & Cryptography APIs




🖥 How to Run
1️⃣ Compile
javac server/*.java
javac client/*.java
2️⃣ Start the Server
java server.ServerApplication
3️⃣ Start Clients (multiple terminals)
java client.ChatClientApplication



🎯 Key Concepts Demonstrated

Concurrent server architecture

Secure key exchange implementation

Hybrid cryptographic communication (RSA + AES)

Real-time messaging system design

GUI integration with networking

Secure multi-user session management




🔮 Possible Enhancements

User authentication system

Persistent message storage (Database integration)

SSL/TLS implementation

File transfer support

Enhanced UI/UX improvements

Packaging as executable JAR
