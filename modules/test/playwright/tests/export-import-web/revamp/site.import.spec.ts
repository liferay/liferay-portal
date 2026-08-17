/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest()
);

const testWithWiki = mergeTests(
	test,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPD-57655': {enabled: true},
	})
);

async function addWidgetPageTemplate(apiHelpers: DataApiHelpers, site: Site) {
	const layoutPageTemplateCollection =
		await apiHelpers.jsonWebServicesLayoutPageTemplateCollection.addLayoutPageTemplateCollection(
			{
				groupId: String(site.id),
				name: getRandomString(),
			}
		);

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
		{
			groupId: String(site.id),
			layoutPageTemplateCollectionId: String(
				layoutPageTemplateCollection.layoutPageTemplateCollectionId
			),
			name: getRandomString(),
			type: 'widget-page',
		}
	);
}

test(
	'Cannot import an instance scoped lar file',
	{tag: '@LPD-99382'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		globalMenuPage,
		site,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{},
			normalizeRestPath(objectDefinition.restContextPath)
		);

		await globalMenuPage.goToApplications('Export');

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.clickNew();

		await exportImportDataSelectionPage.selectOnlyObjectDefinition(
			objectDefinition.name
		);

		await exportImportPage.nameInput.fill(name);

		await exportImportPage.exportButton.click();

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();

		const folderPath = await exportImportPage.download(name);

		await exportImportPage.goToImport(site.friendlyUrlPath);

		await exportImportPage.newButton.click();

		await exportImportPage.expectUploadError(
			folderPath,
			'The LAR file contains one or more entities with a different scope.'
		);
	}
);

testWithWiki(
	'Does not export wiki nodes when they are not selected',
	{tag: '@LPD-40988'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		page,
		site,
	}) => {
		let folderPath: string;

		const name = `MyExport-${getRandomString()}`;

		await testWithWiki.step(
			'Add a wiki node and a widget page template',
			async () => {
				await apiHelpers.headlessDelivery.postWikiNode(site.id);

				await addWidgetPageTemplate(apiHelpers, site);
			}
		);

		await testWithWiki.step(
			'Export the site with the wiki content deselected',
			async () => {
				await exportImportPage.goToExport(site.friendlyUrlPath);

				await exportImportPage.clickNew();

				await exportImportDataSelectionPage.uncheckItem(
					'Content & Data',
					'Wiki'
				);

				await exportImportPage.nameInput.fill(name);

				await exportImportPage.exportButton.click();

				await expect(
					exportImportPage.taskStatusLabel(name)
				).toBeVisible();

				folderPath = await exportImportPage.download(name);
			}
		);

		await testWithWiki.step(
			'Assert the import wizard offers no wiki content',
			async () => {
				await exportImportPage.goToImport(site.friendlyUrlPath);

				await exportImportPage.newButton.click();

				await exportImportPage.goToImportDataSelection({
					folderPath,
					name,
				});

				await expect(page.getByLabel('Page Templates')).not.toHaveCount(
					0
				);

				await expect(page.getByLabel('Wiki')).toHaveCount(0);
			}
		);
	}
);
