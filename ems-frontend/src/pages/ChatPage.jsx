import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import {devicesAPI} from "../services/api";

const Chat = () => {
    // Date utilizator
    const currentUser = localStorage.getItem("userName") || "Anonymous";
    const userRole = localStorage.getItem("userRole") || "client"; // 'admin' sau 'client'
    const storageKey = `chat_history_${currentUser}`;

    // State-uri
    const [messages, setMessages] = useState(() => {
        const saved = localStorage.getItem(storageKey);
        return saved ? JSON.parse(saved) : [];
    });
    const [inputMessage, setInputMessage] = useState('');
    const [isConnected, setIsConnected] = useState(false);

    // State nou: Cui raspunde adminul? (null = broadcast/nimeni, string = username)
    const [targetUser, setTargetUser] = useState(null);

    // State-uri Devices (Adaugate ca să nu crape codul la setLoading)
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Acest ref va permite WebSocket-ului sa vada lista actualizata de device-uri
    const devicesRef = useRef([]);

    // Referințe pentru WebSocket
    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    // 1. Auto-scroll la ultimul mesaj
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
        localStorage.setItem(storageKey, JSON.stringify(messages));
    }, [messages, storageKey]);

    // Fetch Devices la pornire
    useEffect(() => {
        fetchMyDevices();
    }, []);

    // Conectare WebSocket
    useEffect(() => {
        const socket = new SockJS('http://localhost:8086/ws'); // Portul WebSocket Service
        const client = Stomp.over(socket);

        // Dezactivam logurile din consola pt curatenie
        client.debug = () => {};

        client.connect({}, () => {
            setIsConnected(true);
            console.log("Connected to WebSocket");

            // Abonare comuna (pentru mesaje publice sau raspunsuri de la suport bot)
            client.subscribe('/topic/chat', (msg) => {
                const receivedMessage = JSON.parse(msg.body);
                // Afișăm mesajul doar dacă e pentru noi sau e public
                if (!receivedMessage.receiverId || receivedMessage.receiverId === currentUser || receivedMessage.sender === currentUser) {
                    addMessageIfNotExists(receivedMessage);
                }
            });

            // Abonare PENTRU ALERTE de OverConsum
            client.subscribe('/topic/alerts', (msg) => {
                const notification = JSON.parse(msg.body);

                console.log("🔔 Alerta primita:", notification);

                //const storedUserId = localStorage.getItem("userId");
                //const isIdMatch = storedUserId && String(notification.deviceId) === String(storedUserId);
                // Verif: Este deviceId-ul din alerta prezent în lista mea de device-uri?
                const isMyDevice = devicesRef.current.some(d => String(d.id) === String(notification.deviceId));

                if (isMyDevice ) {
                    //alert("⚠️ " + notification.message);
                    addMessageIfNotExists({
                                sender: "SYSTEM ALERT",
                                content: notification.message,
                                timestamp: Date.now(),
                                isAlert: true
                            });
                } else {
                    console.log(`Alerta ignorata. Este pt deviceId: ${notification.deviceId}`);
                }
            });
            // ABONARE PENTRU ADMIN (Asculta cererile de ajutor)
            if (userRole === 'admin') {
                client.subscribe('/topic/admin', (msg) => {
                    const adminMsg = JSON.parse(msg.body);
                    // Adăugăm un flag vizual că e mesaj de sistem/admin
                    adminMsg.isAdminRequest = true;
                    addMessageIfNotExists(adminMsg);
                });
            }
        }, (error) => {
            console.error("Connection error: ", error);
            setIsConnected(false);
        });

        stompClientRef.current = client;

        return () => {
            if (client && client.connected) {
                client.disconnect();
            }
        };
    }, [currentUser, userRole]);

    const fetchMyDevices = async () => {
        const storedUserId = localStorage.getItem("userId");
        setLoading(true);
        setError(null);
        try {
            // citire toate device-urile unui client
            const response = await devicesAPI.getByUserId(storedUserId);

            // filter doar device-urile asociate cu userId-ul curent
            const myDevices = response.data.filter(device => device.userId === parseInt(storedUserId));

            setDevices(myDevices);

            // lista sa fie vizibila în WebSocket
            devicesRef.current = myDevices;

            console.log('My devices:', myDevices);
        } catch (err) {
            setError('Failed to fetch devices: ' + err.message);
            console.error('Error fetching devices:', err);
        } finally {
            setLoading(false);
        }
    };

    // pentru sa nu duplicam mesajele (React StrictMode face dublu render uneori)
    const addMessageIfNotExists = (newMsg) => {
        setMessages((prev) => {
            const exists = prev.some(m =>
                m.content === newMsg.content &&
                m.sender === newMsg.sender &&
                Math.abs((newMsg.timestamp || Date.now()) - (m.timestamp || 0)) < 1000
            );
            if (exists) return prev;
            return [...prev, { ...newMsg, timestamp: Date.now() }];
        });
    };

    // 3. Funcția de trimitere
    const sendMessage = async () => {
        if (inputMessage.trim()) {
            const chatMessage = {
                sender: currentUser,
                content: inputMessage,
                receiverId: targetUser // Adminul setează asta când dă reply
            };

            // Adăugăm mesajul local instant (UX)
            setMessages(prev => [...prev, { ...chatMessage, timestamp: Date.now() }]);
            setInputMessage('');

            try {
                if (userRole === 'admin') {
                    // === CAZ ADMIN ===
                    // Adminul trimite DIRECT la WebSocket Service (Port 8086)
                    // El nu are nevoie de AI, vrea doar să trimită mesajul omului
                    await fetch('http://localhost:8086/api/send-chat', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(chatMessage)
                    });
                } else {
                    // === CAZ CLIENT ===
                    // Clientul trimite la Customer Support (Port 8087) pentru AI/Reguli
                    await fetch('http://localhost:8087/chat/send', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(chatMessage)
                    });
                }
            } catch (error) {
                console.error("Failed to send message", error);
            }
        }
    };

    // Funcție pentru Admin: Selectează userul pentru reply
    const handleUserClick = (username) => {
        if (userRole === 'admin' && username !== currentUser && username !== 'SupportBot') {
            setTargetUser(username);
            console.log("Replying to:", username);
        }
    };

    // Funcție pentru a anula reply-ul
    const cancelReply = () => {
        setTargetUser(null);
    };

    const clearHistory = () => {
        setMessages([]);
        localStorage.removeItem(storageKey);
    };

    return (
        <div className="chat-page-container" style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
            <div className="chat-window" style={{ border: '1px solid #ccc', borderRadius: '8px', overflow: 'hidden', height: '500px', display: 'flex', flexDirection: 'column' }}>

                {/* HEADER */}
                <div className="chat-header" style={{ background: '#f5f5f5', padding: '10px', borderBottom: '1px solid #ddd', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3>
                        {userRole === 'admin' ? 'Admin Dashboard' : 'Support Chat'}
                        <span style={{ color: isConnected ? 'green' : 'red', marginLeft: '10px' }}>●</span>
                    </h3>
                    <button onClick={clearHistory} style={{ fontSize: '10px', background: '#ff4444', color: 'white', border: 'none', padding: '5px', borderRadius: '3px', cursor: 'pointer' }}>Clear</button>
                </div>

                {/* MESSAGES AREA */}
                <div className="chat-messages" style={{ flex: 1, padding: '10px', overflowY: 'auto', background: '#fff' }}>
                    {messages.map((msg, index) => {
                        const isMe = msg.sender === currentUser;
                        const isSystem = msg.sender === 'SupportBot';
                        const isAdminRequest = msg.isAdminRequest; // Flag-ul pus de noi la subscribe
                        const isAlert = msg.isAlert;

                        return (
                            <div key={index} style={{
                                display: 'flex',
                                justifyContent: isMe ? 'flex-end' : 'flex-start',
                                marginBottom: '10px'
                            }}>
                                <div style={{
                                    maxWidth: '70%',
                                    padding: '8px 12px',
                                    borderRadius: '12px',
                                    background: isAlert
                                        ? '#ff4444' // ROȘU (Alertă)
                                        : (isMe ? '#007bff' : (isAdminRequest ? '#ffeb3b' : '#e9ecef')),
                                    color: (isMe || isAlert) ? 'white' : 'black',
                                    border: isAdminRequest ? '2px solid orange' : 'none'
                                }}>
                                    {/* Numele Sender-ului (Clickabil pt Admin) */}
                                    <div
                                        onClick={() => handleUserClick(msg.sender)}
                                        style={{
                                            fontSize: '10px',
                                            fontWeight: 'bold',
                                            marginBottom: '2px',
                                            opacity: 0.8,
                                            cursor: (userRole === 'admin' && !isMe && !isSystem) ? 'pointer' : 'default',
                                            textDecoration: (userRole === 'admin' && !isMe && !isSystem) ? 'underline' : 'none'
                                        }}
                                    >
                                        {msg.sender} {isAdminRequest && '(NEEDS HELP)'}
                                    </div>

                                    <div>{msg.content}</div>
                                </div>
                            </div>
                        );
                    })}
                    <div ref={messagesEndRef} />
                </div>

                {/* INPUT AREA */}
                <div className="chat-input-area" style={{ padding: '10px', borderTop: '1px solid #ddd', background: '#f9f9f9' }}>

                    {/* Indicator cui răspundem (Doar Admin) */}
                    {targetUser && (
                        <div style={{ fontSize: '12px', color: '#666', marginBottom: '5px', display: 'flex', justifyContent: 'space-between' }}>
                            <span>Replying to: <strong>{targetUser}</strong></span>
                            <button onClick={cancelReply} style={{ border: 'none', background: 'transparent', color: 'red', cursor: 'pointer' }}>✕ Cancel</button>
                        </div>
                    )}

                    <div style={{ display: 'flex', gap: '10px' }}>
                        <input
                            type="text"
                            value={inputMessage}
                            onChange={(e) => setInputMessage(e.target.value)}
                            onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
                            placeholder={targetUser ? `Message @${targetUser}...` : "Type a message..."}
                            style={{ flex: 1, padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                        <button
                            onClick={sendMessage}
                            style={{ padding: '8px 16px', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                        >
                            Send
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Chat;