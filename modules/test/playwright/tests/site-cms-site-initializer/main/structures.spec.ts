/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test(
	'Structure can be deleted without confirmation if it does not have an approved status',
	{tag: '@LPD-51516'},
	async ({apiHelpers, page, structuresPage}) => {
		const objectDefinition =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				status: {code: 2},
			})) as ObjectDefinition;
		const stucctureName = objectDefinition.name;

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: stucctureName,
		});
		await waitForAlert(page, `${stucctureName} was deleted successfully`, {
			type: 'success',
		});

		await expect(structuresPage.getItem(stucctureName)).toBeHidden();
	}
);

test(
	'Structure can be deleted after manual confirmation if it has an approved status',
	{tag: '@LPD-51516'},
	async ({apiHelpers, page, structuresPage}) => {
		const objectDefinition =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				status: {code: 0},
			})) as ObjectDefinition;
		const stucctureName = objectDefinition.name;

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: stucctureName,
		});

		await page
			.getByPlaceholder('Confirm Structure Name')
			.fill(stucctureName);
		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page, `${stucctureName} was deleted successfully`, {
			type: 'success',
		});

		await expect(structuresPage.getItem(stucctureName)).toBeHidden();
	}
);

test(
	'Structures cannot be deleted if they have a relation',
	{tag: '@LPD-51516'},
	async ({apiHelpers, page, structuresPage}) => {
		const objectDefinition1 =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				status: {code: 0},
			})) as ObjectDefinition;
		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const objectDefinition2 =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				status: {code: 0},
			})) as ObjectDefinition;
		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

		const objectRelationshipApiClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipApiClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					label: {
						en_US: objectRelationshipLabel,
					},
					name: objectRelationshipName,
					objectDefinitionExternalReferenceCode1:
						objectDefinition1.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId1: objectDefinition1.id,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);
		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: objectDefinition1.name,
		});
		await expect(
			page.getByRole('heading', {name: 'Deletion Not Allowed'})
		).toBeVisible();
		await page.getByRole('button', {name: 'OK'}).click();

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: objectDefinition2.name,
		});
		await expect(
			page.getByRole('heading', {name: 'Deletion Not Allowed'})
		).toBeVisible();
	}
);

test(
	'Some actions are only allowed to non-system structures',
	{tag: '@LPD-51405'},
	async ({apiHelpers, page, structuresPage}) => {
		await structuresPage.goto();

		await page
			.getByRole('row', {name: 'Basic Document'})
			.locator('.dropdown-toggle')
			.click();

		expect(
			page.getByRole('menuitem', {exact: true, name: 'View Usages'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Make a Copy'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Permissions'})
		).toBeVisible();

		expect(
			page.getByRole('menuitem', {exact: true, name: 'Edit'})
		).toBeHidden();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Export as JSON'})
		).toBeHidden();
		expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Import and Override',
			})
		).toBeHidden();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Delete'})
		).toBeHidden();

		const objectDefinition =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				status: {code: 0},
			})) as ObjectDefinition;

		await structuresPage.goto();

		await page
			.getByRole('row', {name: objectDefinition.name})
			.locator('.dropdown-toggle')
			.click();

		expect(
			page.getByRole('menuitem', {exact: true, name: 'Edit'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'View Usages'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Make a Copy'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Export as JSON'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Import and Override',
			})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Permissions'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Delete'})
		).toBeVisible();
	}
);
