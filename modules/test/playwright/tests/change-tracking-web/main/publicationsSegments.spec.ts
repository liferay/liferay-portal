/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {createSegmentsEntry, editSegmentsEntry} from './utils/segments';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true, system: true},
	}),
	changeTrackingPagesTest,
	pageEditorPagesTest
);

test('Can Review Change For Segments With Localization', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const segmentsEntryName = getRandomString();

	await createSegmentsEntry(apiHelpers, segmentsEntryName);

	const localizedSegmentsEntryName = getRandomString();

	await editSegmentsEntry(
		segmentsEntryName,
		page,
		localizedSegmentsEntryName,
		true
	);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.reviewChange(segmentsEntryName);

	await changeTrackingPage.switchLanguage('es_ES');

	await expect(
		page.getByRole('heading', {name: localizedSegmentsEntryName})
	).toBeVisible();
});

test('Can Publish With Segments And Page Experience', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'content'},
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.createExperience('E1');

	await expect(page.getByLabel('Experience: E1')).toBeVisible();

	await pageEditorPage.editExperienceSegment('E1', 'S1');

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await page.reload();

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await page.locator('.page-editor__experience-selector').click();

	const row = page.locator('.dropdown-menu__experience', {hasText: 'E1'});

	await expect(row).toContainText('AudienceS1');
});
