/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Shows the highest-priority experience when multiple match the user segments',
	{
		tag: '@LPS-125710',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a user and register it for performUserSwitch

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		// Create two segments that match the user

		const emailSegment = 'Segment Email Address';
		const screenNameSegment = 'Segment Screen Name';

		await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
			criteria: {
				criteria: {
					user: {
						conjunction: 'and',
						filterString: `(screenName eq '${user.alternateName}')`,
						typeValue: 'model',
					},
				},
				filterString: {
					model: `(screenName eq '${user.alternateName}')`,
				},
			},
			groupId: site.id,
			name: screenNameSegment,
		});

		await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
			criteria: {
				criteria: {
					user: {
						conjunction: 'and',
						filterString: `(emailAddress eq '${user.emailAddress}')`,
						typeValue: 'model',
					},
				},
				filterString: {
					model: `(emailAddress eq '${user.emailAddress}')`,
				},
			},
			groupId: site.id,
			name: emailSegment,
		});

		// Create a page with a Heading fragment and go to edit mode

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

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create the Screen Name experience and prioritize it above Default

		await pageEditorPage.createExperience('Screen Name Experience');

		await waitForAlert(
			page,
			'Success:The experience was created successfully.'
		);

		await pageEditorPage.editExperienceSegment(
			'Screen Name Experience',
			screenNameSegment
		);

		await waitForAlert(
			page,
			'Success:The experience was updated successfully.'
		);

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Screen Name experience text'
		);

		await pageEditorPage.openExperienceSelector();

		await page
			.locator('.dropdown-menu__experience', {
				hasText: 'Screen Name Experience',
			})
			.getByLabel('Prioritize Experience', {exact: true})
			.click();

		await pageEditorPage.closeExperienceSelector();

		// Create the Email Address experience and prioritize it above Default

		await pageEditorPage.createExperience('Email Address Experience');

		await waitForAlert(
			page,
			'Success:The experience was created successfully.'
		);

		await pageEditorPage.editExperienceSegment(
			'Email Address Experience',
			emailSegment
		);

		await waitForAlert(
			page,
			'Success:The experience was updated successfully.'
		);

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Email Address experience text'
		);

		await pageEditorPage.openExperienceSelector();

		await page
			.locator('.dropdown-menu__experience', {
				hasText: 'Email Address Experience',
			})
			.getByLabel('Prioritize Experience', {exact: true})
			.click();

		await pageEditorPage.closeExperienceSelector();

		// Publish and view as the segment-matching user

		await pageEditorPage.publishPage();

		await performUserSwitch(page, user.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(
			page.getByText('Screen Name experience text')
		).toBeVisible();
	}
);

test(
	'Asks for confirmation when canceling segment creation with unsaved changes',
	{
		tag: '@LPS-90588',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Open New Experience editor and start a new segment

		await pageEditorPage.experienceSelector.click();

		await page.getByText('Select Experience').waitFor();
		await page.getByLabel('New Experience').click();

		await page.getByText('New Segment').waitFor();
		await page.getByText('New Segment').click();

		await page.getByText('No Conditions yet').waitFor();

		// Mark the segment dirty so canceling triggers the confirmation

		await page.getByPlaceholder('Untitled Segment').fill('Dirty segment');

		// Accept the unsaved-changes confirm

		page.once('dialog', async (dialog) => {
			expect(dialog.message()).toContain('unsaved changes');

			await dialog.accept();
		});

		await page.getByText('Cancel', {exact: true}).click();

		// Back in the Experience editor with default values

		await expect(
			page.locator('.modal-title', {hasText: 'New Experience'})
		).toBeVisible();

		await expect(page.getByPlaceholder('Experience Name')).toHaveValue('');

		await expect(
			page.getByLabel('Audience').locator(':checked')
		).toHaveText('Anyone');
	}
);
