import { Link, Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Menu, X, User, LogOut } from 'lucide-react';
import { useState } from 'react';

const MainLayout = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="min-h-screen flex flex-col bg-gray-50 font-sans text-slate-800">
            {/* Navbar */}
            <nav className="bg-white/80 backdrop-blur-md sticky top-0 z-50 border-b border-gray-100 shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex items-center">
                            <Link to="/" className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                                Rydlo
                            </Link>
                        </div>

                        {/* Desktop Menu */}
                        <div className="hidden md:flex items-center space-x-8">
                            <Link to="/" className="text-gray-600 hover:text-blue-600 transition font-medium">Home</Link>
                            <Link to="/bikes" className="text-gray-600 hover:text-blue-600 transition font-medium">Browse Bikes</Link>

                            {user ? (
                                <div className="flex items-center space-x-4">
                                    {/* Dashboard Link based on Role */}
                                    {user.roles?.includes('ROLE_OWNER') && (
                                        <Link to="/owner/dashboard" className="text-gray-600 hover:text-blue-600 transition font-medium">Dashboard</Link>
                                    )}
                                    {user.roles?.includes('ROLE_ADMIN') && (
                                        <Link to="/admin/dashboard" className="text-gray-600 hover:text-blue-600 transition font-medium">Dashboard</Link>
                                    )}
                                    {user.roles?.includes('ROLE_CUSTOMER') && (
                                        <>
                                            <Link to="/customer/rides" className={`text-gray-600 hover:text-blue-600 transition font-medium ${location.pathname === '/customer/rides' ? 'text-blue-600' : ''}`}>
                                                My Rides
                                            </Link>
                                            <Link to="/transactions" className={`text-gray-600 hover:text-blue-600 transition font-medium ${location.pathname === '/transactions' ? 'text-blue-600' : ''}`}>
                                                Transactions
                                            </Link>
                                        </>
                                    )}

                                    <span className="text-sm font-semibold text-gray-700">
                                        Hello, {user.firstName || user.email?.split('@')[0]}
                                    </span>
                                    <button
                                        onClick={handleLogout}
                                        className="flex items-center text-gray-500 hover:text-red-600 transition"
                                    >
                                        <LogOut className="w-5 h-5" />
                                    </button>
                                </div>
                            ) : (
                                <div className="flex items-center space-x-4">
                                    <Link to="/login" className="text-gray-600 hover:text-blue-600 font-medium transition">
                                        Login
                                    </Link>
                                    <Link to="/register" className="bg-blue-600 text-white px-5 py-2 rounded-full hover:bg-blue-700 transition shadow-md hover:shadow-lg transform active:scale-95 duration-200">
                                        Sign Up
                                    </Link>
                                </div>
                            )}
                        </div>

                        {/* Mobile menu button */}
                        <div className="md:hidden flex items-center">
                            <button
                                onClick={() => setIsMenuOpen(!isMenuOpen)}
                                className="text-gray-600 hover:text-blue-600 focus:outline-none"
                            >
                                {isMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
                            </button>
                        </div>
                    </div>
                </div>

                {/* Mobile Menu */}
                {isMenuOpen && (
                    <div className="md:hidden bg-white border-b border-gray-100 pb-4">
                        <div className="px-4 space-y-3 pt-2">
                            <Link to="/" className="block text-gray-600 hover:text-blue-600 py-2">Home</Link>
                            <Link to="/bikes" className="block text-gray-600 hover:text-blue-600 py-2">Browse Bikes</Link>

                            {user && user.roles?.includes('ROLE_CUSTOMER') && (
                                <>
                                    <Link to="/customer/rides" className="block text-gray-600 hover:text-blue-600 py-2">My Rides</Link>
                                    <Link to="/transactions" className="block text-gray-600 hover:text-blue-600 py-2">Transactions</Link>
                                </>
                            )}

                            {user ? (
                                <button onClick={handleLogout} className="block w-full text-left text-red-600 py-2">Logout</button>
                            ) : (
                                <>
                                    <Link to="/login" className="block text-gray-600 hover:text-blue-600 py-2">Login</Link>
                                    <Link to="/register" className="block text-blue-600 font-semibold py-2">Sign Up</Link>
                                </>
                            )}
                        </div>
                    </div>
                )}
            </nav>

            {/* Main Content */}
            <main className="flex-grow">
                <Outlet />
            </main>

            {/* Footer */}
            <footer className="bg-white border-t border-gray-200 mt-auto">
                <div className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
                    <p className="text-center text-gray-400 text-sm">
                        © 2026 Rydlo Inc. All rights reserved.
                    </p>
                </div>
            </footer>
        </div>
    );
};

export default MainLayout;
