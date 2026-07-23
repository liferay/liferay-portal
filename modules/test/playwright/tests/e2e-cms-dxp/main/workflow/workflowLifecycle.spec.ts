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
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	workflowPagesTest
);

const APPLICATION_NAME = 'cms/basic-web-contents';

const ENTITY = 'Basic Web Contents (CMS)';

test(
	'A Basic Web Content goes through the reject/edit/approve workflow and the approved content renders on a mapped page for GUEST and USER',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.a']},
	async ({
		apiHelpers,
		browser,
		configurationTabPage,
		page,
		pageEditorPage,
		site,
		workflowTasksPage,
	}) => {
		test.setTimeout(600000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const approvedBody = `Body ${getRandomString()}`;

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			'Basic Web Content'
		);

		try {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					type: 'Space',
				});

			await apiHelpers.headlessAssetLibrary.connectSite(
				space.externalReferenceCode,
				site.externalReferenceCode
			);

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					content: `<p>${bodyValue}</p>`,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				APPLICATION_NAME,
				spaceName
			);

			const objectDefinition =
				await apiHelpers.objectAdmin.getObjectDefinitionByName(
					'CMSBasicWebContent'
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
							.filter({hasText: contentTitle});

						await expect(taskRows).toHaveCount(1, {
							timeout: 5000,
						});

						await expect(
							taskRows.filter({hasText: 'Review'})
						).toHaveCount(1, {timeout: 5000});
					}).toPass({timeout: 120000});
				};

				await openTaskRow();
				await workflowTasksPage.assignToMe(contentTitle);
				await workflowTasksPage.reject(contentTitle);

				await apiHelpers.objectEntry.putObjectEntry(
					{content: `<p>${approvedBody}</p>`, title: contentTitle},
					APPLICATION_NAME,
					entry.id
				);

				await workflowTasksPage.resubmit(contentTitle);

				await openTaskRow();
				await workflowTasksPage.assignToMe(contentTitle);
				await workflowTasksPage.approve(contentTitle);

				const updatedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						APPLICATION_NAME,
						String(entry.id)
					);

				expect(JSON.stringify(updatedEntry.status ?? '')).toMatch(
					/approved|"code":0/i
				);
			});

			const {fragmentId, viewUrl} = await addMappingFragment({
				apiHelpers,
				html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
				pageEditorPage,
				site,
			});

			await test.step('Map the approved content into the page fragment and publish', async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: contentTitle,
						field: 'Title',
					},
				});

				await pageEditorPage.selectEditable(fragmentId, 'content');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: contentTitle,
						field: 'Content',
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
							guestPage.getByText(contentTitle, {exact: true})
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
							userPage.getByText(contentTitle, {exact: true})
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
				'Basic Web Content'
			);
		}
	}
);
