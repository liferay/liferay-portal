/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const SECTIONS = [
	{
		externalReferenceCode: 'L_FILES' as const,
		gotoSection: 'gotoFiles' as const,
		sectionLabel: 'Files',
	},
	{
		externalReferenceCode: 'L_CONTENTS' as const,
		gotoSection: 'gotoContents' as const,
		sectionLabel: 'Contents',
	},
];

for (const {externalReferenceCode, gotoSection, sectionLabel} of SECTIONS) {
	test(
		`Info panel location shows "${sectionLabel}" for a folder under ${externalReferenceCode}`,
		{tag: '@LPD-90001'},
		async ({apiHelpers, assetsPage, page}) => {
			const folderName = `Folder ${getRandomString()}`;

			const folder =
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode:
						externalReferenceCode,
					scopeKey: 'Default',
					title: folderName,
				});

			try {
				await assetsPage[gotoSection]();

				await assetsPage.changeVisualizationMode('Table');

				await page
					.getByRole('row', {name: folderName})
					.getByRole('checkbox')
					.check();

				await page
					.getByRole('button', {name: 'Show Info Panel'})
					.click();

				await expect(
					page
						.getByRole('navigation', {name: 'Breadcrumb'})
						.getByText(sectionLabel, {exact: true})
				).toBeVisible();
			}
			finally {
				await apiHelpers.objectFolder.deleteObjectEntryFolder(
					folder.id
				);
			}
		}
	);
}
