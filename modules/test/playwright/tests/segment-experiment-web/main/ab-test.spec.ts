/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	connectToAnalyticsCloudWithNoSiteSynced,
	syncAnalyticsCloudViaAPI,
} from '../../analytics-settings-web/main/utils/analytics-settings';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {faroConfig} from '../../osb-faro-web/main/faro.config';
import {clickOnLink} from '../../osb-faro-web/main/utils/actions';
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
	loginTest()
);

test(
	'Sync button on empty state should redirect the user to Analytics Cloud when site is not synced',
	{
		tag: '@LPD-34179',
	},
	async ({page}) => {
		await connectToAnalyticsCloudWithNoSiteSynced(page);

		await page.goto(liferayConfig.environment.baseUrl);

		await openABTesSidebar(page);

		await expect(
			page.getByText('Sync to Liferay Analytics Cloud')
		).toBeVisible();

		await expect(
			page.getByText('Do not show me this again')
		).not.toBeVisible();

		const tagA = await page.locator(
			'#_com_liferay_segments_experiment_web_internal_portlet_SegmentsExperimentPortlet_-segments-experiment-root a'
		);

		const href = (await tagA.getAttribute('href')) || '';

		await page.goto(
			href.replace(
				liferayConfig.environment.baseUrl,
				faroConfig.environment.baseUrl
			)
		);

		const title = await page.locator('h1.title');

		expect(await title.textContent()).toBe('Sites');
	}
);

test(
	'Terminated test with no clear winner variant',
	{
		tag: '@LPD-34179',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
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

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.waitForSelector('.segments-experiment-icon');

		await openABTesSidebar(page);

		await page.getByText('Create Test').click();

		await page.getByLabel('Test Name').fill('AB Test');

		await page.getByText('Save').click();

		await page.getByText('Create Variant').click();

		await page.getByLabel('Name').fill('AB Test Variant');

		await page.getByText('Save').click();

		await page.getByText('Review and Run Test').click();

		await page.locator('.modal-item-last').getByText('Run').click();

		await page.locator('.modal-item-last').getByText('OK').click();

		await page.getByText('Terminate Test').click();

		await page.locator('.modal-item-last').getByText('Terminate').click();

		await expect(
			page
				.locator('.alert-warning')
				.getByText(
					'The test has not gathered sufficient data to confidently determine a winner. However, variants can still be published.'
				)
		).toBeVisible();

		const segmentExperimentDetails = await page
			.locator('.segments-experiment-details .c-my-2')
			.all();

		expect(segmentExperimentDetails.length).toBe(3);

		expect(await segmentExperimentDetails[0].textContent()).toBe(
			'Segment:Anyone'
		);
		expect(await segmentExperimentDetails[1].textContent()).toBe(
			'Goal:Bounce Rate'
		);
		expect(await segmentExperimentDetails[2].textContent()).toBe(
			'Confidence Level:95%'
		);

		await clickOnLink({
			baseUrl: faroConfig.environment.baseUrl,
			name: 'View Data in Analytics Cloud',
			page,
		});

		await page.waitForTimeout(3000);

		await expect(page.getByText('Test Was Terminated')).toBeVisible();
		await expect(page.getByText('There is no clear winner.')).toBeVisible();
	}
);

test(
	'AB Test edit modal only persists when the user saves the changes',
	{tag: '@LPS-97882'},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
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

		const originalName = 'AB Test ' + getRandomString();

		await createABTest({name: originalName, page});

		// Create a variant so the AB Test kebab becomes available.

		await createVariant({name: 'V1', page});

		// Open the edit modal, change values, and cancel

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.getByTestId('segments-experiments-drop-down')
				.getByLabel('Show Actions'),
		});

		const updatedName = originalName + ' updated';

		await page.getByLabel('Test Name').fill(updatedName);

		await page.getByLabel('Select Goal').selectOption({label: 'Click'});

		await clickAndExpectToBeHidden({
			target: page.getByLabel('Test Name'),
			trigger: page.locator('.modal-footer').getByText('Cancel'),
		});

		await expect(page.getByText(originalName)).toBeVisible();
		await expect(page.getByText('Bounce Rate')).toBeVisible();

		// Open the edit modal, change values, and close

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.getByTestId('segments-experiments-drop-down')
				.getByLabel('Show Actions'),
		});

		await page.getByLabel('Test Name').fill(updatedName);

		await page.getByLabel('Select Goal').selectOption({label: 'Click'});

		await clickAndExpectToBeHidden({
			target: page.getByLabel('Test Name'),
			trigger: page
				.locator('.modal-header')
				.getByRole('button', {name: 'Close'}),
		});

		await expect(page.getByText(originalName)).toBeVisible();
		await expect(page.getByText('Bounce Rate')).toBeVisible();

		// Open the edit modal, change values, and save

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.getByTestId('segments-experiments-drop-down')
				.getByLabel('Show Actions'),
		});

		await page.getByLabel('Test Name').fill(updatedName);

		await page.getByLabel('Select Goal').selectOption({label: 'Click'});

		await page.locator('.modal-footer').getByText('Save').click();

		await expect(page.getByText(updatedName)).toBeVisible();
		await expect(page.getByText('Click', {exact: true})).toBeVisible();
	}
);

test(
	'Cancelling delete, review, and delete-variant actions preserves the AB Test draft',
	{tag: ['@LPS-97195', '@LPS-97196', '@LPS-99421']},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const abTestName = 'AB Test ' + getRandomString();

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

		await createABTest({name: abTestName, page});

		await createVariant({name: 'V1', page});

		// Cancel the delete-variant action and assert the variant survives

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page
				.locator('table', {hasText: 'V1'})
				.locator('button.dropdown-toggle'),
		});

		await expect(page.getByText('Delete variant?')).toBeVisible();

		await clickAndExpectToBeHidden({
			target: page.getByText('Delete variant?'),
			trigger: page.locator('.modal-footer').getByText('Cancel'),
		});

		await expect(page.locator('[data-title="V1"]')).toBeVisible();

		// Cancel the Review and Run Test modal and assert the button is back

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {
				name: 'Review and Run Test',
			}),
			trigger: page.getByRole('button', {
				name: 'Review and Run Test',
			}),
		});

		await clickAndExpectToBeHidden({
			target: page.getByRole('heading', {
				name: 'Review and Run Test',
			}),
			trigger: page.locator('.modal-footer').getByText('Cancel'),
		});

		await expect(
			page.getByRole('button', {name: 'Review and Run Test'})
		).toBeVisible();

		// Close the Review and Run Test modal and assert the button is back

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {
				name: 'Review and Run Test',
			}),
			trigger: page.getByRole('button', {
				name: 'Review and Run Test',
			}),
		});

		await clickAndExpectToBeHidden({
			target: page.getByRole('heading', {
				name: 'Review and Run Test',
			}),
			trigger: page
				.locator('.modal-header')
				.getByRole('button', {name: 'Close'}),
		});

		await expect(
			page.getByRole('button', {name: 'Review and Run Test'})
		).toBeVisible();

		// Cancel the delete-AB-Test action and assert the draft survives

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page
				.getByTestId('segments-experiments-drop-down')
				.getByRole('button', {name: 'Show Actions'}),
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(abTestName),
			trigger: page.locator('.modal-footer').getByText('Cancel'),
		});
	}
);

test(
	'AB Test by Click goal shows an error when the element ID is invalid',
	{tag: '@LPS-119475'},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-button',
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

		await createABTest({
			goal: 'Click',
			name: 'AB Test ' + getRandomString(),
			page,
		});

		// Type an invalid ID and try to run the test

		await page.locator('#clickableElement').fill('invalidID');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByText('ID was not found.'),
			trigger: page.getByText('Review and Run Test'),
		});
	}
);

test(
	'AB Test cannot be run when all variants have been deleted',
	{tag: '@LPS-86285'},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
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

		// Delete the variant

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page
				.locator('table', {hasText: 'V1'})
				.locator('button.dropdown-toggle'),
		});

		await page.locator('.modal-footer').getByText('Delete').click();

		await waitForAlert(page);

		await expect(
			page.getByText('Create at least one variant to run the test.')
		).toBeVisible();
	}
);

test(
	'AB Test panel shows the empty state image when no tests exist',
	{tag: '@LPS-101167'},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
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

		await expect(
			page.locator('.segments-experiments-empty-state__image')
		).toBeVisible();

		await expect(page.getByText('Create Test')).toBeVisible();
	}
);
