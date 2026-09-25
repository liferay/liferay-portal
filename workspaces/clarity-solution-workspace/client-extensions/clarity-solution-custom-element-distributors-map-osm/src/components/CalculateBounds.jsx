/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';
import {useMap} from 'react-leaflet';

const CalculateBounds = ({distributors}) => {
	const map = useMap();

	useEffect(() => {
		const coordinates = distributors
			.map(({position}) => [position.lat, position.lng])
			.filter(
				([lat, lng]) => Number.isFinite(lat) && Number.isFinite(lng)
			);

		if (coordinates.length) {
			map.fitBounds(coordinates, {padding: [50, 50]});
		}
	}, [distributors, map]);

	return null;
};

export default CalculateBounds;
