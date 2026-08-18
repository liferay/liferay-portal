/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import performLogin from '../../../utils/performLogin';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {WaitAction} from '../pages/EditClientExtensionsPage';
import {editEditorConfigContributorPageTest} from './fixtures/editEditorConfigContributorPageTest';
import {EditEditorConfigContributorPage} from './pages/EditEditorConfigContributorPage';

const test = mergeTests(
	clientExtensionsPageTest,
	editEditorConfigContributorPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	journalPagesTest,
	virtualInstancesPagesTest
);

test('Create, edit and delete editor config contributor client extension @LPS-186870', async ({
	clientExtensionsPage,
	editEditorConfigContributorPage,
}) => {
	await editEditorConfigContributorPage.goto();

	const sampleName1 = getRandomString();

	await editEditorConfigContributorPage.nameInput.fill(sampleName1);

	await editEditorConfigContributorPage.descriptionContentEditable.isEditable();

	await editEditorConfigContributorPage.descriptionContentEditable.fill(
		'Sample Description'
	);

	await editEditorConfigContributorPage.urlInput.fill(
		'https://www.liferay.com'
	);

	await editEditorConfigContributorPage.portletNamesInput.fill(
		'Sample Portlet Name'
	);

	await editEditorConfigContributorPage.editorNamesInput.fill(
		'Sample Editor Names'
	);

	await editEditorConfigContributorPage.editorConfigKeysInput.fill(
		'Sample Editor Config Keys'
	);

	await editEditorConfigContributorPage.publish(WaitAction.SUCCESS);

	const editEditorConfigContributorPage2 =
		await clientExtensionsPage.editClientExtension(
			sampleName1,
			EditEditorConfigContributorPage
		);

	// Synchronize test to avoid flakiness

	await expect(
		editEditorConfigContributorPage2.descriptionCKEditor
	).toBeVisible();

	const sampleName2 = getRandomString();

	await editEditorConfigContributorPage2.nameInput.fill(sampleName2);

	await editEditorConfigContributorPage2.publish(WaitAction.SUCCESS);

	await editEditorConfigContributorPage2.clientExtensionsPage.deleteClientExtension(
		sampleName2
	);
});

test('Add a toolbar button to a CKEditor, by applying editor config contributor client extension @LPS-186870', async ({
	editEditorConfigContributorPage: newEditorConfigContributorPage,
}) => {
	await newEditorConfigContributorPage.goto();

	await expect(
		newEditorConfigContributorPage.descriptionContentEditable
	).toBeEditable();

	await expect(
		newEditorConfigContributorPage.aiCreatorEditorToolbarButton
	).toBeVisible();
});

test('CKEditor is still usable after deploying Client Extension @LPD-31017', async ({
	editEditorConfigContributorPage: newEditorConfigContributorPage,
	journalEditArticlePage,
	page,
}) => {
	await test.step('Create client extension for rich_text editors', async () => {
		await newEditorConfigContributorPage.goto();

		const nameField = page.getByLabel('Name Required', {exact: true});
		const urlField = page.getByLabel('JavaScript URL Required', {
			exact: true,
		});
		const editorConfigKeyField = page.getByLabel('Editor Config Key', {
			exact: true,
		});

		await nameField.click();
		await nameField.fill(getRandomString());

		await urlField.click();
		await urlField.fill(getRandomString());

		await editorConfigKeyField.click();
		await editorConfigKeyField.fill('rich_text');

		await page.getByRole('button', {name: 'Publish'}).click();
	});

	await test.step('Try to create a new Web Content Article after CX has been applied', async () => {
		await journalEditArticlePage.goto();

		const editorTextBox = page
			.locator('.edit-article-panel')
			.frameLocator('iframe[title="editor"]')
			.getByRole('textbox', {name: 'Content'});

		await editorTextBox.click();
		await editorTextBox.fill('LPD-31017');

		await expect(editorTextBox).toHaveText('LPD-31017');
	});
});

test('Check client extension does not apply to new instances @LPD-63018', async ({
	browser,
	virtualInstancesPage,
}) => {
	const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';
	const virtualInstancePage = await browser.newPage({
		baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`,
	});
	await test.step('Create virtual instance', async () => {
		test.slow();

		await virtualInstancesPage.addNewVirtualInstance(
			DEFAULT_VIRTUAL_INSTANCE_NAME
		);

		await performLogin(
			virtualInstancePage,
			'test',
			'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
				'p_p_state=maximized',
			`@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`
		);
	});

	await test.step('Check toolbar does not appear', async () => {
		await virtualInstancePage.getByRole('link', {name: 'Edit'}).click();

		await virtualInstancePage
			.getByLabel('Search Fragments and Widgets')
			.fill('ckeditor');

		await virtualInstancePage
			.getByRole('menuitem', {name: 'CKEditor 4 Sample'})
			.locator('div')
			.first()
			.click();

		await virtualInstancePage.keyboard.press('ArrowLeft');

		await virtualInstancePage.keyboard.press('Enter');

		await virtualInstancePage.keyboard.press('Enter');

		await virtualInstancePage
			.locator('header')
			.filter({hasText: 'CKEditor 4 Sample'})
			.first()
			.waitFor({state: 'visible'});

		await virtualInstancePage.getByLabel('Publish').click();

		await virtualInstancePage.getByRole('link', {name: 'Alloy'}).click();

		await expect(
			virtualInstancePage.getByText('Alloy Editor')
		).toBeVisible();

		await expect(
			virtualInstancePage.getByTitle('Insert Video')
		).not.toBeInViewport();
	});
});
