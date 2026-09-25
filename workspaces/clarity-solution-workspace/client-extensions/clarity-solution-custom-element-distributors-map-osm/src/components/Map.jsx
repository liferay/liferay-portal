/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getDistributors} from 'clarity-solution-js-import-maps-entry-distributors';
import L from 'leaflet';
import {useEffect, useState} from 'react';
import {MapContainer, Marker, TileLayer, Tooltip} from 'react-leaflet';

import CalculateBounds from './CalculateBounds.jsx';

const ICON = new L.Icon({
	iconAnchor: [16, 32],
	iconSize: [32, 32],
	iconUrl: 'https://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png',
});

const Map = () => {
	const [distributors, setDistributors] = useState(null);
	const [hoveredMarker, setHoveredMarker] = useState(null);

	const handleMarkerClick = (distributor) => {
		window.Liferay?.fire('selectDistributor', distributor);
	};

	useEffect(() => {
		const fetchDistributors = async () => {
			const distributors = await getDistributors();

			setDistributors(
				distributors.map((distributor) => ({
					...distributor,
					position: {
						lat: Number(distributor.latitude),
						lng: Number(distributor.longitude),
					},
				}))
			);
		};

		fetchDistributors();
	}, []);

	return (
		distributors && (
			<div style={{height: '500px', width: '100%'}}>
				<MapContainer
					center={[0, 0]}
					style={{height: '100%', width: '100%'}}
					zoom={4}
				>
					<TileLayer
						attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
						url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
					/>

					<CalculateBounds distributors={distributors} />

					{distributors
						.filter(
							({position}) =>
								Number.isFinite(position.lat) &&
								Number.isFinite(position.lng)
						)
						.map((distributor) => (
							<Marker
								eventHandlers={{
									click: () => handleMarkerClick(distributor),
									mouseout: () => setHoveredMarker(null),
									mouseover: () =>
										setHoveredMarker(distributor.id),
								}}
								icon={ICON}
								key={distributor.id}
								position={[
									distributor.position.lat,
									distributor.position.lng,
								]}
							>
								{hoveredMarker === distributor.id && (
									<Tooltip
										direction="top"
										offset={[0, -8]}
										opacity={1}
										permanent
									>
										<div
											style={{
												whiteSpace: 'normal',
												width: 200,
											}}
										>
											<h5>{distributor.name}</h5>

											<div>{distributor.street}</div>

											<div>
												{distributor.city},{' '}
												{distributor.state},{' '}
												{distributor.zipCode}
											</div>
										</div>
									</Tooltip>
								)}
							</Marker>
						))}
				</MapContainer>
			</div>
		)
	);
};

export default Map;
