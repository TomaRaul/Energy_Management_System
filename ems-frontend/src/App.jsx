
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import UsersPage from './pages/UsersPage';
import DevicesPage from './pages/DevicesPage';
import ClientDevicesPage from './pages/ClientDevicesPage';
import EnergyConsumptionPage from './pages/EnergyConsumptionPage';
import LoginPage from './pages/LoginPage';
import ChatPage from './pages/ChatPage';
import './App.css';

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userRole, setUserRole] = useState(null); // 'admin' sau 'client'
    const [userId, setUserId] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        const role = localStorage.getItem('userRole');
        const id = localStorage.getItem('userId');

        if (token && role) {
            setIsAuthenticated(true);
            setUserRole(role);
            setUserId(id);
        }
        setIsLoading(false);
    }, []);

    // functie login
    const handleLoginSuccess = (role, id) => {
        setIsAuthenticated(true);
        setUserRole(role);
        setUserId(id);
    };

    // functie logout
    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userId');
        setIsAuthenticated(false);
        setUserRole(null);
        setUserId(null);
    };

    if (isLoading) {
        return <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}>Loading...</div>;
    }

    return (
        <Router>
            <Routes>
                {/* Ruta pentru Login */}
                <Route
                    path="/login"
                    element={
                        isAuthenticated ? (
                            <Navigate to={userRole === 'admin' ? '/users' : '/my-devices'} replace />
                        ) : (
                            <LoginPage onLoginSuccess={handleLoginSuccess} />
                        )
                    }
                />

                {/* Rutele protejate */}
                <Route
                    path="/*"
                    element={
                        isAuthenticated ? (
                            <div className="app">
                                {/* Navbar adaptata pe rol */}
                                <nav className="navbar">
                                    <div className="nav-container">
                                        <h1 className="nav-logo">⚡ Energy Management System</h1>
                                        <ul className="nav-menu">
                                            {/* Admin vede Users + Devices */}
                                            {userRole === 'admin' && (
                                                <>
                                                    <li className="nav-item">
                                                        <Link to="/users" className="nav-link">
                                                            👥 Users
                                                        </Link>
                                                    </li>
                                                    <li className="nav-item">
                                                        <Link to="/devices" className="nav-link">
                                                            🔌 Devices
                                                        </Link>
                                                    </li>
                                                </>
                                            )}

                                            {/* Client vede doar My Devices */}
                                            {userRole === 'client' && (
                                                <>
                                                <li className="nav-item">
                                                    <Link to="/my-devices" className="nav-link">
                                                        🔌 My Devices
                                                    </Link>
                                                </li>
                                                <li className="nav-item">
                                                    <Link to="/consumption" className="nav-link">
                                                        📊 Consumption
                                                    </Link>
                                                </li>
                                                </>
                                            )}

                                            {/* BUTONUL DE CHAT  */}
                                            <li className="nav-item">
                                                <Link to="/chat" className="nav-link">
                                                    💬 Chat
                                                </Link>
                                            </li>
                                            {/* ----------------------------------------------- */}

                                            {/* Badge pentru role + Logout */}
                                            <li className="nav-item" style={{marginLeft: 'auto', display: 'flex', gap: '1rem', alignItems: 'center'}}>
                                                <span className="role-badge" style={{
                                                    padding: '0.25rem 0.75rem',
                                                    background: userRole === 'admin' ? '#4caf50' : '#2196f3',
                                                    color: 'white',
                                                    borderRadius: '12px',
                                                    fontSize: '12px',
                                                    fontWeight: 'bold',
                                                    textTransform: 'uppercase'
                                                }}>
                                                    {userRole}
                                                </span>
                                                <button
                                                    onClick={handleLogout}
                                                    className="nav-link"
                                                    style={{
                                                        background: '#f44336',
                                                        color: 'white',
                                                        border: 'none',
                                                        padding: '0.5rem 1rem',
                                                        borderRadius: '4px',
                                                        cursor: 'pointer',
                                                        fontWeight: '500'
                                                    }}
                                                >
                                                    🚪 Logout
                                                </button>
                                            </li>
                                        </ul>
                                    </div>
                                </nav>

                                <main className="main-content">
                                    <Routes>
                                        {/* Rute pentru Admin */}
                                        {userRole === 'admin' && (
                                            <>
                                                <Route path="/users" element={<UsersPage />} />
                                                <Route path="/devices" element={<DevicesPage isAdmin={true} />} />
                                                <Route path="/chat" element={<ChatPage />} />
                                                <Route path="/" element={<Navigate to="/users" replace />} />
                                            </>
                                        )}

                                        {/* Rute pentru Client */}
                                        {userRole === 'client' && (
                                            <>
                                                <Route
                                                    path="/my-devices"
                                                    element={<ClientDevicesPage userId={userId} />}
                                                />
                                                <Route
                                                    path="/consumption"
                                                    element={<EnergyConsumptionPage userId={userId} />}
                                                />
                                                <Route path="/chat" element={<ChatPage />} />
                                                <Route path="/" element={<Navigate to="/my-devices" replace />} />
                                            </>
                                        )}

                                        {/* Fallback pentru rute inexistente */}
                                        <Route path="*" element={<Navigate to="/" replace />} />
                                    </Routes>
                                </main>

                                <footer className="footer">
                                    <p>© 2025 Raul Toma - {userRole === 'admin' ? 'Administrator' : 'Client'} Dashboard</p>
                                </footer>
                            </div>
                        ) : (
                            <Navigate to="/login" replace />
                        )
                    }
                />
            </Routes>
        </Router>
    );
}

export default App;
