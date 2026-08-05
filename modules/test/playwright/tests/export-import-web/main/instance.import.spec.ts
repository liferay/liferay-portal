/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {depotAdminPageTest} from '../../../fixtures/depotAdminPageTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {wikiPagesTest} from '../../../fixtures/wikiPagesTest';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {portletExportImportPageTest} from './fixtures/portletExportImportPageTest';
import {stagingPageTest} from './fixtures/stagingPageTest';

export const test = mergeTests(
	companyExportImportPageTest,
	dataApiHelpersTest,
	depotAdminPageTest,
	documentLibraryPagesTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPD-57655': {enabled: false},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	pageTemplatesPagesTest,
	portletExportImportPageTest,
	stagingPageTest,
	wikiPagesTest
);

test('Can see corresponding elements at instance level', async ({
	apiHelpers,
	companyExportImportPage,
	exportImportPage,
	globalMenuPage,
	page,
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
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}`
	);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	await page.goto('/');

	await companyExportImportPage.goToImportOptions(exportFilePath);

	await expect(page.getByRole('group', {name: 'Pages'})).not.toBeVisible();

	await expect(page.getByText('Comments, Ratings')).not.toBeVisible();

	await expect(page.getByText(`${objectDefinition.name}`)).toBeVisible();

	await expect(
		page.getByText(`${objectDefinition.externalReferenceCode} Change`)
	).not.toBeVisible();

	await expect(page.getByLabel('Delete Application Data')).not.toBeVisible();

	await expect(
		page.getByText(
			'Mirror: All data and content inside the imported LAR is created as new the first time while maintaining a reference to the source. Subsequent imports from the same source update the entries instead of creating new entries.'
		)
	).toBeVisible();

	await expect(page.getByText('Mirror with overwriting:')).not.toBeVisible();

	await expect(page.getByText('Copy as New:')).not.toBeVisible();
});
