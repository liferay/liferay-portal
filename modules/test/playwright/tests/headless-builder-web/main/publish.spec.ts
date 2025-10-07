/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {headlessDiscoveryPagesTest} from '../../../fixtures/headlessDiscoveryWebPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {headlessBuilderPagesTest} from './fixtures/headlessBuilderPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	headlessBuilderPagesTest(),
	headlessDiscoveryPagesTest,
	loginTest()
);

test('can get  updated title in response after publish', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const application = await apiHelpers.objectEntry.postObjectEntry(
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

	await headlessBuilderPage.goto();
	await headlessBuilderPage.openApplicationActions(application.title);
	await page.getByRole('menuitem', {name: 'Edit'}).click();

	await applicationPage.applicationTitleTextBox.fill(
		`${application.title} 1`
	);
	await applicationPage.publishButton.click();
	await expect(page.getByText('API application was published')).toBeVisible();

	expect(
		(
			await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
				applicationName: 'headless-builder/applications',
				externalReferenceCode: application.externalReferenceCode,
			})
		).title
	).toEqual(`${application.title} 1`);
});
