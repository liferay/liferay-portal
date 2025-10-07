/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../utils/waitFor';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {itemSelectorSamplePageTest} from './fixtures/itemSelectorSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-178052': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	itemSelectorSamplePageTest,
	isolatedSiteTest,
	loginTest()
);

const spaceName = `Space ${getRandomString()}`;

interface SpaceTest {
	assetLibraryKey: string;
	name: string;
}

interface FileObjectTest {
	file: {id: number};
	id: number;
	title: string;
}

let newSpace: SpaceTest = {
	assetLibraryKey: '',
	name: '',
};

let newSpaceObjectEntry: FileObjectTest = {file: {id: 0}, id: 0, title: ''};
let defaultSpaceObjectEntry: FileObjectTest = {file: {id: 0}, id: 0, title: ''};

const createObjectEntryData = ({title}: {title: string}) => {
	const newTitle = `${title} ${getRandomString()}`;

	return {
		file: {
			fileBase64:
				'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',
			name: `${newTitle.replace(' ', '_')}.png`,
		},
		objectEntryFolderExternalReferenceCode: 'L_FILES',
		title: newTitle,
	};
};

test.beforeEach(async ({apiHelpers, itemSelectorSamplePage, site}) => {
	await test.step('Create Space', async () => {
		newSpace = (await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		})) as SpaceTest;
	});

	await test.step('Upload sample files', async () => {
		const applicationName = 'cms/basic-documents';

		newSpaceObjectEntry = (await apiHelpers.objectEntry.postObjectEntry(
			createObjectEntryData({title: 'new space file title'}),
			applicationName,
			newSpace.assetLibraryKey
		)) as unknown as FileObjectTest;

		defaultSpaceObjectEntry = (await apiHelpers.objectEntry.postObjectEntry(
			createObjectEntryData({title: 'default space file title'}),
			applicationName,
			'Default'
		)) as unknown as FileObjectTest;

		apiHelpers.data.push({
			id: newSpaceObjectEntry.id,
			type: 'document',
		});

		apiHelpers.data.push({
			id: defaultSpaceObjectEntry.id,
			type: 'document',
		});
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_frontend_js_item_selector_sample_web_portlet_FrontendJSItemSelectorSampleWebPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await itemSelectorSamplePage.goToPage({layout, site});
});

test.afterEach(async ({apiHelpers}) => {
	const applicationName = 'cms/basic-documents';

	if (newSpaceObjectEntry.id) {
		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(newSpaceObjectEntry.id)
		);
	}

	if (defaultSpaceObjectEntry.id) {
		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(defaultSpaceObjectEntry.id)
		);
	}
});

test('Item Selector Modal with Spaces filter for when selecting CMS Files', async ({
	itemSelectorSamplePage,
	page,
}) => {
	await test.step('Check that an Item Selector Modal appears in the page', async () => {
		await expect(page.getByText('Item Selector Modal')).toBeVisible();
	});

	await test.step('Check if Spaces filter is available', async () => {
		await itemSelectorSamplePage.selectCMSFileButton.click();

		await expect(
			itemSelectorSamplePage.selectCMSFileModalHeader
		).toBeVisible();

		waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});

		await expect(
			page.getByText(newSpaceObjectEntry.title, {exact: true})
		).toBeVisible();

		await expect(
			page.getByText(defaultSpaceObjectEntry.title, {exact: true})
		).toBeVisible();

		await expect(itemSelectorSamplePage.filtersButton).toBeVisible();
	});

	await test.step(`Filter CMS Files by ${newSpace.name}`, async () => {
		await itemSelectorSamplePage.filtersButton.click();

		await page.getByRole('menuitem', {name: 'Space'}).click();

		await page.getByLabel(newSpace.name).click();

		await page.getByRole('button', {name: 'Add Filter'}).click();

		waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});

		await expect(
			page.getByText(newSpaceObjectEntry.title, {exact: true})
		).toBeVisible();

		await expect(
			page.getByText(defaultSpaceObjectEntry.title, {exact: true})
		).not.toBeVisible();
	});
});
