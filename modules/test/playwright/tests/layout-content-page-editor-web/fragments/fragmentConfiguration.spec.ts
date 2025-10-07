/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {ANIMALS_COLLECTION_NAME} from '../../setup/page-management-site/main/constants/animals';
import getCollectionDefinition from '../main/utils/getCollectionDefinition';
import getContainerDefinition from '../main/utils/getContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';
import getWidgetDefinition from '../main/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

const STYLES = [
	{defaultValue: 'Align Left', label: 'Text Align', type: 'button'},

	{defaultValue: '#00000000', label: 'Background Color', type: 'color'},
	{defaultValue: '#1C1C24', label: 'Border Color', type: 'color'},

	{defaultValue: 'Inherited', label: 'Font Family', type: 'select'},
	{defaultValue: 'Inherited', label: 'Font Size', type: 'select'},
	{defaultValue: 'Inherited', label: 'Font Weight', type: 'select'},

	{defaultValue: '0', label: 'Border Radius', type: 'text'},
	{defaultValue: '0', label: 'Border Width', type: 'text'},
	{defaultValue: '100', label: 'Opacity', type: 'text'},
	{defaultValue: 'none', label: 'Shadow', type: 'text'},
];

const COLOR_PICKER_PALETTES = [
	{sections: ['Brand Colors', 'Gray', 'Theme Colors'], title: 'Color System'},
	{sections: ['Body'], title: 'General'},
	{sections: ['Other'], title: 'Typography'},
	{
		sections: [
			'Button Primary',
			'Button Outline Primary',
			'Button Secondary',
			'Button Outline Secondary',
			'Button Link',
		],
		title: 'Buttons',
	},
];

test.describe('Editable Configuration', () => {
	test(
		'Can map a date in the date editable and change the date format',
		{tag: ['@LPS-147897', '@LPS-147897']},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const basicWebContentTitle = getRandomString();

			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				datePublished: '2024-01-01T00:00:00Z',
				siteId: site.id,
				title: basicWebContentTitle,
				viewableBy: 'Anyone',
			});

			const dateFragmentId = getRandomString();

			const layoutTitle = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: dateFragmentId,
						key: 'BASIC_COMPONENT-date',
					}),
				]),
				siteId: site.id,
				title: layoutTitle,
			});

			// Navigate to the page editor

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Change the link of the containers

			await pageEditorPage.selectEditable(dateFragmentId, 'element-date');

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					entity: 'Web Content',
					entry: basicWebContentTitle,
					field: 'Display Date',
				},
			});

			await expect(page.getByText('1/1/24')).toBeVisible();

			await page.getByLabel('Date Format').selectOption('yy/MM/dd');

			await expect(page.getByText('24/01/01')).toBeVisible();
		}
	);

	test(
		'Can set a link to a link editable',
		{
			tag: ['@LPS-99686', '@LPS-184799'],
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a container

			const buttonId1 = getRandomString();
			const buttonId2 = getRandomString();
			const buttonId3 = getRandomString();

			const layoutTitle = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: buttonId1,
						key: 'BASIC_COMPONENT-button',
					}),
					getFragmentDefinition({
						id: buttonId2,
						key: 'BASIC_COMPONENT-button',
					}),
					getFragmentDefinition({
						id: buttonId3,
						key: 'BASIC_COMPONENT-button',
					}),
				]),
				siteId: pageManagementSite.id,
				title: layoutTitle,
			});

			// Navigate to the page editor

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Change the link of the containers

			await pageEditorPage.selectEditable(buttonId1, 'link');

			await page.getByRole('tab', {exact: true, name: 'Link'}).click();

			await pageEditorPage.setLinkConfiguration({
				type: 'URL',
				url: 'https://liferay.com',
			});

			await expect(
				page.locator(`.lfr-layout-structure-item-${buttonId1} a`)
			).toHaveAttribute('href', 'https://liferay.com');

			await pageEditorPage.selectEditable(buttonId2, 'link');

			await page.getByRole('tab', {exact: true, name: 'Link'}).click();

			await pageEditorPage.setLinkConfiguration({
				layoutTitle,
				type: 'Page',
			});

			await expect(
				page.locator(`.lfr-layout-structure-item-${buttonId2} a`)
			).toHaveAttribute(
				'href',
				`/web${pageManagementSite.friendlyUrlPath}/${layoutTitle}`
			);

			await pageEditorPage.selectEditable(buttonId3, 'link');

			await page.getByRole('tab', {exact: true, name: 'Link'}).click();

			await pageEditorPage.setLinkConfiguration({
				mappingConfiguration: {
					mapping: {
						entity: 'Documents and Media',
						entry: 'poodle.jpg',
						entryLocator: page
							.frameLocator('iframe[title="Select"]')
							.getByText('poodle.jpg', {exact: false}),
						field: 'Download URL',
					},
				},
				type: 'Mapped URL',
			});

			await expect(
				page.locator(`.lfr-layout-structure-item-${buttonId3} a`)
			).toHaveAttribute('href', /poodle\.jpg/);

			await pageEditorPage.publishPage();

			// Check that the links are correct in view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const buttons = page.locator('.component-button a');

			const firstButtonHref = await buttons
				.first()
				.evaluate((element) => element.getAttribute('href'));

			expect(firstButtonHref).toContain('https://liferay.com');

			const secondButtonHref = await buttons
				.nth(1)
				.evaluate((element) => element.getAttribute('href'));

			expect(secondButtonHref).toContain(
				`/web${pageManagementSite.friendlyUrlPath}/${layoutTitle}`
			);

			const thirdButtonHref = await buttons
				.last()
				.evaluate((element) => element.getAttribute('href'));

			expect(thirdButtonHref).toContain('poodle.jpg');
		}
	);
});

test.describe('Advanced Configuration', () => {
	test('Checks custom css can be added to a fragment in different viewports', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {
		const getVariableValue = async (variableName: string) => {
			return await page.evaluate(
				(variableName) => {
					const element = document.createElement('div');

					element.style.backgroundColor = `var(--${variableName})`;

					document.body.appendChild(element);

					const value = getComputedStyle(element).backgroundColor;

					document.body.removeChild(element);

					return value;
				},
				[variableName]
			);
		};

		// Create a content page with a Container fragment

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({id: containerId}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Custom CSS',
			fragmentId: containerId,
			tab: 'Advanced',
			value: '.[$FRAGMENT_CLASS$] { background-color: var(--success); }',
		});

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				style: 'backgroundColor',
			})
		).toBe(await getVariableValue('success'));

		await pageEditorPage.switchViewport('Tablet');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Custom CSS',
			fragmentId: containerId,
			isDesktop: false,
			tab: 'Advanced',
			value: '.[$FRAGMENT_CLASS$] { background-color: var(--warning); }',
		});

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				isDesktop: false,
				style: 'backgroundColor',
			})
		).toBe(await getVariableValue('warning'));

		await pageEditorPage.switchViewport('Landscape Phone');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Custom CSS',
			fragmentId: containerId,
			isDesktop: false,
			tab: 'Advanced',
			value: '.[$FRAGMENT_CLASS$] { background-color: var(--danger); }',
		});

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				isDesktop: false,
				style: 'backgroundColor',
			})
		).toBe(await getVariableValue('danger'));

		await pageEditorPage.switchViewport('Portrait Phone');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Custom CSS',
			fragmentId: containerId,
			isDesktop: false,
			tab: 'Advanced',
			value: '.[$FRAGMENT_CLASS$] { background-color: var(--info); }',
		});

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				isDesktop: false,
				style: 'backgroundColor',
			})
		).toBe(await getVariableValue('info'));
	});

	test('Add multiple css classes to fragment', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a content page with a Heading fragment

		const fragmentId = getRandomString();

		const fragmentDefinition = getFragmentDefinition({
			id: fragmentId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragmentDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Adds css classes and assert that added to the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'CSS Classes',
			fragmentId,
			tab: 'Advanced',
			value: 'background-color',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'CSS Classes',
			fragmentId,
			tab: 'Advanced',
			value: 'border-color',
		});

		const fragmentContent = page.locator('.page-editor__fragment-content');

		await expect(fragmentContent).toHaveClass(/background-color/);
		await expect(fragmentContent).toHaveClass(/border-color/);
	});

	test('Checks that the fragment is hidden from Site Search Results', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {
		const layouts = {fragment: null, searchBar: null};

		// Create a page with the Search Bar widget

		const widgetLayoutId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
		});

		layouts.searchBar = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: widgetLayoutId,
		});

		// Create a page with a fragment and publish it

		const headingId = getRandomString();

		const headingFragmentDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		layouts.fragment = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingFragmentDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layouts.fragment, site.friendlyUrlPath);

		await pageEditorPage.selectFragment(headingId);

		await pageEditorPage.publishPage();

		// Go to the Search Bar page and search for the fragment text

		await page.goto(
			`/web${site.friendlyUrlPath}${layouts.searchBar.friendlyUrlPath}`
		);

		const searchBar = page.getByPlaceholder('Search...');

		await searchBar.click();
		await searchBar.fill('Heading');

		await page
			.locator('.search-bar-suggestions .loading-animation')
			.waitFor();
		await page
			.locator('.search-bar-suggestions .loading-animation')
			.waitFor({state: 'hidden'});

		// Check that there are results

		const searchResults = page.getByText('Suggestions', {exact: true});

		await expect(searchResults).toBeVisible();

		// Go back to the fragment page and hide the fragment from the search results

		await pageEditorPage.goto(layouts.fragment, site.friendlyUrlPath);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Hide from Site Search Results',
			fragmentId: headingId,
			tab: 'Advanced',
			value: true,
		});

		await pageEditorPage.publishPage();

		// Go to the Search Bar page and search for the fragment text

		await page.goto(
			`/web${site.friendlyUrlPath}${layouts.searchBar.friendlyUrlPath}`
		);

		await searchBar.click();
		await searchBar.fill('Heading');

		await page
			.locator('.search-bar-suggestions .loading-animation')
			.waitFor();
		await page
			.locator('.search-bar-suggestions .loading-animation')
			.waitFor({state: 'hidden'});

		// Check that there are no results

		await expect(searchResults).not.toBeVisible();
	});

	test('Checks that the advanced configuration of a fragment appears in its corresponding tab', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create a content page with Page Management Site's Apple fragment

		const fragmentDefinition = getFragmentDefinition({
			fragmentConfig: {
				color: 'red',
			},
			id: getRandomString(),
			key: 'apple',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragmentDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Check advanced configuration appears where it should

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await page.getByTitle('Browser').click();

		await page.getByLabel('Select Apple').click();

		await pageEditorPage.goToConfigurationTab('Advanced');

		await expect(page.getByLabel('Color', {exact: true})).toBeVisible();
	});
});

test.describe('General Configuration', () => {
	test(
		'Allows using a configuration of type itemSelector',
		{tag: '@LPS-164242'},
		async ({
			apiHelpers,
			collectionsPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create a fragment with itemSelector configuration

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: pageManagementSite.id,
						name: getRandomString(),
					}
				);

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								label: 'itemSelector',
								name: 'itemSelector',
								type: 'itemSelector',
							},
						],
					},
				],
			};

			const html = `
			<div class="fragment-name">
				[#if itemSelectorObject??]
					\${itemSelectorObject.getUrlTitle()}
				[/#if]
			</div>
		`;

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: pageManagementSite.id,
				html,
				name: fragmentEntryName,
			});

			// Create a layout with a collection and the fragment

			const animalsClassPK = await collectionsPage.getCollectionClassPK(
				ANIMALS_COLLECTION_NAME,
				pageManagementSite.friendlyUrlPath
			);

			const collectionDefinition = getCollectionDefinition({
				classPK: animalsClassPK,
				id: getRandomString(),
				pageElements: [
					getFragmentDefinition({
						id: fragmentEntryName,
						key: fragmentEntryName,
					}),
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([collectionDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to the layout and check that the fragment is showing the correct mapping

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await expect(
				page.getByText('animal-01-dogs-and-cats-categories')
			).toBeVisible();
			await expect(
				page.getByText('animal-02-dogs-category')
			).toBeVisible();
		}
	);

	test(
		'Allows using a configuration of type collectionSelector',
		{tag: '@LPS-191007'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a fragment with collection selector configuration

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: pageManagementSite.id,
						name: getRandomString(),
					}
				);

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								label: 'Collection',
								name: 'collection',
								type: 'collectionSelector',
								typeOptions: {
									numberOfItems: 1,
								},
							},
						],
					},
				],
			};

			const html = `
			<div class="fragment-configuration">
				<h1>
					List of Items:
				</h1>
				<ul>
					[#if collectionObjectList??]
						[#list collectionObjectList as item]
							<li>\${item.title}</li>
						[/#list]
					[/#if]
				</ul>
			</div>
		`;

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: pageManagementSite.id,
				html,
				name: fragmentEntryName,
			});

			// Create a layout with the fragment

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: fragmentEntryName,
						key: fragmentEntryName,
					}),
				]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to the layout and map to a collection

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.selectFragment(fragmentEntryName);

			await pageEditorPage.setMappedItem({
				customMappingButtonLocator:
					page.getByLabel('Select Collection'),
				entity: 'Collections',
				entry: ANIMALS_COLLECTION_NAME,
			});

			// Check that it shows one item

			await expect(page.getByText('Animal 01')).toBeVisible();
			await expect(page.getByText('Animal 02')).not.toBeVisible();
		}
	);

	test(
		'Allows using a configuration of type url',
		{tag: '@LPS-164081'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a fragment with url configuration

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								label: 'Link',
								name: 'link',
								type: 'url',
							},
						],
					},
				],
			};

			const html = `
			<div class="fragment-configuration">
				[#if configuration.link?has_content] \${configuration.link}[/#if]

				Link example
			</div>
		`;

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: site.id,
				html,
				name: fragmentEntryName,
			});

			// Create a layout with the fragment

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: fragmentEntryName,
						key: fragmentEntryName,
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			// Go to the layout and change the url

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.selectFragment(fragmentEntryName);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'URL',
				fragmentId: fragmentEntryName,
				tab: 'General',
				value: 'example.com',
			});

			// Check that the url is showing

			await expect(
				page
					.locator('.fragment-configuration')
					.filter({hasText: 'example.com'})
			).toBeVisible();
		}
	);

	test(
		'Allows using a configuration of type checkbox',
		{tag: '@LPS-97177'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a fragment with checkbox configuration

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								defaultValue: false,
								label: 'Make Bold',
								name: 'makeBold',
								type: 'checkbox',
							},
						],
					},
				],
			};

			const html = `
			<div class="fragment-configuration">
				[#if configuration.makeBold == true]
					<b>Bold Words</b>
				[#else]
					<p>Not bold words</p>
				[/#if]
			</div>
		`;

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: site.id,
				html,
				name: fragmentEntryName,
			});

			// Create a layout with the fragment

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: fragmentEntryName,
						key: fragmentEntryName,
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check that it shows the default value

			await expect(page.getByText('Not bold words')).toBeVisible();

			// Check the value

			await pageEditorPage.selectFragment(fragmentEntryName);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Make Bold',
				fragmentId: fragmentEntryName,
				tab: 'General',
				value: true,
			});

			// Check that configuration is applied

			await expect(page.getByText('Bold words')).toBeVisible();
		}
	);

	test(
		'Allows using a configuration of type select',
		{tag: '@LPS-96685'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a fragment with select configuration

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								dataType: 'string',
								defaultValue: 'light',
								label: 'Applied Style',
								name: 'headingAppliedStyle',
								type: 'select',
								typeOptions: {
									validValues: [
										{
											value: 'dark',
										},
										{
											value: 'light',
										},
									],
								},
							},
						],
					},
				],
			};

			const html = `
			<div class="fragment-configuration">
				[#if configuration.headingAppliedStyle == "dark"]
				<div class="dark">
					<h1>Title-dark</h1>
				</div>
				[#else]
				<div class="light">
					<h1>Title-light</h1>
				</div>
				[/#if]
			</div>
		`;

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: site.id,
				html,
				name: fragmentEntryName,
			});

			// Create a layout with the fragment

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: fragmentEntryName,
						key: fragmentEntryName,
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check that it shows the default value

			await expect(page.getByText('Title-light')).toBeVisible();

			// Select a new value

			await pageEditorPage.selectFragment(fragmentEntryName);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Applied Style',
				fragmentId: fragmentEntryName,
				tab: 'General',
				value: 'dark',
			});

			await expect(page.getByText('Title-dark')).toBeVisible();
		}
	);

	test(
		'Allows using a configuration of type text',
		{tag: '@LPS-97192'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a fragment with text configuration

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								dataType: 'string',
								defaultValue: 'Go Somewhere',
								label: 'Button Text',
								name: 'buttonText',
								type: 'text',
								typeOptions: {
									placeholder: 'Placeholder',
								},
							},
						],
					},
				],
			};

			const html = `
			<div class="fragment-configuration">
				<button type="button" class="btn btn-primary">\${configuration.buttonText}</button>
			</div>
		`;

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: site.id,
				html,
				name: fragmentEntryName,
			});

			// Create a layout with the fragment

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: fragmentEntryName,
						key: fragmentEntryName,
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check that it shows the default value

			await expect(page.getByText('Go Somewhere')).toBeVisible();

			// Select the fragment and check that the placeholder is visible

			await pageEditorPage.selectFragment(fragmentEntryName);

			await pageEditorPage.goToConfigurationTab('General');

			expect(page.getByPlaceholder('Placeholder')).toBeVisible();

			// Change the value

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Button Text',
				fragmentId: fragmentEntryName,
				tab: 'General',
				value: 'Go Somewhere Else',
			});

			// Check that the new value is applied

			await expect(page.getByText('Go Somewhere Else')).toBeVisible();
		}
	);

	test(
		'Text configuration allows setting a max and min value',
		{tag: '@LPS-109138'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a fragment with type text and validation

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								dataType: 'int',
								defaultValue: '1',
								label: 'Number',
								name: 'number',
								type: 'text',
								typeOptions: {
									validation: {
										max: 10,
										min: 1,
										type: 'number',
									},
								},
							},
						],
					},
				],
			};

			const html = `
			<div class="fragment-configuration">
				<button type="button" class="btn btn-primary">Number: \${configuration.number}</button>
			</div>
		`;

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: site.id,
				html,
				name: fragmentEntryName,
			});

			// Create a layout with the fragment

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: fragmentEntryName,
						key: fragmentEntryName,
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check that it shows the default value

			await expect(page.getByText('Number: 1')).toBeVisible();

			// Select the fragment and check that the validation is applied

			await pageEditorPage.selectFragment(fragmentEntryName);

			await pageEditorPage.goToConfigurationTab('General');

			await page.getByLabel('Number').fill('0');

			await expect(
				page.getByText('You have entered invalid data.')
			).toBeVisible();

			await page.getByLabel('Number').fill('11');

			await expect(
				page.getByText('You have entered invalid data.')
			).toBeVisible();

			// Change the value

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Number',
				fragmentId: fragmentEntryName,
				tab: 'General',
				value: '3',
			});

			// Check that the new value is applied

			await expect(page.getByText('Number: 3')).toBeVisible();
		}
	);
});

test.describe('Localizable Configuration', () => {
	test(
		'Can localize date format on date fragment',
		{tag: ['@LPS-147897', '@LPS-171383']},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const basicWebContentTitle = getRandomString();

			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				datePublished: '2024-01-01T00:00:00Z',
				siteId: site.id,
				title: basicWebContentTitle,
				viewableBy: 'Anyone',
			});

			const dateFragmentId = getRandomString();

			const layoutTitle = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: dateFragmentId,
						key: 'BASIC_COMPONENT-date',
					}),
				]),
				siteId: site.id,
				title: layoutTitle,
			});

			// Navigate to the page editor

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Change the link of the containers

			await pageEditorPage.selectEditable(dateFragmentId, 'element-date');

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					entity: 'Web Content',
					entry: basicWebContentTitle,
					field: 'Display Date',
				},
			});

			await expect(page.getByText('1/1/24')).toBeVisible();

			await pageEditorPage.switchLanguage('es-ES');

			await page.getByLabel('Date Format').selectOption('yy/MM/dd');

			await expect(page.getByText('24/01/01')).toBeVisible();

			await pageEditorPage.switchLanguage('en-US');

			await expect(page.getByText('1/1/24')).toBeVisible();

			// Assert documentation link for custom date format

			await page.getByLabel('Date Format').selectOption('custom');

			const documentationLink = page.getByRole('link', {
				name: 'Learn more about date formats.',
			});

			await expect(documentationLink).toBeVisible();

			await expect(documentationLink).toHaveAttribute(
				'href',
				'https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'
			);
		}
	);

	test(
		'View localizable fragment configuration field value in translated languages',
		{tag: '@LPS-118100'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a fragment with configuration

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			const fragmentEntryName = getRandomString();

			const configuration: FragmentConfiguration = {
				fieldSets: [
					{
						fields: [
							{
								dataType: 'string',
								defaultValue: 'Go Somewhere',
								label: 'Button Text',
								localizable: true,
								name: 'buttonText',
								type: 'text',
							},
						],
						label: 'Button',
					},
				],
			};

			const html =
				'<div class="fragment-example">' +
				'<button type="button" class="btn btn-primary">${configuration.buttonText}</button>' +
				'</div>';

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				configuration,
				fragmentCollectionId,
				groupId: site.id,
				html,
				name: fragmentEntryName,
			});

			// Create a content page with created fragment

			const fragmentName = getRandomString();

			const fragmentDefinition = getFragmentDefinition({
				id: fragmentName,
				key: fragmentEntryName,
			});

			// Create a content page and go to edit mode

			const layoutTitle = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragmentDefinition]),
				siteId: site.id,
				title: layoutTitle,
			});

			// Go to edit mode and change form configuration

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await expect(
				page.locator('.fragment-example').getByText('Go Somewhere')
			).toBeVisible();

			await pageEditorPage.switchLanguage('es-ES');

			await pageEditorPage.selectFragment(fragmentName);

			const value = getRandomString();

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Button Text',
				tab: 'General',
				value,
			});

			await expect(
				page.locator('.fragment-example').getByText(value)
			).toBeVisible();

			await pageEditorPage.switchLanguage('en-US');

			await expect(
				page.locator('.fragment-example').getByText('Go Somewhere')
			).toBeVisible();
		}
	);
});

test.describe('Styles Configuration', () => {
	test(
		'Allows selecting a color palette color',
		{tag: '@LPS-97179'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a page with a Separator fragment

			const separatorId = getRandomString();

			const separatorFragment = getFragmentDefinition({
				id: separatorId,
				key: 'BASIC_COMPONENT-separator',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([separatorFragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			// Go to the created page

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Select the Separator fragment

			await pageEditorPage.selectFragment(separatorId);

			// Select a color in the color palette

			await pageEditorPage.goToConfigurationTab('Styles');

			await page.getByTitle('success', {exact: true}).click();

			await pageEditorPage.waitForChangesSaved();

			// Check that the color is applied

			expect(
				page
					.locator('.component-separator hr')
					.evaluate((element) =>
						element.classList.contains('border-success')
					)
			).toBeTruthy();

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			expect(
				page
					.locator('.component-separator hr')
					.evaluate((element) =>
						element.classList.contains('border-success')
					)
			).toBeTruthy();
		}
	);

	test('Allows changing and resetting spacing', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {
		await page.goto('/');

		// Create a page with a Heading fragment

		const headingId = getRandomString();

		const headingFragment = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingFragment]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Check Saved icon is not visible at the beggining

		await expect(page.getByLabel('Saved')).not.toBeVisible();

		// Change Margin Top with custom value and check change is applied

		await pageEditorPage.changeFragmentSpacing(
			headingId,
			'Margin Top',
			'5',
			'px'
		);
		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: headingId,
				isTopperStyle: true,
				style: 'marginTop',
			})
		).toBe('5px');

		// Change Margin Top value to a custom value

		await pageEditorPage.changeFragmentSpacing(
			headingId,
			'Margin Top',
			'auto',
			'custom'
		);
		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: headingId,
				isTopperStyle: true,
				style: 'marginTop',
			})
		).toBe('0px');

		// Change Margin Top with token value and check change is applied

		await pageEditorPage.changeFragmentSpacing(
			headingId,
			'Margin Top',
			'2'
		);
		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: headingId,
				isTopperStyle: true,
				style: 'marginTop',
			})
		).toBe('8px');

		// Reset to initial value and check change is applied

		await pageEditorPage.resetSpacing(headingId, 'Margin Top');

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: headingId,
				isTopperStyle: true,
				style: 'marginTop',
			})
		).toBe('0px');
	});

	test(
		'Renders all selectors with correct default values',
		{
			tag: '@LPS-136412',
		},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			await page.goto('/');

			// Create a page with a Heading fragment

			const headingId = getRandomString();

			const headingFragment = getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([headingFragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.selectFragment(headingId);

			await pageEditorPage.goToConfigurationTab('Styles');

			// Check correct default values are rendered

			for (const {defaultValue, label, type} of STYLES) {
				if (type === 'button') {
					await expect(
						page.getByRole('button', {
							exact: true,
							name: defaultValue,
						})
					).toHaveAttribute('aria-pressed', 'true');
				}
				else if (type === 'color') {
					await expect(
						page
							.getByLabel(label, {exact: true})
							.getByLabel('Color', {exact: true})
					).toHaveValue(defaultValue);
				}
				else if (type === 'select') {
					expect(
						await page
							.getByLabel(label, {exact: true})
							.evaluate(
								(node: HTMLSelectElement) =>
									node.options[node.selectedIndex].text
							)
					).toBe(defaultValue);
				}
				else {
					await expect(
						page.getByLabel(label, {exact: true})
					).toHaveValue(defaultValue);
				}
			}
		}
	);

	test(
		'Renders correct sections in color picker',
		{tag: '@LPS-140717'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			await page.goto('/');

			// Create a page with a Heading fragment and go to edit mode

			const headingId = getRandomString();

			const headingFragment = getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([headingFragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check correct sections are displayed

			await pageEditorPage.selectFragment(headingId);

			await pageEditorPage.goToConfigurationTab('Styles');

			await page
				.locator('.layout__dropdown-color-picker__selector')
				.click();

			for (const palette of COLOR_PICKER_PALETTES) {
				await expect(
					page
						.locator(
							'.layout__dropdown-color-picker__color-palette'
						)
						.getByText(palette.title)
				).toBeAttached();

				for (const section of palette.sections) {
					await expect(
						page
							.locator(
								'.layout__dropdown-color-picker__color-palette'
							)
							.getByText(section)
					).toBeAttached();
				}
			}
		}
	);

	test('Changes the value in the Color Picker when the reset button is clicked', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {
		await page.goto('/');

		// Create page with heading fragment and go to edit mode

		const headingId = getRandomString();

		const headingFragment = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingFragment]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Select fragment and go to Styles configuration panel

		await pageEditorPage.selectFragment(headingId);

		await pageEditorPage.goToConfigurationTab('Styles');

		const backgroundColorInput = page
			.getByLabel('Background Color')
			.locator('.layout__color-picker__input');

		await fillAndClickOutside(page, backgroundColorInput, '#AAA');

		await page.getByLabel('Reset to Initial Value').click();

		// Check the value gets at least six characters

		await backgroundColorInput.click();

		await fillAndClickOutside(page, backgroundColorInput, '#000');

		await expect(backgroundColorInput).toHaveValue('#000000');
	});
});
