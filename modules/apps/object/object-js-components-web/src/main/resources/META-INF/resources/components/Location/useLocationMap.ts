/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useStableCallback} from 'frontend-js-components-web';
import {useEffect, useRef} from 'react';

import {
	LocationCoordinates,
	LocationValue,
	MAP_PROVIDER,
	MapProviderKey,
} from './locationUtil';

export interface MapPosition {
	address?: string;
	location: {lat: number; lng: number};
	viewport?: unknown;
}

/**
 * Tells whether the map pin `position` already sits on the `locationValue`
 * coordinates, which decides whether the pin must move.
 */
function isSameLocation(
	position: MapPosition | undefined,
	locationValue: LocationValue
) {
	return (
		position?.location?.lat === locationValue.coordinates.latitude &&
		position?.location?.lng === locationValue.coordinates.longitude
	);
}

/**
 * Converts stored `coordinates` into the `{lat, lng}` location the map
 * expects.
 */
function toMapLocation(coordinates: LocationCoordinates) {
	return {lat: coordinates.latitude, lng: coordinates.longitude};
}

/**
 * Creates the map according to `mapProviderKey` inside the `mapElementId`
 * element, keeps its pin on `value`, and reports every position the user
 * picks, through a dragged pin or a picked address suggestion, to
 * `onPositionChange`, unless the field is `disabled`. Returns `setPosition`,
 * which moves the map to a position the caller resolved outside the map,
 * such as a picked Google Maps suggestion.
 */
export function useLocationMap({
	disabled,
	googleMapsAPIKey,
	mapElementId,
	mapProviderKey,
	onPositionChange,
	value,
}: {
	disabled?: boolean;
	googleMapsAPIKey?: string;
	mapElementId: string;
	mapProviderKey: MapProviderKey;
	onPositionChange: (locationValue: LocationValue) => void;
	value: LocationValue | null;
}) {
	const disabledRef = useRef(disabled);
	const ignorePositionChangeRef = useRef(false);
	const mapRef = useRef<any>(null);
	const stableOnPositionChange = useStableCallback(onPositionChange);
	const valueRef = useRef(value);

	// Fixes the pin while the field is disabled, such as when another
	// language is being edited. The map is kept instead of recreated, since
	// the providers cannot create a second map on the same element.

	useEffect(() => {
		disabledRef.current = disabled;

		if (mapRef.current) {
			mapRef.current.draggablePin = !disabled;
		}
	}, [disabled]);

	// Keeps the latest value for when the map is created, without recreating
	// the map every time the value changes.

	useEffect(() => {
		valueRef.current = value;
	}, [value]);

	// Creates the map, and destroys it when the field unmounts or the
	// provider changes.

	useEffect(() => {
		const googleMaps = mapProviderKey === MAP_PROVIDER.googleMaps;

		let destroyed = false;
		let detachGoogleMapsListener: (() => void) | undefined;

		const createMap = (MapBase: any, MapProvider: any) => {
			const {CONTROLS} = MapBase;

			const map = new MapProvider({
				boundingBox: `#${mapElementId}`,
				controls: [
					CONTROLS.HOME,
					CONTROLS.PAN,
					CONTROLS.TYPE,
					CONTROLS.ZOOM,
				],
				draggablePin: !disabledRef.current,
				geolocation: true,
				position: valueRef.current
					? {
							address: valueRef.current.address,
							location: toMapLocation(
								valueRef.current.coordinates
							),
						}
					: {location: {lat: 0, lng: 0}},
			});

			// The map reports a first position when it finishes loading, with
			// an address looked up from the pin. The user did not pick it, so
			// it must not replace the stored value.

			let loaded = false;

			map.on(
				'positionChange',
				({newVal: newValue}: {newVal: MapPosition}) => {
					if (ignorePositionChangeRef.current) {
						return;
					}

					if (!loaded) {
						loaded = true;

						return;
					}

					// A disabled field still shows the map, but nothing picked
					// on it may change the stored value.

					if (disabledRef.current) {
						return;
					}

					stableOnPositionChange({
						address: newValue.address ?? '',
						coordinates: {
							latitude: newValue.location.lat,
							longitude: newValue.location.lng,
						},
					});
				}
			);

			mapRef.current = map;
		};

		// Import the map packages only when a map is created, and only for the
		// chosen provider. The portal links modules in the browser, so every
		// page that imports anything from @liferay/object-js-components-web
		// loads its whole bundle and the bundle's top-level imports, even
		// without a Location field. The map packages, Leaflet included, are
		// too heavy to load on all those pages.

		Promise.all([
			import('@liferay/map-common'),
			googleMaps
				? import('@liferay/map-google-maps')
				: import('@liferay/map-openstreetmap'),
		]).then(([{MapBase}, mapProviderModule]) => {
			if (destroyed) {
				return;
			}

			if (googleMaps) {
				detachGoogleMapsListener = mapProviderModule.loadGoogleMaps(
					googleMapsAPIKey,
					() => createMap(MapBase, mapProviderModule.MapGoogleMaps)
				);
			}
			else {
				createMap(MapBase, mapProviderModule.MapOpenStreetMap);
			}
		});

		return () => {
			destroyed = true;

			detachGoogleMapsListener?.();

			mapRef.current?.dispose();

			mapRef.current = null;
		};
	}, [
		googleMapsAPIKey,
		mapElementId,
		mapProviderKey,
		stableOnPositionChange,
	]);

	// Moves the pin when the value changes outside the map, such as when the
	// editing language changes. The map reports the move back right away, and
	// that report is ignored so it is not saved as a user change.

	useEffect(() => {
		if (
			!mapRef.current ||
			!value ||
			isSameLocation(mapRef.current.position, value)
		) {
			return;
		}

		ignorePositionChangeRef.current = true;

		mapRef.current.position = {
			address: value.address,
			location: toMapLocation(value.coordinates),
		};

		ignorePositionChangeRef.current = false;
	}, [value]);

	/**
	 * Moves the map to a position the caller resolved outside the map, such
	 * as a picked Google Maps suggestion, and reports it to
	 * `onPositionChange` like any other position the user picks. It calls the
	 * map's search handler instead of setting `position` directly, because
	 * the handler also zooms the map to fit the place.
	 */
	const setPosition = (position: MapPosition) => {
		mapRef.current?._handleSearchButtonClicked({position});
	};

	return {setPosition};
}
