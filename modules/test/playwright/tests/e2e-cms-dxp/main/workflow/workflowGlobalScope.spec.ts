/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../../fixtures/workflowPagesTest';
import {
	assignWorkflowToStructure,
	unassignWorkflowFromStructure,
} from '../../../../utils/cmsWorkflow';
import getRandomString from '../../../../utils/getRandomString';

const test = mergeTests(dataApiHelpersTest, loginTest(), workflowPagesTest);

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A workflow assigned globally to Basic Web Content triggers on content created in any Space',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.d']},
	async ({apiHelpers, configurationTabPage}) => {
		test.setTimeout(180000);

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			'Basic Web Content'
		);

		try {
			const spaceNames = [
				`Space ${getRandomString()}`,
				`Space ${getRandomString()}`,
			];

			for (const spaceName of spaceNames) {
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					type: 'Space',
				});
			}

			await test.step('Content created in each Space enters the workflow as Pending', async () => {
				for (const spaceName of spaceNames) {
					const entry = await apiHelpers.objectEntry.postObjectEntry(
						{
							content: `<p>${getRandomString()}</p>`,
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
							title: `Title ${getRandomString()}`,
						},
						APPLICATION_NAME,
						spaceName
					);

					expect(JSON.stringify(entry.status ?? '')).toMatch(
						/pending|"code":1/i
					);
				}
			});
		}
		finally {
			await unassignWorkflowFromStructure(
				configurationTabPage,
				'Basic Web Content'
			);
		}
	}
);
