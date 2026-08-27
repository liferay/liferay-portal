/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {versionHistoryPagesTest} from '../../../fixtures/versionHistoryPagesTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({'LPD-10622': {enabled: true}}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	versionHistoryPagesTest
);

test(
	'Previews each version with its own content, experiences and languages',
	{tag: '@LPD-103548'},
	async ({apiHelpers, pageEditorPage, site, versionHistoryPage}) => {
		const experience = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			options: {publish: false, type: 'content'},
			title: getRandomString(),
		});

		// Publish a first version with a Button

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment('Basic Components', 'Button');

		await pageEditorPage.publishPage();

		// Publish a second version adding a Heading

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		const headingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'English heading'
		);

		await pageEditorPage.publishPage();

		// Publish a third version translating the Heading and adding an experience

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.switchLanguage('es-ES');

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Titular español'
		);

		await pageEditorPage.switchLanguage('en-US');

		await pageEditorPage.createExperience(experience);

		await pageEditorPage.publishPage();

		// Edit the Heading again and leave it unpublished, so there is a draft

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Draft heading'
		);

		await pageEditorPage.waitForChangesSaved();

		// The current page shows the draft content

		await versionHistoryPage.goto(layout, site.friendlyUrlPath);

		await expect(
			versionHistoryPage.preview.getByText('Draft heading')
		).toBeVisible();

		// The first version only had the Button

		await versionHistoryPage.selectVersion('Version 1');

		await expect(
			versionHistoryPage.preview.getByText('Go Somewhere')
		).toBeVisible();

		await expect(
			versionHistoryPage.preview.getByText('English heading')
		).toBeHidden();

		// The second version shows the Heading as it was published

		await versionHistoryPage.selectVersion('Version 2');

		await expect(
			versionHistoryPage.preview.getByText('English heading')
		).toBeVisible();

		// The third version offers the experience and the translation

		await versionHistoryPage.selectVersion('Version 3');

		await versionHistoryPage.switchExperience(experience);

		await expect(versionHistoryPage.experienceSelector).toContainText(
			experience
		);

		await versionHistoryPage.switchExperience('Default');

		await versionHistoryPage.switchLanguage('es-ES');

		await expect(
			versionHistoryPage.preview.getByText('Titular español')
		).toBeVisible();

		// Going back to a version without that experience falls back to Default

		await versionHistoryPage.selectVersion('Version 2');

		await expect(versionHistoryPage.experienceSelector).toContainText(
			'Default'
		);

		// Restoring asks for confirmation because there is a draft

		await versionHistoryPage.restoreVersion('Version 2');

		// The view reloads and the current page holds the restored content

		await versionHistoryPage.selectVersion('Current Page');

		await expect(
			versionHistoryPage.preview.getByText('English heading')
		).toBeVisible();

		await expect(
			versionHistoryPage.preview.getByText('Draft heading')
		).toBeHidden();
	}
);
