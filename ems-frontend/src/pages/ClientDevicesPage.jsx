import React, { useState, useEffect } from 'react';
import { devicesAPI } from '../services/api';
import './DevicesPage.css';

const ClientDevicesPage = ({ userId }) => {
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchMyDevices();
  }, [userId]);

  const fetchMyDevices = async () => {
    setLoading(true);
    setError(null);
    try {
      // citire toate device-urile unui client
      const response = await devicesAPI.getByUserId(userId);
      
      // filter doar device-urile asociate cu userId-ul curent
      const myDevices = response.data.filter(device => device.userId === parseInt(userId));
      
      setDevices(myDevices);
      
      console.log('My devices:', myDevices);
    } catch (err) {
      setError('Failed to fetch devices: ' + err.message);
      console.error('Error fetching devices:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="devices-page">
      <div className="page-header">
        <h1>My Devices</h1>
        <p style={{color: '#666', fontSize: '14px', marginTop: '0.5rem'}}>
          View your assigned energy monitoring devices
        </p>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
          <button onClick={() => setError(null)}>×</button>
        </div>
      )}

      {loading ? (
        <div className="loading">Loading your devices...</div>
      ) : devices.length === 0 ? (
        <div className="empty-state" style={{
          textAlign: 'center',
          padding: '3rem',
          background: '#f9f9f9',
          borderRadius: '8px',
          marginTop: '2rem'
        }}>
          <div style={{fontSize: '48px', marginBottom: '1rem'}}>📭</div>
          <h3 style={{marginBottom: '0.5rem'}}>No Devices Assigned</h3>
          <p style={{color: '#666'}}>You don't have any energy monitoring devices assigned to your account yet.</p>
          <p style={{color: '#666', fontSize: '14px', marginTop: '0.5rem'}}>Please contact your administrator.</p>
        </div>
      ) : (
        <div className="table-container">
          <div className="devices-grid">
            {devices.map(device => (
              <div key={device.id} className="device-card" style={{
                background: 'white',
                border: '1px solid #e0e0e0',
                borderRadius: '8px',
                padding: '1.5rem',
                boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
                transition: 'box-shadow 0.3s',
              }}>
                <div className="device-header" style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'start',
                  marginBottom: '1rem'
                }}>
                  <div>
                    <h3 style={{margin: '0 0 0.5rem 0', fontSize: '18px', color: '#333'}}>
                      🔌 {device.name}
                    </h3>
                    <span style={{
                      fontSize: '12px',
                      color: '#999',
                      background: '#f5f5f5',
                      padding: '0.25rem 0.5rem',
                      borderRadius: '4px'
                    }}>
                      ID: {device.id}
                    </span>
                  </div>
                  <span style={{
                    background: '#4caf50',
                    color: 'white',
                    padding: '0.25rem 0.75rem',
                    borderRadius: '12px',
                    fontSize: '12px',
                    fontWeight: 'bold'
                  }}>
                    Active
                  </span>
                </div>

                <div className="device-details" style={{
                  borderTop: '1px solid #f0f0f0',
                  paddingTop: '1rem'
                }}>
                  <div className="detail-row" style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    marginBottom: '0.75rem'
                  }}>
                    <span style={{color: '#666', fontSize: '14px'}}>Maximum Consumption:</span>
                    <span style={{
                      fontWeight: 'bold',
                      color: '#2196f3',
                      fontSize: '16px'
                    }}>
                      {device.MCV ? `${device.MCV} W` : 'Not specified'}
                    </span>
                  </div>

                  {/* detalii */}
                  <div className="detail-row" style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    padding: '0.75rem',
                    background: '#f9f9f9',
                    borderRadius: '4px',
                    marginTop: '1rem'
                  }}>
                    <span style={{fontSize: '14px', color: '#666'}}>Status:</span>
                    <span style={{fontSize: '14px', fontWeight: '500', color: '#4caf50'}}>
                      ✓ Operational
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Summary Card */}
          <div className="summary-card" style={{
            marginTop: '2rem',
            padding: '1.5rem',
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            borderRadius: '8px',
            color: 'white'
          }}>
            <h3 style={{margin: '0 0 1rem 0', fontSize: '18px'}}>📊 Summary</h3>
            <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem'}}>
              <div>
                <div style={{fontSize: '12px', opacity: 0.9, marginBottom: '0.25rem'}}>Total Devices</div>
                <div style={{fontSize: '24px', fontWeight: 'bold'}}>{devices.length}</div>
              </div>
              <div>
                <div style={{fontSize: '12px', opacity: 0.9, marginBottom: '0.25rem'}}>Total Max Consumption</div>
                <div style={{fontSize: '24px', fontWeight: 'bold'}}>
                  {devices.reduce((sum, d) => sum + (d.MCV || 0), 0)} W
                </div>
              </div>
              <div>
                <div style={{fontSize: '12px', opacity: 0.9, marginBottom: '0.25rem'}}>Average MCV</div>
                <div style={{fontSize: '24px', fontWeight: 'bold'}}>
                  {devices.length > 0 ? Math.round(devices.reduce((sum, d) => sum + (d.MCV || 0), 0) / devices.length) : 0} W
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* CSS pentru Grid Layout */}
      <style jsx>{`
        .devices-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
          gap: 1.5rem;
          margin-top: 1.5rem;
        }
        
        .device-card:hover {
          box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
        }

        @media (max-width: 768px) {
          .devices-grid {
            grid-template-columns: 1fr;
          }
        }
      `}</style>
    </div>
  );
};

export default ClientDevicesPage;
