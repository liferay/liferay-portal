/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {workflowPagesTest} from '../../../../fixtures/workflowPagesTest';
import {addMappingFragment} from '../../../../utils/addMappingFragment';
import {
	assignWorkflowToStructure,
	unassignWorkflowFromStructure,
} from '../../../../utils/cmsWorkflow';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest,
	workflowPagesTest
);

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

const updatedImageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_medium_tall_600x800.jpg')
).toString('base64');

test(
	'A custom File structure with a workflow goes through the reject/edit/approve lifecycle and the approved file renders on a mapped page for GUEST and USER',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.c']},
	async ({
		apiHelpers,
		browser,
		configurationTabPage,
		page,
		pageEditorPage,
		site,
		structureBuilderPage,
		workflowTasksPage,
	}) => {
		test.setTimeout(600000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Doc ${getRandomString()}`;
		const structureName = `Doc${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a custom File structure (Title + File)', async () =>
				structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: true,
					spaces: [spaceName],
					type: 'file',
				}));

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const entityLabel = `${objectDefinition.pluralLabel.en_US} (CMS)`;

		const fileField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'File'
		);

		if (!fileField) {
			throw new Error('File field not found in object definition');
		}

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			objectDefinition.label.en_US
		);

		try {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					[fileField.name]: {
						fileBase64: imageBase64,
						name: `${getRandomString()}.jpg`,
					},
					[objectDefinition.titleObjectFieldName]: titleValue,
					objectEntryFolderExternalReferenceCode: 'L_FILES',
				},
				applicationName,
				spaceName
			);

			const companyId = String(
				await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
			);

			for (const roleName of ['Guest', 'User']) {
				const role = await apiHelpers.jsonWebServicesRole.getRole(
					companyId,
					roleName
				);

				await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
					['DOWNLOAD_FILE', 'VIEW'],
					companyId,
					String(space.siteId),
					objectDefinition.className,
					String(entry.id),
					String(role.roleId)
				);
			}

			await test.step('Reviewer rejects, author re-uploads and resubmits, reviewer approves', async () => {
				const openTaskRow = async () => {
					await expect(async () => {
						await workflowTasksPage.goToAssignedToMyRoles();

						const taskRows = page
							.getByRole('row')
							.filter({hasText: titleValue});

						await expect(taskRows).toHaveCount(1, {
							timeout: 5000,
						});

						await expect(
							taskRows.filter({hasText: 'Review'})
						).toHaveCount(1, {timeout: 5000});
					}).toPass({timeout: 120000});
				};

				await openTaskRow();
				await workflowTasksPage.assignToMe(titleValue);
				await workflowTasksPage.reject(titleValue);

				await apiHelpers.objectEntry.putObjectEntry(
					{
						[fileField.name]: {
							fileBase64: updatedImageBase64,
							name: `${getRandomString()}.jpg`,
						},
					},
					applicationName,
					entry.id
				);

				await workflowTasksPage.resubmit(titleValue);

				await openTaskRow();
				await workflowTasksPage.assignToMe(titleValue);
				await workflowTasksPage.approve(titleValue);

				const updatedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						applicationName,
						String(entry.id)
					);

				expect(JSON.stringify(updatedEntry.status ?? '')).toMatch(
					/approved|"code":0/i
				);
			});

			const {fragmentId, viewUrl} = await addMappingFragment({
				apiHelpers,
				html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><lfr-editable id="image" type="image"><img alt="" src=""/></lfr-editable></div>`,
				pageEditorPage,
				site,
			});

			await test.step('Map the approved file Title and Preview URL into the page fragment and publish', async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: entityLabel,
						entry: titleValue,
						field: 'Title',
					},
				});

				await pageEditorPage.selectEditable(fragmentId, 'image');

				await page
					.getByLabel('Source Selection')
					.selectOption('Mapping');

				await pageEditorPage.setMappedItem({
					entity: entityLabel,
					entry: titleValue,
					field: 'Preview URL',
				});

				await pageEditorPage.waitForChangesSaved();

				await pageEditorPage.publishPage();
			});

			await test.step('GUEST sees the embedded approved file', async () => {
				const guestContext = await browser.newContext();

				const guestPage = await guestContext.newPage();

				try {
					await expect(async () => {
						await guestPage.goto(viewUrl);

						await expect(
							guestPage.getByText(titleValue, {exact: true})
						).toBeVisible({timeout: 10000});
					}).toPass({timeout: 120000});
				}
				finally {
					await guestContext.close();
				}
			});

			await test.step('USER sees the embedded approved file', async () => {
				const user =
					await apiHelpers.headlessAdminUser.postUserAccount();

				userData[user.alternateName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
				await apiHelpers.jsonWebServicesUser.answerReminderQuery(
					user.id
				);

				await apiHelpers.jsonWebServicesUser.addGroupUsers(
					String(site.id),
					[user.id]
				);

				const userContext = await browser.newContext();

				const userPage = await userContext.newPage();

				try {
					await performLoginViaApi({
						page: userPage,
						screenName: user.alternateName,
					});

					await expect(async () => {
						await userPage.goto(viewUrl);

						await expect(
							userPage.getByText(titleValue, {exact: true})
						).toBeVisible({timeout: 10000});
					}).toPass({timeout: 120000});
				}
				finally {
					await userContext.close();
				}
			});
		}
		finally {
			await unassignWorkflowFromStructure(
				configurationTabPage,
				objectDefinition.label.en_US
			);
		}
	}
);
