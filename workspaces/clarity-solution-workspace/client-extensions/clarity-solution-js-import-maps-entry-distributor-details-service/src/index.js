/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export {default as distributors} from './distributors';

const API_KEY = 'YOUR_API_KEY';
const API_URL = 'https://maps.googleapis.com/maps/api/geocode/json';

const api = async (url, options = {}) => {
	return fetch(window.location.origin + url, {
		headers: {
			'Content-Type': 'application/json',
			'x-csrf-token': Liferay.authToken,
		},
		...options,
	});
};

export async function getDistributors() {
	try {
		return (
			await api('/o/c/distributorlocations/').then((response) =>
				response.json()
			)
		).items;
	}
	catch (error) {
		console.error(`Error fetching distributors: ${error}`);

		return [];
	}
}

export async function getDistributorDetails(id) {
	try {
		return await api('/o/c/distributorlocations/' + id).then((response) =>
			response.json()
		);
	}
	catch (error) {
		console.error(`Error fetching distributor with id: ${id}`, error);

		return {};
	}
}

export async function getDistributorLatLng(address) {
	const addressEncoded = encodeURIComponent(address);

	const url = `${API_URL}?address=${addressEncoded}&key=${API_KEY}`;

	try {
		const response = await fetch(url);

		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}

		const data = await response.json();

		if (data.status === 'OK' && !!data.results.length) {
			const location = data.results[0].geometry.location;

			return {
				lat: location.lat,
				lng: location.lng,
			};
		}
		else {
			throw new Error(`Geocoding failed. Status: ${data.status}`);
		}
	}
	catch (error) {
		console.error(`Could not get coordinates: ${error}`);
		throw error;
	}
}
