import React, { useState, useEffect } from 'react';
import {authAPI, usersAPI} from '../services/api';
import '../css/UsersPage.css';

const UsersPage = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingUser, setEditingUser] = useState(null);

    // 'password: ""'
    const [formData, setFormData] = useState({
        id: '',
        name: '',
        email: '',
        username: '',
        role: 'client',
        age: '',
        address: '',
        password: ''
    });

    useEffect(() => {

        fetchUsers();

        // handler pentru beforeunload (confirmare la refresh/închidere)
        const handleBeforeUnload = (e) => {
            e.preventDefault();
            e.returnValue = ''; // mesaj standard browser
        };

        // handler pentru F5 / Ctrl+R
        const handleKeyDown = (e) => {
            if (e.key === "F5" || (e.ctrlKey && e.key === "r")) {
                e.preventDefault();
                alert("Refresh-ul este blocat pe această pagină!");
            }
        };

        // adauga evenimentele
        window.addEventListener("beforeunload", handleBeforeUnload);
        window.addEventListener("keydown", handleKeyDown);

        // cleanup cand componenta se demonteaza
        return () => {
            window.removeEventListener("beforeunload", handleBeforeUnload);
            window.removeEventListener("keydown", handleKeyDown);
        };
    }, []);


    const fetchUsers = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await usersAPI.getAll();
            setUsers(response.data);
        } catch (err) {
            setError('Failed to fetch users: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // adaugat 'password: ""' pentru resetare
    const resetForm = () => {
        setFormData({
            id: '',
            name: '',
            email: '',
            username: '',
            role: 'client',
            age: '',
            address: '',
            password: ''
        });
        setEditingUser(null);
        setIsFormOpen(false);
    };

    // logica pentru parola la trimitere
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        const userData = {
            ...formData,
            id: formData.id ? parseInt(formData.id) : null,
            age: formData.age ? parseInt(formData.age) : null
        };

        // LOGICA PENTRU PAROLĂ:
        // Dacă edităm și parola e goală, o ștergem din obiectul trimis
        // pentru a nu suprascrie parola existentă în backend.
        if (editingUser && !userData.password) {
            delete userData.password;
        }

        try {
            if (editingUser) {
                // update user
                await usersAPI.update(editingUser.id, userData);
            } else {
                // creare user
                const credential = {
                    id: userData.id,
                    username: userData.username,
                    password: userData.password,
                    role: userData.role
                };
                await authAPI.register(credential);
                await usersAPI.create(userData);
            }
            resetForm();
            fetchUsers();
        } catch (err) {
            setError(editingUser ? 'Failed to update user: ' + err.message : 'Failed to create user: ' + err.message);
        }
    };

    // setat 'password: ""' la editare (să nu populeze hash-ul)
    const handleEdit = (user) => {
        setFormData({
            id: user.id || '',
            name: user.name || '',
            email: user.email || '',
            username: user.username || '',
            role: user.role || 'client',
            age: user.age || '',
            address: user.address || '',
            password: '' // IMPORTANT: Se lasă gol la editare
        });
        setEditingUser(user);
        setIsFormOpen(true);
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure you want to delete this user?')) {
            return;
        }

        setError(null);
        try {
            // sterg user
            await usersAPI.delete(id);
            await authAPI.delete(id);
            fetchUsers();
        } catch (err) {
            setError('Failed to delete user: ' + err.message);
        }
    };

    return (
        <div className="users-page">
            <div className="page-header">
                <h1>User Management</h1>
                <button
                    className="btn btn-primary"
                    onClick={() => setIsFormOpen(true)}
                >
                    + Add New User
                </button>
            </div>

            {error && (
                <div className="alert alert-error">
                    {error}
                    <button onClick={() => setError(null)}>×</button>
                </div>
            )}

            {isFormOpen && (
                <div className="modal-overlay" onClick={resetForm}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>{editingUser ? 'Edit User' : 'Add New User'}</h2>
                            <button className="close-btn" onClick={resetForm}>×</button>
                        </div>

                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="id">ID *</label>
                                <input
                                    type="number"
                                    id="id"
                                    name="id"
                                    value={formData.id}
                                    onChange={handleInputChange}
                                    required
                                    disabled={editingUser !== null}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="name">Name *</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="email">Email *</label>
                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="username">Username</label>
                                <input
                                    type="text"
                                    id="username"
                                    name="username"
                                    value={formData.username}
                                    onChange={handleInputChange}
                                />
                            </div>

                            {/* parola în formular */}
                            <div className="form-group">
                                <label htmlFor="password">
                                    Password { !editingUser ? '*' : '' }
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    name="password"
                                    value={formData.password}
                                    onChange={handleInputChange}
                                    required={!editingUser} // Obligatoriu doar la creare
                                    placeholder={editingUser ? "Leave blank to keep same password" : ""}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="role">Role *</label>
                                <select
                                    id="role"
                                    name="role"
                                    value={formData.role}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="admin">Admin</option>
                                    <option value="client">Client</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label htmlFor="age">Age</label>
                                <input
                                    type="number"
                                    id="age"
                                    name="age"
                                    value={formData.age}
                                    onChange={handleInputChange}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="address">Address</label>
                                <input
                                    type="text"
                                    id="address"
                                    name="address"
                                    value={formData.address}
                                    onChange={handleInputChange}
                                />
                            </div>

                            <div className="form-actions">
                                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingUser ? 'Update User' : 'Create User'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {loading ? (
                <div className="loading">Loading users...</div>
            ) : (
                <div className="table-container">
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Username</th>
                            <th>Role</th>
                            <th>Age</th>
                            <th>Address</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.length === 0 ? (
                            <tr>
                                <td colSpan="8" className="empty-state">
                                    No users found. Click "Add New User" to create one.
                                </td>
                            </tr>
                        ) : (
                            users.map(user => (
                                <tr key={user.id}>
                                    <td>{user.id}</td>
                                    <td>{user.name}</td>
                                    <td>{user.email}</td>
                                    <td>{user.username || '-'}</td>
                                    <td>
                      <span className={`badge badge-${user.role}`}>
                        {user.role}
                      </span>
                                    </td>
                                    <td>{user.age || '-'}</td>
                                    <td>{user.address || '-'}</td>
                                    <td>
                                        <div className="action-buttons">
                                            <button
                                                className="btn btn-sm btn-edit"
                                                onClick={() => handleEdit(user)}
                                                title="Edit"
                                            >
                                                ✏️
                                            </button>
                                            <button
                                                className="btn btn-sm btn-delete"
                                                onClick={() => handleDelete(user.id)}
                                                title="Delete"
                                            >
                                                🗑️
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default UsersPage;