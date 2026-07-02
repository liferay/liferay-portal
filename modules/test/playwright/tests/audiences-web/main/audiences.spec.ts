/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true, system: true},
		'LPD-86384': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

const criteria: Segment = {
	criteria: {
		context: {
			conjunction: 'and',
			filterString: `(url eq '/pricing')`,
			typeValue: 'context',
		},
	},
	filterString: {
		context: `(url eq '/pricing')`,
	},
};

test(
	'Can validate audiences and segments are not mixed across portlets',
	{
		tag: '@LPD-91094',
	},
	async ({apiHelpers, page, site}) => {
		const audienceName = 'Audience ' + getRandomString();
		const segmentName = 'Segment ' + getRandomString();

		// Seed one segment (default source) and one audience (AUDIENCE source)

		await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
			criteria,
			groupId: site.id,
			name: segmentName,
		});

		await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
			criteria,
			groupId: site.id,
			name: audienceName,
			source: 'AUDIENCE',
		});

		// Audiences portlet shows only the audience

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.audiences}`
		);

		await expect(page.getByText(audienceName)).toBeVisible();
		await expect(page.getByText(segmentName)).toBeHidden();

		// Segments portlet shows only the segment

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.segments}`
		);

		await expect(page.getByText(segmentName)).toBeVisible();
		await expect(page.getByText(audienceName)).toBeHidden();
	}
);

test(
	'Can validate audiences portlet only exposes the Session contributor',
	{
		tag: '@LPD-91094',
	},
	async ({page, site}) => {
		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.audiences}`
		);

		await page.getByRole('link', {name: 'Add New Audience'}).click();

		// Session is exposed and the User/Organization/Segments contributors
		// are hidden in the Audiences portlet

		await expect(page.locator('div#context')).toBeVisible();
		await expect(page.locator('div#user')).toBeHidden();
		await expect(page.locator('div#user-organization')).toBeHidden();
		await expect(page.locator('div#segments')).toBeHidden();
	}
);
