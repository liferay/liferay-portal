/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId, {
	getWebContentStructureId,
} from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	ANIMALS_COLLECTION_NAME,
	ANIMAL_DDM_STRUCTURE_KEY,
} from '../../setup/page-management-site/main/constants/animals';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getCollectionDefinition from './utils/getCollectionDefinition';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test('Allows selecting specific repeatable field when mapping', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create page with a Heading fragment and go to edit mode

	const headingId = getRandomString();

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			}),
		]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Map editable to repeatable field Country

	await pageEditorPage.selectEditable(headingId, 'element-text');

	await pageEditorPage.selectItemMappingButton.click();

	const webContentOption = page
		.frameLocator('iframe[title="Select"]')
		.getByRole('menuitem', {exact: true, name: 'Web Content'});

	if (
		await webContentOption.evaluate(
			(element) => !element.classList.contains('active')
		)
	) {
		await webContentOption.click();
	}

	const folderCard = page
		.frameLocator('iframe[title="Select"]')
		.getByRole('link', {name: 'Animals'});

	const articleCard = page
		.frameLocator('iframe[title="Select"]')
		.getByText('Animal 01', {exact: false});

	await clickAndExpectToBeVisible({target: articleCard, trigger: folderCard});

	await clickAndExpectToBeHidden({
		target: page.locator('.modal-dialog'),
		trigger: articleCard,
	});

	await page
		.getByLabel('Field')
		.selectOption({value: 'DDMStructure_Country'});

	await pageEditorPage.waitForChangesSaved();

	// Check that all iteration to display option works

	const fragment = page.locator('.component-heading');

	await expect(fragment).toHaveText('Spain');

	await page.getByLabel('Iteration to Display').selectOption('Last');

	await pageEditorPage.waitForChangesSaved();

	await expect(fragment).toHaveText('United Kingdom');

	await page
		.getByLabel('Iteration to Display')
		.selectOption('Specific Number');

	await page.getByLabel('Iteration Number').fill('2');

	await page.getByLabel('Iteration Number').blur();

	await pageEditorPage.waitForChangesSaved();

	await expect(fragment).toHaveText('France');

	await pageEditorPage.switchLanguage('es-ES');

	await expect(fragment).toHaveText('Francia');

	// Publish and check the published page

	await pageEditorPage.publishPage();

	await page.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await expect(fragment).toHaveText('France');
});

test('Allows selecting specific repeatable collection provider', async ({
	apiHelpers,
	collectionsPage,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create definition for a collection mapped to Animals collection

	const animalsClassPK = await collectionsPage.getCollectionClassPK(
		ANIMALS_COLLECTION_NAME,
		pageManagementSite.friendlyUrlPath
	);

	const collectionId = getRandomString();

	const collectionDefinition = getCollectionDefinition({
		classPK: animalsClassPK,
		id: collectionId,
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([collectionDefinition]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Go to edit mode of page

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Add a repeatable field collection with heading fragment

	await pageEditorPage.addFragment(
		'Content Display',
		'Collection Display',
		page.locator('.page-editor__collection-item').first()
	);

	await page.getByText('Select a collection to display.').first().click();

	await pageEditorPage.chooseCollectionDisplayCollection(
		'Repeatable Fields Collection Providers',
		'Species'
	);

	await pageEditorPage.waitForChangesSaved();

	await pageEditorPage.addFragment(
		'Basic Components',
		'Heading',
		page.locator('.page-editor__collection-item.empty').first()
	);

	// Select editable and map it

	await pageEditorPage.goToSidebarTab('Browser');

	await page.getByLabel('Select element-text').click();

	await page.getByLabel('Field').selectOption('Species Name');

	await pageEditorPage.waitForChangesSaved();

	await expect(page.getByText('Balinese')).toBeAttached();
	await expect(page.getByText('Poodle')).toBeAttached();
	await expect(page.getByText('Pug')).toBeAttached();
	await expect(page.getByText('Sphynx')).toBeAttached();

	await pageEditorPage.publishPage();

	await page.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await expect(page.getByText('Balinese')).toBeAttached();
	await expect(page.getByText('Poodle')).toBeAttached();
	await expect(page.getByText('Pug')).toBeAttached();
	await expect(page.getByText('Sphynx')).toBeAttached();
});

test(
	'Link information is still kept when the linked page is deleted',
	{
		tag: '@LPS-120198',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add layout

		const firstLayoutTitle = getRandomString();

		const firstLayout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: firstLayoutTitle,
		});

		// Add layout page with a button fragment

		const buttonId = getRandomString();

		const buttonDefinition = getFragmentDefinition({
			id: buttonId,
			key: 'BASIC_COMPONENT-button',
		});

		const secondLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([buttonDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit mode of page

		await pageEditorPage.goto(secondLayout, site.friendlyUrlPath);

		await pageEditorPage.mapEditableLink({
			editableId: 'link',
			fragmentName: 'Button',
			linkConfiguration: {
				layoutTitle: firstLayoutTitle,
				type: 'Page',
			},
		});

		await expect(page.getByPlaceholder('No Page Selected')).toHaveValue(
			firstLayoutTitle
		);

		await expect(page.getByText('Go Somewhere')).toHaveAttribute(
			'href',
			`/web${site.friendlyUrlPath}/${firstLayoutTitle}`
		);

		await pageEditorPage.publishPage();

		// Delete first layout

		await page.goto(
			`/web${site.friendlyUrlPath}${secondLayout.friendlyUrlPath}`
		);

		await apiHelpers.jsonWebServicesLayout.deleteLayout(firstLayout.id);

		// Go to second layout and assert link is still visible

		await page.goto(
			`/web${site.friendlyUrlPath}${secondLayout.friendlyUrlPath}`
		);

		const link = page.getByRole('link', {name: 'Go Somewhere'});

		await expect(link).toHaveAttribute(
			'href',
			`/web${site.friendlyUrlPath}/${firstLayoutTitle}`
		);

		// Navigate to first layout and assert page not found

		await link.click();

		await expect(page.getByText('Page Not Found')).toBeVisible();
	}
);

test(
	'Map single tag from asset entry to editable field',
	{
		tag: '@LPS-116975',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create web content with tags

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const basicWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			tags: ['Dogs', 'Cats'],
			title: basicWebContentTitle,
		});

		// Add content page

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

		// Go to edit mode

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await pageEditorPage.setMappedItem({
			entity: 'Web Content',
			entry: basicWebContentTitle,
			field: 'Tags (Repeatable)',
		});

		await expect(page.locator('.component-heading')).toHaveText(
			'Dogs, Cats'
		);
	}
);

test(
	'Map specific display page template',
	{
		tag: ['@LPS-184193', '@LPS-191986'],
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Add display page template

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.journal.model.JournalArticle'
			);

		const animalWebContentStructureId = await getWebContentStructureId(
			apiHelpers,
			pageManagementSite.id,
			ANIMAL_DDM_STRUCTURE_KEY
		);

		const displayPageTemplateName = getRandomString();

		const displayPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					classTypeId: String(animalWebContentStructureId),
					groupId: pageManagementSite.id,
					name: displayPageTemplateName,
				}
			);

		// Add content page

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Map display page template to heading fragment

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapEditableLink({
			editableId: 'element-text',
			fragmentName: 'Heading',
			linkConfiguration: {
				mappingConfiguration: {
					mapping: {
						entity: 'Web Content',
						entry: 'Animal 01 - Dogs and Cats categories',
						field: displayPageTemplateName,
						folder: 'Animals',
					},
				},
				type: 'Mapped URL',
			},
		});

		expect(
			await page.getByLabel('URL', {exact: true}).inputValue()
		).toContain(`/e/${displayPageTemplateName}/${className.classNameId}`);

		await pageEditorPage.publishPage();

		// Navigate to display page template

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(
			page.getByRole('link', {name: 'Heading Example'})
		).toHaveAttribute('rel', 'nofollow');

		expect(
			await page
				.getByRole('link', {name: 'Heading Example'})
				.getAttribute('href')
		).toContain(`/e/${displayPageTemplateName}/${className.classNameId}`);

		// Delete layout

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);

		// Delete the display page

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.deleteLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					displayPage.layoutPageTemplateEntryId,
			}
		);
	}
);

test(
	'Map web content field with url to image editable field and text editable field',
	{
		tag: '@LPS-98031',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a heading and image fragment

		const headingId = getRandomString();

		const headingFragment = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const imageId = getRandomString();

		const imageFragment = getFragmentDefinition({
			id: imageId,
			key: 'BASIC_COMPONENT-image',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingFragment, imageFragment]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map link to heading fragment and image fragment

		await pageEditorPage.mapEditableLink({
			editableId: 'element-text',
			fragmentName: 'Heading',
			linkConfiguration: {
				mappingConfiguration: {
					mapping: {
						entity: 'Web Content',
						entry: 'Animal 01 - Dogs and Cats categories',
						field: 'More Info Link',
						folder: 'Animals',
					},
				},
				type: 'Mapped URL',
			},
		});

		await pageEditorPage.mapEditableLink({
			editableId: 'image-square',
			fragmentName: 'Image',
			linkConfiguration: {
				mappingConfiguration: {
					mapping: {
						entity: 'Web Content',
						entry: 'Animal 02 - Dogs category',
						field: 'More Info Link',
						folder: 'Animals',
					},
				},
				type: 'Mapped URL',
			},
		});

		await pageEditorPage.publishPage();

		// Assert link in view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(
			page.getByRole('link', {name: 'Heading Example'})
		).toHaveAttribute('href', 'https://en.wikipedia.org/wiki/Dog');

		await expect(
			page.locator('.component-image').getByRole('link')
		).toHaveAttribute('href', 'https://en.wikipedia.org/wiki/Cat');

		// Check we can clear mapped item

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await page.getByRole('tab', {exact: true, name: 'Link'}).click();

		await pageEditorPage.removeMapping();

		// Delete layout

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);
	}
);

test(
	'Only success object action redirects to the display page template',
	{
		tag: '@LPS-195827',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create object definition

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: potatoProviderObjectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				externalReferenceCode: 'potatoProviderERC',
				label: {
					en_US: 'Potato Provider',
				},
				name: 'PotatoProvider',
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						externalReferenceCode: 'nameERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Name',
						},
						localized: false,
						name: 'name',
						required: false,
					},
					{
						DBType: 'String',
						businessType: 'Text',
						externalReferenceCode: 'locationERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Location',
						},
						localized: false,
						name: 'location',
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Potato Providers',
				},
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName: 'name',
			});

		apiHelpers.data.push({
			id: potatoProviderObjectDefinition.id,
			type: 'objectDefinition',
		});

		// Add object entries

		const applicationName =
			'c/' + potatoProviderObjectDefinition.name.toLowerCase() + 's';

		const firstObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				location: 'Holland',
				name: 'Holland Potatoes',
			},
			applicationName
		);

		const secondObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				location: 'Canary Islands',
				name: 'Canary Potatoes',
			},
			applicationName
		);

		// Add object action

		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectActionAPI);

		await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
			potatoProviderObjectDefinition.externalReferenceCode,
			{
				active: true,
				errorMessage: {
					en_US: 'The location should be Canary Islands.',
				},
				label: {
					en_US: 'addObjectEntryName',
				},
				name: 'addObjectEntryName',
				objectActionExecutorKey: 'add-object-entry',
				objectActionTriggerKey: 'standalone',
				parameters: {
					objectDefinitionExternalReferenceCode:
						getObjectERC('Potato'),
					predefinedValues: [
						{
							businessType: 'Text',
							inputAsValue: false,
							label: {
								en_US: 'Location',
							},
							name: 'potatoOrigin',
							value: 'location',
						},
					],
				},
				system: false,
			}
		);

		// Add display page template

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				potatoProviderObjectDefinition.className
			);

		const displayPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: site.id,
					name: getRandomString(),
				}
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					displayPage.layoutPageTemplateEntryId,
			}
		);

		// Create content page with a button fragment and go to edit mode

		const firstButtonId = getRandomString();

		const firstButtonDefinition = getFragmentDefinition({
			id: firstButtonId,
			key: 'BASIC_COMPONENT-button',
		});

		const secondButtonId = getRandomString();

		const secondButtonDefinition = getFragmentDefinition({
			id: secondButtonId,
			key: 'BASIC_COMPONENT-button',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				firstButtonDefinition,
				secondButtonDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map standalone action to buttons

		await pageEditorPage.mapObjectAction({
			entity: 'Potato Providers',
			entry: firstObjectEntry.name,
			fragmentId: firstButtonId,
		});

		await pageEditorPage.mapObjectAction({
			entity: 'Potato Providers',
			entry: secondObjectEntry.name,
			fragmentId: secondButtonId,
		});

		await pageEditorPage.publishPage();

		// Go to view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Wait a second for the page to load to get the buttons

		await page.waitForTimeout(1000);

		const buttons = page.getByText('Go Somewhere');

		// Click first button and assert error

		await buttons.first().click();

		await waitForAlert(
			page,
			'Error:The location should be Canary Islands.',
			{
				type: 'danger',
			}
		);

		await expect(page).toHaveURL(
			`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Click second button and assert success

		await buttons.last().click();

		await expect(
			page.getByRole('heading', {
				name: secondObjectEntry.name,
			})
		).toBeVisible();
	}
);
