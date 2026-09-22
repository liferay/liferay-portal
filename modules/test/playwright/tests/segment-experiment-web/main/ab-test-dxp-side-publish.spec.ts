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
import {
	ACPage,
	navigateToACPageViaURL,
} from '../../osb-faro-web/main/utils/navigation';
import {
	createABTest,
	createVariant,
	openABTesSidebar,
	runTest,
	terminateTest,
} from './utils/ab-test';

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
	'Publishing the variant of a terminated AB test applies the variant content and the test can be deleted in Analytics Cloud',
	{tag: '@LRAC-14070'},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		const buttonId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: buttonId,
					key: 'BASIC_COMPONENT-button',
				}),
			]),
			siteId: site.id,
			title: 'My Page',
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		const testName = 'AB Test ' + getRandomString();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.waitForSelector('.segments-experiment-icon');

		await openABTesSidebar(page);

		await createABTest({name: testName, page});

		await createVariant({name: 'Variant name', page});

		// Edit the variant so its button text differs from the control

		await clickAndExpectToBeVisible({
			target: page.getByRole('button', {name: 'Save Variant'}),
			trigger: page.getByLabel('Edit Variant'),
		});

		await pageEditorPage.editTextEditable(buttonId, 'link', 'New text');

		await page.getByRole('button', {name: 'Save Variant'}).click();

		await waitForAlert(page, 'Success:The variant was saved successfully.');

		await runTest(page);

		await terminateTest(page);

		// Publishing the control leaves the original button text on the page

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('button', {name: 'Publish'}),
			trigger: page.getByTestId('publish-button-Control'),
		});

		await waitForAlert(page, 'Success:Control was published successfully.');

		await expect(
			page.getByRole('link', {name: 'Go Somewhere'})
		).toBeVisible();

		// The page can still be edited and published afterwards

		await page.getByRole('link', {name: 'Edit'}).first().click();

		await pageEditorPage.editTextEditable(buttonId, 'link', 'Text test');

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByRole('link', {name: 'Text test'})).toBeVisible();

		// The test can be deleted from the Analytics Cloud Tests list

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByText(testName)).toBeVisible();

		await clickAndExpectToBeVisible({
			target: page.getByRole('button', {name: 'Delete'}),
			trigger: page.getByText(testName),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.getByRole('dialog', {name: 'Deleting Test'})
				.getByRole('button', {name: 'Delete'}),
			trigger: page.getByRole('button', {name: 'Delete'}),
		});

		await waitForAlert(page, 'Success:The test has been deleted.');

		await expect(page.getByText(testName)).not.toBeVisible();
	}
);
