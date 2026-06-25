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
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
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
	}),
	itemSelectorSamplePageTest,
	isolatedSiteTest,
	loginTest()
);

interface SpaceTest {
	assetLibraryKey: string;
	name: string;
}

interface FileObjectTest {
	file: {id: number};
	id: number;
	title: string;
}

const APPLICATION_NAME = 'cms/basic-documents';

const firstSpaceName = `Space-1-${getRandomString()}`;
const secondSpaceName = `Space-2-${getRandomString()}`;

let firstSpace: SpaceTest = {
	assetLibraryKey: '',
	name: '',
};

let secondSpace: SpaceTest = {
	assetLibraryKey: '',
	name: '',
};

let firstSpaceObjectEntry: FileObjectTest = {file: {id: 0}, id: 0, title: ''};
let secondSpaceObjectEntry: FileObjectTest = {file: {id: 0}, id: 0, title: ''};

function createObjectEntryData({title}: {title: string}) {
	const newTitle = `${title} ${getRandomString()}`;

	return {
		file: {
			fileBase64:
				'iVBORw0KGgoAAAANSUhEUgAAAD0AAAAXCAIAAAA3N9DuAAAAA3NCSVQICAjb4U/gAAAAEHRFWHRTb2Z0d2FyZQBTaHV0dGVyY4LQCQAABX9JREFUWMOVWFuS3EgOA8BUlXpifuYee4u9/3nWdonEfDAzS91+RKw+HOooKUmCAEiZ//3nP7ZJ4jeX7XR+v15VBizSgO2Q+iarAIoMMcv9FgkbBAyMoI0s7yD9EwkDNiJYZRshAihb69Eqg+igJLheV7k66XKVqxPtd668Ml8kg3GOI0QDaZRB8qrKLBtlhEjilSXO2B24k77SWRbRvxIQYVjiDGV4HbLfLTurIkiybAAE+qiyh6jOUlRnTLI7MGIs8Bgc58C36+XGwBhS2SGGomEWmeURFJHlhr9DShB5pUmTrLKkKneRIEIsG4bIxluk55M03ijMbAFU1c7vfpOZmyrllMZfj49DDFFilm280k2ARqtrLlti2VfWBK9w5aytyiJg9yEkYPRpNsiJ9ysL8xxwVtcs8JUWAElfON0ZR8QuQ5z3j3Gex5PElfXKJPDKjFVGh5EaRhxDG6QmdFcIshbBslxLFTN6OctDnKcRV7rFAGAEI6gvisxKAArh95crz/E8H3FEADgivv3IbnRnUGUDIb6yMn2lR9ALrbJhj+BOdyfUD4zgFGhZxMYbt+q0VdgFhALAEu6vr4ijKs/xHEECIzhCTfFGtNEt4zyi/2ySvH1GrCUAACGGeGWrhd9fWSurMmxzNSHTACo9GyryXQC6AP1siLsnZJD6OD4eQ53T5mWVSXT3s/y6agRJRNx8xsDUH0hg8afsso8Rn6AlSYok0ZIAIc+y3g8aBpDVnlir7uKqLRSc9eIxzueYTduV2t7AP4/49iNtvK7qVofocsu3jCuduTGZNkoR7AtdTKO+K1HbwB3O+8Xlkv0Mya6EoO02osc4/36em7sSSdrdQ2R5hNq5bVSrkOBS6tD0uJ5BTYw2l+m5mHNHYmdvW+0/d1f55ey8WeRE13C/UlVS/PV4PoeaBp3QHpDdm86AgFaik4qk4a6n/ZHLOwiQ7ZPommcGPXS6Hz1rCKBe+pS6vyi1n3oPLAmAGOfx8YiYLxiA28WwPDHEgv3WnNuwuyEjKPEqw+b0ykYeLu9zRFRZk453auuoT5zh/uVP5mjbPsbjHNEairZxslqFbR3kPuhtmkY1hYwRcsO7RHyne/fqjXfd3PBO9DtnWK/fyaCqloo44nlE9LwkcGW1XZI0sCcMyRFqF+KyvKZKO49tENIcMS6X3fvJMhnM/Ysk8/vPDti2aB2/ZL9dWxvdlqHjHM8h7r2KxFX+VLMXCTits827DdQAyUz3iWWDcwtoAxAA4p0o49zrivJ/70iebvgHz7nbDqnz+BihKtjIdFZdWe8dkGvxMLD/bZdsmqwFBvd6PcmjBql5Uq50eU1NHX8DDomLlEOB/+c6x3kOHYMGhjRC0wRbW0trIf7IypwDq6dYdeprLRHZm8n0KIF7Tx+KoAj2lnJVAr3Ru9ybHT+p9feV0NeykXN0yB4oBBaPq3xllZ3pI9QEaOPvydKCjlVeD/krTUCe697sdboM3ylu1Oa089tbvr0nVeJXVmOOfX+M8zxOco5MrY1A0wHfHttxehXZnrNJomDvFK8srR2ogkpXCz+rdurBaLcu23p82VUIhiKoPxNG0DnOj0d01LTJxeP+7tLien9kiHMuqpcldltG0Pb5CO0RaDT+L4G9nzRtyiYIWGtO3SVoOKvSRXDb7c9jyzDJ8/h4jngewcasU19LIoCmafvCVuf+eCNxpY+hKy3DdpGCC2Dx0V0biqtyB+51oNuy8rm2LbK+++bsWQnnZyFMLzri+RzPkHr1bZb3l47IH9ccJFWG3Z8dNvZe1VwfncS0IApoRZfITjp9I7dNMF2ihsKMTRjrKdLvOSDwq15FNQSiPo5nEPu7rtlse4i5uFHzvwB4rc60b/aU/Rc6sWizbSKbGQA',
			name: `${newTitle.replace(' ', '_')}.png`,
		},
		objectEntryFolderExternalReferenceCode: 'L_FILES',
		title: newTitle,
	};
}

async function createSpace(
	apiHelpers: DataApiHelpers,
	name: string
): Promise<SpaceTest> {
	return await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name,
		settings: {},
		type: 'Space',
	});
}

async function uploadSampleFile(
	apiHelpers: DataApiHelpers,
	title: string,
	space: SpaceTest
): Promise<FileObjectTest> {
	const newEntry = (await apiHelpers.objectEntry.postObjectEntry(
		createObjectEntryData({title}),
		APPLICATION_NAME,
		space.assetLibraryKey
	)) as unknown as FileObjectTest;

	apiHelpers.data.push({
		id: newEntry.id,
		type: 'document',
	});

	return newEntry;
}

test.beforeEach(async ({apiHelpers, itemSelectorSamplePage, site}) => {
	await test.step('Create Spaces', async () => {
		firstSpace = await createSpace(apiHelpers, firstSpaceName);
		secondSpace = await createSpace(apiHelpers, secondSpaceName);
	});

	await test.step('Upload sample files', async () => {
		firstSpaceObjectEntry = await uploadSampleFile(
			apiHelpers,
			'first space file title',
			firstSpace
		);
		secondSpaceObjectEntry = await uploadSampleFile(
			apiHelpers,
			'second space file title',
			secondSpace
		);
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

test('Item Selector Modal filters availability for CMS Files', async ({
	itemSelectorSamplePage,
	page,
}) => {
	await test.step('Check if Item Selector Modal appears and open filters', async () => {
		await itemSelectorSamplePage.selectCMSFileButton.click();

		await expect(
			itemSelectorSamplePage.selectCMSFileModalHeader
		).toBeVisible();

		waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});

		await itemSelectorSamplePage.filtersButton.click();
	});

	await test.step('Check that all CMS filters are present in the dropdown', async () => {
		const expectedFilterLabels = [
			'Space',
			'Type',
			'Category',
			'Tags',
			'Author',
			'Status',
			'Create Date',
			'Display Date',
			'Expiration Date',
			'Modified Date',
			'Review Date',
		];

		for (const label of expectedFilterLabels) {
			await expect(
				page.getByRole('menuitem', {name: label})
			).toBeVisible();
		}
	});

	await test.step('Verify Type filter is not empty', async () => {
		await page.getByRole('menuitem', {name: 'Type'}).click();

		await expect(page.getByText('No items were found')).toBeHidden();

		await expect(page.getByRole('checkbox').first()).toBeVisible();

		await page.getByRole('button', {name: 'Back'}).click();
	});

	await test.step('Verify Status filter options', async () => {
		await page.getByRole('menuitem', {name: 'Status'}).click();

		const expectedStatusLabels = [
			'Approved',
			'Pending',
			'Draft',
			'Expired',
			'Scheduled',
		];

		for (const label of expectedStatusLabels) {
			await expect(page.getByLabel(label)).toBeVisible();
		}
	});
});

test('Item Selector Modal Space filter functionality', async ({
	itemSelectorSamplePage,
	page,
}) => {
	await test.step('Open Item Selector Modal', async () => {
		await itemSelectorSamplePage.selectCMSFileButton.click();
		waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});
	});

	await test.step(`Filter CMS Files by ${firstSpace.name}`, async () => {
		await itemSelectorSamplePage.filtersButton.click();
		await page.getByRole('menuitem', {name: 'Space'}).click();
		await page.getByLabel(firstSpace.name).click();
		await page.getByRole('button', {name: 'Add Filter'}).click();

		waitForFDS({page, visualizationMode: EFDSVisualizationMode.CARDS});

		await expect(
			page.getByText(firstSpaceObjectEntry.title, {exact: true})
		).toBeVisible();

		await expect(
			page.getByText(secondSpaceObjectEntry.title, {exact: true})
		).toBeHidden();
	});
});
