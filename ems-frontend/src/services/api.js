import axios from 'axios';

const API_BASE_URL = 'http://localhost:80';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// interceptor pentru a adauga token-ul la fiecare request
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Users API
export const usersAPI = {
    getAll: () => api.get('/users'),
    getById: (id) => api.get(`/users/${id}`),
    create: (user) => api.post('/users', user),
    update: (id, user) => api.put(`/users/${id}`, user),
    delete: (id) => api.delete(`/users/${id}`)
};

// Devices API
export const devicesAPI = {
    getAll: () => api.get('/devices'),
    getById: (id) => api.get(`/devices/${id}`),
    getByUserId: (user_id) => api.get(`/devices/my/${user_id}`),
/*    getByUserId: (user_id) => api.get(`/devices`, {
        params: {
            userId: user_id //devices?userId=123
        }}),
 */
    create: (device) => api.post('/devices', device),
    update: (id, device) => api.put(`/devices/${id}`, device),
    delete: (id) => api.delete(`/devices/${id}`)
};

// Auth API
export const authAPI = {
    login: (credentials) => api.post('/auth/login', credentials),
    register: (userData) => api.post('/auth/register', userData),
    delete: (id) => api.delete(`/auth/delete/${id}`)
};

export default api;