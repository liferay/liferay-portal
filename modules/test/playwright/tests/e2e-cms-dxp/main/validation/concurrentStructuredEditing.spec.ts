/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {addCMSAdministrator} from '../../../../utils/addCMSAdministrator';
import {addMappingFragment} from '../../../../utils/addMappingFragment';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {createRecipient} from '../../../../utils/sharingRecipient';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'When two users concurrently edit different fields of a Structured Content entry, the last published version is served complete',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.m']},
	async ({
		apiHelpers,
		browser,
		contentsPage,
		page,
		pageEditorPage,
		site,
		structureBuilderPage,
	}) => {
		test.setTimeout(300000);

		const editByAHeadline = `Headline by A ${getRandomString()}`;
		const editByBSummary = `Summary by B ${getRandomString()}`;
		const originalHeadline = `Headline ${getRandomString()}`;
		const originalSummary = `Summary ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const structureLabel = `Structure${getRandomString()}`;

		const objectDefinitionId =
			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				page: structureBuilderPage,
				publish: false,
				spaces: [space.name],
			});

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.selectFields([{label: 'Text'}]);
		await structureBuilderPage.changeFieldSettings({label: 'Headline'});

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.selectFields([{label: 'Text'}]);
		await structureBuilderPage.changeFieldSettings({label: 'Summary'});

		await structureBuilderPage.publishStructure();

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const entityLabel = `${objectDefinition.pluralLabel.en_US} (CMS)`;

		const findFieldName = (label: string) => {
			const objectField = objectDefinition.objectFields.find(
				(candidate) =>
					candidate.label && candidate.label.en_US === label
			);

			if (!objectField) {
				throw new Error(
					`${label} field not found in object definition`
				);
			}

			return objectField.name;
		};

		const headlineFieldName = findFieldName('Headline');
		const summaryFieldName = findFieldName('Summary');

		const entryTitle = getRandomString();

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[headlineFieldName]: originalHeadline,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				[summaryFieldName]: originalSummary,
				title: entryTitle,
			},
			applicationName,
			space.name
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			applicationName,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'User'}]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="headline" type="text">Headline</lfr-editable></h1><p><lfr-editable id="summary" type="text">Summary</lfr-editable></p></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map both fields into the page fragment and publish the page', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'headline');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: entityLabel,
						entry: entryTitle,
						field: 'Headline',
					},
				});
			}).toPass({timeout: 60000});

			await pageEditorPage.selectEditable(fragmentId, 'summary');

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					entity: entityLabel,
					entry: entryTitle,
					field: 'Summary',
				},
			});

			await pageEditorPage.publishPage();
		});

		const userB = await addCMSAdministrator(apiHelpers);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			userB.externalReferenceCode
		);

		const contextB = await browser.newContext();
		const pageB = await contextB.newPage();
		const contentsPageB = new ContentsPage(pageB);

		await performLoginViaApi({
			page: pageB,
			screenName: userB.alternateName,
		});

		const editField = async (
			targetPage: Page,
			label: string,
			value: string
		) => {
			const field = targetPage.getByRole('textbox', {
				exact: true,
				name: label,
			});

			await field.fill(value);
			await field.blur();
		};

		await test.step('Both users open the same entry for editing', async () => {
			await contentsPage.goto();
			await contentsPage.editContent(entryTitle);

			await contentsPageB.goto();
			await contentsPageB.editContent(entryTitle);
		});

		await test.step('User A edits the Headline and publishes first', async () => {
			await editField(page, 'Headline', editByAHeadline);

			await contentsPage.saveContent();
		});

		await test.step('User B edits the Summary and publishes second', async () => {
			await editField(pageB, 'Summary', editByBSummary);

			await contentsPageB.saveContent();
		});

		await test.step('USER sees the last published version served complete on the page', async () => {
			const userAccount = await createRecipient(apiHelpers);

			await apiHelpers.jsonWebServicesUser.assignUsersToSite(
				String(site.id),
				String(userAccount.id)
			);

			const userContext = await browser.newContext();
			const userPage = await userContext.newPage();

			try {
				await performLoginViaApi({
					page: userPage,
					screenName: userAccount.alternateName,
				});

				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(editByBSummary, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 120000});

				await expect(
					userPage.getByText(originalHeadline, {exact: true})
				).toBeVisible();

				await expect(
					userPage.getByText(editByAHeadline, {exact: true})
				).toBeHidden();
			}
			finally {
				await userContext.close();
			}
		});

		await contextB.close();
	}
);
