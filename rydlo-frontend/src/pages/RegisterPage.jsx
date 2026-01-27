import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Mail, Lock, Loader2, FileText, CheckCircle, ArrowRight } from 'lucide-react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const RegisterPage = () => {
    const [role, setRole] = useState('customer');
    const [step, setStep] = useState(1); // 1: User Reg, 2: Profile Completion

    // Step 1 Data
    const [userData, setUserData] = useState({ firstName: '', lastName: '', email: '', password: '', phone: '' });

    // Step 2 Data
    const [profileData, setProfileData] = useState({
        drivingLicence: '',
        companyName: '',
        gstNumber: '',
        houseNo: '',
        locality: '',
        city: '',
        state: '',
        pincode: ''
    });

    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleUserChange = (e) => setUserData({ ...userData, [e.target.name]: e.target.value });
    const handleProfileChange = (e) => setProfileData({ ...profileData, [e.target.name]: e.target.value });

    // Step 1: Register User & Login
    const handleStep1 = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            // Map friendly role to Backend Enum
            let roleEnum = 'ROLE_CUSTOMER';
            if (role === 'owner') roleEnum = 'ROLE_OWNER';
            // Admin not allowed for registration

            // 1. Create User
            await api.post('/users/register', {
                ...userData,
                role: roleEnum
            });

            // 2. Login to get Token
            await login(userData.email, userData.password);

            // 3. Move to Step 2
            setStep(2);
        } catch (err) {
            console.error("Registration Error:", err);
            const data = err.response?.data;
            let message = 'Registration failed.';

            if (typeof data === 'string') {
                message = data;
            } else if (data && typeof data === 'object') {
                if (data.message) {
                    message = data.message;
                } else {
                    // Validation map handling
                    const messages = Object.values(data).join(', ');
                    if (messages) message = messages;
                }
            }
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    // Step 2: Complete Profile
    const handleStep2 = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            // Construct address object
            const address = {
                houseNo: profileData.houseNo,
                locality: profileData.locality,
                city: profileData.city,
                state: profileData.state,
                pincode: profileData.pincode
            };

            // Token is already attached by axios interceptor from Step 1 login
            if (role === 'customer') {
                await api.post('/customers/register', {
                    drivingLicence: profileData.drivingLicence,
                    address: address
                });
            } else {
                await api.post('/owners/register', {
                    gstNumber: profileData.gstNumber,
                    companyName: profileData.companyName,
                    address: address
                });
            }
            navigate('/'); // Success!
        } catch (err) {
            console.error("Profile Error:", err);
            const data = err.response?.data;
            let message = 'Profile completion failed.';

            if (typeof data === 'string') {
                message = data;
            } else if (data && typeof data === 'object') {
                if (data.message) {
                    message = data.message;
                } else {
                    // Validation map handling
                    const messages = Object.values(data).join(', ');
                    if (messages) message = messages;
                }
            }
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-xl max-w-lg w-full overflow-hidden">
                <div className="p-8">
                    {/* Header */}
                    <div className="text-center mb-8">
                        <h2 className="text-3xl font-bold text-gray-900">
                            {step === 1 ? 'Create Account' : 'Complete Profile'}
                        </h2>
                        <p className="text-gray-500 mt-2">
                            {step === 1 ? `Join Rydlo as a ${role}` : `One last step to become a ${role}`}
                        </p>
                    </div>

                    {/* Progress Steps */}
                    <div className="flex items-center justify-center space-x-4 mb-8">
                        <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm ${step >= 1 ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-500'}`}>1</div>
                        <div className={`w-12 h-1 bg-gray-200 ${step >= 2 ? 'bg-blue-600' : ''}`}></div>
                        <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm ${step >= 2 ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-500'}`}>2</div>
                    </div>

                    {/* Step 1 Form */}
                    {step === 1 && (
                        <form onSubmit={handleStep1} className="space-y-4">

                            {/* Role Dropdown */}
                            <div>
                                <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Select Role</label>
                                <div className="relative">
                                    <select
                                        value={role}
                                        onChange={(e) => setRole(e.target.value)}
                                        className="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md border"
                                    >
                                        <option value="customer">Customer</option>
                                        <option value="owner">Bike Owner</option>
                                    </select>
                                </div>
                            </div>

                            <div className="flex gap-4">
                                <div className="w-1/2">
                                    <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">First Name</label>
                                    <div className="relative">
                                        <User className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                                        <input name="firstName" type="text" required onChange={handleUserChange} className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" placeholder="John" />
                                    </div>
                                </div>
                                <div className="w-1/2">
                                    <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Last Name</label>
                                    <div className="relative">
                                        <User className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                                        <input name="lastName" type="text" required onChange={handleUserChange} className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" placeholder="Doe" />
                                    </div>
                                </div>
                            </div>

                            <div>
                                <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Phone Number</label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                        <span className="text-gray-400 font-bold">📞</span>
                                    </div>
                                    <input name="phone" type="tel" required onChange={handleUserChange} className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" placeholder="+1 234 567 890" />
                                </div>
                            </div>

                            <div>
                                <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Email</label>
                                <div className="relative">
                                    <Mail className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                                    <input name="email" type="email" required onChange={handleUserChange} className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" placeholder="john@example.com" />
                                </div>
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Password</label>
                                <div className="relative">
                                    <Lock className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                                    <input name="password" type="password" required onChange={handleUserChange} className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" placeholder="••••••••" />
                                </div>
                            </div>

                            {error && <div className="bg-red-50 text-red-600 text-sm p-3 rounded-lg">{error}</div>}

                            <button disabled={loading} className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold shadow-lg transition flex items-center justify-center gap-2">
                                {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : <>Next Step <ArrowRight className="w-4 h-4" /></>}
                            </button>
                        </form>
                    )}

                    {/* Step 2 Form */}
                    {step === 2 && (
                        <form onSubmit={handleStep2} className="space-y-4">
                            <div className="bg-green-50 text-green-700 p-3 rounded-lg text-sm mb-4 flex items-center">
                                <CheckCircle className="w-5 h-5 mr-2" /> Account created successfully!
                            </div>

                            {/* Address Section */}
                            <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
                                <h4 className="text-sm font-bold text-gray-700 uppercase mb-3 text-center">Address Details</h4>
                                <div className="grid grid-cols-2 gap-3">
                                    <div className="col-span-2">
                                        <input name="houseNo" type="text" required onChange={handleProfileChange} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 text-sm outline-none" placeholder="House No / Flat" />
                                    </div>
                                    <div className="col-span-2">
                                        <input name="locality" type="text" required onChange={handleProfileChange} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 text-sm outline-none" placeholder="Locality / Area" />
                                    </div>
                                    <div>
                                        <input name="city" type="text" required onChange={handleProfileChange} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 text-sm outline-none" placeholder="City" />
                                    </div>
                                    <div>
                                        <input name="state" type="text" required onChange={handleProfileChange} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 text-sm outline-none" placeholder="State" />
                                    </div>
                                    <div className="col-span-2">
                                        <input name="pincode" type="text" required onChange={handleProfileChange} className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 text-sm outline-none" placeholder="Pincode" />
                                    </div>
                                </div>
                            </div>

                            {role === 'customer' && (
                                <div className="bg-blue-50 p-4 rounded-lg border border-blue-100">
                                    <label className="block text-xs font-semibold text-blue-800 uppercase mb-1">Driving License No.</label>
                                    <div className="relative">
                                        <FileText className="absolute left-3 top-3 h-5 w-5 text-blue-400" />
                                        <input name="drivingLicence" type="text" required onChange={handleProfileChange} className="w-full pl-10 pr-4 py-2 border border-blue-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" placeholder="DL-12345678" />
                                    </div>
                                </div>
                            )}

                            {role === 'owner' && (
                                <div className="bg-purple-50 p-4 rounded-lg border border-purple-100 space-y-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-purple-800 uppercase mb-1">Company Name</label>
                                        <div className="relative">
                                            <FileText className="absolute left-3 top-3 h-5 w-5 text-purple-400" />
                                            <input name="companyName" type="text" required onChange={handleProfileChange} className="w-full pl-10 pr-4 py-2 border border-purple-200 rounded-lg focus:ring-2 focus:ring-purple-500 outline-none" placeholder="Rydlo Motors" />
                                        </div>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-purple-800 uppercase mb-1">GST Number</label>
                                        <div className="relative">
                                            <FileText className="absolute left-3 top-3 h-5 w-5 text-purple-400" />
                                            <input name="gstNumber" type="text" required onChange={handleProfileChange} className="w-full pl-10 pr-4 py-2 border border-purple-200 rounded-lg focus:ring-2 focus:ring-purple-500 outline-none" placeholder="GSTIN123" />
                                        </div>
                                    </div>
                                </div>
                            )}

                            {error && <div className="text-red-500 text-sm">{error}</div>}

                            <button disabled={loading} className="w-full py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-semibold shadow-lg transition flex items-center justify-center gap-2">
                                {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Complete Registration'}
                            </button>
                        </form>
                    )}

                    {step === 1 && (
                        <div className="mt-6 text-center text-sm text-gray-600">
                            Already have an account? <Link to="/login" className="text-blue-600 hover:underline">Log in</Link>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default RegisterPage;
