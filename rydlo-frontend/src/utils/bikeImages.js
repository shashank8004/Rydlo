
// Public image references from Unsplash/Pexels for demo purposes
export const bikeTypeImages = {
    'SPORT': 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?q=80&w=2070',
    'CRUISER': 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?q=80&w=2070',
    'SCOOTER': 'https://images.unsplash.com/photo-1626372416494-3183a0c4f726?q=80&w=2012',
    'SPORT_TOURING': 'https://images.unsplash.com/photo-1609630809669-04c27a99f36f?q=80&w=1932',
    'OFF_ROAD': 'https://images.unsplash.com/photo-1520633903829-0ec60600021b?q=80&w=2070',
    'DEFAULT': 'https://images.unsplash.com/photo-1558981806-ec527fa84c3d?q=80&w=2070'
};

export const getBikeImage = (bike) => {
    if (bike?.imageUrl) return bike.imageUrl;

    // Check by specific model name (loosely)
    const model = bike?.model?.toLowerCase() || '';
    if (model.includes('royal enfield') || model.includes('classic') || model.includes('bullet'))
        return 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?q=80&w=2070';

    if (model.includes('ktm') || model.includes('duke') || model.includes('rc'))
        return 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?q=80&w=2070';

    if (model.includes('activa') || model.includes('jupiter') || model.includes('access'))
        return 'https://images.unsplash.com/photo-1626372416494-3183a0c4f726?q=80&w=2012';

    if (model.includes('himalayan') || model.includes('xpulse'))
        return 'https://images.unsplash.com/photo-1520633903829-0ec60600021b?q=80&w=2070';

    // Fallback to type
    if (bike?.bikeType) {
        return bikeTypeImages[bike.bikeType] || bikeTypeImages['DEFAULT'];
    }

    return bikeTypeImages['DEFAULT'];
};
