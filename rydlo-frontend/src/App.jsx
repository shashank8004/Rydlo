import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import MainLayout from './layouts/MainLayout';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import OwnerDashboard from './pages/OwnerDashboard';
import AdminDashboard from './pages/AdminDashboard';
import BrowseBikesPage from './pages/BrowseBikesPage';
import MyRidesPage from './pages/MyRidesPage';
import BikeDetailsPage from './pages/BikeDetailsPage';
import TransactionsPage from './pages/TransactionsPage';

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<MainLayout />}>
                        <Route index element={<LandingPage />} />
                        <Route path="bikes" element={<BrowseBikesPage />} />
                        <Route path="bikes/:id" element={<BikeDetailsPage />} />
                    </Route>

                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/forgot-password" element={<ForgotPasswordPage />} />

                    {/* Protected Customer Routes - We can add AuthGuard later */}
                    <Route path="/customer/dashboard" element={<BrowseBikesPage />} /> {/* Default to Browse */}
                    <Route path="/customer/browse" element={<BrowseBikesPage />} />
                    <Route path="/customer/rides" element={<MyRidesPage />} />
                    <Route path="/transactions" element={<TransactionsPage />} />

                    {/* Protected Owner Routes */}
                    <Route path="/owner/dashboard" element={<OwnerDashboard />} />

                    {/* Protected Admin Routes */}
                    <Route path="/admin/dashboard" element={<AdminDashboard />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App
