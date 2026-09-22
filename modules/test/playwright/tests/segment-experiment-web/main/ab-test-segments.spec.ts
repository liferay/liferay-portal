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
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import {
	assertTerminatedABTest,
	createABTest,
	createVariant,
	openABTesSidebar,
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

const criteria: Segment = {
	criteria: {
		user: {
			conjunction: 'and',
			filterString: `(emailAddress eq 'test@liferay.com')`,
			typeValue: 'model',
		},
	},
	filterString: {
		model: `(emailAddress eq 'test@liferay.com')`,
	},
};

test(
	'Segment can be reassigned and renamed while the AB Test is draft, and renamed but not deleted once terminated',
	{tag: '@LPS-99225'},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		pageEditorPage,
		project,
		site,
	}) => {
		test.setTimeout(150000);

		const segmentName = 'EmailAddress Segment';

		await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
			criteria,
			groupId: site.id,
			name: segmentName,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'My Page',
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		// Create an experience and assign the custom segment to it

		await page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
		);

		await pageEditorPage.createExperience('E1');

		await pageEditorPage.editExperienceSegment('E1', segmentName);

		await pageEditorPage.publishPage();

		// Create a draft AB Test on the experience

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: `E1 Segment: ${segmentName} Inactive`,
			}),
			trigger: page.getByLabel('Experience Selector'),
		});

		await openABTesSidebar(page);

		await createABTest({name: 'AB Test ' + getRandomString(), page});

		await createVariant({name: 'Variant ' + getRandomString(), page});

		// Reassign the segment while the AB Test is draft

		await page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?p_l_mode=edit`
		);

		await pageEditorPage.editExperienceSegment('E1', 'Anyone');

		// Rename the segment via the Segments admin while the AB Test is draft

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.segments}`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page.getByLabel(`Show More Options for ${segmentName}`),
		});

		const segmentRenamed = segmentName + ' (renamed)';

		await fillAndClickOutside(
			page,
			page.getByPlaceholder('Untitled Segment'),
			segmentRenamed
		);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.segments}`
		);

		await expect(page.getByText(segmentRenamed)).toBeVisible();

		// Run and terminate the AB Test

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: `E1 Segment: ${segmentRenamed} Inactive`,
			}),
			trigger: page.getByLabel('Experience Selector'),
		});

		await openABTesSidebar(page);

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

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('button', {name: 'Terminate'}),
			trigger: page.getByText('Terminate Test'),
		});

		await assertTerminatedABTest(page);

		// Rename the segment again now that the AB Test is terminated

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.segments}`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page.getByLabel(`Show More Options for ${segmentRenamed}`),
		});

		const segmentRenamedTwice = segmentName + ' (renamed twice)';

		await fillAndClickOutside(
			page,
			page.getByPlaceholder('Untitled Segment'),
			segmentRenamedTwice
		);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.segments}`
		);

		await expect(page.getByText(segmentRenamedTwice)).toBeVisible();

		// Deleting the segment fails because it is required by an experience

		page.on('dialog', (dialog) => dialog.accept());

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page.getByLabel(
				`Show More Options for ${segmentRenamedTwice}`
			),
		});

		await expect(
			page.getByText(
				'The segment cannot be deleted because it is required by one or more experiences.'
			)
		).toBeVisible();
	}
);
