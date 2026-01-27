import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, Key, Loader2, CheckCircle, ArrowRight } from 'lucide-react';
import api from '../api/axios';

const ForgotPasswordPage = () => {
    const [step, setStep] = useState(1); // 1: Email, 2: OTP, 3: New Password
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [password, setPassword] = useState('');

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const navigate = useNavigate();

    // Helper to extract error message
    const getErrorMessage = (err, defaultMsg) => {
        const data = err.response?.data;
        if (typeof data === 'string') return data;
        if (data?.message) return data.message;
        return defaultMsg;
    };

    // Step 1: Send OTP
    const handleSendOtp = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await api.post('/auth/forgot-password', { email });
            setStep(2);
            setSuccess(`OTP sent to ${email}`);
        } catch (err) {
            setError(getErrorMessage(err, 'Failed to send OTP. User may not exist.'));
        } finally {
            setLoading(false);
        }
    };

    // Step 2: Verify OTP
    const handleVerifyOtp = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await api.post('/auth/verify-otp', { email, otp });
            setStep(3);
            setSuccess('OTP Verified. Please set a new password.');
        } catch (err) {
            setError(getErrorMessage(err, 'Invalid or Expired OTP.'));
        } finally {
            setLoading(false);
        }
    };

    // Step 3: Reset Password
    const handleResetPassword = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await api.post('/auth/reset-password', { email, otp, password });
            setSuccess('Password Reset Successfully! Redirecting to login...');
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setError(getErrorMessage(err, 'Failed to reset password.'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-xl max-w-md w-full overflow-hidden p-8">

                <h2 className="text-2xl font-bold text-center text-gray-900 mb-2">Reset Password</h2>
                <p className="text-center text-gray-500 mb-8 text-sm">
                    {step === 1 && "Enter your email to receive an OTP"}
                    {step === 2 && "Enter the OTP sent to your email"}
                    {step === 3 && "Create a new secure password"}
                </p>

                {error && (
                    <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm mb-4">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="bg-green-50 text-green-600 p-3 rounded-lg text-sm mb-4 flex items-center">
                        <CheckCircle className="w-4 h-4 mr-2" /> {success}
                    </div>
                )}

                {/* Step 1: Email Form */}
                {step === 1 && (
                    <form onSubmit={handleSendOtp} className="space-y-4">
                        <div className="relative">
                            <Mail className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                            <input
                                type="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                placeholder="Enter your email"
                            />
                        </div>
                        <button disabled={loading} className="w-full py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold shadow-lg transition flex justify-center items-center gap-2">
                            {loading ? <Loader2 className="animate-spin w-5 h-5" /> : <>Send OTP <ArrowRight className="w-4 h-4" /></>}
                        </button>
                    </form>
                )}

                {/* Step 2: OTP Form */}
                {step === 2 && (
                    <form onSubmit={handleVerifyOtp} className="space-y-4">
                        <div className="relative">
                            <Key className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                            <input
                                type="text"
                                required
                                value={otp}
                                onChange={(e) => setOtp(e.target.value)}
                                className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none tracking-widest"
                                placeholder="Enter 6-digit OTP"
                            />
                        </div>
                        <button disabled={loading} className="w-full py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold shadow-lg transition flex justify-center items-center gap-2">
                            {loading ? <Loader2 className="animate-spin w-5 h-5" /> : "Verify OTP"}
                        </button>
                        <button type="button" onClick={() => setStep(1)} className="w-full text-sm text-gray-500 hover:text-gray-700">
                            Change Email
                        </button>
                    </form>
                )}

                {/* Step 3: New Password Form */}
                {step === 3 && (
                    <form onSubmit={handleResetPassword} className="space-y-4">
                        <div className="relative">
                            <Lock className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                            <input
                                type="password"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                placeholder="New Password"
                            />
                        </div>
                        <button disabled={loading} className="w-full py-2.5 bg-green-600 text-white rounded-lg hover:bg-green-700 font-semibold shadow-lg transition flex justify-center items-center gap-2">
                            {loading ? <Loader2 className="animate-spin w-5 h-5" /> : "Reset Password"}
                        </button>
                    </form>
                )}

                <div className="mt-6 text-center">
                    <Link to="/login" className="text-sm text-gray-600 hover:text-blue-600">
                        Back to Login
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;
