import React, { useState, useEffect } from 'react';
import { devicesAPI, usersAPI } from '../services/api';
import '../css/DevicesPage.css';

const DevicesPage = () => {
  const [devices, setDevices] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingDevice, setEditingDevice] = useState(null);
  const [formData, setFormData] = useState({
    id: '',
    name: '',
    MCV: '',
    userId: ''
  });

    useEffect(() => {
        // 1. Apelează functiile de fetch
        fetchDevices();
        fetchUsers();

        // 2. Handler pentru beforeunload (confirmare la refresh/închidere)
        const handleBeforeUnload = (e) => {
            e.preventDefault();
            e.returnValue = '';
        };

        // 3. Handler pentru F5 / Ctrl+R
        const handleKeyDown = (e) => {
            if (e.key === "F5" || (e.ctrlKey && e.key === "r")) {
                e.preventDefault();
                alert("Refresh-ul este blocat pe această pagină!");
            }
        };

        // 4. Adauga evenimentele
        window.addEventListener("beforeunload", handleBeforeUnload);
        window.addEventListener("keydown", handleKeyDown);

        // 5. Cleanup
        return () => {
            window.removeEventListener("beforeunload", handleBeforeUnload);
            window.removeEventListener("keydown", handleKeyDown);
        };
    }, []);


  const fetchDevices = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await devicesAPI.getAll();
      setDevices(response.data);
    } catch (err) {
      setError('Failed to fetch devices: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await usersAPI.getAll();
      setUsers(response.data);
    } catch (err) {
      console.error('Failed to fetch users:', err);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const resetForm = () => {
    setFormData({
      id: '',
      name: '',
      MCV: '',
      userId: ''
    });
    setEditingDevice(null);
    setIsFormOpen(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const deviceData = {
      ...formData,
      id: formData.id ? parseInt(formData.id) : null,
      MCV: formData.MCV ? parseInt(formData.MCV) : null,
      userId: formData.userId ? parseInt(formData.userId) : null
    };

    try {
      if (editingDevice) {
        await devicesAPI.update(editingDevice.id, deviceData);
      } else {
        await devicesAPI.create(deviceData);
      }
      resetForm();
      fetchDevices();
    } catch (err) {
      setError(editingDevice ? 'Failed to update device: ' + err.message : 'Failed to create device: ' + err.message);
    }
  };

  const handleEdit = (device) => {
    setFormData({
      id: device.id || '',
      name: device.name || '',
      MCV: device.MCV || '',
      userId: device.userId || ''
    });
    setEditingDevice(device);
    setIsFormOpen(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this device?')) {
      return;
    }

    setError(null);
    try {
      await devicesAPI.delete(id);
      fetchDevices();
    } catch (err) {
      setError('Failed to delete device: ' + err.message);
    }
  };

  const getUserName = (userId) => {
    const user = users.find(u => u.id === userId);
    return user ? user.name : 'Unassigned';
  };

  return (
    <div className="devices-page">
      <div className="page-header">
        <h1>Device Management</h1>
        <button 
          className="btn btn-primary"
          onClick={() => setIsFormOpen(true)}
        >
          + Add New Device
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
              <h2>{editingDevice ? 'Edit Device' : 'Add New Device'}</h2>
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
                  disabled={editingDevice !== null}
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
                <label htmlFor="mcv">Maximum Consumption Value (MCV)</label>
                <input
                  type="number"
                  id="MCV"
                  name="MCV"
                  value={formData.MCV}
                  onChange={handleInputChange}
                  placeholder="Enter MCV in kWh"
                />
              </div>

              <div className="form-group">
                <label htmlFor="userId">Assign to User</label>
                <select
                  id="userId"
                  name="userId"
                  value={formData.userId}
                  onChange={handleInputChange}
                >
                  <option value="">-- Unassigned --</option>
                  {users.map(user => (
                    <option key={user.id} value={user.id}>
                      {user.name} ({user.email})
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingDevice ? 'Update Device' : 'Create Device'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {loading ? (
        <div className="loading">Loading devices...</div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>MCV (kWh)</th>
                <th>Assigned User</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {devices.length === 0 ? (
                <tr>
                  <td colSpan="5" className="empty-state">
                    No devices found. Click "Add New Device" to create one.
                  </td>
                </tr>
              ) : (
                devices.map(device => (
                  <tr key={device.id}>
                    <td>{device.id}</td>
                    <td>{device.name}</td>
                    <td>{device.MCV || '-'}</td>
                    <td>
                      {device.userId ? (
                        <span className="badge badge-user">
                          {getUserName(device.userId)}
                        </span>
                      ) : (
                        <span className="badge badge-unassigned">Unassigned</span>
                      )}
                    </td>
                    <td>
                      <div className="action-buttons">
                        <button 
                          className="btn btn-sm btn-edit"
                          onClick={() => handleEdit(device)}
                          title="Edit"
                        >
                          ✏️
                        </button>
                        <button 
                          className="btn btn-sm btn-delete"
                          onClick={() => handleDelete(device.id)}
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

export default DevicesPage;
