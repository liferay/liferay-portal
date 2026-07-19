/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '@playwright/test';

import {CMSLoginPage} from './pages/CMSLoginPage';

test.describe('Standalone CMS login page', () => {
	let cmsLoginPage: CMSLoginPage;
	let slideDotLabels: string[];
	let slideTitles: string[];

	test.beforeEach(async ({page}) => {
		cmsLoginPage = new CMSLoginPage(page);

		await cmsLoginPage.goto();

		slideDotLabels = await cmsLoginPage.getSlideDotLabels();
		slideTitles = await cmsLoginPage.getSlideTitles();
	});

	test(
		'shows the CMS welcome message and the sign in form',
		{tag: '@LPD-95488'},
		async () => {
			await expect(cmsLoginPage.welcomeHeading).toBeVisible();

			await expect(cmsLoginPage.emailAddressInput).toBeVisible();
			await expect(cmsLoginPage.passwordInput).toBeVisible();
		}
	);

	test(
		'shows a login carousel with a slide and a dot for every highlight',
		{tag: '@LPD-95488'},
		async ({page}) => {

			// Pause autoplay so the asserted slide does not advance mid-check

			await cmsLoginPage.pauseButton.click();

			// Every highlight is reachable through its navigation dot

			for (const label of slideDotLabels) {
				await expect(cmsLoginPage.dot(label)).toBeVisible();
			}

			// Only the active slide is exposed to assistive technology

			await expect(page.getByRole('heading', {level: 2})).toHaveText(
				slideTitles[0]
			);

			await expect(cmsLoginPage.dot(slideDotLabels[0])).toHaveAttribute(
				'aria-current',
				'true'
			);
		}
	);

	test(
		'toggles between pausing and resuming the carousel autoplay',
		{tag: '@LPD-95488'},
		async () => {
			await expect(cmsLoginPage.pauseButton).toBeVisible();

			await cmsLoginPage.pauseButton.click();

			await expect(cmsLoginPage.resumeButton).toBeVisible();

			await cmsLoginPage.resumeButton.click();

			await expect(cmsLoginPage.pauseButton).toBeVisible();
		}
	);

	test(
		'navigates to a slide when its dot is clicked',
		{tag: '@LPD-95488'},
		async ({page}) => {
			await cmsLoginPage.pauseButton.click();

			await cmsLoginPage.dot(slideDotLabels[2]).click();

			await expect(page.getByRole('heading', {level: 2})).toHaveText(
				slideTitles[2]
			);

			await expect(cmsLoginPage.dot(slideDotLabels[2])).toHaveAttribute(
				'aria-current',
				'true'
			);
		}
	);
});
