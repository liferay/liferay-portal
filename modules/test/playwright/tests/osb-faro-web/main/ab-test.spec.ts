/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {syncAnalyticsCloud} from '../../analytics-settings-web/main/utils/analytics-settings';
import {
	assertTerminatedABTest,
	checkEmptyStateOnDXPSide,
	clickOnABTestModalButton,
	createABTest,
	createVariant,
	openABTesSidebar,
} from '../../segment-experiment-web/main/utils/ab-test';
import {checkEmptyStateOnACSide, clickOnActionButton} from './utils/ab-test';
import {ACPage, navigateTo, navigateToACPageViaURL} from './utils/navigation';
import {
	createSitePage,
	navigateToDXPandDeleteSite,
	navigateToSitePage,
} from './utils/portal';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginAnalyticsCloudTest(),
	loginTest(),
	pageEditorPagesTest
);

test(
	'Draft AB test Review button redirects to DXP',
	{
		tag: '@LRAC-14220',
	},
	async ({apiHelpers, page}) => {
		const siteName = getRandomString();

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		const pageTitle = 'MyPage-' + getRandomString();

		await createSitePage({
			apiHelpers,
			pageTitle,
			siteName,
		});

		const channelName = 'My Property - ' + getRandomString();

		const {channel, project} = await syncAnalyticsCloud({
			apiHelpers,
			channelName,
			page,
			siteName,
		});

		await test.step('Go to site page', async () => {
			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await page.waitForSelector('.segments-experiment-icon');
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await openABTesSidebar(page);

			await createABTest({
				name: abTestName,
				page,
			});

			await createVariant({
				name: 'Variant -' + getRandomString(),
				page,
			});
		});

		await test.step('Go to AC test page and click on Review button', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.testPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await navigateTo({
				page,
				pageName: abTestName,
			});

			await clickOnActionButton({name: 'Review', page});
		});

		await test.step('Check Review and Run Test modal is being displayed', async () => {
			const modalHeader = await page.getByRole('heading', {
				name: 'Review and Run Test',
			});

			await expect(modalHeader).toBeVisible();
		});

		await test.step('Delete the property that was used during automation execution', async () => {
			await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
				`[${channel.id}]`,
				project.groupId
			);
		});

		await test.step('delete site on DXP side', async () => {
			await navigateToDXPandDeleteSite({apiHelpers, page, site});
		});
	}
);

test(
	'Draft AB test delete button redirects to DXP',
	{
		tag: '@LRAC-14220',
	},
	async ({apiHelpers, page}) => {
		const siteName = getRandomString();

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		const pageTitle = 'MyPage-' + getRandomString();

		await createSitePage({
			apiHelpers,
			pageTitle,
			siteName,
		});

		const channelName = 'My Property - ' + getRandomString();

		const {channel, project} = await syncAnalyticsCloud({
			apiHelpers,
			channelName,
			page,
			siteName,
		});

		await test.step('Go to site page', async () => {
			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await page.waitForSelector('.segments-experiment-icon');
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await openABTesSidebar(page);

			await createABTest({
				name: abTestName,
				page,
			});

			await createVariant({
				name: 'Variant -' + getRandomString(),
				page,
			});
		});

		await test.step('Go to AC test page and click on Delete button', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.testPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await navigateTo({
				page,
				pageName: abTestName,
			});

			await clickOnActionButton({name: 'Delete', page});
		});

		await clickOnABTestModalButton({buttonName: 'Delete', page});

		await checkEmptyStateOnDXPSide(page);

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await checkEmptyStateOnACSide(page);

		await test.step('Delete the property that was used during automation execution', async () => {
			await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
				`[${channel.id}]`,
				project.groupId
			);
		});

		await test.step('delete site on DXP side', async () => {
			await navigateToDXPandDeleteSite({apiHelpers, page, site});
		});
	}
);

test(
	'Edit Experience with Draft AB Test',
	{
		tag: '@LPS-103334',
	},
	async ({apiHelpers, page, pageEditorPage}) => {
		const siteName = getRandomString();

		await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await createSitePage({
			apiHelpers,
			pageTitle,
			siteName,
		});

		const channelName = 'My Property - ' + getRandomString();

		await syncAnalyticsCloud({
			apiHelpers,
			channelName,
			page,
			siteName,
		});

		await test.step('Go to site page', async () => {
			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await page.waitForSelector('.segments-experiment-icon');
		});

		await test.step('Create a new Experience', async () => {
			await navigateToSitePage({
				layout,
				page,
				pageName: pageTitle,
				siteName,
			});

			await pageEditorPage.createExperience('Experience 1');

			await pageEditorPage.publishPage();
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'Experience 1 Segment: Anyone Inactive',
				}),
				trigger: page.getByLabel('Experience Selector'),
			});

			await openABTesSidebar(page);

			await createABTest({
				name: abTestName,
				page,
			});

			await createVariant({
				name: 'Variant -' + getRandomString(),
				page,
			});
		});

		await test.step('Able to Edit Experience', async () => {
			await navigateToSitePage({
				layout,
				page,
				pageName: pageTitle,
				siteName,
			});

			await pageEditorPage.openExperienceSelector();

			await expect(page.getByLabel('Edit Experience')).toBeVisible();
		});
	}
);

test(
	'Not able to Edit Experience After Terminated Test',
	{
		tag: '@LPS-101341',
	},
	async ({apiHelpers, page, pageEditorPage}) => {
		const siteName = getRandomString();

		await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await createSitePage({
			apiHelpers,
			pageTitle,
			siteName,
		});

		const channelName = 'My Property - ' + getRandomString();

		await syncAnalyticsCloud({
			apiHelpers,
			channelName,
			page,
			siteName,
		});

		await test.step('Create a new Experience', async () => {
			await navigateToSitePage({
				layout,
				page,
				pageName: pageTitle,
				siteName,
			});

			await pageEditorPage.createExperience('Experience 1');

			await pageEditorPage.publishPage();
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'Experience 1 Segment: Anyone Inactive',
				}),
				trigger: page.getByLabel('Experience Selector'),
			});

			await openABTesSidebar(page);

			await createABTest({
				name: abTestName,
				page,
			});

			await createVariant({
				name: 'Variant -' + getRandomString(),
				page,
			});
		});

		await test.step('Run AB Test', async () => {
			const reviewButton = await page.getByText('Review and Run Test');

			await reviewButton.click();

			await page.locator('.modal-footer').getByText('Run').click();

			await expect(page.getByText('Test is now running.')).toBeVisible();

			await page.locator('.modal-footer').getByText('Ok').click();
		});

		await test.step('Terminate test', async () => {
			await page.getByText('Terminate Test').click();

			await clickOnABTestModalButton({buttonName: 'Terminate', page});

			await assertTerminatedABTest(page);
		});

		await test.step('Not able to Edit Experience', async () => {
			await navigateToSitePage({
				layout,
				page,
				pageName: pageTitle,
				siteName,
			});

			await pageEditorPage.openExperienceSelector();

			await expect(page.getByLabel('Edit Experience')).not.toBeVisible();
		});
	}
);

test(
	'Not able to Edit Experience During Running Test',
	{
		tag: '@LPS-103334',
	},
	async ({apiHelpers, page, pageEditorPage}) => {
		const siteName = getRandomString();

		await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await createSitePage({
			apiHelpers,
			pageTitle,
			siteName,
		});

		const channelName = 'My Property - ' + getRandomString();

		await syncAnalyticsCloud({
			apiHelpers,
			channelName,
			page,
			siteName,
		});

		await test.step('Create a new Experience', async () => {
			await navigateToSitePage({
				layout,
				page,
				pageName: pageTitle,
				siteName,
			});

			await pageEditorPage.createExperience('Experience 1');

			await pageEditorPage.publishPage();
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'Experience 1 Segment: Anyone Inactive',
				}),
				trigger: page.getByLabel('Experience Selector'),
			});

			await openABTesSidebar(page);

			await createABTest({
				name: abTestName,
				page,
			});

			await createVariant({
				name: 'Variant -' + getRandomString(),
				page,
			});
		});

		await test.step('Run AB Test', async () => {
			const reviewButton = await page.getByText('Review and Run Test');

			await reviewButton.click();

			await page.locator('.modal-footer').getByText('Run').click();

			await expect(page.getByText('Test is now running.')).toBeVisible();

			await page.locator('.modal-footer').getByText('Ok').click();
		});

		await test.step('Not able to Edit Experience', async () => {
			await navigateToSitePage({
				layout,
				page,
				pageName: pageTitle,
				siteName,
			});

			await pageEditorPage.openExperienceSelector();

			await expect(page.getByLabel('Edit Experience')).not.toBeVisible();
		});
	}
);

test(
	'Terminate button in AC is redirecting to DXP',
	{
		tag: '@LRAC-14220',
	},
	async ({apiHelpers, page}) => {
		const siteName = getRandomString();

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		const pageTitle = 'MyPage-' + getRandomString();

		await createSitePage({
			apiHelpers,
			pageTitle,
			siteName,
		});

		const channelName = 'My Property - ' + getRandomString();

		const {channel, project} = await syncAnalyticsCloud({
			apiHelpers,
			channelName,
			page,
			siteName,
		});

		await test.step('Go to site page', async () => {
			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await page.waitForSelector('.segments-experiment-icon');
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await openABTesSidebar(page);

			await createABTest({
				name: abTestName,
				page,
			});

			await createVariant({
				name: 'Variant -' + getRandomString(),
				page,
			});
		});

		await test.step('Run AB Test', async () => {
			const reviewButton = await page.getByText('Review and Run Test');

			await reviewButton.click();

			await page.locator('.modal-footer').getByText('Run').click();

			await expect(page.getByText('Test is now running.')).toBeVisible();

			await page.locator('.modal-footer').getByText('Ok').click();
		});

		await test.step('Go to AC test page and click on Terminate button', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.testPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await navigateTo({
				page,
				pageName: abTestName,
			});

			await clickOnActionButton({name: 'Terminate', page});
		});

		await test.step('Terminate test', async () => {
			await clickOnABTestModalButton({buttonName: 'Terminate', page});

			await assertTerminatedABTest(page);
		});

		await test.step('Delete the property that was used during automation execution', async () => {
			await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
				`[${channel.id}]`,
				project.groupId
			);
		});

		await test.step('delete site on DXP side', async () => {
			await navigateToDXPandDeleteSite({apiHelpers, page, site});
		});
	}
);
