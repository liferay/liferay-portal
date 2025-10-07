/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import getContainerDefinition from '../main/utils/getContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Can save fragment composition with save inline content and with save mapping configuration and link, then create a new content page and add the saved fragment composition',
	{
		tag: '@LPS-101255',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add basic web content

		const basicWebContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: null,
			siteId: site.id,
			title: basicWebContentTitle,
		});

		// Add basic document

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(
					__dirname,
					'../main/dependencies/file_upload_image_1.jpg'
				)
			)
		);

		// Add new widget layout

		const widgetLayoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: widgetLayoutTitle,
		});

		// Add new layout with a container with multiple fragments

		const containerId = getRandomString();

		const buttonId = getRandomString();

		const buttonDefinition = getFragmentDefinition({
			id: buttonId,
			key: 'BASIC_COMPONENT-button',
		});

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const imageId = getRandomString();

		const imageDefinition = getFragmentDefinition({
			id: imageId,
			key: 'BASIC_COMPONENT-image',
		});

		const paragraphId = getRandomString();

		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const containerDefinition = getContainerDefinition({
			id: containerId,
			pageElements: [
				buttonDefinition,
				headingDefinition,
				imageDefinition,
				paragraphDefinition,
			],
		});

		const firstlayoutTitle = getRandomString();

		const firstLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([containerDefinition]),
			siteId: site.id,
			title: firstlayoutTitle,
		});

		// Navigate to the page editor

		await pageEditorPage.goto(firstLayout, site.friendlyUrlPath);

		// Change the link of the containers

		await pageEditorPage.selectEditable(buttonId, 'link');

		await page.getByRole('tab', {exact: true, name: 'Link'}).click();

		await pageEditorPage.setLinkConfiguration({
			layoutTitle: widgetLayoutTitle,
			type: 'Page',
		});

		// Map heading fragment to web content title

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await pageEditorPage.setMappedItem({
			entity: 'Web Content',
			entry: basicWebContentTitle,
			field: 'Title',
		});

		// Select the image directly

		await pageEditorPage.selectDirectImage(document.title, imageId);

		// Add inline text to paragraph fragment

		await pageEditorPage.editTextEditable(
			paragraphId,
			'element-text',
			'New editable fragment text'
		);

		// Configure the styles of heading fragment

		await pageEditorPage.selectFragment(headingId);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Heading Level',
			fragmentId: headingId,
			tab: 'General',
			value: 'h2',
		});

		await pageEditorPage.goToConfigurationTab('Styles');

		await page.getByLabel('Align Center').click();

		// Save fragment composition

		await pageEditorPage.clickFragmentOption(
			containerId,
			'Save Composition'
		);

		const compositionName = getRandomString();

		await page.getByPlaceholder('Name').fill(compositionName);

		await page.getByLabel('Save Inline Content').check();

		await page.getByLabel('Save Mapping Configuration and Link').check();

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'The fragment was created successfully.');

		// Add another layout

		const secondLayoutTitle = getRandomString();

		const secondLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: secondLayoutTitle,
		});

		// Go to edit mode

		await pageEditorPage.goto(secondLayout, site.friendlyUrlPath);

		// Add fragment composition

		await pageEditorPage.addFragment('Saved Fragments', compositionName);

		// Assert button fragment

		const secondButtonId = await pageEditorPage.getFragmentId('Button');

		await pageEditorPage.selectEditable(secondButtonId, 'link');

		await page.getByRole('tab', {exact: true, name: 'Link'}).click();

		await expect(
			page
				.getByLabel('Configuration Panel')
				.locator('.form-group')
				.getByLabel('Link', {exact: true})
		).toHaveValue('fromLayout');

		await expect(page.getByPlaceholder('No Page Selected')).toHaveValue(
			widgetLayoutTitle
		);

		// Assert heading fragment

		const secondHeadingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectFragment(secondHeadingId);

		await expect(
			page.getByLabel('Heading Level', {exact: true})
		).toHaveValue('h2');

		await pageEditorPage.goToConfigurationTab('Styles');

		await expect(page.getByLabel('Align Center')).toHaveAttribute(
			'aria-pressed',
			'true'
		);

		// Assert image fragment

		const secondImageId = await pageEditorPage.getFragmentId('Image');

		await pageEditorPage.selectEditable(secondImageId, 'image-square');

		await expect(page.getByLabel('Source Selection')).toHaveValue('direct');

		await expect(
			page.getByPlaceholder('No Image Selected')
		).not.toBeEmpty();

		expect(
			await page.locator('.component-image img').getAttribute('src')
		).toContain(document.title);

		// Assert paragraph fragment

		await expect(
			page.getByText('New editable fragment text')
		).toBeVisible();
	}
);

test(
	'Can save fragment composition without save inline content and without save mapping configuration and link, then create a new content page and add the saved fragment composition',
	{
		tag: '@LPS-101255',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add basic web content

		const basicWebContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: null,
			siteId: site.id,
			title: basicWebContentTitle,
		});

		// Add basic document

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(
					__dirname,
					'../main/dependencies/file_upload_image_1.jpg'
				)
			)
		);

		// Add new widget layout

		const widgetLayoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: widgetLayoutTitle,
		});

		// Add new layout with a container with multiple fragments

		const containerId = getRandomString();

		const buttonId = getRandomString();

		const buttonDefinition = getFragmentDefinition({
			id: buttonId,
			key: 'BASIC_COMPONENT-button',
		});

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const imageId = getRandomString();

		const imageDefinition = getFragmentDefinition({
			id: imageId,
			key: 'BASIC_COMPONENT-image',
		});

		const paragraphId = getRandomString();

		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const containerDefinition = getContainerDefinition({
			id: containerId,
			pageElements: [
				buttonDefinition,
				headingDefinition,
				imageDefinition,
				paragraphDefinition,
			],
		});

		const firstlayoutTitle = getRandomString();

		const firstLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([containerDefinition]),
			siteId: site.id,
			title: firstlayoutTitle,
		});

		// Navigate to the page editor

		await pageEditorPage.goto(firstLayout, site.friendlyUrlPath);

		// Change the link of the containers

		await pageEditorPage.selectEditable(buttonId, 'link');

		await page.getByRole('tab', {exact: true, name: 'Link'}).click();

		await pageEditorPage.setLinkConfiguration({
			layoutTitle: widgetLayoutTitle,
			type: 'Page',
		});

		// Map heading fragment to web content title

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await pageEditorPage.setMappedItem({
			entity: 'Web Content',
			entry: basicWebContentTitle,
			field: 'Title',
		});

		// Select the image directly

		await pageEditorPage.selectDirectImage(document.title, imageId);

		// Add inline text to paragraph fragment

		await pageEditorPage.editTextEditable(
			paragraphId,
			'element-text',
			'New editable fragment text'
		);

		// Configure the styles of heading fragment

		await pageEditorPage.selectFragment(headingId);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Heading Level',
			fragmentId: headingId,
			tab: 'General',
			value: 'h2',
		});

		await pageEditorPage.goToConfigurationTab('Styles');

		await page.getByLabel('Align Center').click();

		// Save fragment composition

		await pageEditorPage.clickFragmentOption(
			containerId,
			'Save Composition'
		);

		const compositionName = getRandomString();

		await page.getByPlaceholder('Name').fill(compositionName);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'The fragment was created successfully.');

		// Add another layout

		const secondLayoutTitle = getRandomString();

		const secondLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: secondLayoutTitle,
		});

		// Go to edit mode

		await pageEditorPage.goto(secondLayout, site.friendlyUrlPath);

		// Add fragment composition

		await pageEditorPage.addFragment('Saved Fragments', compositionName);

		// Assert button fragment

		const secondButtonId = await pageEditorPage.getFragmentId('Button');

		await pageEditorPage.selectEditable(secondButtonId, 'link');

		await page.getByRole('tab', {exact: true, name: 'Link'}).click();

		await expect(
			page
				.getByLabel('Configuration Panel')
				.locator('.form-group')
				.getByLabel('Link', {exact: true})
		).toHaveValue('manual');

		await expect(
			page
				.getByLabel('Configuration Panel')
				.locator('.form-group')
				.getByLabel('URL', {exact: true})
		).toBeEmpty();

		// Assert heading fragment

		const secondHeadingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectFragment(secondHeadingId);

		await expect(
			page.getByLabel('Heading Level', {exact: true})
		).toHaveValue('h2');

		await pageEditorPage.goToConfigurationTab('Styles');

		await expect(page.getByLabel('Align Center')).toHaveAttribute(
			'aria-pressed',
			'true'
		);

		// Assert image fragment

		const secondImageId = await pageEditorPage.getFragmentId('Image');

		await pageEditorPage.selectEditable(secondImageId, 'image-square');

		await expect(page.getByLabel('Source Selection')).toHaveValue('direct');

		await expect(page.getByPlaceholder('No Image Selected')).toBeEmpty();

		expect(
			await page.locator('.component-image img').getAttribute('src')
		).not.toContain(document.title);

		// Assert paragraph fragment

		await expect(
			page.getByText('A paragraph is a self-contained unit')
		).toBeVisible();
	}
);
