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
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getGridDefinition from '../../layout-content-page-editor-web/main/utils/getGridDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {
	createABTest,
	createVariant,
	getClickElementId,
	openABTesSidebar,
	selectClickElement,
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
	'AB Test by Click goal supports element selection, ID box interactions, edit target, and variant persistence',
	{tag: ['@LPS-119475', '@LPS-152323', '@LPS-96791']},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const gridDefinition = getGridDefinition({
			columns: [
				{
					pageElements: [
						getFragmentDefinition({
							id: getRandomString(),
							key: 'BASIC_COMPONENT-button',
						}),
					],
					size: 6,
				},
				{
					pageElements: [
						getFragmentDefinition({
							id: getRandomString(),
							key: 'BASIC_COMPONENT-button',
						}),
					],
					size: 6,
				},
			],
			id: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([gridDefinition]),
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

		// Discover the IDs of both buttons via the picker

		const firstButtonId = await selectClickElement({page});

		await expect(page.locator('#clickableElement')).toHaveValue(
			firstButtonId
		);

		// Edit the target via Change Clickable Element

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator(
				'.lfr-segments-experiment-click-goal-target-delete'
			),
			trigger: page.getByText('Change Clickable Element'),
		});

		await clickAndExpectToBeVisible({
			target: page
				.locator('.lfr-segments-experiment-click-goal-target-popover')
				.getByRole('button'),
			trigger: page
				.locator('.lfr-segments-experiment-click-goal-target-overlay')
				.nth(1),
		});

		const secondButtonId = await getClickElementId({page});

		await page
			.locator('.lfr-segments-experiment-click-goal-target-popover')
			.getByRole('button')
			.click();

		await waitForAlert(page);

		await expect(page.locator('#clickableElement')).toHaveValue(
			secondButtonId
		);

		// Variant ID persistence: create variant and switch between control and variant

		await createVariant({name: 'V1', page});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator('[data-title="Control"]'),
			trigger: page.locator('[data-title="V1"]'),
		});

		await expect(page.locator('#clickableElement')).toHaveValue(
			secondButtonId
		);
	}
);

test(
	'AB Test by Click goal warns when the target element is removed via the X button or via the page editor',
	{tag: ['@LPS-104203', '@LPS-145992']},
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

		await selectClickElement({page});

		// Eye button toggles the highlight without changing the target

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator(
				'.lfr-segments-experiment-click-goal-target-overlay'
			),
			trigger: page.getByLabel('Show Element'),
		});

		// Click the X on the target overlay and assert the warning

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByText('An element needs to be selected.'),
			trigger: page.locator(
				'.lfr-segments-experiment-click-goal-target-delete'
			),
		});

		await waitForAlert(page);

		// Re-select the element, then remove the button fragment from the page

		await selectClickElement({page});

		await page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
		);

		await pageEditorPage.removeFragment(buttonId);

		await pageEditorPage.publishPage();

		// Reopen the AB Test panel and assert the warning

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await openABTesSidebar(page);

		await expect(
			page.getByText('An element needs to be selected.')
		).toBeVisible();
	}
);

test(
	'AB Test by Click goal selects submit elements with an ID and ignores submit elements without one',
	{tag: '@LPS-119476'},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		test.setTimeout(150000);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-html',
				}),
			]),
			siteId: site.id,
			title: 'My Page',
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.editHTMLEditable({
			editableId: 'element-html',
			fragmentId: await pageEditorPage.getFragmentId('HTML'),
			value: '<input type="submit" id="customId" /><input type="submit" />',
		});

		await pageEditorPage.publishPage();

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

		// Typing the submit element ID selects it

		await page.locator('#clickableElement').fill('customId');

		await page.locator('#clickableElement').press('Enter');

		await expect(page.getByText('Target', {exact: true})).toBeVisible();

		await expect(page.locator('#clickableElement')).toHaveValue('customId');

		// Submit elements without an ID do not show as selectable

		await clickAndExpectToBeHidden({
			target: page.locator(
				'.lfr-segments-experiment-click-goal-target-overlay-selected'
			),
			trigger: page.locator(
				'.lfr-segments-experiment-click-goal-target-delete'
			),
		});

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.locator(
				'.lfr-segments-experiment-click-goal-target-overlay'
			),
			trigger: page.getByRole('button', {
				name: 'Select Clickable Element',
			}),
		});

		await expect(
			page.locator('.lfr-segments-experiment-click-goal-target-overlay')
		).toHaveCount(1);
	}
);

test(
	'Running a Click goal AB Test disables the Change Clickable Element button and makes the ID box readonly',
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
			title: 'My Page',
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

		await selectClickElement({page});

		await createVariant({name: 'V1', page});

		// Run the test

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('button', {name: 'Run'}),
			trigger: page.getByText('Review and Run Test'),
		});

		await expect(page.getByText('Test is now running.')).toBeVisible();

		await clickAndExpectToBeHidden({
			target: page.getByText('Test is now running.'),
			trigger: page.getByRole('button', {name: 'OK'}),
		});

		// The Change Clickable Element button is gone and the ID box is readonly

		await expect(
			page.getByRole('button', {name: 'Change Clickable Element'})
		).toHaveCount(0);

		await expect(page.locator('#clickableElement')).toHaveAttribute(
			'readonly',
			''
		);
	}
);
