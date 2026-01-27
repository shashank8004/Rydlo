import { useState, useEffect } from 'react';
import { Bike, Loader2, ArrowLeft } from 'lucide-react';
import api from '../api/axios';
import { Link } from 'react-router-dom';

const MyRidesPage = () => {
    const [myBookings, setMyBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchMyBookings = async () => {
        setLoading(true);
        try {
            const response = await api.get('/bookings/my');
            setMyBookings(response.data);
        } catch (error) {
            console.error("Error fetching my bookings", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMyBookings();
    }, []);

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-6xl mx-auto space-y-6">

                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-extrabold text-gray-900">My Booking History</h1>
                    <Link to="/customer/browse" className="text-blue-600 hover:text-blue-700 font-semibold flex items-center gap-2">
                        <ArrowLeft className="w-4 h-4" /> Browse Bikes
                    </Link>
                </div>

                {loading ? (
                    <div className="flex justify-center py-20">
                        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
                    </div>
                ) : myBookings.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
                        <Bike className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                        <h3 className="text-xl font-medium text-gray-900">No rides yet</h3>
                        <p className="text-gray-500 mt-2">Book your first ride today!</p>
                        <Link to="/customer/browse" className="mt-4 inline-block px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">
                            Find a Bike
                        </Link>
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
                                                <div className="flex flex-col gap-2">
                                                    <span className={`px-2 py-1 rounded text-xs font-bold w-fit ${b.bookingStatus === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                                                            b.bookingStatus === 'BOOKED' ? 'bg-blue-100 text-blue-700' :
                                                                b.bookingStatus === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                                                                    'bg-gray-100 text-gray-700'
                                                        }`}>
                                                        {b.bookingStatus}
                                                    </span>

                                                    {/* Cancel Button Logic */}
                                                    {b.bookingStatus === 'BOOKED' && (
                                                        (() => {
                                                            const pickup = new Date(b.pickupDateTime);
                                                            const now = new Date();
                                                            const diffHours = (pickup - now) / (1000 * 60 * 60);

                                                            if (diffHours >= 24) {
                                                                return (
                                                                    <button
                                                                        onClick={async () => {
                                                                            if (window.confirm("Are you sure you want to cancel this booking? (Refund policies apply)")) {
                                                                                try {
                                                                                    await api.post(`/bookings/${b.id}/cancel`);
                                                                                    fetchMyBookings(); // Refresh list
                                                                                } catch (err) {
                                                                                    alert(err.response?.data?.message || "Failed to cancel booking");
                                                                                }
                                                                            }
                                                                        }}
                                                                        className="text-xs text-red-600 hover:text-red-800 font-medium underline text-left"
                                                                    >
                                                                        Cancel Booking
                                                                    </button>
                                                                );
                                                            }
                                                            return null;
                                                        })()
                                                    )}
                                                </div>
                                            </td>
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

export default MyRidesPage;
