import { useState } from 'react';
import { Search, MapPin, Bike, Loader2, IndianRupee, Home } from 'lucide-react';
import api from '../api/axios';
import { Link } from 'react-router-dom';

const CustomerDashboard = () => {
    // Migrated state declarations to the top
    const [query, setQuery] = useState('');
    const [bikes, setBikes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [searched, setSearched] = useState(false);

    // New state for My Rides
    const [activeTab, setActiveTab] = useState('search'); // 'search', 'rides'
    const [myBookings, setMyBookings] = useState([]);

    const fetchMyBookings = async () => {
        try {
            const response = await api.get('/bookings/my');
            setMyBookings(response.data);
        } catch (error) {
            console.error("Error fetching my bookings", error);
        }
    };

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!query.trim()) return;

        setLoading(true);
        setError('');
        setSearched(true);
        try {
            const response = await api.get(`/bikes/search?query=${query}`);
            setBikes(response.data);
        } catch (err) {
            console.error("Search error:", err);
            setError('Failed to fetch bikes. Please try again.');
            setBikes([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-6xl mx-auto space-y-8 relative">

                {/* Home Button */}
                <div className="absolute top-0 left-0">
                    <Link to="/" className="flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold bg-white px-4 py-2 rounded-lg shadow-sm">
                        <Home className="w-5 h-5" /> Home
                    </Link>
                </div>

                {/* Tab Navigation */}
                <div className="flex justify-center gap-4 pt-4">
                    <button
                        onClick={() => setActiveTab('search')}
                        className={`px-6 py-2 rounded-full font-bold transition ${activeTab === 'search' ? 'bg-blue-600 text-white shadow-lg' : 'bg-white text-gray-600 hover:bg-gray-100'}`}
                    >
                        Search Bikes
                    </button>
                    <button
                        onClick={() => { setActiveTab('rides'); fetchMyBookings(); }}
                        className={`px-6 py-2 rounded-full font-bold transition ${activeTab === 'rides' ? 'bg-blue-600 text-white shadow-lg' : 'bg-white text-gray-600 hover:bg-gray-100'}`}
                    >
                        My Rides
                    </button>
                </div>

                {/* Search Tab */}
                {activeTab === 'search' && (
                    <>
                        <div className="text-center space-y-4">
                            <h1 className="text-4xl font-extrabold text-gray-900">Find Your Ride</h1>
                            <p className="text-lg text-gray-600">Search by City, Locality, or Pincode</p>
                        </div>

                        {/* Search Bar */}
                        <div className="bg-white p-4 rounded-2xl shadow-lg max-w-2xl mx-auto">
                            <form onSubmit={handleSearch} className="flex gap-2">
                                <div className="relative flex-1">
                                    <MapPin className="absolute left-4 top-3.5 h-5 w-5 text-gray-400" />
                                    <input
                                        type="text"
                                        placeholder="Enter location (e.g., Pune, 411038)"
                                        className="w-full pl-12 pr-4 py-3 bg-gray-50 border-transparent focus:bg-white focus:ring-2 focus:ring-blue-500 rounded-xl transition outline-none"
                                        value={query}
                                        onChange={(e) => setQuery(e.target.value)}
                                    />
                                </div>
                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="bg-blue-600 text-white px-8 py-3 rounded-xl font-semibold hover:bg-blue-700 transition flex items-center gap-2"
                                >
                                    {loading ? <Loader2 className="animate-spin h-5 w-5" /> : <><Search className="h-5 w-5" /> Search</>}
                                </button>
                            </form>
                        </div>

                        {/* Results Grid */}
                        {searched && (
                            <div className="space-y-4">
                                <h2 className="text-xl font-bold text-gray-800">
                                    {bikes.length > 0 ? `Found ${bikes.length} bikes` : 'No bikes found'}
                                </h2>

                                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                                    {bikes.map((bike) => (
                                        <div key={bike.id} className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition group">
                                            <div className="h-48 bg-gray-200 relative overflow-hidden">
                                                <div className="absolute inset-0 flex items-center justify-center text-gray-400 bg-gray-100">
                                                    <Bike className="h-16 w-16 opacity-50" />
                                                </div>
                                                <div className="absolute top-3 right-3 bg-white/90 backdrop-blur px-2 py-1 rounded text-xs font-bold text-blue-800 uppercase">
                                                    {bike.bikeType}
                                                </div>
                                            </div>

                                            <div className="p-5 space-y-4">
                                                <div>
                                                    <h3 className="text-xl font-bold text-gray-900 line-clamp-1">{bike.model}</h3>
                                                    <p className="text-sm text-gray-500">Available Now</p>
                                                </div>

                                                <div className="flex justify-between items-center text-sm font-medium">
                                                    <div className="flex items-center text-gray-700 bg-gray-50 px-3 py-1.5 rounded-lg">
                                                        <IndianRupee className="w-3 h-3 mr-1" />
                                                        {bike.rentPerDay}<span className="text-gray-400 font-normal ml-1">/ day</span>
                                                    </div>
                                                    <div className="flex items-center text-gray-700 bg-gray-50 px-3 py-1.5 rounded-lg">
                                                        <IndianRupee className="w-3 h-3 mr-1" />
                                                        {bike.rentPerKm}<span className="text-gray-400 font-normal ml-1">/ km</span>
                                                    </div>
                                                </div>

                                                <button className="w-full py-2.5 bg-gray-900 text-white rounded-lg font-medium hover:bg-blue-600 transition">
                                                    View Details
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}
                    </>
                )}

                {/* My Rides Tab */}
                {activeTab === 'rides' && (
                    <div className="space-y-6">
                        <h2 className="text-2xl font-bold text-gray-900 text-center">My Booking History</h2>
                        {myBookings.length === 0 ? (
                            <div className="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
                                <Bike className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                                <h3 className="text-xl font-medium text-gray-900">No rides yet</h3>
                                <p className="text-gray-500 mt-2">Book your first ride today!</p>
                            </div>
                        ) : (
                            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left">
                                        <thead className="bg-gray-50 text-gray-600 text-sm uppercase">
                                            <tr>
                                                <th className="p-4">Booking ID</th>
                                                <th className="p-4">Bike</th>
                                                <th className="p-4">Dates</th>
                                                <th className="p-4">Amount</th>
                                                <th className="p-4">Status</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-gray-100">
                                            {myBookings.map(b => (
                                                <tr key={b.id} className="hover:bg-gray-50 transition">
                                                    <td className="p-4 text-gray-500">#{b.id}</td>
                                                    <td className="p-4">
                                                        <div className="font-bold text-gray-900">{b.bikeModel}</div>
                                                        <div className="text-xs text-gray-500">{b.bikeNumber}</div>
                                                    </td>
                                                    <td className="p-4 text-sm">
                                                        <div className="text-gray-900">From: {new Date(b.pickupDateTime).toLocaleDateString()}</div>
                                                        <div className="text-gray-500">To: {new Date(b.dropOffDateTime).toLocaleDateString()}</div>
                                                    </td>
                                                    <td className="p-4 font-bold text-gray-900">₹{b.totalAmount}</td>
                                                    <td className="p-4">
                                                        <span className={`px-2 py-1 rounded text-xs font-bold ${b.bookingStatus === 'COMPLETED' ? 'bg-green-100 text-green-700' : b.bookingStatus === 'BOOKED' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-700'}`}>
                                                            {b.bookingStatus}
                                                        </span>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default CustomerDashboard;
