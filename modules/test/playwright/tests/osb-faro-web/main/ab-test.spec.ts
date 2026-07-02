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
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
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

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest(),
	pageEditorPagesTest
);

test(
	'Draft AB test Review button redirects to DXP',
	{
		tag: '@LRAC-14220',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		await test.step('Go to site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
			const modalHeader = page.getByRole('heading', {
				name: 'Review and Run Test',
			});

			await expect(modalHeader).toBeVisible();
		});
	}
);

test(
	'Draft AB test delete button redirects to DXP',
	{
		tag: '@LRAC-14220',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		await test.step('Go to site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
	}
);

test(
	'Edit Experience with Draft AB Test',
	{
		tag: '@LPS-103334',
	},
	async ({
		analyticsChannel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel: analyticsChannel,
			project,
			siteId: Number(site.id),
		});

		await test.step('Go to site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page.waitForSelector('.segments-experiment-icon');
		});

		await test.step('Create a new Experience', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
			);

			await pageEditorPage.createExperience('Experience 1');

			await pageEditorPage.publishPage();
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
			);

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
	async ({
		analyticsChannel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel: analyticsChannel,
			project,
			siteId: Number(site.id),
		});

		await test.step('Create a new Experience', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
			);

			await pageEditorPage.createExperience('Experience 1');

			await pageEditorPage.publishPage();
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
			const reviewButton = page.getByText('Review and Run Test');

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
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
			);

			await pageEditorPage.openExperienceSelector();

			await expect(page.getByLabel('Edit Experience')).not.toBeVisible();
		});
	}
);

test(
	'Not able to Edit Experience During Running Test',
	{
		tag: ['@LPS-101341', '@LPS-103334'],
	},
	async ({
		analyticsChannel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel: analyticsChannel,
			project,
			siteId: Number(site.id),
		});

		await test.step('Create a new Experience', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
			);

			await pageEditorPage.createExperience('Experience 1');

			await pageEditorPage.publishPage();
		});

		const abTestName = 'AB Test -' + getRandomString();

		await test.step('Create a new AB Test with a variant', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
			const reviewButton = page.getByText('Review and Run Test');

			await reviewButton.click();

			await page.locator('.modal-footer').getByText('Run').click();

			await expect(page.getByText('Test is now running.')).toBeVisible();

			await page.locator('.modal-footer').getByText('Ok').click();
		});

		await test.step('Not able to Edit Experience', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
			);

			await pageEditorPage.openExperienceSelector();

			await expect(page.getByLabel('Edit Experience')).not.toBeVisible();

			await expect(page.locator('.lexicon-icon-test')).toBeVisible();

			await page.locator('.lexicon-icon-lock').click();

			await expect(
				page.getByText(
					'Edit is not allowed for this experience because there is an A/B test in progress.'
				)
			).toBeVisible();
		});
	}
);

test(
	'Terminate button in AC is redirecting to DXP',
	{
		tag: '@LRAC-14220',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const pageTitle = 'MyPage-' + getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		await test.step('Go to site page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
			const reviewButton = page.getByText('Review and Run Test');

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
	}
);

test(
	'Validate if the variant for AB Test that was added on Liferay portal is visible in the Analytics Cloud',
	{tag: '@LPS-97195'},
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

		// Create an AB Test draft with a variant in DXP

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.waitForSelector('.segments-experiment-icon');

		await openABTesSidebar(page);

		const abTestName = 'AB Test ' + getRandomString();

		await createABTest({name: abTestName, page});

		const variantName = 'Variant ' + getRandomString();

		await createVariant({name: variantName, page});

		// Run AB Test

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator('.modal-footer').getByText('Run'),
			trigger: page.getByText('Review and Run Test'),
		});

		await expect(page.getByText('Test is now running.')).toBeVisible();

		await page.locator('.modal-footer').getByText('Ok').click();

		// Open the AB Test in Analytics Cloud and assert the variant is listed

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(async () => {
			await expect(
				page.getByRole('link', {name: abTestName}).first()
			).toBeVisible({timeout: 3000});

			await page.reload();
		}).toPass();

		await navigateTo({page, pageName: abTestName});

		await expect(
			page
				.locator('.analytics-variant-card-table')
				.getByText(variantName, {exact: true})
		).toBeVisible();
	}
);

test(
	'AB Test status notifications go only to the creator of the test',
	{tag: '@LPS-96787'},
	async ({analyticsChannel, apiHelpers, page, project, site}) => {
		const layout1 = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		const layout2 = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel: analyticsChannel,
			project,
			siteId: Number(site.id),
		});

		// Create a second user with the minimum permissions to terminate the AB Test

		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'AB Test Terminate ' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['UPDATE'],
					primaryKey: String(companyId),
					resourceName: 'com.liferay.portal.kernel.model.Layout',
					scope: 1,
				},
			],
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		// Self-termination must not generate a notification for the admin

		await page.goto(
			`/web${site.friendlyUrlPath}${layout1.friendlyUrlPath}`
		);

		await openABTesSidebar(page);

		const selfABTestName = 'Self AB Test ' + getRandomString();

		await createABTest({name: selfABTestName, page});

		await createVariant({name: 'V1', page});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator('.modal-footer').getByText('Run'),
			trigger: page.getByText('Review and Run Test'),
		});

		await expect(page.getByText('Test is now running.')).toBeVisible();

		await clickAndExpectToBeHidden({
			target: page.getByRole('heading', {
				name: 'Test Started Successfully',
			}),
			trigger: page.locator('.modal-footer').getByText('Ok'),
		});

		await page.getByText('Terminate Test').click();

		await clickOnABTestModalButton({buttonName: 'Terminate', page});

		await assertTerminatedABTest(page);

		await expect(page.getByLabel('New Notification')).not.toBeVisible();

		// Cross-user termination must generate a notification for the admin

		await page.goto(
			`/web${site.friendlyUrlPath}${layout2.friendlyUrlPath}`
		);

		await openABTesSidebar(page);

		const crossABTestName = 'Cross AB Test ' + getRandomString();

		await createABTest({name: crossABTestName, page});

		await createVariant({name: 'V1', page});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator('.modal-footer').getByText('Run'),
			trigger: page.getByText('Review and Run Test'),
		});

		await expect(page.getByText('Test is now running.')).toBeVisible();

		await clickAndExpectToBeHidden({
			target: page.getByRole('heading', {
				name: 'Test Started Successfully',
			}),
			trigger: page.locator('.modal-footer').getByText('Ok'),
		});

		// Sign in as the secondary user and terminate the running test

		await performUserSwitch(page, user.alternateName);

		await page.goto(
			`/web${site.friendlyUrlPath}${layout2.friendlyUrlPath}`
		);

		await openABTesSidebar(page);

		await page.getByText('Terminate Test').click();

		await clickOnABTestModalButton({buttonName: 'Terminate', page});

		await assertTerminatedABTest(page);

		// Sign back in as the admin

		await performUserSwitch(page, 'test');

		await page.goto(
			`/web${site.friendlyUrlPath}${layout2.friendlyUrlPath}`
		);

		await openABTesSidebar(page);

		// Click the notification and confirm the page opens with the AB Test panel

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('link', {
				name: 'A/B test has changed to status terminated.',
			}),
			trigger: page.getByLabel('New Notification'),
		});

		await expect(page).toHaveURL(/segmentsExperimentKey=/);

		await expect(page.locator('#segmentsExperimentSidebar')).toBeVisible();
	}
);

test(
	'Create, terminate, and delete an AB Test from the AC tests list',
	{tag: ['@LRAC-11512', '@LRAC-11513']},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: 'MyPage-' + getRandomString(),
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		const abTestName = 'AB Test -' + getRandomString();

		// Create and run an AB Test with a variant

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.waitForSelector('.segments-experiment-icon');

		await openABTesSidebar(page);

		await createABTest({name: abTestName, page});

		await createVariant({name: 'Variant -' + getRandomString(), page});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator('.modal-footer').getByText('Run'),
			trigger: page.getByText('Review and Run Test'),
		});

		await expect(page.getByText('Test is now running.')).toBeVisible();

		await page.locator('.modal-footer').getByText('Ok').click();

		// The test appears in the AC tests list with RUNNING status (LRAC-11512)

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(async () => {
			await expect(
				page.getByRole('link', {name: abTestName}).first()
			).toBeVisible({timeout: 3000});

			await page.reload();
		}).toPass();

		await expect(page.getByText('RUNNING').first()).toBeVisible();

		// Terminate the test from AC; the action redirects to DXP and confirms

		await navigateTo({page, pageName: abTestName});

		await clickOnActionButton({name: 'Terminate', page});

		await clickOnABTestModalButton({buttonName: 'Terminate', page});

		await assertTerminatedABTest(page);

		// The AC list reflects the TERMINATED status

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByText('TERMINATED').first()).toBeVisible();

		// Delete the test from AC; verify empty state on both ends (LRAC-11513)

		await navigateTo({page, pageName: abTestName});

		await clickOnActionButton({name: 'Delete', page});

		await clickOnABTestModalButton({buttonName: 'Delete', page});

		await checkEmptyStateOnDXPSide(page);

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await checkEmptyStateOnACSide(page);
	}
);
