/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {WaitAction} from '../pages/EditClientExtensionsPage';
import {ViewClientExtensionPage} from '../pages/ViewClientExtensionPage';
import {editIFramePageTest} from './fixtures/editIFramePageTest';
import {EditIFramePage} from './pages/EditIFramePage';

const test = mergeTests(
	clientExtensionsPageTest,
	editIFramePageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
	}),
	loginTest()
);

const testSample = mergeTests(
	isolatedLayoutTest({publish: false}),
	pageEditorPagesTest,
	loginTest()
);

testSample.describe('Samples', () => {
	const SAMPLES = [
		{
			erc: 'LXC:liferay-sample-iframe-2-baseball',
			name: 'Baseball',
			url: 'https://en.wikipedia.org/wiki/Baseball',
		},
		{
			erc: 'LXC:liferay-sample-iframe-1-counter-app',
			name: 'Counter App',
			url: 'https://arnab-datta.github.io/counter-app',
		},
		{
			erc: 'LXC:liferay-sample-iframe-2-football',
			name: 'Football',
			url: 'https://en.wikipedia.org/wiki/Football',
		},
		{
			erc: 'LXC:liferay-sample-iframe-2-hockey',
			name: 'Hockey',
			url: 'https://en.wikipedia.org/wiki/Hockey',
		},
	];

	for (const sample of SAMPLES) {
		testSample(`${sample.name} is registered`, async ({page}) => {
			const viewClientExtensionPage = new ViewClientExtensionPage(
				page,
				sample.erc
			);

			await viewClientExtensionPage.goto();

			await expect(viewClientExtensionPage.nameInput).toHaveValue(
				sample.name
			);
			await expect(
				viewClientExtensionPage.getInputByLabel('URL')
			).toHaveValue(sample.url);
		});

		testSample(
			`${sample.name} can be added to a page and is rendered`,
			async ({layout, page, pageEditorPage}) => {
				await pageEditorPage.goto(layout);
				await pageEditorPage.addWidget(
					'Client Extensions',
					sample.name
				);
				await pageEditorPage.publishPage();

				await expect(
					page.locator(`iframe[src="${sample.url}"]`)
				).toBeVisible();
			}
		);
	}
});

test(
	'Publishing with invalid field values results in error',
	{tag: '@LPD-75288'},
	async ({editIFramePage, page}) => {
		await test.step('Go to "Add Iframe" page', async () => {
			await editIFramePage.goto();
		});

		await test.step('Name cannot be empty', async () => {
			await editIFramePage.fillRequiredFields();

			await editIFramePage.nameInput.clear();

			await editIFramePage.publish(WaitAction.ERROR);
		});

		await test.step('URL cannot be empty', async () => {
			await editIFramePage.fillRequiredFields();

			await editIFramePage.urlInput.clear();

			await editIFramePage.publish(WaitAction.NONE);

			await expect(
				page.getByText('The URL field is required')
			).toBeVisible();
		});
	}
);

test('Client extension can be created, edited and deleted', async ({
	clientExtensionsPage,
	editIFramePage,
}) => {
	const clientExtensionName = getRandomString();
	const newClientExtensionName = getRandomString();

	await editIFramePage.goto();

	await test.step('Create a new client extension', async () => {
		await editIFramePage.nameInput.fill(clientExtensionName);
		await editIFramePage.descriptionContentEditable.fill(getRandomString());
		await editIFramePage.urlInput.fill(`https://${getRandomString()}`);
		await editIFramePage.friendlyURLMappingInput.fill(getRandomString());
		await editIFramePage.sourceCodeURLInput.fill(getRandomString());

		await editIFramePage.publish(WaitAction.SUCCESS);

		await clientExtensionsPage.goto();

		await expect(
			clientExtensionsPage.getRowByText(clientExtensionName)
		).toBeVisible();
	});

	await test.step('Edit the client extension', async () => {
		await clientExtensionsPage.editClientExtension(
			clientExtensionName,
			EditIFramePage
		);

		const newDescription = getRandomString();
		const newURL = `https://${getRandomString()}`;
		const newFriendlyURLMapping = getRandomString();
		const newSourceCodeUrl = getRandomString();

		await editIFramePage.nameInput.fill(newClientExtensionName);
		await editIFramePage.descriptionContentEditable.fill(newDescription);
		await editIFramePage.urlInput.fill(newURL);
		await editIFramePage.friendlyURLMappingInput.fill(
			newFriendlyURLMapping
		);
		await editIFramePage.sourceCodeURLInput.fill(newSourceCodeUrl);

		await editIFramePage.publish(WaitAction.SUCCESS);

		await clientExtensionsPage.goto();

		await clientExtensionsPage.editClientExtension(
			newClientExtensionName,
			EditIFramePage
		);

		await expect(editIFramePage.nameInput).toHaveValue(
			newClientExtensionName
		);
		await expect(
			editIFramePage.descriptionContentEditable.getByText(newDescription)
		).toBeVisible();
		await expect(editIFramePage.urlInput).toHaveValue(newURL);
		await expect(editIFramePage.friendlyURLMappingInput).toHaveValue(
			newFriendlyURLMapping
		);
		await expect(editIFramePage.sourceCodeURLInput).toHaveValue(
			newSourceCodeUrl
		);
	});

	await test.step('Delete the client extension', async () => {
		await clientExtensionsPage.goto();

		await clientExtensionsPage.deleteClientExtension(
			newClientExtensionName
		);

		await expect(
			clientExtensionsPage.getRowByText(newClientExtensionName)
		).not.toBeVisible();
	});
});
