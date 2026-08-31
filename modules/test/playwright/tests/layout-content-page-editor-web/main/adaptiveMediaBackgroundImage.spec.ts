/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import getContainerDefinition from './utils/getContainerDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(apiHelpersTest, isolatedSiteTest, loginTest());

test(
	'Applies the adaptive media background image only within its media query',
	{tag: '@LPD-102769'},
	async ({apiHelpers, page, site}) => {

		// Upload the image the container uses as its background

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/high_resolution_image.jpg')
			)
		);

		// Create a page with a container whose background image is the document

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({
					backgroundImage: {
						url: {
							mapping: {
								itemReference: {
									className:
										'com.liferay.portal.kernel.repository.model.FileEntry',
									classPK: String(document.id),
								},
							},
							value: document.contentUrl,
						},
					},
					id: containerId,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// View the page narrower than the smallest adaptive media resolution

		const container = page.locator(
			`.lfr-layout-structure-item-${containerId}`
		);

		const getBackgroundImage = () =>
			container.evaluate(
				(element) => getComputedStyle(element).backgroundImage
			);

		await page.setViewportSize({height: 800, width: 280});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(container).toBeAttached();

		// The generated rule has to parse, match the container, and outrank the
		// unprefixed background image the common styles stylesheet applies

		await expect
			.poll(getBackgroundImage)
			.toContain('/o/adaptive-media/image/');

		// Wider than every adaptive media resolution, the original image wins

		await page.setViewportSize({height: 800, width: 1400});

		await expect
			.poll(getBackgroundImage)
			.not.toContain('/o/adaptive-media/image/');

		// Two identical requests have to generate the same rule, otherwise no
		// cached response of the page can ever be revalidated

		const generatedCSS = await container
			.locator('style')
			.first()
			.textContent();

		await page.reload();

		expect(await container.locator('style').first().textContent()).toBe(
			generatedCSS
		);
	}
);
