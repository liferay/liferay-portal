/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {getAsGuest} from './getAsGuest';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	structureBuilderPagesTest
);

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'An anonymous client can read a published Structured Content entry with all field values through the headless API',
	{tag: ['@LPD-95541', '@LPD-95541/TC-18.b']},
	async ({apiHelpers, browser, site, structureBuilderPage}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Article ${getRandomString()}`;
		const structureName = `Article${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const fileName = `sample_${getRandomString()}.jpg`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		const objectDefinitionId =
			await test.step('Build a custom structure with Rich Text and Upload fields', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: false,
					spaces: [spaceName],
				});

				await structureBuilderPage.addField('Rich Text');
				await structureBuilderPage.selectFields([{label: 'Rich Text'}]);
				await structureBuilderPage.changeFieldSettings({label: 'Body'});

				await structureBuilderPage.addField('Upload');

				await structureBuilderPage.publishStructure();

				return id;
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const bodyField = objectDefinition.objectFields.find(
			(objectField) => objectField.label?.en_US === 'Body'
		);

		const uploadField = objectDefinition.objectFields.find(
			(objectField) => objectField.label?.en_US === 'Upload'
		);

		if (!bodyField || !uploadField) {
			throw new Error(
				'Body or Upload field not found in object definition'
			);
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[bodyField.name]: `<p>${bodyValue}</p>`,
				[uploadField.name]: {
					fileBase64: imageBase64,
					name: fileName,
				},
				[objectDefinition.titleObjectFieldName]: titleValue,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			applicationName,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		const {body, status} = await getAsGuest(
			browser,
			`/o/${applicationName}/${entry.id}`
		);

		expect(status).toBe(200);
		expect(body?.title).toBe(titleValue);
		expect(String(body?.[bodyField.name])).toContain(bodyValue);

		const uploadValue = body?.[uploadField.name] as {
			fileURL?: string;
			name?: string;
		};

		expect(uploadValue?.name).toBe(fileName);
		expect(uploadValue?.fileURL).toBeTruthy();

		const downloadStatus = await getAsGuest(
			browser,
			String(uploadValue?.fileURL)
		);

		expect(downloadStatus.status).toBe(200);
	}
);
