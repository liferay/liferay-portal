import ValidationError from 'shared/util/ValidationError';
import {forEach, get, keys, mapValues} from 'lodash';
import {reloadPage} from 'shared/util/router';

export const UNAUTHORIZED_ACCESS = 'Unauthorized Access';

export function addParams(url, params) {
	const separator = url.includes('?') ? '&' : '?';

	return `${url}${separator}${serializeQueryString(params)}`;
}

export function getFormData(data) {
	const formData = new FormData();

	forEach(data, (val, key) => {
		formData.append(key, encodeURIComponent(val));
	});

	return formData;
}

/**
 * Helper function to JSON.parse a value.
 * @param {string} value - The JSON to parse.
 * @returns {Object} - Parsed value from JSON.
 */
export function parseFromJSON(value) {
	let result;

	try {
		result = JSON.parse(value);
	}
	catch (err) {}

	return result;
}

/**
 * Return parsed error if it matches one of the response error codes.
 * @param {Object} error - The error object.
 * @returns {object|null} - The parsed error or null if not one of the response error codes.
 */
export function getServiceError(err) {
	const parsedError = parseFromJSON(err.message);

	return get(parsedError, 'status') ? parsedError : null;
}

export function serializeQueryString(params) {
	return keys(params)
		.map(
			(key) =>
				`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
		)
		.join('&');
}

export function stringifyValues(data) {
	return mapValues(data, (value) =>
		value instanceof Object && !(value instanceof File)
			? JSON.stringify(value)
			: value
	);
}

/**
 * Serializes a request payload and drops every undefined value. Undefined keys
 * must never reach `getFormData`: it coerces each value to a string, so an
 * undefined field would be sent as the literal string "undefined" and
 * overwrite the stored value.
 */
export function getRequestData(data) {
	return Object.entries({
		...stringifyValues(data),
	})
		.filter(([, value]) => value !== undefined)
		.reduce((obj, [key, value]) => {
			obj[key] = value;
			return obj;
		}, {});
}

export default (request) => {
	const {
		baseURL = '/o/faro',
		contentType = 'json',
		data,
		method,
		path,
	} = request;

	let requestURL = `${baseURL}/${path}`;

	const authData = data && getRequestData(data);

	const config = {method};

	if (authData) {
		if (method === 'GET') {
			requestURL = `${requestURL}?${new URLSearchParams(authData)}`;
		}
		else {
			config.body = getFormData(authData);
		}
	}

	return fetch(requestURL, config).then(async (response) => {
		const status = response.status;

		if (status === 204) {
			return {};
		}
		else if (status === 400 || status === 500) {
			const {field, localizedMessage, messageKey} =
				parseFromJSON(await response.text()) || {};

			if (field) {
				throw new ValidationError(field, localizedMessage);
			}

			if (messageKey) {
				throw new Error(messageKey);
			}

			throw new Error(
				localizedMessage ? localizedMessage : 'Request Error'
			);
		}
		else if (status === 401) {
			reloadPage();
		}
		else if (status === 403) {
			throw new Error(UNAUTHORIZED_ACCESS);
		}
		else if (status >= 300) {
			throw new Error('Request error');
		}
		else if (contentType === 'json') {
			return response.json();
		}
		else {
			return response.text();
		}
	});
};
