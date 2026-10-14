/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {headlessDiscoveryPagesTest} from '../../../fixtures/headlessDiscoveryWebPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import getRandomString from '../../../utils/getRandomString';
import {headlessBuilderPagesTest} from './fixtures/headlessBuilderPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	headlessBuilderPagesTest({}),
	headlessDiscoveryPagesTest,
	loginTest(),
	uiElementsPageTest
);

test.describe('Headless Builder - API Application', () => {
	let application: any;

	test.beforeEach(async ({apiHelpers, headlessBuilderPage}) => {
		application = await apiHelpers.objectEntry.postObjectEntry(
			{
				apiApplicationToAPISchemas: [
					{
						description: 'API Application Schema',
						externalReferenceCode: 'api-application-schema',
						mainObjectDefinitionERC: 'L_API_APPLICATION',
						name: 'API Application Schema',
					},
				],
				applicationStatus: 'unpublished',
				baseURL: 'basic-application',
				description: 'Test API Application',
				externalReferenceCode: 'basic-application',
				title: 'Basic application',
			},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.openApplicationAndEdit(application.title);
	});

	test('Can get updated title in response after publish', async ({
		apiHelpers,
		applicationPage,
		page,
	}) => {
		await applicationPage.applicationTitleTextBox.fill(
			`${application.title} 1`
		);
		await applicationPage.publishButton.click();

		await expect(
			page.getByText('API application was published')
		).toBeVisible();
		await expect(
			page.getByText('API application was published')
		).not.toBeVisible();

		const updatedApp =
			await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
				applicationName: 'headless-builder/applications',
				externalReferenceCode: application.externalReferenceCode,
			});

		expect(updatedApp.applicationStatus.key).toEqual('published');
		expect(updatedApp.title).toEqual(`${application.title} 1`);
	});

	test('Can see cancel and publish buttons enabled after publish application', async ({
		applicationPage,
		page,
		uiElementsPage,
	}) => {
		await applicationPage.publishButton.click();

		await expect(
			page.getByText('API application was published')
		).toBeVisible();
		await expect(
			page.getByText('API application was published')
		).not.toBeVisible();

		await expect(uiElementsPage.cancelButton).toBeEnabled();

		await expect(applicationPage.publishButton).toBeEnabled();
	});
});

test(
	'Can get unpublished status in response after unpublish',
	{tag: '@LPD-106934'},
	async ({apiHelpers, headlessBuilderPage, page}) => {
		const application = await apiHelpers.objectEntry.postObjectEntry(
			{
				applicationStatus: 'published',
				baseURL: `test-${getRandomString()}`,
				title: getRandomString(),
			},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.goto();
		await headlessBuilderPage.openApplicationActions(application.title);

		await page
			.getByRole('menuitem', {name: 'Change Publication Status'})
			.click();
		await page
			.getByRole('button', {exact: true, name: 'Unpublish'})
			.click();

		await expect(async () => {
			const updatedApplication =
				await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode(
					{
						applicationName: 'headless-builder/applications',
						externalReferenceCode:
							application.externalReferenceCode,
					}
				);

			expect(updatedApplication.applicationStatus.key).toBe(
				'unpublished'
			);
		}).toPass({timeout: 15000});
	}
);
