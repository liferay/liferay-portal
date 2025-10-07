/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {getWebContentStructureId} from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {
	ANIMAL_01_FRIENDLY_URL,
	ANIMAL_DDM_STRUCTURE_KEY,
} from '../../setup/page-management-site/main/constants/animals';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import {goToObjectEntity} from '../../setup/page-management-site/main/utils/goToObjectEntity';

const test = mergeTests(
	applicationsMenuPageTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-60546': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest,
	pagesPagesTest
);

async function addDefaultAnimalDisplayPageTemplate(
	apiHelpers: ApiHelpers,
	displayPageTemplateName: string,
	site: Site
) {
	const className = await apiHelpers.jsonWebServicesClassName.fetchClassName(
		'com.liferay.journal.model.JournalArticle'
	);

	const animalWebContentStructureId = await getWebContentStructureId(
		apiHelpers,
		site.id,
		ANIMAL_DDM_STRUCTURE_KEY
	);

	const displayPage =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				classTypeId: String(animalWebContentStructureId),
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
		{
			layoutPageTemplateEntryId: displayPage.layoutPageTemplateEntryId,
		}
	);
}

test.describe('General', () => {
	test('Allow mapping repeatable fields collection provider', async ({
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create DPT for Animal

		await displayPageTemplatesPage.goto(pageManagementSite.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentSubtype: 'Animal',
			contentType: 'Web Content Article',
			name: displayPageTemplateName,
		});

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		// Add collection and image fragment

		await pageEditorPage.addFragment(
			'Content Display',
			'Collection Display'
		);

		await page.locator('.lfr-layout-structure-item-collection').click();

		await pageEditorPage.chooseCollectionDisplayCollection(
			'Repeatable Fields Collection Providers',
			'Species'
		);

		await pageEditorPage.waitForChangesSaved();

		await pageEditorPage.addFragment(
			'Basic Components',
			'Image',
			page.locator('.page-editor__collection-item.empty').first()
		);

		// Map editable to image field

		const imageFragmentId = await pageEditorPage.getFragmentId('Image');

		await pageEditorPage.selectEditable(imageFragmentId, 'image-square');

		await page.getByLabel('Source Selection').selectOption('Mapping');

		await page.getByLabel('Field').selectOption('Species Image');

		await pageEditorPage.waitForChangesSaved();

		// Change preview with item

		await displayPageTemplatesPage.changePreviewItem('Animal 01');

		// Check src of images

		const imageFragments = page.locator('.component-image img');

		expect(await imageFragments.first().getAttribute('src')).toContain(
			'poodle.jpg'
		);
		expect(await imageFragments.last().getAttribute('src')).toContain(
			'pug.jpg'
		);
	});

	test('Allow mapping editables to fields of related object', async ({
		displayPageTemplatesPage,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create DPT for Lemon

		await displayPageTemplatesPage.goto(pageManagementSite.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Lemon',
			name: displayPageTemplateName,
		});

		// Add fragment and select editable

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		const headingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectEditable(headingId, 'element-text');

		// Map to field from related Lemon Basket object

		await pageEditorPage.setMappingConfiguration({
			mapping: {
				field: 'Lemon Basket Color',
			},
			relationship: 'Lemon Basket',
			source: 'relationship',
		});

		// Check editable is mapped

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: headingId,
		});

		await expect(editable).toHaveClass(/page-editor__editable--mapped/);
	});

	test(
		'Allow mapping background image',
		{
			tag: '@LPS-98030',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create display page template for Animal and mark as default

			const displayPageTemplateName = getRandomString();

			await addDefaultAnimalDisplayPageTemplate(
				apiHelpers,
				displayPageTemplateName,
				pageManagementSite
			);

			// Go to edit display page template

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Map background image

			await pageEditorPage.addFragment('Layout Elements', 'Container');

			const containerId = await pageEditorPage.getFragmentId('Container');

			await pageEditorPage.selectFragment(containerId);

			await page.getByRole('tab', {exact: true, name: 'Styles'}).click();

			await page
				.getByLabel('Image Source', {exact: true})
				.selectOption({label: 'Mapping'});

			await pageEditorPage.waitForChangesSaved();

			await page
				.getByLabel('Field', {exact: true})
				.selectOption({label: 'Main Image'});

			// Publish display page template

			await displayPageTemplatesPage.publishTemplate();

			// Assert background image in view mode

			await page.goto(
				`web${pageManagementSite.friendlyUrlPath}/w/${ANIMAL_01_FRIENDLY_URL}`
			);

			await expect(
				page.locator(
					'.lfr-layout-structure-item-container[style*="dogs.jpg"]'
				)
			).toBeAttached();

			// Delete default display page template

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.deleteTemplate(
				displayPageTemplateName
			);

			await expect(
				page.getByText(displayPageTemplateName, {exact: true})
			).not.toBeVisible();
		}
	);

	test(
		'Allow mapping link',
		{
			tag: '@LPS-98030',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create display page template for Animal and mark as default

			const displayPageTemplateName = getRandomString();

			await addDefaultAnimalDisplayPageTemplate(
				apiHelpers,
				displayPageTemplateName,
				pageManagementSite
			);

			// Go to edit display page template

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Map link to header fragment

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await pageEditorPage.mapEditableLink({
				editableId: 'element-text',
				fragmentName: 'Heading',
				linkConfiguration: {
					mappingConfiguration: {
						mapping: {
							field: 'More Info Link',
						},
						source: 'structure',
					},
					type: 'Mapped URL',
				},
			});

			// Map link to image fragment

			await pageEditorPage.addFragment('Basic Components', 'Image');

			await pageEditorPage.mapEditableLink({
				editableId: 'image-square',
				fragmentName: 'Image',
				linkConfiguration: {
					mappingConfiguration: {
						mapping: {
							field: 'More Info Link',
						},
						source: 'structure',
					},
					type: 'Mapped URL',
				},
			});

			// Map link to button fragment

			await pageEditorPage.addFragment('Basic Components', 'Button');

			await pageEditorPage.mapEditableLink({
				editableId: 'link',
				fragmentName: 'Button',
				linkConfiguration: {
					mappingConfiguration: {
						mapping: {
							field: 'More Info Link',
						},
						source: 'structure',
					},
					type: 'Mapped URL',
				},
			});

			// Publish display page template

			await displayPageTemplatesPage.publishTemplate();

			// Assert mapped link in view mode

			await page.goto(
				`web${pageManagementSite.friendlyUrlPath}/w/${ANIMAL_01_FRIENDLY_URL}`
			);

			expect(
				await page
					.locator('.component-heading')
					.getByRole('link')
					.getAttribute('href')
			).toContain('https://en.wikipedia.org/wiki/Dog');

			expect(
				await page
					.locator('.component-image')
					.getByRole('link')
					.getAttribute('href')
			).toContain('https://en.wikipedia.org/wiki/Dog');

			expect(
				await page
					.locator('.component-button')
					.getByRole('link')
					.getAttribute('href')
			).toContain('https://en.wikipedia.org/wiki/Dog');

			// Delete default display page template

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.deleteTemplate(
				displayPageTemplateName
			);

			await expect(
				page.getByText(displayPageTemplateName, {exact: true})
			).not.toBeVisible();
		}
	);

	test(
		'Allow mapping text fields and image fields',
		{
			tag: ['@LPS-86550', '@LPS-182999'],
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create display page template for Animal and mark as default

			const displayPageTemplateName = getRandomString();

			await addDefaultAnimalDisplayPageTemplate(
				apiHelpers,
				displayPageTemplateName,
				pageManagementSite
			);

			// Go to edit display page template

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Map author name

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			const headingFragmentId =
				await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(
				headingFragmentId,
				'element-text'
			);

			await page.getByLabel('Field').selectOption('Author Name');

			// Map author profile image

			await pageEditorPage.addFragment('Basic Components', 'Image');

			const imageFragmentId = await pageEditorPage.getFragmentId('Image');

			await pageEditorPage.selectEditable(
				imageFragmentId,
				'image-square'
			);

			await page
				.getByLabel('Source Selection', {exact: true})
				.selectOption('Mapping');

			await pageEditorPage.waitForChangesSaved();

			await page.getByLabel('Field').selectOption('Author Profile Image');

			// Publish display page template

			await displayPageTemplatesPage.publishTemplate();

			// Assert mapped fields in view mode

			await page.goto(
				`web${pageManagementSite.friendlyUrlPath}/w/${ANIMAL_01_FRIENDLY_URL}`
			);

			await expect(
				page.getByRole('heading', {name: 'Test Test'})
			).toBeVisible();

			await expect(
				page.getByRole('img', {name: 'Test Test'})
			).toBeVisible();

			// Delete default display page template

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.deleteTemplate(
				displayPageTemplateName
			);

			await expect(
				page.getByText(displayPageTemplateName, {exact: true})
			).not.toBeVisible();
		}
	);
});

test.describe('Image Resolution', () => {
	test(
		'Check image resolution in display page template',
		{
			tag: '@LPS-125191',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
			simulationMenuPage,
		}) => {

			// Create display page template

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					'com.liferay.portal.kernel.repository.model.FileEntry'
				);

			const displayPageTemplateName = getRandomString();

			const displayPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId: className.classNameId,
						classTypeId: '0',
						groupId: pageManagementSite.id,
						name: displayPageTemplateName,
					}
				);

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPage.layoutPageTemplateEntryId,
				}
			);

			// Go to edit display page template

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Map Image

			await pageEditorPage.addFragment('Layout Elements', 'Container');

			const containerId = await pageEditorPage.getFragmentId('Container');

			await pageEditorPage.selectFragment(containerId);

			await page.getByRole('tab', {exact: true, name: 'Styles'}).click();

			await page
				.getByLabel('Image Source', {exact: true})
				.selectOption({label: 'Mapping'});

			await pageEditorPage.waitForChangesSaved();

			await page
				.getByLabel('Field', {exact: true})
				.selectOption({label: 'File URL'});

			await displayPageTemplatesPage.publishTemplate();

			// Go to view mode

			await page.goto(
				`web${pageManagementSite.friendlyUrlPath}/d/high_resolution_photo-jpg`
			);

			// Open simulation panel

			await simulationMenuPage.openSimulationPanel();

			// Assert image in desktop

			const iframe = page.frameLocator(
				'iframe[title="Simulation Preview"]'
			);

			await expect(
				iframe.locator('.lfr-layout-structure-item-container').first()
			).toHaveCSS('background-image', /documents/);

			// Assert image in tablet

			await page.getByLabel('Tablet').click();

			await expect(
				iframe.locator('.lfr-layout-structure-item-container').first()
			).toHaveCSS('background-image', /Preview-1000x0/);
		}
	);
});

test.describe('Preview Item', () => {
	test(
		'View mapped content in page editor when select preview items',
		{
			tag: '@LPS-128904',
		},
		async ({
			context,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create DPT for Animal

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Animal',
				contentType: 'Web Content Article',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Add heading fragment

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			const headingFragmentId =
				await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(
				headingFragmentId,
				'element-text'
			);

			await page.getByLabel('Field').selectOption('Title');

			await pageEditorPage.waitForChangesSaved();

			// Check that only one request is made

			// Change preview with item

			await displayPageTemplatesPage.changePreviewItem('Animal 01');

			// Assert title

			await expect(
				page.locator('.component-heading').getByText('Animal 01')
			).toBeVisible();

			// Map author profile image

			await pageEditorPage.addFragment('Basic Components', 'Image');

			const imageFragmentId = await pageEditorPage.getFragmentId('Image');

			await pageEditorPage.changeEditableConfiguration({
				editableId: 'image-square',
				fieldLabel: 'Source Selection',
				fragmentId: imageFragmentId,
				tab: 'Image Source',
				value: 'Mapping',
			});

			await pageEditorPage.changeEditableConfiguration({
				editableId: 'image-square',
				fieldLabel: 'Field',
				fragmentId: imageFragmentId,
				tab: 'Image Source',
				value: 'Species Image',
			});

			// Assert image

			expect(
				await page.locator('.component-image img').getAttribute('src')
			).toContain('poodle.jpg');

			// Change the item and check that the page has been updated

			await displayPageTemplatesPage.changePreviewItem('Animal 02');

			await expect(
				page.locator('.component-heading').getByText('Animal 02')
			).toBeVisible();

			// Preview in a new tab

			const pagePromise = context.waitForEvent('page');

			const previewButton = page.getByRole('menuitem', {
				name: 'Preview in a New Tab',
			});

			await expect(async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: previewButton,
					trigger: page
						.locator('.control-menu-nav-item')
						.getByLabel('Options', {exact: true}),
				});

				const newPage = await pagePromise;

				await expect(
					newPage.locator('.component-heading').getByText('Animal 02')
				).toBeVisible({
					timeout: 100,
				});
			}).toPass();
		}
	);

	test(
		'Update the page when Products are selected from the Preview With selector',
		{tag: '@LPD-59397'},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			site,
		}) => {
			const changePreviewProduct = async (itemName: string) => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('menuitem', {
						name: 'Select Other Item',
					}),
					trigger: page.getByLabel('Preview With'),
				});

				const modal = page.frameLocator('iframe[title="Select"]');

				await clickAndExpectToBeHidden({
					target: page.locator('.modal-dialog'),
					trigger: modal.getByText(itemName),
				});
			};

			// Create two products with a specification that uses the same key

			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Channel',
				siteGroupId: site.id,
			});

			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			const specification =
				await apiHelpers.headlessCommerceAdminCatalog.postSpecification();

			const product1 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product 1'},
					productSpecifications: [
						{
							specificationKey: specification.key,
							value: {
								en_US: 'Product Spectification 1',
							},
						},
					],
				});

			const key = product1.productSpecifications[0].key;

			const product2 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product 2'},
					productSpecifications: [
						{
							specificationKey: specification.key,
							value: {
								en_US: 'Product Spectification 2',
							},
						},
					],
				});

			await apiHelpers.headlessCommerceAdminCatalog.patchProductSpecification(
				product2.productSpecifications[0].id,
				{key}
			);

			// Create a Display Page Template with a Product Specification fragment

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Product',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment(
				'Product',
				'Product Specification'
			);

			// Change the Key field to use the shared key

			await page.getByLabel('Key', {exact: true}).fill(key);

			// Check that the page is updated when the selection in Preview With selector is changed

			await changePreviewProduct('Product 1');

			await expect(
				page.locator('[data-name="Product Specification"]')
			).toContainText('Product Spectification 1');

			await changePreviewProduct('Product 2');

			await expect(
				page.locator('[data-name="Product Specification"]')
			).toContainText('Product Spectification 2');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Product 1',
				}),
				trigger: page.getByLabel('Preview With'),
			});

			await expect(
				page.locator('[data-name="Product Specification"]')
			).toContainText('Product Spectification 1');
		}
	);
});

test.describe('Object Display page', () => {
	test(
		'Can display relationship element in display page',
		{tag: '@LPS-191554'},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create DPT for Lemon Basket

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Lemon Basket',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Add a collection display fragment with related collection provider

			await pageEditorPage.addFragment(
				'Content Display',
				'Collection Display'
			);

			await pageEditorPage.chooseCollectionDisplayCollection(
				'Related Items Collection Providers',
				'Lemon Basket to Lemon'
			);

			await pageEditorPage.addFragment(
				'Basic Components',
				'Heading',
				page.locator('.page-editor__collection-item.empty').first()
			);

			// Map editable to field

			const headingFragmentId =
				await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(
				headingFragmentId,
				'element-text'
			);

			await page.getByLabel('Field').selectOption('Lemon History');

			await displayPageTemplatesPage.publishTemplate();

			// Create a lemon basket and lemons

			const lemonBasket = await apiHelpers.objectEntry.postObjectEntry(
				{
					lemonDimensions: ['large'],
					material: 'plastic',
				},
				'c/lemonbaskets',
				pageManagementSite.key
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					lemonHistory: 'one',
					lemonSize: 'lemonSize',
					lemonWeight: 5,
					r_lemonBasketToLemons_c_lemonBasketId: lemonBasket.id,
				},
				'c/lemons',
				pageManagementSite.key
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					lemonHistory: 'two',
					lemonSize: 'lemonSize',
					lemonWeight: 5,
					r_lemonBasketToLemons_c_lemonBasketId: lemonBasket.id,
				},
				'c/lemons',
				pageManagementSite.key
			);

			// Go to lemon basket display page

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon Basket')
				)
			).body;

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinitionClassName
				);

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${lemonBasket.id}`
			);

			// Assert collection contains all lemons

			await expect(page.getByText('one')).toBeVisible();
			await expect(page.getByText('two')).toBeVisible();
		}
	);

	test(
		'Can map an object action',
		{tag: '@LPS-165556'},
		async ({
			apiHelpers,
			applicationsMenuPage,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			site,
		}) => {

			// Create ticket object definition

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: ticketObjectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode: 'ticketERC',
					label: {
						en_US: 'Ticket',
					},
					name: 'Ticket',
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'textERC',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Text',
							},
							localized: false,
							name: 'text',
							required: false,
						},
					],
					panelCategoryKey: 'control_panel.users',
					pluralLabel: {
						en_US: 'Tickets',
					},
					scope: 'company',
					status: {
						code: 0,
					},
				});

			apiHelpers.data.push({
				id: ticketObjectDefinition.id,
				type: 'objectDefinition',
			});

			// Add object entry

			const applicationName =
				'c/' + ticketObjectDefinition.name.toLowerCase() + 's';

			const ticketObjectEntry =
				await apiHelpers.objectEntry.postObjectEntry(
					{
						text: 'text1',
					},
					applicationName
				);

			// Add object action

			const objectActionAPIClient =
				await apiHelpers.buildRestClient(ObjectActionAPI);

			await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
				ticketObjectDefinition.externalReferenceCode,
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
							ticketObjectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {
									en_US: 'Text',
								},
								name: 'text',
								value: 'sample text',
							},
						],
					},
					system: false,
				}
			);

			// Add display page template

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					ticketObjectDefinition.className
				);

			const displayPageTemplateName = getRandomString();

			const displayPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId: className.classNameId,
						groupId: site.id,
						name: displayPageTemplateName,
					}
				);

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPage.layoutPageTemplateEntryId,
				}
			);

			// Add button and configure it to execute action

			displayPageTemplatesPage.goto(site.friendlyUrlPath);

			displayPageTemplatesPage.editTemplate(displayPageTemplateName);

			await pageEditorPage.addFragment('Basic Components', 'Button');

			const buttonId = await pageEditorPage.getFragmentId('Button');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Type',
				fragmentId: buttonId,
				tab: 'General',
				value: 'Action',
			});

			await pageEditorPage.selectEditable(buttonId, 'action');

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Action',
				tab: 'Action',
				value: 'addObjectEntryName',
			});

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Success Interaction',
				tab: 'Action',
				value: 'Show Notification',
			});

			// Publish display page template

			await displayPageTemplatesPage.publishTemplate();

			// Go to display page and execute action

			await page.goto(
				`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${ticketObjectEntry.id}`
			);

			// Wait for action button to be ready

			await page.waitForTimeout(1000);

			// Execute action

			await page.getByRole('button', {name: 'Go somewhere'}).click();

			await waitForAlert(page);

			// Check object entry was created

			await applicationsMenuPage.goToControlPanel();

			page.getByRole('menuitem', {
				exact: true,
				name: 'Tickets',
			}).click();

			await expect(page.getByText('sample text')).toBeVisible();
		}
	);

	test(
		'Can edit one field from an object in a display page',
		{
			tag: '@LPS-191389',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create a default display page for lemon object

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinitionClassName
				);

			const displayPageTemplateName = getRandomString();

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: pageManagementSite.id,
					name: displayPageTemplateName,
				}
			);

			// Edit display page template and add a form container when only one field

			displayPageTemplatesPage.goto(pageManagementSite.friendlyUrlPath);

			displayPageTemplatesPage.editTemplate(displayPageTemplateName);

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			await pageEditorPage.mapFormFragment(
				await pageEditorPage.getFragmentId('Form Container'),
				'Lemon (Default)',
				['Lemon Size']
			);

			await displayPageTemplatesPage.publishTemplate();

			// Create a lemon object entry

			const lemonObjectEntry =
				await apiHelpers.objectEntry.postObjectEntry(
					{
						lemonHistory: 'one',
						lemonSize: 'lemonSize',
						lemonWeight: 5,
					},
					'c/lemons',
					pageManagementSite.key
				);

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${lemonObjectEntry.id}`
			);

			// Edit only the lemon size field

			await page
				.getByRole('textbox', {name: 'Lemon Size'})
				.fill('lemonSize2');

			await page.getByRole('button', {name: 'Submit'}).click();

			// Go to admin and check that only lemon size was updated

			goToObjectEntity({
				entityName: 'Lemon',
				page,
			});

			const row = page.locator('.fds tbody tr').first();

			await expect(row).toContainText('one');
			await expect(row).toContainText('lemonSize2');
			await expect(row).toContainText('5');
		}
	);

	test(
		'Can map object attachment to image fragment',
		{
			tag: '@LPS-182999',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			site,
		}) => {

			// Create object definition with attachment field

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode: 'attachmentERC',
					label: {
						en_US: 'Attachment',
					},
					name: 'Attachment',
					objectFields: [
						{
							DBType: 'Long',
							businessType: 'Attachment',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'customAttachment',
							},
							name: 'customAttachment',
							objectFieldSettings: [
								{
									name: 'acceptedFileExtensions',
									value: 'jpeg, jpg, pdf, png',
								} as any,
								{
									name: 'fileSource',
									value: 'documentsAndMedia',
								} as any,
								{
									name: 'maximumFileSize',
									value: '100',
								} as any,
							],
							required: false,
							type: 'Long',
						},
					],
					pluralLabel: {
						en_US: 'Attachments',
					},
					scope: 'company',
					status: {
						code: 0,
					},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			// Create a default display page

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					objectDefinition.externalReferenceCode
				)
			).body;

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinitionClassName
				);

			const displayPageTemplateName = getRandomString();

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: site.id,
					name: displayPageTemplateName,
				}
			);

			// Edit display display page and map image fragment to attachment field

			displayPageTemplatesPage.goto(site.friendlyUrlPath);

			displayPageTemplatesPage.editTemplate(displayPageTemplateName);

			await pageEditorPage.addFragment('Basic Components', 'Image');

			await pageEditorPage.selectEditable(
				await pageEditorPage.getFragmentId('Image'),
				'image-square'
			);

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappingConfiguration({
				mapping: {field: 'Preview URL'},
				source: 'structure',
			});

			await displayPageTemplatesPage.publishTemplate();

			// Create an object with attachment

			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					customAttachment: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: 'attachment.png',
					},
				},
				'c/attachments'
			);

			apiHelpers.data.push({
				id: objectEntry.customAttachment.id,
				type: 'document',
			});

			// Check that the mapping is working

			await page.goto(
				`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
			);

			expect(
				await page.locator('.component-image img').getAttribute('src')
			).toContain('attachment.png');
		}
	);

	test(
		'Can edit values in display page',
		{
			tag: '@LPS-182999',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			site,
		}) => {

			// Create list definitions

			const genrePicklist =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'horror',
				listTypeDefinitionExternalReferenceCode:
					genrePicklist.externalReferenceCode,
				name_i18n: {en_US: 'horror'},
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'musical',
				listTypeDefinitionExternalReferenceCode:
					genrePicklist.externalReferenceCode,
				name_i18n: {en_US: 'musical'},
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'thriller',
				listTypeDefinitionExternalReferenceCode:
					genrePicklist.externalReferenceCode,
				name_i18n: {en_US: 'thriller'},
			});

			apiHelpers.data.push({
				id: genrePicklist.id,
				type: 'listTypeDefinition',
			});

			const originPicklist =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'hollywood',
				listTypeDefinitionExternalReferenceCode:
					originPicklist.externalReferenceCode,
				name_i18n: {en_US: 'hollywood'},
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'bollywood',
				listTypeDefinitionExternalReferenceCode:
					originPicklist.externalReferenceCode,
				name_i18n: {en_US: 'bollywood'},
			});

			apiHelpers.data.push({
				id: originPicklist.id,
				type: 'listTypeDefinition',
			});

			// Create object definition

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode: 'filmERC',
					label: {
						en_US: 'Film',
					},
					name: 'Film',
					objectFields: [
						{
							DBType: 'String',
							businessType: 'MultiselectPicklist',
							externalReferenceCode: 'genreERC',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Genre',
							},
							listTypeDefinitionExternalReferenceCode:
								genrePicklist.externalReferenceCode,
							name: 'genre',
						},
						{
							DBType: 'String',
							businessType: 'Picklist',
							externalReferenceCode: 'originERC',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Origin',
							},
							listTypeDefinitionExternalReferenceCode:
								originPicklist.externalReferenceCode,
							name: 'origin',
						},
						{
							DBType: 'DateTime',
							externalReferenceCode: 'releaseDateERC',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Release Date',
							},
							name: 'releaseDate',
							objectFieldSettings: [
								{
									name: 'timeStorage',
									value: {},
								},
							],
						},
					],
					pluralLabel: {
						en_US: 'Films',
					},
					scope: 'company',
					status: {
						code: 0,
					},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			// Create a display page

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					objectDefinition.externalReferenceCode
				)
			).body;

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinitionClassName
				);

			const displayPageTemplateName = getRandomString();

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: site.id,
					name: displayPageTemplateName,
				}
			);

			// Edit display display page and add form container

			displayPageTemplatesPage.goto(site.friendlyUrlPath);

			displayPageTemplatesPage.editTemplate(displayPageTemplateName);

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			await pageEditorPage.mapFormFragment(
				await pageEditorPage.getFragmentId('Form Container'),
				'Film (Default)'
			);

			await displayPageTemplatesPage.publishTemplate();

			// Create an Object Entry

			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					genre: ['horror', 'thriller'],
					origin: 'hollywood',
					releaseDate: '2025-01-10T17:26:00Z',
				},
				'c/films'
			);

			// Go to the display page and edit the values

			await page.goto(
				`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
			);

			await page.getByLabel('horror', {exact: true}).uncheck();

			await page.locator('.select-from-list').getByRole('button').click();

			await page.getByRole('option', {name: 'bollywood'}).click();

			await page
				.getByRole('textbox', {name: 'Release Date'})
				.fill('2020-03-02T05:15');

			await page.getByRole('button', {name: 'Submit'}).click();

			// Check that the values were updated

			await page.goto(
				`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
			);

			await expect(
				page.getByLabel('horror', {exact: true})
			).not.toBeChecked();
			await expect(
				page.getByLabel('musical', {exact: true})
			).not.toBeChecked();
			await expect(
				page.getByLabel('thriller', {exact: true})
			).toBeChecked();

			await expect(
				page.getByRole('combobox', {name: 'Origin'})
			).toHaveValue('bollywood');

			await expect(
				page.getByRole('textbox', {name: 'Release Date'})
			).toHaveValue('2020-03-02T05:15');
		}
	);
});
