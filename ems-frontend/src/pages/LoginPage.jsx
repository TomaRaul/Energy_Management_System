
import React, { useState } from 'react';
import { authAPI, usersAPI } from '../services/api';
import '../css/LoginPage.css';

function LoginPage({ onLoginSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            const credentials = { username, password };
            const response = await authAPI.login(credentials);

            if (response.data) {
                console.log('Login response:', response.data);

                const token = response.data.token;

                if (token) {
                    localStorage.setItem('authToken', token);
                }

                const usersResponse = await usersAPI.getAll();
                const currentUser = usersResponse.data.find(
                    user => user.username === username
                );

                if (!currentUser) {
                    setError('User not found in system. Please contact administrator.');
                    setIsLoading(false);
                    return;
                }

                const role = currentUser.role || 'client';
                const userId = currentUser.id;
                const userName = currentUser.name;

                localStorage.setItem('userRole', role);
                localStorage.setItem('userId', userId);
                localStorage.setItem('userName', userName);

                onLoginSuccess(role, userId);
            } else {
                setError('Invalid response from server.');
            }

        } catch (err) {
            setIsLoading(false);

            if (err.response && (err.response.status === 401 || err.response.status === 400)) {
                setError('Invalid username/password');
            } else {
                setError('An error occurred. Please try again later.');
            }
            console.error('Login error:', err);
        }

        setIsLoading(false);
    };

    return (
        <div className="login-container" style={{height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f5f5f5'}}>
            <form className="login-box" onSubmit={handleSubmit} style={{padding: '2rem', background: 'white', borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)', minWidth: '350px'}}>
                <h2 style={{textAlign: 'center', marginBottom: '1.5rem', color: '#333'}}>🔐 Login</h2>

                {error && <p className="error-message" style={{color: '#d32f2f', background: '#ffebee', padding: '10px', borderRadius: '4px', marginBottom: '1rem'}}>{error}</p>}

                <div className="input-group" style={{marginBottom: '1rem', textAlign: 'left'}}>
                    <label htmlFor="username" style={{display: 'block', marginBottom: '0.5rem', fontWeight: '500', color: '#555'}}>Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        disabled={isLoading}
                        required
                        style={{width: '100%', padding: '0.75rem', border: '1px solid #ddd', borderRadius: '4px', fontSize: '14px'}}
                    />
                </div>

                <div className="input-group" style={{marginBottom: '1.5rem', textAlign: 'left'}}>
                    <label htmlFor="password" style={{display: 'block', marginBottom: '0.5rem', fontWeight: '500', color: '#555'}}>Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        disabled={isLoading}
                        required
                        style={{width: '100%', padding: '0.75rem', border: '1px solid #ddd', borderRadius: '4px', fontSize: '14px'}}
                    />
                </div>

                <button
                    type="submit"
                    className="login-button"
                    disabled={isLoading}
                    style={{
                        width: '100%',
                        padding: '0.75rem',
                        border: 'none',
                        background: isLoading ? '#ccc' : '#007bff',
                        color: 'white',
                        cursor: isLoading ? 'not-allowed' : 'pointer',
                        borderRadius: '4px',
                        fontSize: '16px',
                        fontWeight: '500',
                        transition: 'background 0.3s'
                    }}
                >
                    {isLoading ? 'Logging in...' : 'Login'}
                </button>

            </form>
        </div>
    );
}

export default LoginPage;
