import { useState, useEffect } from 'react';
import { Users, MapPin, Bike, Trash2, Plus, Loader2, Home } from 'lucide-react';
import api from '../api/axios';
import { Link } from 'react-router-dom';

const AdminDashboard = () => {
    const [stats, setStats] = useState({ users: 0, locations: 0, bikes: 0 });
    const [locations, setLocations] = useState([]);
    const [users, setUsers] = useState([]);
    const [bikes, setBikes] = useState([]); // New state for bikes
    const [bookings, setBookings] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);

    const [activeTab, setActiveTab] = useState('overview'); // 'overview', 'locations', 'users', 'bikes', 'bookings', 'transactions'

    // Form State
    const [newLocation, setNewLocation] = useState({
        street: '',
        locality: '',
        city: '',
        state: '',
        pincode: ''
    });

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        try {
            const [statsRes, locRes, usersRes, bikesRes, bookingsRes, txnRes] = await Promise.all([
                api.get('/admin/stats'),
                api.get('/admin/pickup-locations'),
                api.get('/admin/all'),
                api.get('/admin/bikes'), // Fetch all bikes
                api.get('/admin/bookings').catch(() => ({ data: [] })),
                api.get('/admin/transactions').catch(() => ({ data: [] }))
            ]);
            setStats(statsRes.data);
            setLocations(locRes.data);
            setUsers(usersRes.data);
            setBikes(bikesRes.data);
            setBookings(bookingsRes.data || []);
            setTransactions(txnRes.data || []);
        } catch (error) {
            console.error("Error fetching admin data", error);
        } finally {
            setLoading(false);
        }
    };

    const [editingLocation, setEditingLocation] = useState(null);

    const handleAddLocation = async (e) => {
        e.preventDefault();
        try {
            if (editingLocation) {
                await api.put(`/admin/pickup-locations/${editingLocation.id}`, newLocation);
                alert("Location Updated!");
                setEditingLocation(null);
            } else {
                await api.post('/admin/add-pickup-location', newLocation);
                alert("Location Added!");
            }
            setNewLocation({ street: '', locality: '', city: '', state: '', pincode: '' });
            fetchDashboardData();
        } catch (error) {
            console.error("Location operation failed:", error);
            if (error.response?.data && typeof error.response.data === 'object' && !error.response.data.message) {
                const errors = Object.values(error.response.data).join('\n');
                alert("Validation Failed:\n" + errors);
            } else {
                alert("Failed to save location");
            }
        }
    };

    const handleEditLocation = (loc) => {
        setEditingLocation(loc);
        setNewLocation({
            street: loc.street,
            locality: loc.locality,
            city: loc.city,
            state: loc.state,
            pincode: loc.pincode
        });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const cancelEdit = () => {
        setEditingLocation(null);
        setNewLocation({ street: '', locality: '', city: '', state: '', pincode: '' });
    };

    const handleDeleteLocation = async (id) => {
        if (!window.confirm("Delete this location?")) return;
        try {
            await api.delete(`/admin/pickup-locations/${id}`);
            fetchDashboardData();
        } catch (error) {
            alert("Failed to delete location. It might be in use.");
        }
    };

    if (loading) return <div className="min-h-screen flex items-center justify-center"><Loader2 className="animate-spin h-8 w-8 text-blue-600" /></div>;

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-7xl mx-auto space-y-8">
                <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                    <div className="flex items-center gap-4">
                        <Link to="/" className="p-2 bg-white rounded-lg shadow-sm text-gray-600 hover:text-blue-600 transition">
                            <Home className="w-6 h-6" />
                        </Link>
                        <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
                    </div>
                    <div className="flex flex-wrap gap-2">
                        <button onClick={() => setActiveTab('overview')} className={`px-4 py-2 rounded-lg ${activeTab === 'overview' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600'}`}>Overview</button>
                        <button onClick={() => setActiveTab('locations')} className={`px-4 py-2 rounded-lg ${activeTab === 'locations' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600'}`}>Locations</button>
                        <button onClick={() => setActiveTab('users')} className={`px-4 py-2 rounded-lg ${activeTab === 'users' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600'}`}>Users</button>
                        <button onClick={() => setActiveTab('bikes')} className={`px-4 py-2 rounded-lg ${activeTab === 'bikes' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600'}`}>Bikes</button>
                        <button onClick={() => setActiveTab('bookings')} className={`px-4 py-2 rounded-lg ${activeTab === 'bookings' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600'}`}>Bookings</button>
                        <button onClick={() => setActiveTab('transactions')} className={`px-4 py-2 rounded-lg ${activeTab === 'transactions' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600'}`}>Transactions</button>
                    </div>
                </div>

                {/* Overview Stats */}
                {activeTab === 'overview' && (
                    <div className="grid md:grid-cols-3 gap-6">
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center">
                            <div className="p-4 bg-blue-50 rounded-lg text-blue-600 mr-4"><Users className="h-8 w-8" /></div>
                            <div><p className="text-gray-500 text-sm">Total Users</p><p className="text-2xl font-bold text-gray-900">{stats.users}</p></div>
                        </div>
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center">
                            <div className="p-4 bg-green-50 rounded-lg text-green-600 mr-4"><MapPin className="h-8 w-8" /></div>
                            <div><p className="text-gray-500 text-sm">Pickup Locations</p><p className="text-2xl font-bold text-gray-900">{stats.locations}</p></div>
                        </div>
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center">
                            <div className="p-4 bg-purple-50 rounded-lg text-purple-600 mr-4"><Bike className="h-8 w-8" /></div>
                            <div><p className="text-gray-500 text-sm">Active Bikes</p><p className="text-2xl font-bold text-gray-900">{stats.bikes}</p></div>
                        </div>
                    </div>
                )}

                {/* Locations Tab */}
                {(activeTab === 'locations') && (
                    <div className="grid lg:grid-cols-2 gap-8">
                        {/* Add/Edit Location Form */}
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-fit">
                            <h2 className="text-xl font-bold mb-4 flex items-center">
                                <Plus className="w-5 h-5 mr-2" /> {editingLocation ? 'Edit Pickup Location' : 'Add Pickup Location'}
                            </h2>
                            <form onSubmit={handleAddLocation} className="space-y-4">
                                <input className="w-full p-2 border rounded" placeholder="Street" value={newLocation.street} onChange={e => setNewLocation({ ...newLocation, street: e.target.value })} required />
                                <div className="grid grid-cols-2 gap-4">
                                    <input className="w-full p-2 border rounded" placeholder="Locality" value={newLocation.locality} onChange={e => setNewLocation({ ...newLocation, locality: e.target.value })} required />
                                    <input className="w-full p-2 border rounded" placeholder="City" value={newLocation.city} onChange={e => setNewLocation({ ...newLocation, city: e.target.value })} required />
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <input className="w-full p-2 border rounded" placeholder="State" value={newLocation.state} onChange={e => setNewLocation({ ...newLocation, state: e.target.value })} required />
                                    <input className="w-full p-2 border rounded" placeholder="Pincode" value={newLocation.pincode} onChange={e => setNewLocation({ ...newLocation, pincode: e.target.value })} required />
                                </div>
                                <div className="flex gap-2">
                                    <button className="flex-1 bg-black text-white py-2 rounded hover:bg-gray-800 transition">
                                        {editingLocation ? 'Update Location' : 'Add Location'}
                                    </button>
                                    {editingLocation && (
                                        <button type="button" onClick={cancelEdit} className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition">
                                            Cancel
                                        </button>
                                    )}
                                </div>
                            </form>
                        </div>

                        {/* Locations List */}
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-fit">
                            <h2 className="text-xl font-bold mb-4">Existing Locations</h2>
                            <div className="space-y-3 max-h-80 overflow-y-auto pr-2">
                                {locations.map(loc => (
                                    <div key={loc.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg group">
                                        <div><p className="font-semibold">{loc.locality}, {loc.city}</p><p className="text-xs text-gray-500">{loc.pincode}</p></div>
                                        <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition">
                                            <button onClick={() => handleEditLocation(loc)} className="text-blue-500 hover:bg-blue-50 p-2 rounded">Edit</button>
                                            <button onClick={() => handleDeleteLocation(loc.id)} className="text-red-500 hover:bg-red-50 p-2 rounded"><Trash2 className="w-4 h-4" /></button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                )}

                {/* Users List */}
                {activeTab === 'users' && (
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                        <h2 className="text-xl font-bold mb-4">All Users</h2>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 text-gray-600 text-sm uppercase">
                                    <tr>
                                        <th className="p-3">ID</th>
                                        <th className="p-3">Name</th>
                                        <th className="p-3">Email</th>
                                        <th className="p-3">Role</th>
                                        <th className="p-3">Phone</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {users.map(u => (
                                        <tr key={u.id}>
                                            <td className="p-3 text-gray-500">#{u.id}</td>
                                            <td className="p-3 font-medium">{u.firstName} {u.lastName}</td>
                                            <td className="p-3 text-gray-500">{u.email}</td>
                                            <td className="p-3"><span className={`px-2 py-1 rounded text-xs font-bold ${u.role === 'ROLE_ADMIN' ? 'bg-purple-100 text-purple-700' : u.role === 'ROLE_OWNER' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-700'}`}>{u.role}</span></td>
                                            <td className="p-3 text-gray-500">{u.phone}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* Bikes List */}
                {activeTab === 'bikes' && (
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                        <h2 className="text-xl font-bold mb-4">All Bikes</h2>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 text-gray-600 text-sm uppercase">
                                    <tr>
                                        <th className="p-3">ID</th>
                                        <th className="p-3">Model</th>
                                        <th className="p-3">Type</th>
                                        <th className="p-3">Rent/Day</th>
                                        <th className="p-3">Location</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {bikes.map(b => (
                                        <tr key={b.id}>
                                            <td className="p-3 text-gray-500">#{b.id}</td>
                                            <td className="p-3 font-medium">{b.model}</td>
                                            <td className="p-3">{b.bikeType}</td>
                                            <td className="p-3">₹{b.rentPerDay}</td>
                                            <td className="p-3 text-gray-500">{b.city}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* Bookings List */}
                {activeTab === 'bookings' && (
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                        <h2 className="text-xl font-bold mb-4">All Bookings</h2>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 text-gray-600 text-sm uppercase">
                                    <tr>
                                        <th className="p-3">ID</th>
                                        <th className="p-3">Customer</th>
                                        <th className="p-3">Bike</th>
                                        <th className="p-3">Dates</th>
                                        <th className="p-3">Amount</th>
                                        <th className="p-3">Status</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {bookings.map(b => (
                                        <tr key={b.id}>
                                            <td className="p-3 text-gray-500">#{b.id}</td>
                                            <td className="p-3 font-medium">{b.customerName}</td>
                                            <td className="p-3">{b.bikeModel} <span className="text-xs text-gray-500">({b.bikeNumber})</span></td>
                                            <td className="p-3 text-xs">
                                                From: {new Date(b.pickupDateTime).toLocaleDateString()}<br />
                                                To: {new Date(b.dropOffDateTime).toLocaleDateString()}
                                            </td>
                                            <td className="p-3 font-bold">₹{b.totalAmount}</td>
                                            <td className="p-3"><span className="px-2 py-1 bg-blue-50 text-blue-700 rounded text-xs">{b.bookingStatus}</span></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* Transactions List */}
                {activeTab === 'transactions' && (
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                        <h2 className="text-xl font-bold mb-4">All Transactions</h2>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 text-gray-600 text-sm uppercase">
                                    <tr>
                                        <th className="p-3">ID</th>
                                        <th className="p-3">Booking ID</th>
                                        <th className="p-3">Amount</th>
                                        <th className="p-3">Type</th>
                                        <th className="p-3">Status</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {transactions.map(t => (
                                        <tr key={t.id}>
                                            <td className="p-3 text-gray-500">#{t.id}</td>
                                            <td className="p-3">#{t.bookingId}</td>
                                            <td className="p-3 font-bold">₹{t.amount}</td>
                                            <td className="p-3">{t.transactionType}</td>
                                            <td className="p-3"><span className={`px-2 py-1 rounded text-xs ${t.transactionStatus === 'SUCCESS' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>{t.transactionStatus}</span></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

            </div>
        </div>
    );
};

export default AdminDashboard;
