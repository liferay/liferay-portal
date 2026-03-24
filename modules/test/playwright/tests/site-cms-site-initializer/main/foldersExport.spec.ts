import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {exportImportPagesTest} from '../../export-import-web/main/fixtures/exportImportPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'System folders L_CONTENTS and L_FILES are excluded from export results in a new CMS Space',
	{tag: '@LPD-83026'},
	async ({apiHelpers, exportImportPage, homePage, page}) => {
		const spaceName = `Space ${getRandomString()}`;

		const assetLibrary = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const userFolderName = `User Folder ${getRandomString()}`;

		await apiHelpers.post(
			`/o/headless-object/v1.0/scopes/${assetLibrary.siteId}/object-entry-folders`,
			{
				data: {
					label: userFolderName,
					title: userFolderName,
				},
			}
		);

		await homePage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'All Spaces'}),
			trigger: page.getByRole('menuitem', {exact: true, name: 'Spaces'}),
		});

		await page.getByRole('link', {exact: true, name: spaceName}).click();

		await page.getByRole('button', {name: 'More Actions'}).waitFor();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Export'}),
			trigger: page.getByRole('button', {name: 'More Actions'}),
		});

		await exportImportPage.newExportButton.click();

		await expect(
			page.getByText('Object Entry Folders 1 Items').first()
		).toBeVisible();
	}
);
