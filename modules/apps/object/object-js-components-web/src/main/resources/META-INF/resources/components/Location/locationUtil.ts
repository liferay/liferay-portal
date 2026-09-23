/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const MAP_PROVIDER = {
	googleMaps: 'GoogleMaps',
	openStreetMap: 'OpenStreetMap',
} as const;

export type MapProviderKey = (typeof MAP_PROVIDER)[keyof typeof MAP_PROVIDER];

export interface LocationCoordinates {
	latitude: number;
	longitude: number;
}

export interface LocationValue {
	address: string;
	coordinates: LocationCoordinates;
}

/**
 * Parses a stored value into an address with coordinates. Anything without
 * numeric coordinates is treated as empty. For example,
 * `'{"address":"Recife","coordinates":{"latitude":-8,"longitude":-34}}'`
 * becomes `{address: 'Recife', coordinates: {latitude: -8, longitude: -34}}`,
 * while `''`, `'{}'`, and broken JSON become `null`.
 */
export function parseLocationValue(value?: string): LocationValue | null {
	if (!value) {
		return null;
	}

	try {
		const parsed = JSON.parse(value);

		if (
			typeof parsed?.coordinates?.latitude === 'number' &&
			typeof parsed?.coordinates?.longitude === 'number'
		) {
			return {
				address: parsed.address ?? '',
				coordinates: {
					latitude: parsed.coordinates.latitude,
					longitude: parsed.coordinates.longitude,
				},
			};
		}
	}
	catch {
		return null;
	}

	return null;
}

/**
 * Serializes a location into the JSON string the field stores, the
 * counterpart of `parseLocationValue`. Only the address and coordinates are
 * kept, so anything extra the map attached, such as a Places `viewport`, is
 * not stored. For example,
 * `{address: 'Recife', coordinates: {latitude: -8, longitude: -34}}` becomes
 * `'{"address":"Recife","coordinates":{"latitude":-8,"longitude":-34}}'`,
 * while `null` becomes `''`.
 */
export function stringifyLocationValue(locationValue: LocationValue | null) {
	if (!locationValue) {
		return '';
	}

	const {address, coordinates} = locationValue;

	return JSON.stringify({
		address,
		coordinates: {
			latitude: coordinates.latitude,
			longitude: coordinates.longitude,
		},
	});
}
