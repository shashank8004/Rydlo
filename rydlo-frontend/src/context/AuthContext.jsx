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
        if (token) {
            try {
                const decoded = jwtDecode(token);
                // Assuming your JWT has 'sub' (email) and 'roles'
                setUser({
                    email: decoded.sub,
                    roles: decoded.roles, // or decoded.authorities depending on your JWT structure
                    token: token
                });
            } catch (error) {
                console.error("Invalid token on startup", error);
                localStorage.removeItem('token');
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await api.post('/auth/login', { email, password });
            const { token } = response.data; // Adjust based on your AuthController response structure

            localStorage.setItem('token', token);

            const decoded = jwtDecode(token);
            setUser({
                email: decoded.sub,
                roles: decoded.roles,
                token: token
            });
            return true;
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
