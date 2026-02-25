/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {pagesPagesTest} from './fixtures/pagesPagesTest';

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true, system: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pagesPagesTest,
	pageViewModePagesTest
);

test.describe('Page content', () => {
	test(
		'Preview content on content page by experience',
		{
			tag: '@LPS-186155',
		},
		async ({
			apiHelpers,
			page,
			pageEditorPage,
			simulationMenuPage,
			site,
		}) => {

			// Create page and go to view mode

			const headingId = getRandomString();

			const headingDefinition = getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([headingDefinition]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.editTextEditable(
				headingId,
				'element-text',
				'Default Text'
			);

			// Create experience

			await pageEditorPage.createExperience('E1');

			await pageEditorPage.editTextEditable(
				headingId,
				'element-text',
				'E1 Text'
			);

			await pageEditorPage.publishPage();

			// Go to view page and open simulation panel

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await simulationMenuPage.openSimulationPanel();

			// Select experiences

			await simulationMenuPage.changeCombobox(
				'Preview By',
				'Experiences'
			);

			// Assert default experience

			const iframe = page.frameLocator(
				'iframe[title="Simulation Preview"]'
			);

			await expect(iframe.getByText('Default Text')).toBeVisible();

			// Assert custom experience

			await simulationMenuPage.changeCombobox('Experience', 'E1');

			await expect(
				page.getByText('Showing content for the experience "E1".')
			).toBeVisible();

			await expect(iframe.getByText('E1 Text')).toBeVisible();
		}
	);

	test(
		'Preview content in a widget page by segment',
		{
			tag: '@LPS-186155',
		},
		async ({
			apiHelpers,
			collectionsPage,
			page,
			simulationMenuPage,
			site,
			widgetPagePage,
		}) => {

			// Create blogs entry

			const blogsEntryName = getRandomString();

			await apiHelpers.headlessDelivery.postBlog(site.id, {
				headline: blogsEntryName,
			});

			// Create document

			const documentName = getRandomString();

			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/attachment.txt')
				),
				{
					fileName: 'attachment.txt',
					title: documentName,
				}
			);

			// Create segments entry

			const userName = getRandomString();
			const segmentsEntryName = getRandomString();

			const segmentsEntry =
				await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
					criteria: {
						criteria: {
							user: {
								conjunction: 'and',
								filterString: `(firstName eq '${userName}')`,
								typeValue: 'model',
							},
						},
						filterString: {
							model: `(firstName eq '${userName}')`,
						},
					},
					groupId: site.id,
					name: segmentsEntryName,
				});

			// Create dynamic asset list

			const assetListEntryName = getRandomString();

			const dlFileEntryClassName =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					'com.liferay.document.library.kernel.model.DLFileEntry'
				);

			const assetListEntry =
				await apiHelpers.jsonWebServicesAssetListEntry.addDynamicAssetListEntry(
					{
						groupId: site.id,
						title: assetListEntryName,
						typeSettings: `anyAssetType=${dlFileEntryClassName.classNameId}`,
					}
				);

			const blogsEntryClassName =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					'com.liferay.blogs.model.BlogsEntry'
				);

			await apiHelpers.jsonWebServicesAssetListEntry.updateAssetListEntry(
				{
					assetListEntryId: assetListEntry.assetListEntryId,
					groupId: site.id,
					segmentsEntryId: segmentsEntry.segmentsEntryId,
					typeSettings: `anyAssetType=${blogsEntryClassName.classNameId}`,
				}
			);

			// Update collection variations priority

			await collectionsPage.goto(site.friendlyUrlPath);

			await page.getByRole('link', {name: assetListEntryName}).click();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Deprioritize'}),
				trigger: page.getByLabel('Actions for Anyone'),
			});

			await waitForAlert(
				page,
				'Success:Variation Anyone moved to position 2.'
			);

			// Create page and go to view mode

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			// Add Asset Publisher widget

			await widgetPagePage.addPortlet('Asset Publisher');

			// Select custom collection

			await widgetPagePage.clickOnAction(
				'Asset Publisher',
				'Configuration'
			);

			const configurationIFrame = page.frameLocator(
				`iframe[title*="Asset Publisher"]`
			);

			const selectCollectionIframe = configurationIFrame.frameLocator(
				'iframe[title="Select Collection"]'
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: selectCollectionIframe.getByRole('button', {
					name: assetListEntryName,
				}),
				trigger: configurationIFrame.getByLabel('Select Collection'),
			});

			await expect(
				configurationIFrame.getByRole('textbox', {name: 'Collection'})
			).toHaveValue(assetListEntryName);

			await widgetPagePage.saveAndClose('Asset Publisher');

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			// Open simulation panel

			await simulationMenuPage.openSimulationPanel();

			// Assert default collection

			await expect(
				page.getByText('Showing content for the segment "Anyone".')
			).toBeVisible({timeout: 5000});

			const simulationPreviewIframe = page.frameLocator(
				'iframe[title="Simulation Preview"]'
			);

			await expect(
				simulationPreviewIframe.getByRole('link', {
					name: documentName,
				})
			).toBeVisible({timeout: 1000});

			await expect(
				simulationPreviewIframe.getByRole('link', {
					name: blogsEntryName,
				})
			).not.toBeVisible({timeout: 1000});

			// Assert segmented collection

			await expect(async () => {
				await simulationMenuPage.changeCombobox('Segment', 'Anyone');

				await simulationMenuPage.changeCombobox(
					'Segment',
					segmentsEntryName
				);

				await expect(
					page.getByText(
						`Showing content for the segment "${segmentsEntryName}".`
					)
				).toBeVisible({timeout: 5000});

				await expect(
					simulationPreviewIframe.getByRole('link', {
						name: blogsEntryName,
					})
				).toBeVisible({timeout: 1000});

				await expect(
					simulationPreviewIframe.getByRole('link', {
						name: documentName,
					})
				).not.toBeVisible({timeout: 1000});
			}).toPass();
		}
	);

	test(
		'View info messages',
		{
			tag: ['@LPS-186155', '@LPS-187159'],
		},
		async ({apiHelpers, page, simulationMenuPage, site}) => {

			// Create page

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Open simulation panel

			await simulationMenuPage.openSimulationPanel();

			// Assert empty messages

			await expect(
				page.getByText('Showing content for the segment "Anyone".')
			).toBeVisible({timeout: 5000});

			await expect(
				page.getByText(
					'No segments have been added yet. To add a new segment go to Product Menu > People > Segments.'
				)
			).toBeVisible({timeout: 1000});

			await expect(async () => {
				await simulationMenuPage.changeCombobox(
					'Preview By',
					'Segments'
				);

				await simulationMenuPage.changeCombobox(
					'Preview By',
					'Experiences'
				);

				await expect(
					page.getByText(
						'Showing content for the experience "Default".'
					)
				).toBeVisible({timeout: 5000});

				await expect(
					page.getByText('No experiences have been added yet.')
				).toBeVisible({timeout: 5000});
			}).toPass();
		}
	);
});

test.describe('Screen Size', () => {
	test('View web content is shown in Web Content Display after be added via content panel', async ({
		apiHelpers,
		page,
		simulationMenuPage,
		site,
	}) => {

		// Create page and go to view mode

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Open simulation panel

		await simulationMenuPage.openSimulationPanel();

		// Assert desktop

		const device = page.locator('.device');

		await page.getByLabel('Desktop').click();

		await expect(device).toHaveCSS('height', '1050px');
		await expect(device).toHaveCSS('width', '1300px');

		// Assert tablet

		await page.getByLabel('Tablet').click();

		await expect(device).toHaveCSS('height', '900px');
		await expect(device).toHaveCSS('width', '808px');

		// Assert mobile

		await page.getByLabel('Mobile').click();

		await expect(device).toHaveCSS('height', '640px');
		await expect(device).toHaveCSS('width', '400px');

		// Assert custom

		await page.getByLabel('Custom').click();

		await expect(device).toHaveCSS('height', '600px');
		await expect(device).toHaveCSS('width', '600px');

		await page.getByLabel('Height(px)').fill('500');
		await page.getByLabel('Width(px)').fill('500');

		await page.getByLabel('Apply Custom Size').click();

		await expect(device).toHaveCSS('height', '500px');
		await expect(device).toHaveCSS('width', '500px');
	});
});
