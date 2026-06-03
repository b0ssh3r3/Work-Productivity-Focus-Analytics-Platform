import axios from "axios"

const API_URL = 'http://localhost:8080/api'
let authToken = null;
let currentUserId = null;

const api = axios.create({
    baseURL: API_URL
});

api.interceptors.request.use((config) => {
    const userId = currentUserId || localStorage.getItem('userId');
    const token = authToken || localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }

    if (userId) {
        config.headers['X-User-ID'] = userId;
    }

    return config;
});

export const setAuthContext = ({ token, userId }) => {
    authToken = token || null;
    currentUserId = userId || null;
};

export const clearAuthContext = () => {
    authToken = null;
    currentUserId = null;
};

export const getActivities = () => api.get('/focus-sessions');
export const addActivity = (activity) => api.post('/focus-sessions', activity);
export const getActivityDetail = (id) => api.get(`/insights/focus-session/${id}`);
