import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Search, MapPin, Bike, Loader2, IndianRupee, Calendar } from 'lucide-react';
import api from '../api/axios';

const BrowseBikesPage = () => {
    // Search State
    const [query, setQuery] = useState('');
    const [pickupDate, setPickupDate] = useState('');
    const [pickupTime, setPickupTime] = useState('');
    const [dropOffDate, setDropOffDate] = useState('');
    const [dropOffTime, setDropOffTime] = useState('');

    const [bikes, setBikes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [searched, setSearched] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState('All');

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!query.trim() || !pickupDate || !pickupTime || !dropOffDate || !dropOffTime) {
            setError("Please fill in all search fields.");
            return;
        }

        // Validate Dates
        const start = new Date(`${pickupDate}T${pickupTime}`);
        const end = new Date(`${dropOffDate}T${dropOffTime}`);
        const now = new Date();

        if (start < now) {
            setError("Pickup time cannot be in the past.");
            return;
        }

        if (end <= start) {
            setError("Drop-off time must be strictly after pickup time.");
            return;
        }

        setLoading(true);
        setError('');
        setSearched(true);
        setSelectedCategory('All');

        const searchRequest = {
            locationQuery: query,
            pickupDate: pickupDate,
            pickupTime: pickupTime + ":00",
            dropOffDate: dropOffDate,
            dropOffTime: dropOffTime + ":00",
            bikeType: null // Optional
        };

        try {
            const response = await api.post('/bikes/available', searchRequest);
            setBikes(response.data);
        } catch (err) {
            console.error("Search error:", err);
            setError(err.response?.data?.message || 'Failed to find available bikes. Please try again.');
            setBikes([]);
        } finally {
            setLoading(false);
        }
    };

    const filteredBikes = selectedCategory === 'All'
        ? bikes
        : bikes.filter(bike => bike.bikeType === selectedCategory);

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-6xl mx-auto space-y-8">

                <div className="text-center space-y-4">
                    <h1 className="text-4xl font-extrabold text-gray-900">Find Available Rides</h1>
                    <p className="text-lg text-gray-600">Enter location and travel dates to see what's available</p>
                </div>

                {/* Search Bar */}
                <div className="bg-white p-6 rounded-3xl shadow-xl max-w-5xl mx-auto border border-gray-100">
                    <form onSubmit={handleSearch} className="flex flex-col xl:flex-row gap-4 xl:items-end">

                        {/* Location */}
                        <div className="w-full xl:flex-1">
                            <label className="block text-sm font-medium text-gray-700 mb-1 ml-1">Location / Pincode</label>
                            <div className="relative">
                                <MapPin className="absolute left-4 top-3.5 h-5 w-5 text-gray-400" />
                                <input
                                    type="text"
                                    placeholder="e.g. Pune, 411038"
                                    className="w-full pl-12 pr-4 py-3 bg-gray-50 border-transparent focus:bg-white focus:ring-2 focus:ring-blue-500 rounded-xl transition outline-none"
                                    value={query}
                                    onChange={(e) => setQuery(e.target.value)}
                                />
                            </div>
                        </div>

                        {/* Pickup */}
                        <div className="w-full xl:w-auto">
                            <label className="block text-sm font-medium text-gray-700 mb-1 ml-1">Pickup</label>
                            <div className="flex flex-col sm:flex-row gap-2">
                                <input type="date" required className="w-full sm:w-40 p-3 bg-gray-50 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-500" value={pickupDate} onChange={e => setPickupDate(e.target.value)} />
                                <input type="time" required className="w-full sm:w-32 p-3 bg-gray-50 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-500" value={pickupTime} onChange={e => setPickupTime(e.target.value)} />
                            </div>
                        </div>

                        {/* Dropoff */}
                        <div className="w-full xl:w-auto">
                            <label className="block text-sm font-medium text-gray-700 mb-1 ml-1">Drop-off</label>
                            <div className="flex flex-col sm:flex-row gap-2">
                                <input type="date" required className="w-full sm:w-40 p-3 bg-gray-50 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-500" value={dropOffDate} onChange={e => setDropOffDate(e.target.value)} />
                                <input type="time" required className="w-full sm:w-32 p-3 bg-gray-50 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-500" value={dropOffTime} onChange={e => setDropOffTime(e.target.value)} />
                            </div>
                        </div>

                        {/* Button */}
                        <div className="w-full xl:w-auto">
                            <button
                                type="submit"
                                disabled={loading}
                                className="w-full xl:w-auto px-8 h-[46px] bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition flex items-center justify-center gap-2 shadow-lg shadow-blue-200"
                            >
                                {loading ? <Loader2 className="animate-spin h-5 w-5" /> : 'Search'}
                            </button>
                        </div>
                    </form>
                </div>

                {/* Error Message */}
                {error && (
                    <div className="text-center text-red-500 font-medium bg-red-50 p-3 rounded-lg max-w-lg mx-auto border border-red-100">{error}</div>
                )}

                {/* Results Grid */}
                {searched && (
                    <div className="space-y-6">
                        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                            <h2 className="text-xl font-bold text-gray-800">
                                {filteredBikes.length > 0 ? `Found ${filteredBikes.length} available bikes` : 'No bikes found for these dates'}
                            </h2>

                            {/* Category Filters */}
                            <div className="flex flex-wrap gap-2">
                                {['All', 'SCOOTY', 'SPORTS', 'ELECTRIC'].map(cat => (
                                    <button
                                        key={cat}
                                        onClick={() => setSelectedCategory(cat)}
                                        className={`px-4 py-1.5 rounded-full text-sm font-semibold transition ${selectedCategory === cat
                                            ? 'bg-gray-900 text-white'
                                            : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
                                            }`}
                                    >
                                        {cat === 'All' ? 'All Types' : cat.charAt(0) + cat.slice(1).toLowerCase()}
                                    </button>
                                ))}
                            </div>
                        </div>

                        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {filteredBikes.map((bike) => (
                                <div key={bike.id} className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition group border border-gray-100">
                                    <div className="h-48 bg-gray-100 relative overflow-hidden flex items-center justify-center">

                                        {/* Placeholder Image Logic */}
                                        <Bike className="h-20 w-20 text-gray-300" />

                                        <div className="absolute top-3 right-3 bg-white/90 backdrop-blur px-2 py-1 rounded text-xs font-bold text-blue-800 uppercase shadow-sm">
                                            {bike.bikeType}
                                        </div>
                                    </div>

                                    <div className="p-5 space-y-4">
                                        <div>
                                            <h3 className="text-xl font-bold text-gray-900 line-clamp-1">{bike.model}</h3>
                                            <div className="flex items-center text-gray-500 text-xs mt-1">
                                                <MapPin className="w-3 h-3 mr-1" /> {bike.locality || 'Pune'}, {bike.city || 'MH'}
                                            </div>
                                        </div>

                                        <div className="flex justify-between items-center text-sm font-medium">
                                            <div className="flex items-center text-gray-700 bg-gray-50 px-3 py-1.5 rounded-lg border border-gray-100">
                                                <IndianRupee className="w-3 h-3 mr-1" />
                                                {bike.rentPerDay}<span className="text-gray-400 font-normal ml-1">/ day</span>
                                            </div>
                                            <div className="flex items-center text-gray-700 bg-gray-50 px-3 py-1.5 rounded-lg border border-gray-100">
                                                <IndianRupee className="w-3 h-3 mr-1" />
                                                {bike.rentPerKm}<span className="text-gray-400 font-normal ml-1">/ km</span>
                                            </div>
                                        </div>

                                        <Link
                                            to={`/bikes/${bike.id}`}
                                            state={{ pickupDate, pickupTime, dropOffDate, dropOffTime }}
                                            className="block w-full text-center py-2.5 bg-gray-900 text-white rounded-lg font-medium hover:bg-blue-600 transition shadow-lg shadow-gray-200"
                                        >
                                            View Details
                                        </Link>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default BrowseBikesPage;
