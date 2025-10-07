/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import dragAndDropElement from '../../../utils/dragAndDropElement';
import getGlobalSiteId from '../../../utils/getGlobalSiteId';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import {goToObjectEntity} from '../../setup/page-management-site/main/utils/goToObjectEntity';
import getContainerDefinition from '../main/utils/getContainerDefinition';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	displayPageTemplatesPagesTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	masterPagesPagesTest,
	objectPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

const ENTER_KEY = 'Enter';

test.describe('Related Asset Fragment', () => {
	test(
		'Related Asset fragment displays linked web contents',
		{
			tag: '@LPD-38492',
		},
		async ({
			apiHelpers,
			context,
			displayPageTemplatesPage,
			journalEditArticlePage,
			journalPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create two web contents

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const journalArticleTitle1 = getRandomString();

			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				description: getRandomString(),
				siteId: pageManagementSite.id,
				title: journalArticleTitle1,
				viewableBy: 'Anyone',
			});

			const journalArticleTitle2 = getRandomString();

			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				description: getRandomString(),
				siteId: pageManagementSite.id,
				title: journalArticleTitle2,
				viewableBy: 'Anyone',
			});

			// Add related asset to the first web content

			await journalPage.goto(pageManagementSite.friendlyUrlPath);

			await journalEditArticlePage.editArticle(journalArticleTitle1);

			const row = page
				.frameLocator('iframe[title="Select Basic Web Content"]')
				.locator('.list-group-item', {hasText: journalArticleTitle2});

			await expect(async () => {
				await journalEditArticlePage.openRelatedAsset(
					'Basic Web Content'
				);

				await expect(
					page.getByText('Select Basic Web Content')
				).toBeVisible({timeout: 3000});
			}).toPass();

			await row.getByRole('checkbox').check({trial: true});

			await row.getByRole('checkbox').check();

			await clickAndExpectToBeHidden({
				target: page.locator('.modal-dialog'),
				trigger: page.getByRole('button', {name: 'Done'}),
			});

			await journalEditArticlePage.publishArticle(true);

			await expect(page.locator('.alert-success')).toBeVisible();

			// Create a display page template for Basic Web Content

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Web Content',
				contentType: 'Web Content Article',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Add related assets widget to the template

			await pageEditorPage.addWidget(
				'Content Management',
				'Related Assets'
			);

			// Check that the related assets widget is displayed in the preview with feature

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Select Other Item',
				}),
				trigger: page.getByLabel('Preview With'),
			});

			await clickAndExpectToBeHidden({
				target: page.locator('.modal-dialog'),
				trigger: page
					.frameLocator('iframe[title="Select"]')
					.getByText(journalArticleTitle1, {exact: false}),
			});

			await expect(page.getByText(journalArticleTitle2)).toBeVisible();

			// Check that the related assets widget is displayed in the preview in a new page

			const pagePromise = context.waitForEvent('page');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Preview in a New Tab',
				}),
				trigger: page
					.locator('.control-menu-nav-item')
					.getByLabel('Options', {exact: true}),
			});

			const newPage = await pagePromise;

			await expect(newPage.getByText(journalArticleTitle2)).toBeVisible();
		}
	);
});

test.describe('Banner Slider Fragment', () => {
	test('Check the functionality of the Banner Slider fragment', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a content page with a Banner Slider fragment

		const bannerSliderId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: bannerSliderId,
					key: 'FEATURED_CONTENT-banner-slider',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Change the number of slides

		await pageEditorPage.selectFragment(bannerSliderId);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Number of Slides',
			fragmentId: bannerSliderId,
			tab: 'General',
			value: '4',
		});

		// Check that the number of slides is displayed correctly

		expect(await page.getByLabel('Focus slide').count()).toBe(4);

		// Check that the fourth slide is displayed

		await page.getByLabel('Focus Slide 4').click();

		await expect(
			page.locator('[data-lfr-editable-id="04-01-image"]')
		).toBeVisible();

		await pageEditorPage.editTextEditable(
			bannerSliderId,
			'04-02-title',
			'New title'
		);

		// Check the banner slider is displayed correctly in the view mode

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		expect(await page.getByLabel('Focus slide').count()).toBe(4);

		await expect(page.getByText('New title')).toBeAttached();
	});
});

test.describe('Dropdown Fragment', () => {
	async function openDropdownAndCheckStyle(
		pageEditorPage: PageEditorPage,
		dropdownId: string,
		isDesktop = true,
		style: string,
		value: string
	) {

		// Select dropdown

		await pageEditorPage.selectFragment(dropdownId, isDesktop);

		const dropdownFragment = pageEditorPage.getFragment(
			dropdownId,
			isDesktop
		);

		const dropdownButton = dropdownFragment.locator(
			'.dropdown-fragment-toggle'
		);

		// Open dropdown

		await dropdownButton.press(ENTER_KEY);

		const dropdownMenu = dropdownFragment.locator(
			'.dropdown-fragment-menu'
		);

		await dropdownMenu.waitFor();

		// Check style

		expect(
			await dropdownMenu.evaluate((element, style) => {
				return window.getComputedStyle(element).getPropertyValue(style);
			}, style)
		).toBe(value);

		// Close dropdown

		await dropdownButton.press(ENTER_KEY);

		await dropdownMenu.waitFor({state: 'hidden'});
	}

	test('Check the functionality of the Dropdown fragment', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create content page with a Dropdown fragment inside a Container

		const dropdownId = getRandomString();

		const dropdownDefinition = getFragmentDefinition({
			id: dropdownId,
			key: 'BASIC_COMPONENT-dropdown',
		});

		const containerWidth = '300px';

		const containerDefinition = getContainerDefinition({
			fragmentStyle: {width: containerWidth},
			id: getRandomString(),
			pageElements: [dropdownDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([containerDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Change the dropdown button text

		await pageEditorPage.selectFragment(dropdownId);

		await pageEditorPage.editTextEditable(
			dropdownId,
			'dropdown-text',
			'My Dropdown'
		);

		const dropdownButton = page
			.locator('button')
			.filter({hasText: 'My Dropdown'});

		const dropdownMenu = page.locator('.dropdown-fragment-menu');

		// Check that the dropdown menu opens when hovering over the fragment

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Display on Hover',
			fragmentId: dropdownId,
			tab: 'General',
			value: true,
		});

		await dropdownButton.hover();

		await expect(dropdownButton).toHaveAttribute('aria-expanded', 'true');
		await expect(dropdownMenu).toBeVisible();

		await page.locator('#banner.page-editor__disabled-area').hover();

		await expect(dropdownButton).toHaveAttribute('aria-expanded', 'false');
		await expect(dropdownMenu).not.toBeVisible();

		// Check that the dropdown menu always keeps open in edit mode

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Keep Panel Open in Edit Mode',
			fragmentId: dropdownId,
			tab: 'General',
			value: true,
		});

		await pageEditorPage.publishPage();

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(dropdownButton).toHaveAttribute('aria-expanded', 'true');
		await expect(dropdownMenu).toBeVisible();

		// Change the Panel Type config and check that it works correctly

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Panel Type',
			fragmentId: dropdownId,
			tab: 'General',
			value: 'Full Width',
		});

		await expect(dropdownMenu).toHaveCSS('width', containerWidth);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Panel Type',
			fragmentId: dropdownId,
			tab: 'General',
			value: 'Mega Menu',
		});

		const layoutWidth = await page
			.locator('.page-editor__layout-viewport')
			.evaluate((element) => element.getBoundingClientRect().width);

		await expect(dropdownMenu).toHaveCSS('width', `${layoutWidth}px`);

		// Check that a fragment can be added to the dropzone

		await pageEditorPage.addFragment(
			'Basic Components',
			'Heading',
			page.getByText('Drag and drop fragments or widgets here.', {
				exact: true,
			})
		);

		await expect(page.getByText('Heading Example')).toBeVisible();
	});

	test('Check dropdown menu is displayed correctly in all resolutions', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create content page with a Dropdown fragment and go to edit mode

		const dropdownId = getRandomString();

		const fragmentDefinition = getFragmentDefinition({
			cssClasses: ['d-flex', 'justify-content-end'],
			id: dropdownId,
			key: 'BASIC_COMPONENT-dropdown',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragmentDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Check dropdown style in all viewports

		await openDropdownAndCheckStyle(
			pageEditorPage,
			dropdownId,
			true,
			'right',
			'280px'
		);

		await pageEditorPage.switchViewport('Portrait Phone');

		await openDropdownAndCheckStyle(
			pageEditorPage,
			dropdownId,
			false,
			'right',
			'0px'
		);

		await pageEditorPage.goToConfigurationTab('Advanced');

		await page.getByRole('button', {name: 'Clear All'}).click();

		await pageEditorPage.waitForChangesSaved();

		await openDropdownAndCheckStyle(
			pageEditorPage,
			dropdownId,
			false,
			'left',
			'0px'
		);
	});
});

test.describe('External Video', () => {
	test(
		'Uses External Video fragment and display a video from document library',
		{
			tag: '@LPS-130453',
		},
		async ({
			apiHelpers,
			documentLibraryEditFilePage,
			documentLibraryPage,
			page,
			pageEditorPage,
			site,
		}) => {

			// Add document library external video shortcut

			await documentLibraryPage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'External Video Shortcut',
				}),
				trigger: page.getByRole('button', {exact: true, name: 'New'}),
			});

			await page
				.getByLabel('Video URL')
				.fill('https://www.youtube.com/watch?v=2EPZxIC5ogU');

			await expect(page.getByLabel('Title')).toHaveValue(
				'Life at Liferay - A Look into Liferay Culture'
			);

			await documentLibraryEditFilePage.publishFileEntry();

			// Create page with a Video URL fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-external-video',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Edit video URL and publish the page

			await pageEditorPage.selectVideo({
				fragmentId,
				title: 'Life at Liferay - A Look into Liferay Culture',
			});

			await pageEditorPage.publishPage();

			// Go to view mode and assert video

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const videoIframeLocator = page
				.locator('.video-container')
				.frameLocator('iframe');

			await expect(
				videoIframeLocator.getByText(
					'Life at Liferay - A Look into Liferay Culture'
				)
			).toBeVisible();
		}
	);

	test(
		'Uses External Video fragment and display a video from URL',
		{
			tag: '@LPS-130453',
		},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create page with a Video URL fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-external-video',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.selectVideo({
				fragmentId,
				videoURL: 'https://www.youtube.com/watch?v=2EPZxIC5ogU',
			});

			// Select video URL and publish the page

			await pageEditorPage.publishPage();

			// Go to view mode and assert video

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const videoIframeLocator = page
				.locator('.video-container')
				.frameLocator('iframe');

			await expect(
				videoIframeLocator.getByText(
					'Life at Liferay - A Look into Liferay Culture'
				)
			).toBeVisible();
		}
	);
});

test.describe('Heading Fragment', () => {
	test(
		'Can edit text editable',
		{tag: ['@LPS-78726', '@LPS-85872']},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create page with a heading fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-heading',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check heading editable can be edited

			await pageEditorPage.editTextEditable(
				fragmentId,
				'element-text',
				'New editable fragment text'
			);

			await expect(
				page.getByText('New editable fragment text')
			).toBeAttached();
		}
	);
});

test.describe('HTML Fragment', () => {
	const CUSTOM_FRAGMENT_HTML = `<lfr-editable id="element-html" type="html">
		<h1>HTML Example</h1>
	</lfr-editable>`;

	test(
		'Can edit custom html editable with lfr-editable',
		{tag: ['@LPS-98553', '@LPD-41105']},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create a fragment with lfr-editable

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: getRandomString(),
					}
				);

			const fragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				fragmentCollectionId,
				groupId: site.id,
				html: CUSTOM_FRAGMENT_HTML,
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

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check html editable can be edited

			const value =
				'<div class="text-success"><h1>test html</h1></div><script data-test="test" class="testClass">console.log("test")</script>';

			await pageEditorPage.editHTMLEditable({
				editableId: 'element-html',
				fragmentId: fragmentName,
				value,
			});

			// Check value is loaded in the page correctly

			await expect(page.getByText('test html')).toHaveCount(1);

			// Check value is loaded in the editor correctly

			const editable = pageEditorPage.getEditable({
				editableId: 'element-html',
				fragmentId: fragmentName,
			});

			await editable.click();

			await expect(page.getByText(value)).toBeAttached();

			// Remove the page

			await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);

			// Delete data

			await apiHelpers.jsonWebServicesFragmentCollection.deleteFragmentCollection(
				fragmentCollectionId
			);
		}
	);

	test(
		'Can edit html editable',
		{tag: '@LPS-98553'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create page with a HTML fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-html',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check html editable can be edited

			await pageEditorPage.editHTMLEditable({
				editableId: 'element-html',
				fragmentId,
				value: '<div class="text-success"><h1>test html</h1></div>',
			});

			await expect(page.getByText('test html')).toHaveCount(1);
		}
	);
});

test.describe('Image Fragment', () => {
	test(
		'Select image from document and media',
		{tag: ['@LPS-95045', '@LPS-101328']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with an image fragment

			const imageId = getRandomString();

			const imageFragment = getFragmentDefinition({
				id: imageId,
				key: 'BASIC_COMPONENT-image',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([imageFragment]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Select the image directly

			await pageEditorPage.selectDirectImage('poodle.jpg', imageId);

			expect(
				await page
					.locator('.component-image img')
					.first()
					.getAttribute('src')
			).toContain('poodle.jpg');
		}
	);
});

test.describe('Multiselect Fragment', () => {
	test(
		'Allow submit form if the field is required and at least one item is checked',
		{tag: '@LPD-32038'},
		async ({
			context,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create a display page for the Lemon Basket objects

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

			// Add a form container and map it

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			const fragmentId =
				await pageEditorPage.getFragmentId('Form Container');

			await pageEditorPage.mapFormFragment(
				fragmentId,
				'Lemon Basket (Default)',
				['Lemon Dimensions']
			);

			// Preview the page with a created object

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Select Other Item',
				}),
				trigger: page.getByLabel('Preview With'),
			});

			await clickAndExpectToBeHidden({
				target: page.locator('.modal-dialog'),
				trigger: page
					.frameLocator('iframe[title="Select"]')
					.getByText('plastic', {exact: false}),
			});

			const pagePromise = context.waitForEvent('page');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Preview in a New Tab',
				}),
				trigger: page
					.locator('.control-menu-nav-item')
					.getByLabel('Options', {exact: true}),
			});

			// Extract classNameId and classPK to build the display page url

			const newPage = await pagePromise;

			const pageURL = new URL(newPage.url());

			const classNameId = pageURL.searchParams.get('classNameId');
			const classPK = pageURL.searchParams.get('classPK');

			await displayPageTemplatesPage.publishTemplate();

			// Go to display page url and try the form

			await expect(async () => {
				await page.goto(
					`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${classNameId}/${classPK}`
				);

				await page
					.getByText('Submit', {exact: true})
					.click({timeout: 1000});

				await expect(
					page.getByText(
						'Thank you. Your information was successfully received.'
					)
				).toBeVisible({timeout: 2000});
			}).toPass();
		}
	);
});

test.describe('Paragraph Fragment', () => {
	test(
		'Can edit text editable',
		{tag: ['@LPS-127732']},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create page with a paragraph fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-paragraph',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Check paragraph editable can be edited

			await pageEditorPage.editTextEditable(
				fragmentId,
				'element-text',
				'New editable fragment text'
			);

			await expect(
				page.getByText('New editable fragment text')
			).toBeAttached();
		}
	);

	test(
		'Can use CKEditor options when editing a rich text editable',
		{tag: ['@LPS-127732']},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create page with a paragraph fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-paragraph',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Open editor options

			await pageEditorPage.selectEditable(fragmentId, 'element-text');

			const editable = pageEditorPage.getEditable({
				editableId: 'element-text',
				fragmentId,
			});

			await editable.click();

			await editable.locator('[contenteditable="true"]').click();

			await page.keyboard.press('ControlOrMeta+KeyA');

			const toolbar = page.locator('.ck-toolbar', {
				hasText: 'Text alignment',
			});

			await toolbar.waitFor();

			// Check that the button is visible and works

			await toolbar.getByLabel('Text alignment', {exact: true}).click();
			await toolbar.getByLabel('Align right', {exact: true}).click();

			expect(
				await page
					.locator('.ck-editor__editable p')
					.evaluate((element) => element.style.textAlign)
			).toBe('right');
		}
	);

	test(
		'Editor config contributor client extension is applied',
		{tag: ['@LPD-54262']},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create page with a paragraph fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-paragraph',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Open editor options

			await pageEditorPage.selectEditable(fragmentId, 'element-text');

			const editable = pageEditorPage.getEditable({
				editableId: 'element-text',
				fragmentId,
			});

			await editable.dblclick();

			await editable.locator('[contenteditable="true"]').dblclick();

			// Assert "Accessibility Help" button is visible as provided by the CX

			await page.getByText('A paragraph').selectText();

			await expect(
				page.getByLabel('Accessibility help')
			).toBeInViewport();
		}
	);
});

test.describe('Slider Fragment', () => {
	test('Checks that the Slider fragment works correctly', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {
		const expectSlideIsActive = async (name: string) => {
			await expect(page.getByLabel(name, {exact: true})).toHaveClass(
				/active/
			);
			await expect(page.getByLabel(`Go to ${name}`)).toHaveAttribute(
				'aria-current',
				'true'
			);
		};

		const expectSlideIsNotActive = async (name: string) => {
			await expect(page.getByLabel(name, {exact: true})).not.toHaveClass(
				/active/
			);
			await expect(page.getByLabel(`Go to ${name}`)).toHaveAttribute(
				'aria-current',
				'false'
			);
		};

		// Create page with Slider fragment and go to edit mode

		const sliderId = getRandomString();

		const sliderDefinition = getFragmentDefinition({
			fragmentConfig: {
				numberOfSlides: 3,
			},
			fragmentFields: [
				{
					id: '02-02-title',
					value: {
						text: {
							value_i18n: {
								en_US: 'Slide 2',
							},
						},
					},
				},
				{
					id: '01-02-title',
					value: {
						text: {
							value_i18n: {
								en_US: 'Slide 1',
							},
						},
					},
				},
			],
			id: sliderId,
			key: 'BASIC_COMPONENT-slider',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([sliderDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Change the number of slides

		const slide = page.locator('[aria-roledescription="slide"]');

		await slide.first().waitFor();

		expect(await slide.all()).toHaveLength(3);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Number of Slides',
			fragmentId: sliderId,
			tab: 'General',
			value: '2',
		});

		expect(await slide.all()).toHaveLength(2);

		// Check Auto Hide Play button

		const playButton = page.locator('.stopped');

		await expect(playButton).not.toHaveClass(
			/carousel-toggle-button--always-visible/
		);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Auto Hide Play Button',
			fragmentId: sliderId,
			tab: 'General',
			value: false,
		});

		await expect(playButton).toHaveClass(
			/carousel-toggle-button--always-visible/
		);

		await pageEditorPage.publishPage();

		// Go to view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Pause the carousel

		await expect(page.getByText('Stop slide rotation')).toBeVisible();

		await page.locator('.carousel-toggle-button.playing').click();

		await expect(page.getByText('Start slide rotation')).toBeVisible();

		// Check the slide 1 is active

		await expectSlideIsActive('Slide 1');
		await expectSlideIsNotActive('Slide 2');

		// Click next slide and check the slide 2 is active

		await page.getByLabel('Next Slide').click();

		await expectSlideIsActive('Slide 2');
		await expectSlideIsNotActive('Slide 1');

		// Click previous slide and check the slide 1 is active

		await page.getByLabel('Previous Slide').click();

		await expectSlideIsActive('Slide 1');
		await expectSlideIsNotActive('Slide 2');

		// Check accessibility

		await checkAccessibility({page, selectors: ['.component-slider']});
	});
});

test.describe('Tabs Fragment', () => {
	const getTab = (tabName: string, page: Page) =>
		page.locator('.nav-item button', {
			has: page.getByText(tabName),
		});

	test('Checks that the Tabs fragment works correctly and has the correct semantics in small resolution', async ({
		apiHelpers,
		page,
		site,
	}) => {

		// Create page with a Tabs fragment

		const tabsDefinition = getFragmentDefinition({
			fragmentConfig: {
				numberOfTabs: 2,
			},
			fragmentFields: [
				{
					id: 'title1',
					value: {},
				},
				{
					id: 'title2',
					value: {},
				},
			],
			id: getRandomString(),
			key: 'BASIC_COMPONENT-tabs',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([tabsDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Set small resolution and go to view mode

		await page.setViewportSize({height: 600, width: 600});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		let dropdownButton = page.getByLabel('Current Selection: Tab 1');

		await expect(dropdownButton).toHaveAttribute(
			'aria-activedescendant',
			''
		);
		await expect(dropdownButton).toHaveAttribute('aria-expanded', 'false');
		await expect(dropdownButton).toHaveAttribute(
			'aria-haspopup',
			'listbox'
		);
		await expect(dropdownButton).toHaveAttribute('role', 'combobox');

		// Open the dropdown and navigate by keyboard to select the Tab 2

		await dropdownButton.press('Enter');

		await expect(dropdownButton).toHaveAttribute('aria-expanded', 'true');

		await page.keyboard.press('Tab');
		await page.keyboard.press('Tab');
		await page.keyboard.press('Enter');

		dropdownButton = page.getByLabel('Current Selection: Tab 2');

		// Check that the button has the correct text and the focus when the tab is selected

		expect((await dropdownButton.textContent()).trim()).toBe('Tab 2');

		await expect(dropdownButton).toBeFocused();

		// Check accessibility

		await checkAccessibility({page, selectors: ['.component-tabs']});
	});

	test('Check the Persist Selected Tab configuration', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create page with a Tabs fragment

		const tabsId = getRandomString();

		const tabsDefinition = getFragmentDefinition({
			fragmentConfig: {
				numberOfTabs: 2,
			},
			fragmentFields: [
				{
					id: 'title1',
					value: {},
				},
				{
					id: 'title2',
					value: {},
				},
			],
			id: tabsId,
			key: 'BASIC_COMPONENT-tabs',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([tabsDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		const firstTab = getTab('Tab 1', page);

		const secondTab = getTab('Tab 2', page);

		// Check that the first tab is activated

		await expect(firstTab).toHaveAttribute('aria-selected', 'true');
		await expect(secondTab).toHaveAttribute('aria-selected', 'false');

		// Select the second tab and refresh the page to check if the selection persists

		await secondTab.press(ENTER_KEY);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(firstTab).toHaveAttribute('aria-selected', 'false');
		await expect(secondTab).toHaveAttribute('aria-selected', 'true');

		// Change the configuration so that the selection does not persist

		await pageEditorPage.selectFragment(tabsId);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Persist Selected Tab',
			fragmentId: tabsId,
			tab: 'General',
			value: false,
		});

		// Refresh the page and check that the selection does not persist

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(secondTab).toHaveAttribute('aria-selected', 'false');
		await expect(firstTab).toHaveAttribute('aria-selected', 'true');
	});

	test('Check that tabs have their corresponding content on a master page', async ({
		apiHelpers,
		masterPagesPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a master page and add a Tabs fragment

		const masterPageName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: site.id,
				name: masterPageName,
				type: 'master-layout',
			}
		);

		await masterPagesPage.goto(site.friendlyUrlPath);

		await masterPagesPage.editMaster(masterPageName);

		await pageEditorPage.addFragment('Basic Components', 'Tabs');

		// Add the main drop zone on the first tab and a Heading fragment in the second tab

		const tabDropZone = page
			.getByText('Drag and drop fragments or widgets here.')
			.first();

		await dragAndDropElement({
			dragTarget: page.locator('[data-name="Drop Zone"]'),
			dropTarget: tabDropZone,
			page,
		});

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		const secondTab = getTab('Tab 2', page);

		await secondTab.press('Enter');

		await tabDropZone.waitFor();

		await dragAndDropElement({
			dragTarget: page.locator('[data-name="Heading"]'),
			dropTarget: page
				.getByText('Drag and drop fragments or widgets here.')
				.first(),
			page,
		});

		// Check that each tab has the corresponding content

		const firstTab = getTab('Tab 1', page);

		await firstTab.press('Enter');

		await expect(
			page.getByText(
				'Fragments and widgets for pages based on this master will be placed here.'
			)
		).toBeVisible();

		await secondTab.press('Enter');

		await expect(page.getByText('Heading Example')).toBeVisible();
	});
});

test.describe('Tags Fragment', () => {
	test('Uses Tags fragment for Forms in a Content Page', async ({
		apiHelpers,
		page,
		pageManagementSite,
	}) => {

		// Get the id of Lemon object from the site initializer

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Lemon')
			)
		).body;

		// Create a Form Container with a Tags fragment and Submit fragment

		const firstTagsFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'com.liferay.fragment.renderer.categorization.inputs.internal.TagsInputFragmentRenderer',
		});

		const secondTagsFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'com.liferay.fragment.renderer.categorization.inputs.internal.TagsInputFragmentRenderer',
		});

		const submitFragmentDefinition = getFragmentDefinition({
			fragmentConfig: {
				buttonSize: 'nm',
				buttonType: 'primary',
				submittedEntryStatus: 'approved',
			},
			fragmentFields: [
				{
					id: 'submit-button-text',
					value: {
						fragmentLink: {},
					},
				},
			],
			id: getRandomString(),
			key: 'INPUTS-submit-button',
		});

		const inputDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_lemonSize',
			},
			id: getRandomString(),
			key: 'INPUTS-text-input',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [
				inputDefinition,
				firstTagsFragmentDefinition,
				secondTagsFragmentDefinition,
				submitFragmentDefinition,
			],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Create two tags in Page Management Site

		for (const tagName of ['Dogs', 'Cats']) {
			await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
				name: tagName,
				siteId: pageManagementSite.id,
			});
		}

		// Create one tag on Global

		const globalSiteId = await getGlobalSiteId(apiHelpers);

		const globalTag =
			await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
				name: 'Rabbits',
				siteId: globalSiteId,
			});

		// Go to view mode of the created page, select a tag for each fragment and submit the form

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await page.getByRole('textbox', {name: 'Lemon Size'}).fill('Tags test');

		await page.getByRole('combobox').first().click();
		await page.getByRole('option', {exact: true, name: 'Dogs'}).click();

		await page.getByRole('combobox').first().click();
		await page.getByRole('option', {exact: true, name: 'Rabbits'}).click();

		await page.getByRole('combobox').nth(1).click();
		await page.getByRole('option', {exact: true, name: 'Cats'}).click();

		await page.getByRole('button', {name: 'Submit'}).click();

		await page
			.getByText('Thank you. Your information was successfully received.')
			.waitFor();

		// Go to the object definition page and check the Tags fragment

		await goToObjectEntity({
			entityName: 'Lemon',
			page,
		});

		const objectRow = page
			.locator('.fds tr')
			.filter({hasText: 'Tags test'});

		await objectRow.waitFor();

		await objectRow.getByRole('link').click();

		await expect(page.getByLabel('Other Metadata')).toContainText(
			'RabbitsCatsDogs'
		);

		// Remove the tag created on Global

		await apiHelpers.headlessAdminTaxonomy.deleteKeyword({
			id: globalTag.id,
		});
	});

	test('Checks that an info message appears when categorization is disabled', async ({
		apiHelpers,
		objectDetailsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Get Lemon Basket object id from the site initializer

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Lemon Basket')
			)
		).body;

		// Set the "Enable Categorization of Object entries" configuration to false

		await objectDetailsPage.goto('Lemon Basket');

		await objectDetailsPage.updateConfiguration({
			fieldLabel: 'Enable Categorization of Object entries',
			value: false,
		});

		// Create a Form Container with a Tags fragment

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'com.liferay.fragment.renderer.categorization.inputs.internal.TagsInputFragmentRenderer',
				}),
			],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and check the info message

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await expect(
			page.getByText(
				'Categorization is disabled for the selected content. To show categories in this fragment, categorization must be enabled.'
			)
		).toBeVisible();

		// Reset initial configuration

		await objectDetailsPage.goto('Lemon Basket');

		await objectDetailsPage.updateConfiguration({
			fieldLabel: 'Enable Categorization of Object entries',
			value: true,
		});
	});
});

test.describe('Video URL', () => {
	test(
		'Uses Video URL fragment and display a video',
		{
			tag: '@LPS-99176',
		},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create page with a Video URL fragment and go to edit mode

			const fragmentId = getRandomString();

			const fragment = getFragmentDefinition({
				id: fragmentId,
				key: 'BASIC_COMPONENT-video',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([fragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Edit video URL and publish the page

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'URL',
				fragmentId,
				tab: 'General',
				value: 'https://www.youtube.com/watch?v=2EPZxIC5ogU',
			});

			await pageEditorPage.publishPage();

			// Go to view mode and assert video

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const videoIframeLocator = page.frameLocator(
				'iframe[title="Life at Liferay - A Look into Liferay Culture"]'
			);

			await expect(
				videoIframeLocator.getByText(
					'Life at Liferay - A Look into Liferay Culture'
				)
			).toBeVisible();
		}
	);
});

test.describe('Dropzone', () => {
	test(
		'Create a fragment with multiple Drop Zone areas and add a fragment into one of them to save it as a composition',
		{
			tag: '@LPS-101258',
		},
		async ({
			apiHelpers,
			fragmentEditorPage,
			fragmentsPage,
			page,
			pageEditorPage,
			site,
		}) => {

			// Go to fragment administration and create fragment set

			await fragmentsPage.goto(site.friendlyUrlPath);

			const fragmentSetName = getRandomString();

			await fragmentsPage.createFragmentSet(fragmentSetName);

			// Create fragment

			const fragmentName = getRandomString();

			await fragmentsPage.createFragment(
				fragmentSetName,
				fragmentName,
				'basic'
			);

			await fragmentEditorPage.addHTML(` 
			<lfr-drop-zone></lfr-drop-zone>	
			<lfr-drop-zone></lfr-drop-zone>	
			`);

			await fragmentEditorPage.publish();

			// Create a page with a container and the fragment inside

			const title = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title,
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.addFragment('Layout Elements', 'Container');

			const containerId = await pageEditorPage.getFragmentId('Container');

			await pageEditorPage.addFragment(
				fragmentSetName,
				fragmentName,
				pageEditorPage.getFragment(containerId)
			);

			// Add a fragment in one of the drop zones

			await pageEditorPage.addFragment(
				'Basic Components',
				'Button',
				page
					.getByText('Drag and drop fragments or widgets here.')
					.first()
			);

			// Verify that the fragments are present

			await expect(page.getByText('Go Somewhere')).toBeVisible();

			await expect(
				page.getByText('Drag and drop fragments or widgets here.')
			).toBeVisible();

			// Save the composition

			await pageEditorPage.clickFragmentOption(
				containerId,
				'Save Composition'
			);

			const compositionName = getRandomString();

			await page.getByPlaceholder('Name').fill(compositionName);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page, 'The fragment was created successfully.');

			// Check the composition has been saved

			await fragmentsPage.goto(site.friendlyUrlPath);

			await fragmentsPage.gotoFragmentSet(fragmentSetName);

			await expect(page.getByText(compositionName)).toBeVisible();
		}
	);
});

test.describe('Custom Fragments', () => {
	test(
		'Create a fragment using data-lfr-priority and check it works',
		{
			tag: '@LPS-121281',
		},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create new fragment collection

			const fragmentCollectionName = getRandomString();

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: fragmentCollectionName,
					}
				);

			// Create custom fragment using data-lfr-priority

			const fragmentName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				fragmentCollectionId,
				groupId: site.id,
				html: `<div class="fragment-example">
					<p data-lfr-editable-id="priority-2" data-lfr-editable-type="text" data-lfr-priority="2">
							Priority 2
					</p>
					<p data-lfr-editable-id="priority-1" data-lfr-editable-type="text" data-lfr-priority="1">
						Priority 1
					</p>
				</div>`,
				name: fragmentName,
				type: 'component',
			});

			// Create a content page and go to edit mode

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Add the fragment to the page and go to Browser panel

			await pageEditorPage.addFragment(
				fragmentCollectionName,
				fragmentName
			);

			await pageEditorPage.goToSidebarTab('Browser');

			// Check data-lfr-priority works

			const fragmentId = await pageEditorPage.getFragmentId(fragmentName);

			await pageEditorPage.selectFragment(fragmentId);

			await page
				.locator('.page-editor__page-structure__tree-node')
				.filter({hasText: 'priority-1'})
				.waitFor();

			const fragmentNode = page.locator('.treeview-item', {
				hasText: fragmentName,
			});

			await expect(
				fragmentNode.locator('.treeview-item').nth(0)
			).toContainText('priority-1');

			await expect(
				fragmentNode.locator('.treeview-item').nth(1)
			).toContainText('priority-2');
		}
	);
});

test.describe('Accordion Fragment', () => {
	test('Check the functionality of the Accordion fragment', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create content page with an Accordion fragment inside a Container

		const accordionId = getRandomString();

		const accordionDefinition = getFragmentDefinition({
			id: accordionId,
			key: 'BASIC_COMPONENT-accordion',
		});

		const containerDefinition = getContainerDefinition({
			id: getRandomString(),
			pageElements: [accordionDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([containerDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Change the accordion text

		await pageEditorPage.editTextEditable(
			accordionId,
			'accordion-title',
			'My Accordion'
		);

		// Check that a fragment can be added to the dropzone of the accordion

		await pageEditorPage.addFragment(
			'Basic Components',
			'Heading',
			page.getByText('Drag and drop fragments or widgets here.', {
				exact: true,
			})
		);

		await expect(page.getByText('Heading Example')).toHaveCount(1);

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const accordionButton = page
			.locator('button')
			.filter({hasText: 'My Accordion'});

		await accordionButton.click();

		await expect(
			page.locator('.collapse-icon-closed').first()
		).toBeVisible();
		await expect(
			page.locator('.collapse-icon-open').first()
		).not.toBeVisible();

		await expect(
			accordionButton.getByText('Heading Example')
		).not.toBeVisible();
	});
});

test(
	'Check multiple fragments can be duplicated after multiselecting them',
	{tag: ['@LPD-47080']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a heading and a button and go to edit mode

		const headingId = getRandomString();
		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const buttonId = getRandomString();
		const buttonDefinition = getFragmentDefinition({
			id: buttonId,
			key: 'BASIC_COMPONENT-button',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				buttonDefinition,
				headingDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Go to Browser and select both fragments

		await pageEditorPage.goToSidebarTab('Browser');

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__topper__title', {
				hasText: 'Heading',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Heading',
			}),
		});

		await page.keyboard.down('Control');

		await clickAndExpectToBeVisible({
			target: page.getByText('2 Items Selected'),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Button',
			}),
		});

		// Duplicate the fragments and check it works well

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Duplicate'}),
			trigger: page.getByLabel('Actions for Selected Items'),
		});

		await pageEditorPage.waitForChangesSaved();

		await expect(page.getByText('Go Somewhere')).toHaveCount(2);
		await expect(page.getByText('Heading Example')).toHaveCount(2);
	}
);

test(
	'Check parent fragments are selected when doing a range multiselect of editables',
	{tag: ['@LPD-48393']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page and go to edit mode

		const headingId = getRandomString();
		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const buttonId = getRandomString();
		const buttonDefinition = getFragmentDefinition({
			id: buttonId,
			key: 'BASIC_COMPONENT-button',
		});

		const paragraphId = getRandomString();
		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				headingDefinition,
				buttonDefinition,
				paragraphDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Go to Browser and expand fragments

		await pageEditorPage.goToSidebarTab('Browser');

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'element-text',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Heading',
			}),
		});

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'link',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Button',
			}),
		});

		await clickAndExpectToBeVisible({
			target: page
				.locator('.page-editor__page-structure__tree-node', {
					hasText: 'text',
				})
				.nth(1),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Paragraph',
			}),
		});

		// Select Heading

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__topper__title', {
				hasText: 'Heading',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Heading',
			}),
		});

		// Select next fragment editables with Shift and check selection is correct

		await page.keyboard.down('Shift');

		await clickAndExpectToBeVisible({
			target: page.getByText('2 Items Selected'),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'link',
			}),
		});

		await clickAndExpectToBeVisible({
			target: page.getByText('3 Items Selected'),
			trigger: page
				.locator('.page-editor__page-structure__tree-node', {
					hasText: 'text',
				})
				.nth(1),
		});
	}
);
