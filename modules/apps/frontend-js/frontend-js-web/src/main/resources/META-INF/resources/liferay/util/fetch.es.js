/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * @param {!string|!Request|!URL} resource
 * @param {!string} newLocation
 * @return {!string|!Request|!URL}
 */
function setNewLocation(resource, newLocation) {
	if (typeof resource === 'string') {
		resource = newLocation;
	}
	else if (resource instanceof URL) {
		resource = new URL(newLocation);
	}
	else if (resource instanceof Request) {
		resource = new Request(newLocation, resource);
	}
	else {
		console.warn(
			'Resource passed to `fetch()` must either be a string, Request, or URL.'
		);
	}

	return resource;
}

/**
 * Fetches a resource. A thin wrapper around ES6 Fetch API, with standardized
 * default configuration.
 * @param {!string|!Request|!URL} resource The URL to the resource, or a Resource
 * object.
 * @param {Object=} init An optional object containing custom configuration.
 * @return {Promise} A Promise that resolves to a Response object.
 */

export default function defaultFetch(resource, init = {}) {
	if (!resource) {
		resource = '/o/';
	}

	let resourceLocation = resource.url ? resource.url : resource.toString();

	if (resourceLocation.startsWith('/')) {
		const pathContext = Liferay.ThemeDisplay.getPathContext();

		if (pathContext && !resourceLocation.startsWith(pathContext)) {
			resourceLocation = pathContext + resourceLocation;

			resource = setNewLocation(resource, resourceLocation);
		}

		resourceLocation = window.location.origin + resourceLocation;
	}

	const resourceURL = new URL(resourceLocation);

	const headers = new Headers({});
	const config = {};

	if (resourceURL.origin === window.location.origin) {
		headers.set('x-csrf-token', Liferay.authToken);
		config.credentials = 'include';

		const doAsUserIdEncoded = Liferay.ThemeDisplay.getDoAsUserIdEncoded();

		if (doAsUserIdEncoded) {
			resourceURL.searchParams.set('doAsUserId', doAsUserIdEncoded);

			resourceLocation = resourceURL.toString();

			resource = setNewLocation(resource, resourceLocation);
		}

		const launchSetMode = Liferay.ThemeDisplay.getLaunchSetMode?.();

		if (launchSetMode) {
			headers.set(
				'x-liferay-launch-set-id',
				Liferay.ThemeDisplay.getLaunchSetId()
			);
			headers.set('x-liferay-launch-set-mode', launchSetMode);
		}
	}

	new Headers(init.headers || {}).forEach((value, key) => {
		headers.set(key, value);
	});

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	return fetch(resource, {...config, ...init, headers});
}
