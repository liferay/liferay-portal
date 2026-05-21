/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {getTempDir} from '../../../utils/temp';
import {waitForAlert} from '../../../utils/waitForAlert';
import {readFileFromZip} from '../../../utils/zip';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const baseTest = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	exportImportPagesTest,
	isolatedSiteTest,
	loginTest(),
	productMenuPageTest,
	uiElementsPageTest,
	masterPagesPagesTest,
	pageTemplatesPagesTest,
	pageEditorPagesTest,
	pagesPagesTest
);

export const test = mergeTests(
	baseTest,
	featureFlagsTest({
		'LPD-35443': {enabled: false},
		'LPD-76864': {enabled: true},
	})
);

test('can export at site level with custom export task name', async ({
	exportImportPage,
}) => {
	await exportImportPage.goToExport();

	const taskName = 'MyExport-' + getRandomString();

	const exportFilePath = await exportImportPage.export({taskName});

	expect(exportFilePath).toMatch(
		new RegExp(`${getTempDir()}${taskName + '.lar'}`)
	);
});

test(
	'cannot export at site level without file name',
	{tag: '@LPD-76875'},
	async ({exportImportPage}) => {
		await exportImportPage.goToExport();

		await exportImportPage.newExportButton.click();

		await exportImportPage.exportButton.click();

		await expect(
			exportImportPage.page.getByRole('alert').filter({
				hasText: 'Please enter a file with a valid file name.',
			})
		).toBeVisible();
	}
);

test(
	'can export twice with the same name without overwriting the original file',
	{tag: '@LPD-76875'},
	async ({apiHelpers, exportImportPage}) => {
		await exportImportPage.goToExport();

		const taskName = 'MyExport-' + getRandomString();

		const exportFilePath1 = await exportImportPage.export({taskName});
		const content1 = await readFileFromZip('manifest.xml', exportFilePath1);

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName = normalizeRestPath(
			objectDefinition.restContextPath
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: objectDefinition.name},
			`${applicationName}/scopes/Guest`
		);

		await exportImportPage.goToExport();

		const exportFilePath2 = await exportImportPage.export({taskName});
		const content2 = await readFileFromZip('manifest.xml', exportFilePath2);

		expect(exportFilePath1).toMatch(
			new RegExp(`${getTempDir()}${taskName + '.lar'}`)
		);
		expect(exportFilePath1).toEqual(exportFilePath2);

		expect(content1).not.toContain(objectDefinition.name);
		expect(content2).toContain(objectDefinition.name);
	}
);

test('can see corresponding elements at site level', async ({
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();
	await productMenuPage.goToPublishingExport();
	await productMenuPage.page
		.getByRole('link', {name: 'Custom Export'})
		.click();

	await expect(
		productMenuPage.page.getByText('Comments, Ratings')
	).toBeVisible();

	await expect(
		productMenuPage.page.getByRole('link', {name: 'Refresh Counts'})
	).toBeVisible();
});

test(
	'can see the Deletions label at the site level',
	{tag: ['@LPD-37317']},
	async ({exportImportPage, productMenuPage, uiElementsPage}) => {
		await productMenuPage.openProductMenuIfClosed();
		await productMenuPage.goToPublishingExport();

		await uiElementsPage.clickNewButton();

		const deletionsLabelText =
			await exportImportPage.deletionsLabel.textContent();

		expect(deletionsLabelText?.replace(/\s+/g, ' ').trim()).toBe(
			'Export Individual Deletions: If this is checked, the delete operations performed will be exported in the LAR file.'
		);
	}
);

test(
	'can see the correct counts of master page templates at site level',
	{tag: ['@LPD-67433']},
	async ({
		exportImportPage,
		masterPagesPage,
		pageTemplatesPage,
		productMenuPage,
		site,
		uiElementsPage,
	}) => {
		await masterPagesPage.goto(site.friendlyUrlPath);
		await masterPagesPage.createNewMaster(getRandomString());
		await masterPagesPage.createNewMaster(getRandomString());

		await pageTemplatesPage.goto(site.friendlyUrlPath);
		await pageTemplatesPage.addPageTemplateCollection(getRandomString());
		await pageTemplatesPage.addWidgetPageTemplate(getRandomString());

		await pageTemplatesPage.goto(site.friendlyUrlPath);
		await pageTemplatesPage.addWidgetPageTemplate(getRandomString());

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		await productMenuPage.openProductMenuIfClosed();
		await productMenuPage.goToPublishingExport();

		await uiElementsPage.clickNewButton();

		await exportImportPage.expectPortletCounts(/^\s*Pages\s*/, {
			registrations: [{counts: {items: 2}, label: 'Master Pages'}],
		});
	}
);

test('cannot see Site Pages checkbox', async ({
	exportImportPage,
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();
	await productMenuPage.goToPublishingExport();
	await productMenuPage.page
		.getByRole('link', {name: 'Custom Export'})
		.click();

	await exportImportPage.expectPortletAbsent('Site Pages');
});

test('Can see deletion counts at site level', async ({
	apiHelpers,
	exportImportPage,
	uiElementsPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			scope: 'site',
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const applicationName = normalizeRestPath(objectDefinition.restContextPath);

	const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
		{textField: objectDefinition.name},
		applicationName + '/scopes/Guest'
	);

	const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
		{textField: objectDefinition.name},
		applicationName + '/scopes/Guest'
	);

	await exportImportPage.goToExport();
	await uiElementsPage.clickNewButton();

	await exportImportPage.deletionsLabel.check();

	await exportImportPage.expectPortletCounts(objectDefinition.name, {
		counts: {items: 2},
	});

	await apiHelpers.objectEntry.deleteObjectEntry(
		applicationName,
		String(objectEntry1.id)
	);

	await exportImportPage.refreshCountsLink.click();

	await exportImportPage.expectPortletCounts(objectDefinition.name, {
		counts: {deletions: 1, items: 1},
	});

	await apiHelpers.objectEntry.deleteObjectEntry(
		applicationName,
		String(objectEntry2.id)
	);

	await exportImportPage.refreshCountsLink.click();

	await exportImportPage.expectPortletCounts(objectDefinition.name, {
		counts: {deletions: 2},
	});

	await exportImportPage.deletionsLabel.uncheck();

	await exportImportPage.expectPortletDeletionsHidden(objectDefinition.name);
});

test(
	'Can see the correct deletion counts for multiple registrations at site level',
	{tag: ['@LPD-67433']},
	async ({
		displayPageTemplatesPage,
		exportImportPage,
		masterPagesPage,
		pageEditorPage,
		pageTemplatesPage,
		productMenuPage,
		site,
		uiElementsPage,
		utilityPagesPage,
	}) => {
		await test.step('Create and delete a display page template folder', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateFolderName = getRandomString();

			await displayPageTemplatesPage.createFolder(
				displayPageTemplateFolderName,
				getRandomString()
			);
			await displayPageTemplatesPage.deleteTemplate(
				displayPageTemplateFolderName
			);
		});

		await test.step('Create and delete a display page template', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Web Content',
				contentType: 'Web Content Article',
				name: displayPageTemplateName,
			});
			await displayPageTemplatesPage.deleteTemplate(
				displayPageTemplateName
			);
		});

		await test.step('Create and delete a master page', async () => {
			await masterPagesPage.goto(site.friendlyUrlPath);

			const masterPageName = getRandomString();

			await masterPagesPage.createNewMaster(masterPageName);
			await masterPagesPage.deleteMaster(masterPageName);
		});

		await test.step('Create and delete a page template set and a page template', async () => {
			await pageTemplatesPage.goto(site.friendlyUrlPath);

			const pageTemplateCollectionName = getRandomString();

			await pageTemplatesPage.addPageTemplateCollection(
				pageTemplateCollectionName
			);

			await pageTemplatesPage.goto(site.friendlyUrlPath);

			const contentPageTemplateName = getRandomString();

			await pageTemplatesPage.addContentPageTemplate(
				contentPageTemplateName
			);

			await pageEditorPage.publishButton.click();

			await waitForAlert(
				pageTemplatesPage.page,
				'Success:The page template was published successfully.'
			);

			await pageTemplatesPage.deletePageTemplate(contentPageTemplateName);
			await pageTemplatesPage.deletePageTemplateCollection(
				pageTemplateCollectionName
			);
		});

		await test.step('Create and delete a utility page', async () => {
			await utilityPagesPage.goto(site.friendlyUrlPath);

			const utilityPageName = getRandomString();

			await utilityPagesPage.createPage({
				name: utilityPageName,
				type: '404 Error',
			});
			await utilityPagesPage.markAsDefault(utilityPageName);
			await utilityPagesPage.deletePage(utilityPageName);
		});

		await test.step('Assert deletion counts are correct', async () => {
			await productMenuPage.openProductMenuIfClosed();
			await productMenuPage.goToPublishingExport();

			await uiElementsPage.clickNewButton();

			await exportImportPage.expectPortletDeletionsHidden('Pages');

			await exportImportPage.deletionsLabel.check();

			await exportImportPage.expectPortletCounts('Pages', {
				counts: {deletions: 6},
				registrations: [
					{
						counts: {deletions: 1},
						label: 'Display Page Template Folders',
					},
					{
						counts: {deletions: 1},
						label: 'Display Page Templates',
					},
					{counts: {deletions: 1}, label: 'Master Pages'},
					{
						counts: {deletions: 1},
						label: /^\s*Page Templates\s*/,
					},
					{counts: {deletions: 1}, label: 'Page Template Sets'},
					{counts: {deletions: 1}, label: 'Utility Pages'},
				],
			});
		});
	}
);
