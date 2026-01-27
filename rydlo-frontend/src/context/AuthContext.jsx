import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';
import { jwtDecode } from 'jwt-decode'; // You might need to install this: npm install jwt-decode

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check if user is logged in on mount
        const token = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');

        if (token) {
            try {
                if (storedUser) {
                    setUser(JSON.parse(storedUser));
                } else {
                    // Fallback to decoding token if user data missing
                    const decoded = jwtDecode(token);
                    setUser({
                        email: decoded.sub,
                        roles: decoded.roles,
                        token: token
                    });
                }
            } catch (error) {
                console.error("Invalid token on startup", error);
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await api.post('/auth/login', { email, password });
            const { token, userId, firstName, lastName, role } = response.data;

            const userData = {
                id: userId,
                email,
                firstName,
                lastName,
                roles: [role],
                token
            };

            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(userData));

            setUser(userData);
            return userData; // Return the full user object
        } catch (error) {
            console.error("Login failed", error);
            throw error;
        }
    };

    const register = async (userData) => {
        // This calls /auth/register or similar
        // Implementation can vary based on your flow (auto-login after reg?)
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    const hasRole = (roleName) => {
        if (!user || !user.roles) return false;
        return user.roles.includes(roleName);
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout, hasRole }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
