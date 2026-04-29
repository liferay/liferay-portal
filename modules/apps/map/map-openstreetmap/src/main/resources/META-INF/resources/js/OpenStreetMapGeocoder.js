/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const OPEN_STREET_MAP_URL = 'https://nominatim.openstreetmap.org/';

/**
 * OpenStreetMapGeocoder
 */
export default class OpenStreetMapGeocoder {

	/**
	 * Handles the server response of a successfull address forward
	 * @param {Object} response Server response
	 * @param {function} callback Callback that will be executed on success
	 * @protected
	 * @review
	 */
	_handleForward(response, callback) {
		callback(response);
	}

	/**
	 * Handles the server response of a successfull location reverse
	 * @param {Object} response Server response
	 * @param {function} callback Callback that will be executed on success
	 * @protected
	 * @review
	 */
	_handleReverse(response, callback) {
		const {display_name, error, lat, lon} = response;

		const result = {
			data: {},
			error,
		};

		if (!result.error) {
			result.data = {
				address: display_name,
				location: {
					lat: parseFloat(lat) || 0,
					lng: parseFloat(lon) || 0,
				},
			};
		}

		callback(result);
	}

	/**
	 * Transforms a given address into valid latitude and longitude
	 * @param {string} query Address to be transformed into latitude and longitude
	 * @param {function} callback Callback that will be executed on success
	 * @review
	 */
	forward(query, callback) {
		const forwardURL =
			OpenStreetMapGeocoder.TPL_FORWARD_GEOCODING_URL.replace(
				'{query}',
				query
			);

		fetch(forwardURL)
			.then((response) => response.json())
			.then((response) => this._handleForward(response, callback));
	}

	/**
	 * Transforms a given location object (lat, lng) into a valid address
	 * @param {string} location Location information to be sent to the server
	 * @param {function} callback Callback that will be executed on success
	 * @review
	 */
	reverse(location, callback) {
		const reverseURL =
			OpenStreetMapGeocoder.TPL_REVERSE_GEOCODING_URL.replace(
				'{lat}',
				location.lat
			).replace('{lng}', location.lng);

		fetch(reverseURL)
			.then((response) => response.json())
			.then((response) =>
				this._handleReverse(
					response.error ? location : response,
					callback
				)
			);
	}
}

/**
 * Url template used for OpenStreetMapGeocoder.forward() method
 * @review
 * @see OpenStreetMapGeocoder.forward()
 * @type {string}
 */
OpenStreetMapGeocoder.TPL_FORWARD_GEOCODING_URL = `${OPEN_STREET_MAP_URL}search?q={query}&format=jsonv2`;

/**
 * Url template used for OpenStreetMapGeocoder.reverse() method
 * @review
 * @see OpenStreetMapGeocoder.reverse()
 * @type {string}
 */
OpenStreetMapGeocoder.TPL_REVERSE_GEOCODING_URL = `${OPEN_STREET_MAP_URL}reverse?lat={lat}&lon={lng}&format=jsonv2`;
