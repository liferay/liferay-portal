/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import getRandomString from '../../../utils/getRandomString';
import getContainerDefinition from '../main/utils/getContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';
import getWidgetDefinition from '../main/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test('Fails to duplicate a container if it has a not instanceable widget', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create a page with a container using the API

	const containerId = getRandomString();

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getContainerDefinition({
				id: containerId,
				pageElements: [
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_blogs_web_portlet_BlogsPortlet',
					}),
				],
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	// Navigate to the page editor

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Click on the duplicate option

	await pageEditorPage.clickFragmentOption(containerId, 'Duplicate');

	// Assert that the error message is displayed

	await expect(page.locator('.alert-danger')).toContainText(
		'The layout could not be duplicated because it contains a widget (Blogs) that can only appear once in the page.'
	);
});

test('Can duplicate a container and the mapping and configuration are preserved', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a page with a container and a heading

	const containerId = getRandomString();
	const headingId = getRandomString();

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getContainerDefinition({
				id: containerId,
				pageElements: [
					getFragmentDefinition({
						id: headingId,
						key: 'BASIC_COMPONENT-heading',
					}),
				],
			}),
		]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Navigate to the page editor

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Change heading configuration

	await pageEditorPage.selectFragment(headingId);

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Heading Level',
		fragmentId: headingId,
		tab: 'General',
		value: 'h2',
	});

	await pageEditorPage.selectEditable(headingId, 'element-text');

	await pageEditorPage.setMappingConfiguration({
		mapping: {
			entity: 'Web Content',
			entry: 'Animal 01 - Dogs and Cats categories',
			field: 'Title',
			folder: 'Animals',
		},
	});

	await pageEditorPage.selectFragment(containerId);

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Heading Level',
		fragmentId: headingId,
		tab: 'General',
		value: 'h2',
	});

	// Duplicate the container

	await pageEditorPage.clickFragmentOption(containerId, 'Duplicate');

	// Assert that the duplicated heading has the same configuration as the original one

	const duplicatedHeadingId = await pageEditorPage.getFragmentId(
		'Heading',
		1
	);

	await pageEditorPage.selectFragment(duplicatedHeadingId);

	expect(page.getByLabel('Heading Level', {exact: true})).toHaveValue('h2');

	await pageEditorPage.selectEditable(duplicatedHeadingId, 'element-text');

	await expect(page.getByLabel('Item', {exact: true})).toHaveValue(
		'Animal 01 - Dogs and Cats categories'
	);
});

test.describe('Container configuration', () => {
	test('Can change the tag of a container', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with a container

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({
					id: containerId,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Navigate to the page editor

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Change the HTML tag

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'HTML Tag',
			fragmentId: containerId,
			tab: 'Advanced',
			value: 'article',
		});

		// Check that the HTML tag has been changed

		expect(
			await page
				.locator('.page-editor__container')
				.evaluate((element) => element.tagName)
		).toBe('ARTICLE');

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		expect(
			await page
				.locator('.lfr-layout-structure-item-container')
				.evaluate((element) => element.tagName)
		).toBe('ARTICLE');
	});

	test('Can make a fixed width container', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with a container

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({
					id: containerId,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Navigate to the page editor

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Change the container width

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Container Width',
			fragmentId: containerId,
			tab: 'General',
			value: 'Fixed Width',
		});

		// Check that the container width has been changed

		expect(
			await page
				.locator('.page-editor__container')
				.evaluate((element) =>
					element.classList.contains('container-fluid')
				)
		).toBeTruthy();

		await pageEditorPage.goToConfigurationTab('Styles');

		// Check that the margin left and right fields are disabled

		expect(page.getByLabel('Margin Left')).toBeDisabled();

		expect(page.getByLabel('Margin Right')).toBeDisabled();
	});

	test('Can configure custom styles', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with a container

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({
					id: containerId,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Navigate to the page editor

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Change container styles

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Content Display',
			fragmentId: containerId,
			tab: 'General',
			value: 'flex-row',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Background Color',
			fragmentId: containerId,
			tab: 'Styles',
			value: 'Danger',
			valueFromStylebook: true,
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Width',
			fragmentId: containerId,
			tab: 'General',
			value: '100',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Height',
			fragmentId: containerId,
			tab: 'General',
			value: '200',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Set Content Visibility to Auto',
			fragmentId: containerId,
			tab: 'Advanced',
			value: true,
		});

		// Check that the styles have been applied in edit mode

		expect(
			await pageEditorPage
				.getFragment(containerId)
				.evaluate((element) => element.classList.contains('flex-row'))
		).toBeTruthy();

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				style: 'backgroundColor',
			})
		).toBe('rgb(218, 20, 20)');

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				isTopperStyle: true,
				style: 'width',
			})
		).toBe('100px');

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				style: 'height',
			})
		).toBe('200px');

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				style: 'contentVisibility',
			})
		).toBe('auto');

		// Check that the styles have been applied in view mode

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const container = page.locator('.lfr-layout-structure-item-container');

		expect(
			await container.evaluate((element) =>
				element.classList.contains('flex-row')
			)
		).toBeTruthy();

		const styles = await container.evaluate((element) =>
			getComputedStyle(element)
		);

		expect(styles.backgroundColor).toBe('rgb(218, 20, 20)');

		// @ts-ignore

		expect(styles.contentVisibility).toBe('auto');
		expect(styles.height).toBe('200px');
		expect(styles.width).toBe('100px');
	});

	test('Can configure a background image', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create a page with a container

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({
					id: containerId,
				}),
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Navigate to the page editor

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map background image

		await pageEditorPage.selectFragment(containerId);

		await pageEditorPage.goToConfigurationTab('Styles');

		await page.getByLabel('Select Image').click();

		const card = page
			.frameLocator('iframe[title="Select"]')
			.locator('[data-title="liferay_logo.png"]');

		await clickAndExpectToBeHidden({
			target: page.locator('.modal-dialog'),
			trigger: card,
		});

		await pageEditorPage.waitForChangesSaved();

		// Check correct image is used for background

		await page
			.locator(
				'.lfr-layout-structure-item-container[style*="liferay_logo-png"]'
			)
			.waitFor();

		// Check that the background image have been applied in view mode

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Assert background image

		await expect(
			page.locator(
				'.lfr-layout-structure-item-container[style*="liferay_logo-png"]'
			)
		).toBeAttached();
	});

	test('Can set a link to the container', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create a page with a container

		const containerId1 = getRandomString();
		const containerId2 = getRandomString();
		const containerId3 = getRandomString();

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({id: containerId1}),
				getContainerDefinition({id: containerId2}),
				getContainerDefinition({id: containerId3}),
			]),
			siteId: pageManagementSite.id,
			title: layoutTitle,
		});

		// Navigate to the page editor

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Change the link of the containers

		await pageEditorPage.selectFragment(containerId1);

		await pageEditorPage.setLinkConfiguration({
			type: 'URL',
			url: 'https://liferay.com',
		});

		await expect(
			page.locator(`.lfr-layout-structure-item-topper-${containerId1} a`)
		).toHaveAttribute('href', 'https://liferay.com');

		await pageEditorPage.selectFragment(containerId2);

		await pageEditorPage.setLinkConfiguration({
			layoutTitle,
			type: 'Page',
		});

		await expect(
			page.locator(`.lfr-layout-structure-item-topper-${containerId2} a`)
		).toHaveAttribute(
			'href',
			`/web${pageManagementSite.friendlyUrlPath}/${layoutTitle}`
		);

		await pageEditorPage.selectFragment(containerId3);

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
			page.locator(`.lfr-layout-structure-item-topper-${containerId3} a`)
		).toHaveAttribute('href', /poodle\.jpg/);

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		const containers = page.locator('.lfr-layout-structure-item-container');

		const firstContainerHref = await containers
			.first()
			.evaluate((element) => element.closest('a')?.getAttribute('href'));

		expect(firstContainerHref).toContain('https://liferay.com');

		const secondContainerHref = await containers
			.nth(1)
			.evaluate((element) => element.closest('a')?.getAttribute('href'));

		expect(secondContainerHref).toContain(
			`/web${pageManagementSite.friendlyUrlPath}/${layoutTitle}`
		);

		const threeContainerHref = await containers
			.last()
			.evaluate((element) => element.closest('a')?.getAttribute('href'));

		expect(threeContainerHref).toContain('poodle.jpg');
	});
});
