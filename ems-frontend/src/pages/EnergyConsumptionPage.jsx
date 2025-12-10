import React, { useState, useEffect } from 'react';
import { devicesAPI, monitoringAPI } from '../services/api';
import {
    LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid,
    Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import './EnergyConsumptionPage.css';

const EnergyConsumptionPage = ({ userId }) => {
    const [devices, setDevices] = useState([]);
    const [selectedDevice, setSelectedDevice] = useState(null);
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
    const [consumptionData, setConsumptionData] = useState([]);
    const [chartType, setChartType] = useState('line'); // 'line' or 'bar'
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Fetch devices când se încarcă componenta
    useEffect(() => {
        fetchUserDevices();
    }, [userId]);

    // Fetch consumption data când se schimbă device-ul sau data
    useEffect(() => {
        if (selectedDevice && selectedDate) {
            fetchConsumptionData();
        }
    }, [selectedDevice, selectedDate]);

    const fetchUserDevices = async () => {
        try {
            const response = await devicesAPI.getByUserId(userId);
            const userDevices = response.data.filter(d => d.userId === parseInt(userId));
            setDevices(userDevices);

            // Selectează automat primul device
            if (userDevices.length > 0) {
                setSelectedDevice(userDevices[0].id);
            }
        } catch (err) {
            console.error('Error fetching devices:', err);
            setError('Failed to load devices');
        }
    };

    const fetchConsumptionData = async () => {
        setLoading(true);
        setError(null);

        try {
            // API call: GET /monitor/consumption?deviceId=1&date=2025-01-15
            const response = await monitoringAPI.getConsumption(selectedDevice, selectedDate);

            // Transformă datele pentru chart
            // Răspunsul are forma: { hour: 6, hourConsumption: 16.91 }
            const chartData = response.data.map(item => ({
                hour: `${String(item.hour).padStart(2, '0')}:00`,
                consumption: parseFloat((item.hourConsumption || item.totalKwh || 0).toFixed(2)),
                measurements: item.measurementCount || 0
            }));

            // Adaugă ore lipsă cu valoare 0
            const fullDayData = [];
            for (let h = 0; h < 24; h++) {
                const existingData = chartData.find(d => d.hour === `${String(h).padStart(2, '0')}:00`);
                fullDayData.push({
                    hour: `${String(h).padStart(2, '0')}:00`,
                    consumption: existingData ? existingData.consumption : 0,
                    measurements: existingData ? existingData.measurements : 0
                });
            }

            setConsumptionData(fullDayData);

        } catch (err) {
            console.error('Error fetching consumption data:', err);
            setError('Failed to load consumption data. Make sure the device has data for the selected date.');
            setConsumptionData([]);
        } finally {
            setLoading(false);
        }
    };

    const totalConsumption = consumptionData.reduce((sum, item) => sum + item.consumption, 0);
    const avgConsumption = consumptionData.length > 0 ? totalConsumption / 24 : 0;
    const peakHour = consumptionData.reduce((max, item) =>
            item.consumption > max.consumption ? item : max,
        { hour: '00:00', consumption: 0 }
    );

    return (
        <div className="energy-consumption-page">
            {/* Header */}
            <div className="page-header">
                <h1>📊 Energy Consumption Analytics</h1>
                <p style={{ color: '#666', fontSize: '14px', marginTop: '0.5rem' }}>
                    View your hourly energy consumption patterns
                </p>
            </div>

            {/* Filters */}
            <div className="filters-container">
                {/* Device Selection */}
                <div className="filter-group">
                    <label htmlFor="device-select">
                        <span className="filter-icon">🔌</span>
                        Select Device
                    </label>
                    <select
                        id="device-select"
                        value={selectedDevice || ''}
                        onChange={(e) => setSelectedDevice(parseInt(e.target.value))}
                        className="filter-select"
                    >
                        <option value="">Choose a device...</option>
                        {devices.map(device => (
                            <option key={device.id} value={device.id}>
                                {device.name} (ID: {device.id})
                            </option>
                        ))}
                    </select>
                </div>

                {/* Date Selection */}
                <div className="filter-group">
                    <label htmlFor="date-select">
                        <span className="filter-icon">📅</span>
                        Select Date
                    </label>
                    <input
                        id="date-select"
                        type="date"
                        value={selectedDate}
                        onChange={(e) => setSelectedDate(e.target.value)}
                        max={new Date().toISOString().split('T')[0]}
                        className="filter-input"
                    />
                </div>

                {/* Chart Type Toggle */}
                <div className="filter-group">
                    <label>
                        <span className="filter-icon">📈</span>
                        Chart Type
                    </label>
                    <div className="chart-type-toggle">
                        <button
                            className={`toggle-btn ${chartType === 'line' ? 'active' : ''}`}
                            onClick={() => setChartType('line')}
                        >
                            📈 Line Chart
                        </button>
                        <button
                            className={`toggle-btn ${chartType === 'bar' ? 'active' : ''}`}
                            onClick={() => setChartType('bar')}
                        >
                            📊 Bar Chart
                        </button>
                    </div>
                </div>
            </div>

            {/* Error Message */}
            {error && (
                <div className="alert alert-error">
                    {error}
                    <button onClick={() => setError(null)}>×</button>
                </div>
            )}

            {/* Statistics Cards */}
            {!loading && consumptionData.length > 0 && (
                <div className="stats-container">
                    <div className="stat-card">
                        <div className="stat-icon" style={{ background: '#2196f3' }}>⚡</div>
                        <div className="stat-content">
                            <div className="stat-label">Total Consumption</div>
                            <div className="stat-value">{totalConsumption.toFixed(2)} kWh</div>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon" style={{ background: '#4caf50' }}>📊</div>
                        <div className="stat-content">
                            <div className="stat-label">Average per Hour</div>
                            <div className="stat-value">{avgConsumption.toFixed(2)} kWh</div>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon" style={{ background: '#ff9800' }}>🔥</div>
                        <div className="stat-content">
                            <div className="stat-label">Peak Hour</div>
                            <div className="stat-value">{peakHour.hour}</div>
                            <div className="stat-sublabel">{peakHour.consumption.toFixed(2)} kWh</div>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon" style={{ background: '#9c27b0' }}>📅</div>
                        <div className="stat-content">
                            <div className="stat-label">Date</div>
                            <div className="stat-value" style={{ fontSize: '16px' }}>
                                {new Date(selectedDate).toLocaleDateString('en-US', {
                                    weekday: 'short',
                                    year: 'numeric',
                                    month: 'short',
                                    day: 'numeric'
                                })}
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Chart Container */}
            <div className="chart-container">
                {loading ? (
                    <div className="loading-state">
                        <div className="spinner"></div>
                        <p>Loading consumption data...</p>
                    </div>
                ) : !selectedDevice ? (
                    <div className="empty-state">
                        <div className="empty-icon">🔌</div>
                        <h3>Select a Device</h3>
                        <p>Choose a device from the dropdown above to view its energy consumption</p>
                    </div>
                ) : consumptionData.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">📭</div>
                        <h3>No Data Available</h3>
                        <p>No consumption data found for the selected device and date.</p>
                        <p style={{ fontSize: '14px', color: '#999', marginTop: '0.5rem' }}>
                            Try selecting a different date or make sure the device is sending data.
                        </p>
                    </div>
                ) : (
                    <div className="chart-wrapper">
                        <h3 className="chart-title">
                            Hourly Energy Consumption - {selectedDate}
                        </h3>

                        <ResponsiveContainer width="100%" height={400}>
                            {chartType === 'line' ? (
                                <LineChart
                                    data={consumptionData}
                                    margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                                >
                                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                    <XAxis
                                        dataKey="hour"
                                        label={{ value: 'Hour of Day', position: 'insideBottom', offset: -10 }}
                                        tick={{ fontSize: 12 }}
                                    />
                                    <YAxis
                                        label={{ value: 'Energy (kWh)', angle: -90, position: 'insideLeft' }}
                                        tick={{ fontSize: 12 }}
                                    />
                                    <Tooltip
                                        contentStyle={{
                                            backgroundColor: 'rgba(255, 255, 255, 0.95)',
                                            border: '1px solid #ddd',
                                            borderRadius: '8px',
                                            padding: '10px'
                                        }}
                                        formatter={(value, name) => {
                                            if (name === 'consumption') return [`${value} kWh`, 'Energy'];
                                            if (name === 'measurements') return [`${value} readings`, 'Measurements'];
                                            return value;
                                        }}
                                    />
                                    <Legend
                                        wrapperStyle={{ paddingTop: '20px' }}
                                    />
                                    <Line
                                        type="monotone"
                                        dataKey="consumption"
                                        stroke="#2196f3"
                                        strokeWidth={2}
                                        dot={{ fill: '#2196f3', r: 4 }}
                                        activeDot={{ r: 6 }}
                                        name="Energy Consumption"
                                    />
                                </LineChart>
                            ) : (
                                <BarChart
                                    data={consumptionData}
                                    margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                                >
                                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                    <XAxis
                                        dataKey="hour"
                                        label={{ value: 'Hour of Day', position: 'insideBottom', offset: -10 }}
                                        tick={{ fontSize: 12 }}
                                    />
                                    <YAxis
                                        label={{ value: 'Energy (kWh)', angle: -90, position: 'insideLeft' }}
                                        tick={{ fontSize: 12 }}
                                    />
                                    <Tooltip
                                        contentStyle={{
                                            backgroundColor: 'rgba(255, 255, 255, 0.95)',
                                            border: '1px solid #ddd',
                                            borderRadius: '8px',
                                            padding: '10px'
                                        }}
                                        formatter={(value, name) => {
                                            if (name === 'consumption') return [`${value} kWh`, 'Energy'];
                                            if (name === 'measurements') return [`${value} readings`, 'Measurements'];
                                            return value;
                                        }}
                                    />
                                    <Legend
                                        wrapperStyle={{ paddingTop: '20px' }}
                                    />
                                    <Bar
                                        dataKey="consumption"
                                        fill="#4caf50"
                                        name="Energy Consumption"
                                        radius={[8, 8, 0, 0]}
                                    />
                                </BarChart>
                            )}
                        </ResponsiveContainer>
                    </div>
                )}
            </div>
        </div>
    );
};

export default EnergyConsumptionPage;