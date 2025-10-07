/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MapGoogleMaps} from '@liferay/map-google-maps';
import {MapOpenStreetMap} from '@liferay/map-openstreetmap';
import {parseName} from 'data-engine-js-components-web';
import Leaflet from 'leaflet';
import {useEffect, useRef} from 'react';

export const MAP_PROVIDER = {
	googleMaps: 'GoogleMaps',
	openStreetMap: 'OpenStreetMap',
};

const {CONTROLS} = Liferay.MapBase;

const MAP_CONFIG = {
	controls: [
		CONTROLS.HOME,
		CONTROLS.PAN,
		CONTROLS.SEARCH,
		CONTROLS.TYPE,
		CONTROLS.ZOOM,
	],
	geolocation: true,
	position: {location: {}},
};

const getMapName = (name) => {
	const {fieldName, instanceId, portletNamespace, repeatedIndex} =
		parseName(name);

	return `${portletNamespace}map$$${fieldName}$${instanceId}$${repeatedIndex}`;
};

const parseJSONValue = (value) => {
	if (typeof value === 'string') {
		return JSON.parse(value);
	}

	return value;
};

const setupMapOpenStreetMaps = (callback) => {
	Leaflet.Icon.Default.imagePath =
		'https://npmcdn.com/leaflet@1.7.1/dist/images/';

	if (!window['L']) {
		window['L'] = Leaflet;
	}

	callback();
};

const setupGoogleMaps = (googleMapsAPIKey, callback) => {
	if (
		window.google &&
		window.google.maps &&
		Liferay.Maps &&
		Liferay.Maps.gmapsReady
	) {
		callback();
	}
	else {
		Liferay.namespace('Maps').onGMapsReady = function () {
			Liferay.Maps.gmapsReady = true;
			Liferay.fire('gmapsReady');
		};

		Liferay.once('gmapsReady', () => callback());

		let apiURL = `${location.protocol}//maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&callback=Liferay.Maps.onGMapsReady`;

		if (googleMapsAPIKey) {
			apiURL += '&key=' + googleMapsAPIKey;
		}

		let script = document.createElement('script');

		script.setAttribute('src', apiURL);

		document.head.appendChild(script);

		script = null;
	}
};

export function useGeolocation({
	disabled,
	editingLanguageId,
	googleMapsAPIKey,
	instanceId,
	mapProviderKey,
	name,
	onChange,
	value,
	viewMode,
}) {
	const mapRef = useRef(null);

	useEffect(() => {
		if (!disabled || viewMode) {
			const mapConfig = {
				...MAP_CONFIG,
				boundingBox: `#map_${instanceId}`,
			};

			if (value) {
				mapConfig.position.location = parseJSONValue(value);
			}
			else {
				mapConfig.position.location = {lat: 0, lng: 0};
			}

			const registerMapBase = (MapProvider, mapConfig) => {
				mapRef.current = new MapProvider(mapConfig);

				Liferay.MapBase.register(
					getMapName(name),
					mapRef.current,
					`#map_${instanceId}`
				);

				mapRef.current.removeAllListeners('positionChange');

				mapRef.current.on('positionChange', onChange);

				if (value) {
					mapRef.current.setCenter(parseJSONValue(value));
				}
			};

			switch (mapProviderKey) {
				case MAP_PROVIDER.openStreetMap:
					setupMapOpenStreetMaps(() =>
						registerMapBase(MapOpenStreetMap, mapConfig)
					);
					break;

				case MAP_PROVIDER.googleMaps:
					setupGoogleMaps(googleMapsAPIKey, () =>
						registerMapBase(MapGoogleMaps, mapConfig)
					);
					break;

				default:
					throw new Error('mapProvider is not supported!');
			}
		}

		return () => {
			if (mapRef.current) {
				mapRef.current.dispose();
			}
		};

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		if (mapRef.current) {
			mapRef.current.removeAllListeners('positionChange');

			mapRef.current.on('positionChange', onChange);
		}
	}, [onChange]);

	useEffect(() => {
		if (value) {
			let _value = value;

			if (typeof _value !== 'string') {
				_value = JSON.stringify(value);
			}

			document
				.getElementById(`input_value_${instanceId}`)
				.setAttribute('value', _value);

			if (mapRef.current) {
				mapRef.current.setCenter(parseJSONValue(value));
			}
		}
	}, [instanceId, value]);

	useEffect(() => {
		if (mapRef.current && editingLanguageId) {
			const location = value ? parseJSONValue(value) : {lat: 0, lng: 0};

			if (typeof mapRef.current._geocoder?.reverse === 'function') {
				mapRef.current._geocoder.reverse(location, ({data}) => {
					const address = data?.address ?? '';

					mapRef.current.emit('positionChange', {
						newVal: {address, location},
					});
				});
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [editingLanguageId]);
}
