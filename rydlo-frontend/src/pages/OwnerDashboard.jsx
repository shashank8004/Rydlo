import { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, MapPin, IndianRupee, Bike, X, Save, Search, Loader2 } from 'lucide-react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

const OwnerDashboard = () => {
    const [bikes, setBikes] = useState([]);
    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false); // For add/update/delete operations
    const [showModal, setShowModal] = useState(false);
    const [editingBike, setEditingBike] = useState(null); // specific bike being edited

    // Form State
    const [formData, setFormData] = useState({
        model: '',
        number: '',
        bikeType: 'SCOOTY',
        rentPerDay: '',
        rentPerKm: '',
        usedKm: '',
        pickupLocationId: ''
    });

    const navigate = useNavigate();

    // Fetch initial data
    useEffect(() => {
        fetchBikes();
        fetchLocations();
    }, []);

    const fetchBikes = async () => {
        try {
            const response = await api.get('/owners/bikes');
            setBikes(response.data);
        } catch (error) {
            console.error("Error fetching bikes:", error);
            // Handle unauthorized specifically if needed
        } finally {
            setLoading(false);
        }
    };

    const fetchLocations = async () => {
        try {
            const response = await api.get('/owners/pickup-locations');
            setLocations(response.data);
        } catch (error) {
            console.error("Error fetching locations:", error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setActionLoading(true);
        try {
            if (editingBike) {
                // Update
                await api.put(`/owners/bikes/${editingBike.id}`, formData);
                alert("Bike Updated Successfully!");
            } else {
                // Add
                await api.post('/owners/bikes', formData);
                alert("Bike Added Successfully!");
            }
            setShowModal(false);
            setEditingBike(null);
            resetForm();
            fetchBikes(); // Refresh list
        } catch (error) {
            console.error("Operation failed:", error);
            alert("Operation failed: " + (error.response?.data?.message || error.message));
        } finally {
            setActionLoading(false);
        }
    };

    const handleDelete = async (bikeId) => {
        if (!window.confirm("Are you sure you want to delete this bike?")) return;

        setActionLoading(true);
        try {
            await api.delete(`/owners/bikes/${bikeId}`);
            setBikes(prev => prev.filter(b => b.id !== bikeId)); // Optimistic update
        } catch (error) {
            console.error("Delete failed:", error);
            alert("Delete failed.");
            fetchBikes(); // Revert on failure
        } finally {
            setActionLoading(false);
        }
    };

    const openAddModal = () => {
        setEditingBike(null);
        resetForm();
        setShowModal(true);
    };

    const openEditModal = (bike) => {
        setEditingBike(bike);
        setFormData({
            model: bike.model,
            number: bike.number || '', // Ensure field exists in DTO locally if needed, or fetch details. Assuming basic details are in list or we handle it. 
            // NOTE: The List DTO might not have 'number' or 'usedKm'. 
            // If the List DTO is missing fields, we might need to fetch full details. 
            // For now, let's assume the user re-enters or we map what we have.
            // *CORRECTION*: The BikeResDTO from backend DOES NOT have 'number', 'usedKm', 'pickupLocationId'.
            // We should ideally fetch details by ID, but for MVP let's see if we can just edit what's visible or if we need to fetch.
            // Let's implement a 'fetch details' for edit to be safe.
            bikeType: bike.bikeType,
            rentPerDay: bike.rentPerDay,
            rentPerKm: bike.rentPerKm,
            usedKm: 0, // Placeholder if missing from List DTO
            pickupLocationId: locations[0]?.id || ''
        });

        // Fetch full details for the specific bike to populate form correctly
        fetchBikeDetailsForEdit(bike.id);

        setShowModal(true);
    };

    const fetchBikeDetailsForEdit = async (id) => {
        try {
            const res = await api.post(`/bikes/${id}`); // Using existing endpoint to get details
            const data = res.data;
            setFormData({
                model: data.model,
                number: data.number,
                bikeType: data.bikeType,
                rentPerDay: data.rentPerDay,
                rentPerKm: data.rentPerKm,
                usedKm: data.usedKm,
                pickupLocationId: locations.find(l => l.city === data.city && l.locality === data.locality)?.id || '' // Try to match location
            });
        } catch (e) {
            console.error("Failed to fetch bike details", e);
        }
    }

    const resetForm = () => {
        setFormData({
            model: '',
            number: '',
            bikeType: 'SCOOTY',
            rentPerDay: '',
            rentPerKm: '',
            usedKm: '',
            pickupLocationId: locations[0]?.id || ''
        });
    };

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-7xl mx-auto">
                <div className="flex justify-between items-center mb-8">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900">Owner Dashboard</h1>
                        <p className="text-gray-600">Manage your fleet and earnings</p>
                    </div>
                    <button
                        onClick={openAddModal}
                        className="bg-blue-600 text-white px-6 py-2.5 rounded-xl font-semibold hover:bg-blue-700 transition flex items-center shadow-lg hover:shadow-xl"
                    >
                        <Plus className="w-5 h-5 mr-2" /> Add New Bike
                    </button>
                </div>

                {loading ? (
                    <div className="flex justify-center py-20"><Loader2 className="animate-spin h-10 w-10 text-blue-600" /></div>
                ) : bikes.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
                        <Bike className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                        <h3 className="text-xl font-medium text-gray-900">No bikes listed yet</h3>
                        <p className="text-gray-500 mt-2">Start earning by adding your first bike!</p>
                    </div>
                ) : (
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {bikes.map(bike => (
                            <div key={bike.id} className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition group border border-gray-100">
                                <div className="h-40 bg-gray-100 flex items-center justify-center relative">
                                    <Bike className="w-16 h-16 text-gray-400 opacity-50" />
                                    <span className="absolute top-3 right-3 bg-white/90 backdrop-blur px-2 py-1 rounded text-xs font-bold text-blue-800 uppercase border border-blue-50">
                                        {bike.bikeType}
                                    </span>
                                </div>
                                <div className="p-5">
                                    <h3 className="text-lg font-bold text-gray-900 mb-1">{bike.model}</h3>

                                    <div className="grid grid-cols-2 gap-4 mt-4 mb-6">
                                        <div className="text-sm">
                                            <p className="text-gray-500">Rent/Day</p>
                                            <p className="font-semibold text-gray-900 flex items-center"><IndianRupee className="w-3 h-3" /> {bike.rentPerDay}</p>
                                        </div>
                                        <div className="text-sm">
                                            <p className="text-gray-500">Rent/Km</p>
                                            <p className="font-semibold text-gray-900 flex items-center"><IndianRupee className="w-3 h-3" /> {bike.rentPerKm}</p>
                                        </div>
                                    </div>

                                    <div className="flex gap-2 border-t pt-4">
                                        <button
                                            onClick={() => openEditModal(bike)}
                                            className="flex-1 py-2 bg-gray-50 text-gray-700 rounded-lg hover:bg-gray-100 font-medium transition flex justify-center items-center"
                                        >
                                            <Edit2 className="w-4 h-4 mr-2" /> Edit
                                        </button>
                                        <button
                                            onClick={() => handleDelete(bike.id)}
                                            className="flex-1 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 font-medium transition flex justify-center items-center"
                                        >
                                            <Trash2 className="w-4 h-4 mr-2" /> Delete
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Modal */}
            {showModal && (
                <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto shadow-2xl animate-in fade-in zoom-in duration-200">
                        <div className="p-6 border-b flex justify-between items-center sticky top-0 bg-white z-10">
                            <h2 className="text-xl font-bold">{editingBike ? 'Edit Bike' : 'Add New Bike'}</h2>
                            <button onClick={() => setShowModal(false)} className="p-2 hover:bg-gray-100 rounded-full transition">
                                <X className="w-5 h-5" />
                            </button>
                        </div>

                        <form onSubmit={handleSubmit} className="p-6 space-y-6">
                            <div className="grid md:grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Bike Model</label>
                                    <input
                                        type="text"
                                        name="model"
                                        required
                                        value={formData.model}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                        placeholder="e.g. Honda Activa 6G"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Registration Number</label>
                                    <input
                                        type="text"
                                        name="number"
                                        required
                                        value={formData.number}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                        placeholder="e.g. MH 12 AB 1234"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Bike Type</label>
                                    <select
                                        name="bikeType"
                                        value={formData.bikeType}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    >
                                        <option value="SCOOTY">Scooty</option>
                                        <option value="SPORTS">Sports Bike</option>
                                        <option value="ELECTRIC">Electric</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Pickup Location</label>
                                    <select
                                        name="pickupLocationId"
                                        required
                                        value={formData.pickupLocationId}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    >
                                        <option value="">Select Location</option>
                                        {locations.map(loc => (
                                            <option key={loc.id} value={loc.id}>
                                                {loc.locality}, {loc.city} ({loc.pincode})
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Rent Per Day (₹)</label>
                                    <input
                                        type="number"
                                        name="rentPerDay"
                                        required
                                        value={formData.rentPerDay}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Rent Per Km (₹)</label>
                                    <input
                                        type="number"
                                        name="rentPerKm"
                                        required
                                        value={formData.rentPerKm}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Km Driven</label>
                                    <input
                                        type="number"
                                        name="usedKm"
                                        required
                                        value={formData.usedKm}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    />
                                </div>
                            </div>

                            <button
                                type="submit"
                                disabled={actionLoading}
                                className="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition flex justify-center items-center gap-2"
                            >
                                {actionLoading ? <Loader2 className="animate-spin w-5 h-5" /> : <><Save className="w-5 h-5" /> {editingBike ? 'Update Bike' : 'Save Bike'}</>}
                            </button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default OwnerDashboard;
