/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export const GEOCODED_ADDRESS =
	'1400 Montefino Avenue, Diamond Bar, CA 91765, USA';

export const GEOCODED_COORDINATES = {
	latitude: 33.9976963,
	longitude: -117.844664,
};

/**
 * Moves the map pin away from its current position, which makes the map
 * reverse geocode the new coordinates and record the result as the picked
 * location.
 */
export async function dragMapPin(mapContainer: Locator, page: Page) {
	const mapPin = mapContainer.locator('.leaflet-marker-draggable');

	await mapPin.hover();

	await page.mouse.down();

	const boundingBox = (await mapContainer.boundingBox())!;

	await page.mouse.move(
		boundingBox.x + boundingBox.width / 2 + 60,
		boundingBox.y + boundingBox.height / 2 + 40,
		{steps: 10}
	);

	await page.mouse.up();
}

/**
 * Returns the wrapper element of one location field in the entry form. The
 * location field renders no labeled input, so the label cannot anchor the
 * lookup the way it does for other field types.
 */
export function getLocationFieldContainer(fieldName: string, page: Page) {
	return page.locator(`div.form-group[data-field-name*="${fieldName}"]`);
}

/**
 * Returns the hidden input holding the field's serialized location, told
 * apart from the other hidden inputs in the field container by the DDM
 * field name pattern.
 */
export function getLocationValueInput(
	fieldContainer: Locator,
	fieldName: string
) {
	return fieldContainer.locator(
		`input[name*="ddm$$${fieldName}$"][name$="$$en_US"]`
	);
}

/**
 * Keeps map requests from leaving the test: tile images are dropped, since
 * Leaflet works without them, and the browser's reverse geocoding gets a
 * fixed Nominatim response, so a pin drag always resolves to
 * `GEOCODED_ADDRESS`.
 */
export async function interceptMapRequests(page: Page) {
	await page.route('https://nominatim.openstreetmap.org/reverse*', (route) =>
		route.fulfill({
			body: JSON.stringify({
				display_name: GEOCODED_ADDRESS,
				lat: String(GEOCODED_COORDINATES.latitude),
				lon: String(GEOCODED_COORDINATES.longitude),
			}),
			contentType: 'application/json',
		})
	);

	await page.route(/tile\.openstreetmap\.org/, (route) => route.abort());
}
