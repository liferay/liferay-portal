/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {documentLibraryPagesTest} from '../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {waitForAlert} from '../../utils/waitForAlert';

const MOCKED_IMAGE_PATH =
	'USER_IMAGES_URL_https://images.freeimages.com/images/large-previews/83f/paris-1213603.jpg';

const test = mergeTests(
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Create AI Image option in Management Toolbar without API Key opens an alert',
	{tag: '@LPD-6717'},
	async ({
		aiCreatorInstanceSettingsPage,
		documentLibraryPage,
		page,
		site,
	}) => {
		await aiCreatorInstanceSettingsPage.enableDalleCreateImages();
		await aiCreatorInstanceSettingsPage.removeApiKey();

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.openCreateAIImage();
		await expect(page.getByText('Configure OpenAI')).toBeVisible();
	}
);

test(
	'Create AI Image option is hidden when disabled from Instance Settings',
	{tag: ['@LPD-6717', '@LPD-6691']},
	async ({
		aiCreatorInstanceSettingsPage,
		documentLibraryPage,
		page,
		site,
	}) => {
		await aiCreatorInstanceSettingsPage.disableDalleCreateImages();

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.openNewButton();
		await expect(
			page.getByRole('menuitem', {name: 'Create AI Image'})
		).not.toBeVisible();

		await aiCreatorInstanceSettingsPage.enableDalleCreateImages();
	}
);

test(
	'Can add images to DM when API Key is provided',
	{tag: '@LPD-6677'},
	async ({
		aiCreatorInstanceSettingsPage,
		documentLibraryPage,
		gogoShellPage,
		page,
		site,
	}) => {
		await gogoShellPage.addCommand(
			'scr:enable com.liferay.ai.creator.openai.web.internal.client.MockAICreatorOpenAIClient'
		);
		await aiCreatorInstanceSettingsPage.addApiKey();

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.openCreateAIImage();
		await expect(page.getByText('Create AI Image')).toBeVisible();

		const createAIImageModalPage = page.frameLocator(
			'iframe[title="Create AI Image"]'
		);
		await createAIImageModalPage
			.getByPlaceholder('Write something...')
			.fill(MOCKED_IMAGE_PATH);
		await createAIImageModalPage
			.getByRole('button', {name: 'Create'})
			.click();
		await createAIImageModalPage.getByRole('checkbox').first().check();
		await createAIImageModalPage
			.getByRole('button', {name: 'Add Selected'})
			.click();
		await waitForAlert(page, 'Success:1 files were successfully added.');
		await expect(
			page.getByRole('link').filter({hasText: 'AI-image-'})
		).toHaveCount(1);

		await aiCreatorInstanceSettingsPage.removeApiKey();
		await gogoShellPage.addCommand(
			'scr:disable com.liferay.ai.creator.openai.web.internal.client.MockAICreatorOpenAIClient'
		);
	}
);
