/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

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

test(
	'A custom Event structure with a workflow goes through the reject/edit/approve lifecycle and the approved content renders on a mapped page for GUEST and USER',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.b']},
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
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const numericValue = 100000 + (getRandomInt() % 800000);
		const approvedBody = `Body ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a custom Event structure (localizable Body text + Numeric)', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: false,
					spaces: [spaceName],
				});

				await structureBuilderPage.addField('Text');
				await structureBuilderPage.selectFields([{label: 'Text'}]);
				await structureBuilderPage.changeFieldSettings({
					label: 'Body',
					localizable: true,
				});

				await structureBuilderPage.addField('Numeric');

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

		const entityLabel = `${objectDefinition.pluralLabel.en_US} (CMS)`;

		const bodyField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Body'
		);

		const numericField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Numeric'
		);

		if (!bodyField || !numericField) {
			throw new Error(
				'Body or Numeric field not found in object definition'
			);
		}

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			objectDefinition.label.en_US
		);

		try {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					[bodyField.name]: bodyValue,
					[numericField.name]: numericValue,
					[objectDefinition.titleObjectFieldName]: titleValue,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
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
					['VIEW'],
					companyId,
					String(space.siteId),
					objectDefinition.className,
					String(entry.id),
					String(role.roleId)
				);
			}

			await test.step('Reviewer rejects, author edits and resubmits, reviewer approves', async () => {
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
					{[bodyField.name]: approvedBody},
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
				html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><p><lfr-editable id="body" type="text">Body</lfr-editable></p></div>`,
				pageEditorPage,
				site,
			});

			await test.step('Map the approved Title and Body into the page fragment and publish', async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: entityLabel,
						entry: titleValue,
						field: 'Title',
					},
				});

				await pageEditorPage.selectEditable(fragmentId, 'body');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: entityLabel,
						entry: titleValue,
						field: 'Body',
					},
				});

				await pageEditorPage.publishPage();
			});

			await test.step('GUEST sees the approved content', async () => {
				const guestContext = await browser.newContext();

				const guestPage = await guestContext.newPage();

				try {
					await expect(async () => {
						await guestPage.goto(viewUrl);

						await expect(
							guestPage.getByText(titleValue, {exact: true})
						).toBeVisible({timeout: 10000});

						await expect(
							guestPage.getByText('Body', {exact: false})
						).toBeVisible({timeout: 10000});
					}).toPass({timeout: 120000});
				}
				finally {
					await guestContext.close();
				}
			});

			await test.step('USER sees the approved content', async () => {
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

						await expect(
							userPage.getByText('Body', {exact: false})
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
