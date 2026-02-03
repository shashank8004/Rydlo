import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { User, Phone, Mail, BadgeCheck, Save, AlertCircle, Loader2 } from 'lucide-react';

const ProfilePage = () => {
    const { user } = useAuth();
    const [profile, setProfile] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        role: ''
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            setLoading(true);
            const response = await api.get('/users/profile');
            setProfile(response.data);
        } catch (error) {
            console.error("Failed to fetch profile", error);
            setMessage({ type: 'error', text: 'Failed to load profile details.' });
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfile(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Basic frontend validation
        if (!profile.firstName.trim() || !profile.lastName.trim()) {
            setMessage({ type: 'error', text: 'First name and Last name cannot be empty or blank.' });
            return;
        }

        const phoneRegex = /^(\+91)?[6-9][0-9]{9}$/;
        if (!phoneRegex.test(profile.phone)) {
            setMessage({ type: 'error', text: 'Please enter a valid Indian phone number.' });
            return;
        }

        try {
            setSaving(true);
            setMessage({ type: '', text: '' });
            await api.put('/users/profile', {
                ...profile,
                firstName: profile.firstName.trim(),
                lastName: profile.lastName.trim(),
                phone: profile.phone.trim()
            });
            setMessage({ type: 'success', text: 'Profile updated successfully!' });
        } catch (error) {
            console.error("Failed to update profile", error);
            setMessage({
                type: 'error',
                text: error.response?.data?.message || 'Failed to update profile.'
            });
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[60vh]">
                <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto px-4 py-12">
            <div className="bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
                {/* Header Decor */}
                <div className="h-32 bg-gradient-to-r from-blue-600 to-indigo-600"></div>

                <div className="px-8 pb-8">
                    {/* User Avatar & Title */}
                    <div className="relative -mt-16 flex items-end space-x-6 mb-8">
                        <div className="bg-white p-2 rounded-2xl shadow-lg border border-gray-100">
                            <div className="w-32 h-32 bg-gray-100 rounded-xl flex items-center justify-center text-blue-600">
                                <User className="w-16 h-16" />
                            </div>
                        </div>
                        <div className="pb-2">
                            <h1 className="text-3xl font-bold text-gray-900">{profile.firstName} {profile.lastName}</h1>
                            <div className="flex items-center text-gray-500 mt-1">
                                <BadgeCheck className="w-4 h-4 mr-1 text-blue-500" />
                                <span className="text-sm font-medium uppercase tracking-wider">{profile.role?.replace('ROLE_', '')}</span>
                            </div>
                        </div>
                    </div>

                    {message.text && (
                        <div className={`p-4 rounded-xl mb-6 flex items-center gap-3 ${message.type === 'success' ? 'bg-green-50 text-green-700 border border-green-100' : 'bg-red-50 text-red-700 border border-red-100'
                            }`}>
                            {message.type === 'error' && <AlertCircle className="w-5 h-5 flex-shrink-0" />}
                            <p className="font-medium">{message.text}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* Personal Info Section */}
                        <div className="space-y-6">
                            <h2 className="text-lg font-semibold text-gray-800 border-b border-gray-100 pb-2">Personal Information</h2>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">First Name</label>
                                <div className="relative">
                                    <input
                                        type="text"
                                        name="firstName"
                                        value={profile.firstName}
                                        onChange={handleChange}
                                        className="w-full pl-4 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
                                        required
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
                                <input
                                    type="text"
                                    name="lastName"
                                    value={profile.lastName}
                                    onChange={handleChange}
                                    className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
                                    required
                                />
                            </div>
                        </div>

                        {/* Contact & Security Info */}
                        <div className="space-y-6">
                            <h2 className="text-lg font-semibold text-gray-800 border-b border-gray-100 pb-2">Contact Details</h2>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Email Address (Read-only)</label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <Mail className="w-5 h-5 text-gray-400" />
                                    </div>
                                    <input
                                        type="email"
                                        value={profile.email}
                                        className="w-full pl-11 pr-4 py-3 bg-gray-100 border border-gray-200 rounded-xl text-gray-500 cursor-not-allowed outline-none"
                                        readOnly
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <Phone className="w-5 h-5 text-gray-400" />
                                    </div>
                                    <input
                                        type="tel"
                                        name="phone"
                                        value={profile.phone}
                                        onChange={handleChange}
                                        pattern="^(\+91)?[6-9][0-9]{9}$"
                                        title="Please enter a valid Indian phone number (e.g. +919876543210 or 9876543210)"
                                        className="w-full pl-11 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
                                        required
                                    />
                                </div>
                            </div>
                        </div>

                        <div className="md:col-span-2 pt-6 flex justify-end">
                            <button
                                type="submit"
                                disabled={saving}
                                className="flex items-center space-x-2 bg-blue-600 text-white px-8 py-3 rounded-xl hover:bg-blue-700 transition shadow-lg hover:shadow-xl transform active:scale-95 disabled:bg-blue-400 disabled:cursor-not-allowed"
                            >
                                {saving ? (
                                    <>
                                        <Loader2 className="w-5 h-5 animate-spin" />
                                        <span>Saving Changes...</span>
                                    </>
                                ) : (
                                    <>
                                        <Save className="w-5 h-5" />
                                        <span>Save Changes</span>
                                    </>
                                )}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
