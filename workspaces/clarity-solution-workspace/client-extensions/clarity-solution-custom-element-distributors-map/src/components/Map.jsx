import { useState, useEffect } from 'react';
import { getDistributors } from 'clarity-distributors-api';
import { APIProvider, Map as GoogleMap } from '@vis.gl/react-google-maps';

import AdvancedMarkerWithInfoWindow from './AdvancedMarkerWithInfoWindow';

const Map = ({ promoCodeStore }) => {
	const [activeMarker, setActiveMarker] = useState(null);
	const [center, setCenter] = useState(null);
	const [hoveredMarker, setHoveredMarker] = useState(null);
	const [distributors, setDistributors] = useState(null);

	const handleMarkerClick = (distributorId) => {
		setActiveMarker(markerId);
		Liferay.fire('selectDistributor', distributors.find(distributor => distributor.id === distributorId));
	};
	const handleMouseOut = () => {
		setHoveredMarker(null);
	};
	const handleMouseOver = (distributorId) => {
		setHoveredMarker(distributorId);
	};

	useEffect(() => {
		const fetchDistributors = async () => {
			const distributors = await getDistributors();

			setDistributors(distributors.map(distributor => ({
				...distributor,
				position: { 'lat': distributor.latitude, 'lng': distributor.longitude }
			})));

			if (distributors && distributors.length > 0) {
				setCenter({ 'lat': distributors[0].latitude, 'lng': distributors[0].longitude });
			}
		};

		fetchDistributors();
	}, []);

	return (
		distributors && <APIProvider apiKey="YOUR_API_KEY">
			<GoogleMap
				defaultCenter={center}
				defaultZoom={5}
				mapId="claritySolutionCustomElementDistributorsMap"
				style={{ width: '100%', height: '500px' }}
			>
				{
					distributors.map(distributor => (
						<AdvancedMarkerWithInfoWindow
							distributor={distributor}
							hoveredMarker={hoveredMarker}
							key={distributor.id}
							onClick={handleMarkerClick}
							onClose={setActiveMarker}
							onMouseOver={handleMouseOver}
							onMouseOut={handleMouseOut}
							promoCodeStore={promoCodeStore}
						/>
					))
				}
			</GoogleMap>
		</APIProvider>
	);
};

export default Map;