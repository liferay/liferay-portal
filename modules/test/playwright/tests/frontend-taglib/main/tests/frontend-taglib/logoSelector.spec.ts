/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import getRandomString from '../../../../../utils/getRandomString';
import getFragmentDefinition from '../../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {samplePageTest} from '../../fixtures/samplePageTest';
import {TabName} from '../../pages/SamplePage';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	samplePageTest
);

test(
	'Logo selector changes do not affect to every selector in the page',
	{tag: '@LPD-39308'},
	async ({page, samplePage}) => {
		await test.step('Select Logo Selector link', async () => {
			await samplePage.selectTab(TabName.LOGO_SELECTOR);
		});

		await test.step('Open modal to change first logo selector and fire change event', async () => {
			await page.getByLabel('Change First Logo').click();

			await page.evaluate(() => {
				Liferay.fire('changeLogo', {
					tempImageFileName: 'New Logo Name',
				});
			});

			await page
				.frameLocator('iframe[title="Upload First Logo"]')
				.getByRole('button', {name: 'Done'})
				.click();
		});

		await test.step('Check second logo selector has not been changed', async () => {
			const secondInput = page
				.locator('div')
				.filter({hasText: /^Second Logo$/})
				.locator(
					'[id^="_com_liferay_frontend_taglib_sample_web_portlet_SamplePortlet_INSTANCE_"]'
				);

			await expect(secondInput).toHaveValue('Default');
		});
	}
);

test(
	'Logo Selector can be rendered in a fragment',
	{tag: '@LPD-43308'},
	async ({apiHelpers, page, site}) => {
		const fragmentName = getRandomString();

		await test.step('Create a fragment collection with a custom basic fragment', async () => {
			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				fragmentCollectionId,
				groupId: site.id,
				html: '<div class="fragment-name">[@liferay_frontend["logo-selector"] currentLogoURL="/image/user_female_portrait.png" defaultLogoURL="/image/user_female_portrait.png" portletNamespace="com_liferay_image_uploader_web_portlet_ImageUploaderPortlet_"/]</div>',
				name: fragmentName,
			});
		});

		let layout: Layout;

		await test.step('Add fragment to a page', async () => {
			const basicFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: fragmentName,
			});

			layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([basicFragmentDefinition]),
				siteId: site.id,
				title: getRandomString(),
			});
		});

		await test.step('Check that logo selector is available on the page', async () => {
			await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

			const logoSelector = page.getByRole('img', {
				name: 'Current Logo',
			});

			await expect(logoSelector).toBeVisible();
		});
	}
);
