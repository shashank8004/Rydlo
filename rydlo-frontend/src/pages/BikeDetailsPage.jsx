import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, MapPin, IndianRupee, Gauge, Calendar, ShieldCheck, Star, Clock, AlertCircle, CheckCircle, Bike, Loader2 } from 'lucide-react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const BikeDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const { user } = useAuth();

    const [bike, setBike] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Booking State - Initialize from navigation state if available
    const [pickupDate, setPickupDate] = useState(location.state?.pickupDate || '');
    const [pickupTime, setPickupTime] = useState(location.state?.pickupTime || '');
    const [dropOffDate, setDropOffDate] = useState(location.state?.dropOffDate || '');
    const [dropOffTime, setDropOffTime] = useState(location.state?.dropOffTime || '');

    // Price Preview State
    const [priceDetails, setPriceDetails] = useState(null);
    const [calculatingPrice, setCalculatingPrice] = useState(false);
    const [priceError, setPriceError] = useState('');

    // Booking Submission State
    const [submitting, setSubmitting] = useState(false);
    const [bookingSuccess, setBookingSuccess] = useState(false);

    useEffect(() => {
        const fetchBikeDetails = async () => {
            try {
                const response = await api.get(`/bikes/${id}`);
                setBike(response.data);
            } catch (err) {
                console.error("Error fetching bike details:", err);
                setError('Failed to load bike details. Please try again.');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchBikeDetails();
        }
    }, [id]);

    // Auto-calculate price if dates are pre-filled from search
    useEffect(() => {
        if (bike && pickupDate && pickupTime && dropOffDate && dropOffTime && !priceDetails) {
            calculatePrice();
        }
    }, [bike, pickupDate, pickupTime, dropOffDate, dropOffTime]);

    const calculatePrice = async () => {
        if (!pickupDate || !pickupTime || !dropOffDate || !dropOffTime) {
            setPriceError('Please select all dates and times.');
            return;
        }

        // Validate Dates
        const start = new Date(`${pickupDate}T${pickupTime}`);
        const end = new Date(`${dropOffDate}T${dropOffTime}`);
        const now = new Date();

        if (start < now) {
            setPriceError("Pickup time cannot be in the past.");
            return;
        }

        if (end <= start) {
            setPriceError("Drop-off time must be strictly after pickup time.");
            return;
        }

        setCalculatingPrice(true);
        setPriceError('');
        setPriceDetails(null);

        const requestData = {
            pickupDate,
            pickupTime: pickupTime + ":00", // Append seconds if needed by backend LocalTime
            dropOffDate,
            dropOffTime: dropOffTime + ":00"
        };

        try {
            const response = await api.post(`/bikes/${id}/price-preview`, requestData);
            setPriceDetails(response.data);
        } catch (err) {
            console.error("Price calculation error:", err);
            setPriceError('Invalid dates. Drop-off must be after pickup.');
        } finally {
            setCalculatingPrice(false);
        }
    };

    // Payment State
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [isProcessingPayment, setIsProcessingPayment] = useState(false);

    const initiateBooking = () => {
        if (!user) {
            navigate('/login');
            return;
        }

        if (!user.id) {
            setError("Session invalid. Please Logout and Login again to refresh your profile.");
            return;
        }

        if (!priceDetails) {
            setPriceError('Please check price first.');
            return;
        }

        setShowPaymentModal(true);
    };

    const handlePaymentAndBooking = async () => {
        setIsProcessingPayment(true);

        // Simulate Payment Gateway Delay
        await new Promise(resolve => setTimeout(resolve, 2000));

        const simulatedPaymentId = "pay_" + Math.random().toString(36).substr(2, 9);

        setSubmitting(true);
        try {
            const bookingRequest = {
                bikeId: id,
                customerId: user.id,
                pickupDate,
                pickupTime: pickupTime + ":00",
                dropOffDate,
                dropOffTime: dropOffTime + ":00",
                initialKm: bike.usedKm || 0,
                paymentId: simulatedPaymentId
            };

            await api.post('/bookings', bookingRequest);
            setShowPaymentModal(false);
            setBookingSuccess(true);
        } catch (err) {
            console.error("Booking error:", err);
            setPriceError('Booking failed. Please try again.');
            setShowPaymentModal(false);
        } finally {
            setSubmitting(false);
            setIsProcessingPayment(false);
        }
    };

    if (loading) return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
    );

    if (error || !bike) return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50 space-y-4">
            <p className="text-red-500 font-medium text-lg">{error || 'Bike not found'}</p>
            <Link to="/bikes" className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">
                Back to Browse
            </Link>
        </div>
    );

    if (bookingSuccess) return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-green-50 p-6 text-center">
            <CheckCircle className="w-16 h-16 text-green-600 mb-4" />
            <h1 className="text-3xl font-bold text-green-800 mb-2">Booking Confirmed!</h1>
            <p className="text-green-700 mb-6">Your ride is reserved. Have a safe journey!</p>
            <Link to="/customer/rides" className="px-6 py-3 bg-green-600 text-white rounded-xl font-bold hover:bg-green-700 transition">
                View My Rides
            </Link>
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-50 py-10 px-4 sm:px-6 lg:px-8">
            <div className="max-w-6xl mx-auto">
                {/* Back Button */}
                <Link to="/customer/browse" className="inline-flex items-center text-gray-600 hover:text-blue-600 font-medium mb-8 transition">
                    <ArrowLeft className="w-5 h-5 mr-2" /> Back to Browse
                </Link>

                <div className="grid lg:grid-cols-3 gap-8">
                    {/* Left Column: Bike Details */}
                    <div className="lg:col-span-2 space-y-6">
                        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden">
                            <div className="bg-gray-100 p-8 flex items-center justify-center relative min-h-[300px]">
                                {bike.imageUrl ? (
                                    <img src={bike.imageUrl} alt={bike.model} className="w-full h-auto object-cover rounded-xl" />
                                ) : (
                                    <div className="text-gray-300">
                                        <Bike className="w-32 h-32 mx-auto opacity-20" />
                                        <p className="text-center mt-4 text-xl font-medium opacity-50">No Image Available</p>
                                    </div>
                                )}
                                <div className="absolute top-6 left-6 bg-white/90 backdrop-blur px-3 py-1 rounded-lg text-sm font-bold text-blue-800 uppercase shadow-sm">
                                    {bike.bikeType}
                                </div>
                            </div>

                            <div className="p-8">
                                <h1 className="text-3xl font-extrabold text-gray-900 mb-2">{bike.model}</h1>
                                <div className="flex items-center text-gray-500 text-sm mb-6">
                                    <MapPin className="w-4 h-4 mr-1" />
                                    {bike.city ? `${bike.locality || ''}, ${bike.city}, ${bike.state || ''}` : 'Location available on booking'}
                                </div>

                                <h3 className="text-lg font-bold text-gray-900 mb-4">Vehicle Details</h3>
                                <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                                    <div className="flex flex-col items-center p-3 bg-gray-50 rounded-xl">
                                        <Gauge className="w-5 h-5 text-gray-400 mb-1" />
                                        <span className="text-sm font-semibold">{bike.mileage || '45'} kmpl</span>
                                        <span className="text-xs text-gray-400">Mileage</span>
                                    </div>
                                    <div className="flex flex-col items-center p-3 bg-gray-50 rounded-xl">
                                        <Calendar className="w-5 h-5 text-gray-400 mb-1" />
                                        <span className="text-sm font-semibold">2023</span>
                                        <span className="text-xs text-gray-400">Model</span>
                                    </div>
                                    <div className="flex flex-col items-center p-3 bg-blue-50 rounded-xl text-blue-600">
                                        <ShieldCheck className="w-5 h-5 mb-1" />
                                        <span className="text-sm font-semibold">Verified</span>
                                        <span className="text-xs text-blue-400">Status</span>
                                    </div>
                                    <div className="flex flex-col items-center p-3 bg-yellow-50 rounded-xl text-yellow-600">
                                        <Star className="w-5 h-5 mb-1" />
                                        <span className="text-sm font-semibold">4.8</span>
                                        <span className="text-xs text-yellow-400">Rating</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right Column: Booking Widget */}
                    <div className="lg:col-span-1">
                        <div className="bg-white rounded-3xl shadow-xl border border-gray-100 p-6 sticky top-6">
                            <div className="flex justify-between items-center mb-6">
                                <div>
                                    <p className="text-sm text-gray-500 font-medium uppercase">Daily Rate</p>
                                    <div className="flex items-baseline text-gray-900">
                                        <IndianRupee className="w-5 h-5" />
                                        <span className="text-3xl font-bold">{bike.rentPerDay}</span>
                                    </div>
                                </div>
                                <div className="text-right">
                                    <p className="text-sm text-gray-500 font-medium uppercase">Per KM</p>
                                    <div className="flex items-baseline justify-end text-gray-700">
                                        <IndianRupee className="w-4 h-4" />
                                        <span className="text-xl font-bold">{bike.rentPerKm}</span>
                                    </div>
                                </div>
                            </div>

                            <hr className="border-gray-100 mb-6" />

                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Pickup Date & Time</label>
                                    <div className="flex gap-2">
                                        <input
                                            type="date"
                                            min={new Date().toLocaleDateString('en-CA')}
                                            className="w-full p-2.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                                            value={pickupDate}
                                            onChange={(e) => {
                                                setPickupDate(e.target.value);
                                                if (dropOffDate && e.target.value > dropOffDate) {
                                                    setDropOffDate(''); // Clear invalid dropoff
                                                }
                                            }}
                                        />
                                        <input
                                            type="time"
                                            className="w-full p-2.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                                            value={pickupTime}
                                            onChange={(e) => setPickupTime(e.target.value)}
                                        />
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Drop-off Date & Time</label>
                                    <div className="flex gap-2">
                                        <input
                                            type="date"
                                            min={pickupDate || new Date().toLocaleDateString('en-CA')}
                                            className="w-full p-2.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                                            value={dropOffDate}
                                            onChange={(e) => setDropOffDate(e.target.value)}
                                        />
                                        <input
                                            type="time"
                                            className="w-full p-2.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                                            value={dropOffTime}
                                            onChange={(e) => setDropOffTime(e.target.value)}
                                        />
                                    </div>
                                </div>

                                {priceError && (
                                    <div className="bg-red-50 text-red-600 text-sm p-3 rounded-lg flex items-start gap-2">
                                        <AlertCircle className="w-4 h-4 mt-0.5 flex-shrink-0" />
                                        {priceError}
                                    </div>
                                )}

                                {!priceDetails ? (
                                    <button
                                        onClick={calculatePrice}
                                        disabled={calculatingPrice}
                                        className="w-full py-3 bg-blue-50 text-blue-600 font-bold rounded-xl hover:bg-blue-100 transition flex justify-center items-center gap-2"
                                    >
                                        {calculatingPrice ? <Loader2 className="animate-spin w-5 h-5" /> : 'Check Availability & Price'}
                                    </button>
                                ) : (
                                    <div className="bg-gray-50 p-4 rounded-xl space-y-3">
                                        <div className="flex justify-between text-sm text-gray-600">
                                            <span>Duration</span>
                                            <span className="font-semibold">{priceDetails.durationHours} Hours</span>
                                        </div>
                                        <div className="flex justify-between text-sm text-gray-600">
                                            <span>Base Amount</span>
                                            <span>₹{priceDetails.baseAmount}</span>
                                        </div>
                                        <div className="flex justify-between text-sm text-gray-600">
                                            <span>Taxes (CGST+SGST)</span>
                                            <span>₹{(priceDetails.cgst + priceDetails.sgst).toFixed(2)}</span>
                                        </div>
                                        <div className="border-t border-gray-200 pt-2 flex justify-between font-bold text-gray-900 text-lg">
                                            <span>Total</span>
                                            <span>₹{priceDetails.totalPayable}</span>
                                        </div>

                                        <button
                                            onClick={initiateBooking}
                                            disabled={submitting}
                                            className="w-full py-3 bg-gray-900 text-white font-bold rounded-xl hover:bg-gray-800 transition shadow-lg mt-4 flex justify-center items-center gap-2"
                                        >
                                            Proceed to Pay ₹{priceDetails.totalPayable}
                                        </button>
                                        <button
                                            onClick={() => setPriceDetails(null)}
                                            className="w-full text-center text-sm text-gray-500 hover:text-gray-900 underline"
                                        >
                                            Change Dates
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                {/* Payment Simulation Modal */}
                {showPaymentModal && (
                    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
                        <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 animate-in fade-in zoom-in duration-200">
                            <div className="flex justify-between items-center mb-6">
                                <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                    <ShieldCheck className="text-green-600" /> Secure Payment
                                </h2>
                                <button onClick={() => setShowPaymentModal(false)} className="text-gray-400 hover:text-gray-600">
                                    ✕
                                </button>
                            </div>

                            <div className="space-y-4 mb-6">
                                <div className="flex justify-between py-3 border-b border-gray-100">
                                    <span className="text-gray-600">Total Amount</span>
                                    <span className="text-xl font-bold text-gray-900">₹{priceDetails?.totalPayable}</span>
                                </div>

                                {/* Payment Method Selection */}
                                <div className="grid grid-cols-2 gap-3 mb-4">
                                    <button className="px-4 py-2 border border-blue-500 bg-blue-50 text-blue-700 font-semibold rounded-lg">
                                        Card
                                    </button>
                                    <button className="px-4 py-2 border border-gray-200 text-gray-600 font-medium rounded-lg hover:border-blue-300">
                                        UPI
                                    </button>
                                </div>

                                {/* Mock Inputs */}
                                <div className="space-y-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-gray-500 mb-1">CARD NUMBER</label>
                                        <input type="text" placeholder="0000 0000 0000 0000" className="w-full p-2.5 border border-gray-200 rounded-lg text-sm outline-none focus:border-blue-500" />
                                    </div>
                                    <div className="flex gap-3">
                                        <div className="w-1/2">
                                            <label className="block text-xs font-semibold text-gray-500 mb-1">EXPIRY</label>
                                            <input type="text" placeholder="MM/YY" className="w-full p-2.5 border border-gray-200 rounded-lg text-sm outline-none focus:border-blue-500" />
                                        </div>
                                        <div className="w-1/2">
                                            <label className="block text-xs font-semibold text-gray-500 mb-1">CVV</label>
                                            <input type="text" placeholder="123" className="w-full p-2.5 border border-gray-200 rounded-lg text-sm outline-none focus:border-blue-500" />
                                        </div>
                                    </div>
                                </div>

                                <div className="bg-blue-50 p-3 rounded-lg text-xs text-blue-800">
                                    <p className="font-semibold mb-0.5">Test Mode Enabled</p>
                                    <p>Enter any dummy card details. No actual money will be deducted.</p>
                                </div>
                            </div>

                            <button
                                onClick={handlePaymentAndBooking}
                                disabled={isProcessingPayment}
                                className="w-full py-3.5 bg-green-600 text-white font-bold rounded-xl hover:bg-green-700 transition shadow-lg flex justify-center items-center gap-2"
                            >
                                {isProcessingPayment ? (
                                    <>
                                        <Loader2 className="animate-spin w-5 h-5" /> Processing...
                                    </>
                                ) : (
                                    `Pay ₹${priceDetails?.totalPayable}`
                                )}
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default BikeDetailsPage;
