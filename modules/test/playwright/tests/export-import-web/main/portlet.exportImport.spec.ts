/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {performLoginViaApi} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {portletExportImportPageTest} from './fixtures/portletExportImportPageTest';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-57655': {enabled: false},
	}),
	globalMenuPagesTest,
	loginTest(),
	objectPagesTest,
	portletExportImportPageTest
);

test('Can import object with different classname via portlet', async ({
	apiHelpers,
	globalMenuPage,
	page,
	portletExportImportPage,
	viewObjectDefinitionsPage,
}) => {
	test.slow();
	let objectDefinition: ObjectDefinition;
	let objectDefinitionsFilePath: string;
	let objectEntriesFilePath: string;

	await test.step('Create and download Object Definition LAR', async () => {
		objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'control_panel.object',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: viewObjectDefinitionsPage.page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Export / Import'}),
			trigger: viewObjectDefinitionsPage.page.getByLabel('Options'),
		});

		({filePath: objectDefinitionsFilePath} =
			await portletExportImportPage.exportLARFile({
				fileNamePattern: /Objects-\d+\.portlet\.lar/,
			}));
	});

	await test.step('Create and download Object Entry LAR', async () => {
		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: objectDefinition.name},
			`${normalizeRestPath(objectDefinition.restContextPath)}`
		);

		await globalMenuPage.goToObjectDefinition(objectDefinition.name);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Export / Import'}),
			trigger: page.getByLabel('Options'),
		});

		({filePath: objectEntriesFilePath} =
			await portletExportImportPage.exportLARFile({
				fileNamePattern: /ObjectDefinition\d+-\d+\.portlet\.lar/,
			}));
	});

	let virtualInstanceApiHelpers: DataApiHelpers;

	await test.step('Create and Configure Virtual Instance (Able)', async () => {
		const virtualInstance =
			await apiHelpers.headlessPortalInstance.addVirtualInstance({
				domain: 'liferay.com',
				portalInstanceId: 'www.able.com',
				virtualHost: 'www.able.com',
			});

		apiHelpers.data.push({
			id: virtualInstance.portalInstanceId,
			type: 'virtual-instance',
		});

		await performLoginViaApi({
			loginUrl: `http://www.able.com:${liferayConfig.environment.port}`,
			page,
			screenName: 'test',
		});

		virtualInstanceApiHelpers = new DataApiHelpers(
			page,
			`http://www.able.com:${liferayConfig.environment.port}`
		);
	});

	await test.step('Object Definition into Virtual Instance', async () => {
		await page.goto(
			`http://www.able.com:${liferayConfig.environment.port}/group/guest${PORTLET_URLS.objects}`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Export / Import'}),
			trigger: page.getByLabel('Options'),
		});

		await portletExportImportPage.importLARFile({
			filePath: objectDefinitionsFilePath,
		});
	});

	await test.step('Import Object Entry into Virtual Instance & Verify', async () => {
		await page.goto(
			`http://www.able.com:${liferayConfig.environment.port}`
		);

		await globalMenuPage.goToObjectDefinition(objectDefinition.name);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Export / Import'}),
			trigger: page.getByLabel('Options'),
		});

		const {totalCount: beforeImportingTotalCount} =
			await virtualInstanceApiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(beforeImportingTotalCount).toBe(0);

		await portletExportImportPage.importLARFile({
			filePath: objectEntriesFilePath,
		});

		const {totalCount: afterImportingTotalCount} =
			await virtualInstanceApiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(afterImportingTotalCount).toBe(1);
	});
});

test(
	'Can import custom company scoped object entry deletions via portlet',
	{tag: '@LPD-82880'},
	async ({apiHelpers, globalMenuPage, page, portletExportImportPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'control_panel.object',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: objectDefinition.name},
			`${normalizeRestPath(objectDefinition.restContextPath)}`
		);

		await apiHelpers.objectEntry.deleteObjectEntry(
			normalizeRestPath(objectDefinition.restContextPath),
			String(objectEntry.id)
		);

		await globalMenuPage.goToObjectDefinition(objectDefinition.name);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Export / Import'}),
			trigger: page.getByLabel('Options'),
		});

		const {filePath: deletionsLARFilePath} =
			await portletExportImportPage.exportLARFile({
				fileNamePattern: /ObjectDefinition\d+-\d+\.portlet\.lar/,
				includeDeletions: true,
			});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				externalReferenceCode: objectEntry.externalReferenceCode,
				textField: objectEntry.textField,
			},
			`${normalizeRestPath(objectDefinition.restContextPath)}`
		);

		await globalMenuPage.goToObjectDefinition(objectDefinition.name);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Export / Import'}),
			trigger: page.getByLabel('Options'),
		});

		await portletExportImportPage.importLARFile({
			filePath: deletionsLARFilePath,
			includeDeletions: true,
		});

		expect(
			await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
				applicationName: `${normalizeRestPath(objectDefinition.restContextPath)}`,
				externalReferenceCode: objectEntry.externalReferenceCode,
			})
		).toEqual({status: 'NOT_FOUND'});
	}
);
