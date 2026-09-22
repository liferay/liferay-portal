/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {createABTest, createVariant, openABTesSidebar} from './utils/ab-test';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest(),
	pageEditorPagesTest
);

test(
	'AB Test variant content can be saved and discarded from the variant editor',
	{tag: '@LPS-99349'},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await openABTesSidebar(page);

		await createABTest({name: 'AB Test ' + getRandomString(), page});

		await createVariant({name: 'V1', page});

		// Switch to the variant editor, add a fragment, and save

		await clickAndExpectToBeVisible({
			target: page.getByRole('button', {name: 'Save Variant'}),
			trigger: page.getByLabel('Edit Variant'),
		});

		await pageEditorPage.addFragment('Basic Components', 'Button');

		await page.getByRole('button', {name: 'Save Variant'}).click();

		await waitForAlert(page, 'Success:The variant was saved successfully.');

		await clickAndExpectToBeVisible({
			target: page.getByRole('link', {name: 'Go Somewhere'}),
			trigger: page.locator(`[data-title="V1"]`),
		});

		// Switch to the variant editor, add another fragment, and discard

		await clickAndExpectToBeVisible({
			target: page.getByRole('button', {name: 'Save Variant'}),
			trigger: page.getByLabel('Edit Variant'),
		});

		await pageEditorPage.addFragment('Basic Components', 'Button');

		page.on('dialog', (dialog) => dialog.accept());

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator(`[data-title="V1"]`),
			trigger: page.getByRole('button', {name: 'Discard Variant'}),
		});

		// Switch back to the variant view and assert the second fragment is gone

		await expect(
			page.getByRole('link', {name: 'Go Somewhere'})
		).toHaveCount(1);
	}
);

test(
	'AB Test variant can be edited via the top toolbar pencil icon',
	{tag: '@LPS-146003'},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		const headingId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: headingId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await openABTesSidebar(page);

		await createABTest({name: 'AB Test ' + getRandomString(), page});

		await createVariant({name: 'V1', page});

		// Switch to the variant view and open the editor via the top toolbar pencil icon

		await expect(async () => {
			await page.locator(`[data-title="V1"]`).click();

			await expect(
				page.locator('tr').filter({hasText: 'V1'})
			).toHaveClass(/table-active/, {timeout: 3000});
		}).toPass();

		await clickAndExpectToBeVisible({
			target: page.getByRole('button', {name: 'Save Variant'}),
			trigger: page.getByRole('link', {name: 'Edit'}),
		});

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'New text'
		);

		await page.getByRole('button', {name: 'Save Variant'}).click();

		await waitForAlert(page, 'Success:The variant was saved successfully.');

		// Switch back to the variant view and assert the new text

		await clickAndExpectToBeVisible({
			target: page.getByText('New text'),
			trigger: page.locator(`[data-title="V1"]`),
		});
	}
);
