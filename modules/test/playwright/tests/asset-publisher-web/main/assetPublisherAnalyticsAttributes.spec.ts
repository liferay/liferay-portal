/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {assetPublisherPagesTest} from '../../../fixtures/assetPublisherPagesTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {templatesPageTest} from '../../template-web/main/fixtures/templatesPageTest';

const ANALYTICS_CONFIGURATION_PID =
	'com.liferay.analytics.settings.configuration.AnalyticsConfiguration';

const ANALYTICS_CONFIGURATION_URL = `/o/headless-admin-configuration/v1.0/instance-configurations/${ANALYTICS_CONFIGURATION_PID}`;

const test = mergeTests(
	apiHelpersTest,
	assetPublisherPagesTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-65399': {enabled: true},
		'LPS-155284': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	templatesPageTest
);

let originalAnalyticsProperties: Record<string, unknown> = {};

test.beforeEach(async ({apiHelpers}) => {
	const configuration = await apiHelpers.get(ANALYTICS_CONFIGURATION_URL);

	originalAnalyticsProperties = configuration?.properties ?? {};

	await apiHelpers.put(ANALYTICS_CONFIGURATION_URL, {
		data: {
			externalReferenceCode: ANALYTICS_CONFIGURATION_PID,
			properties: {
				liferayAnalyticsDataSourceId: 'playwright-stub-data-source',
				liferayAnalyticsFaroBackendSecuritySignature:
					'playwright-stub-signature',
				liferayAnalyticsFaroBackendURL:
					'http://playwright-stub.invalid',
			},
		},
		failOnStatusCode: true,
	});
});

test.afterEach(async ({apiHelpers}) => {
	await apiHelpers.put(ANALYTICS_CONFIGURATION_URL, {
		data: {
			externalReferenceCode: ANALYTICS_CONFIGURATION_PID,
			properties: originalAnalyticsProperties,
		},
		failOnStatusCode: true,
	});
});

test(
	'Emits data-analytics-asset-* attributes for Asset Publisher display templates',
	{
		tag: '@LPD-83537',
	},
	async ({apiHelpers, assetPublisherPage, page, pageEditorPage, site}) => {
		test.slow();

		const displayTemplates = {
			'Abstracts': 'div.asset-abstract p.component-title',
			'Rich Summary': 'h3.asset-title',
			'Table': 'td.table-title',
			'Title List': 'p.list-group-title',
		};

		const title = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: '2024-01-01T00:00:00Z',
			siteId: site.id,
			title,
			viewableBy: 'Anyone',
		});

		const templateNames = Object.keys(displayTemplates);

		const widgetIds = templateNames.map(() => getRandomString());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(
				widgetIds.map((id) =>
					getWidgetDefinition({
						id,
						widgetName:
							'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
					})
				)
			),
			siteId: site.id,
			title: getRandomString(),
		});

		for (let i = 0; i < templateNames.length; i++) {

			// Reload to avoid stale modal tooltip state.

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.goToWidgetConfiguration(widgetIds[i]);

			await assetPublisherPage.openAssetSelectionTab();

			await assetPublisherPage.selectCollectionProvider('Recent Content');

			if (templateNames[i] !== 'Abstracts') {
				await assetPublisherPage.openDisplaySettingsTab();

				await assetPublisherPage.selectDisplayTemplate(
					templateNames[i]
				);
			}

			await assetPublisherPage.saveConfiguration();

			await assetPublisherPage.closeConfiguration();
		}

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		for (const [name, titleElement] of Object.entries(displayTemplates)) {
			const titleLocator = page.locator(
				`${titleElement}[data-analytics-asset-title="${title}"]`
			);

			await expect(titleLocator, `${name}: title`).toBeVisible();

			await expect(titleLocator, `${name}: type`).toHaveAttribute(
				'data-analytics-asset-type',
				'web-content'
			);

			await expect(titleLocator, `${name}: subtype`).toHaveAttribute(
				'data-analytics-asset-subtype',
				'basic-web-content'
			);

			await expect(
				titleLocator,
				`${name}: external reference code`
			).toHaveAttribute('data-analytics-external-reference-code', /.+/);

			await expect(
				titleLocator,
				`${name}: impression action`
			).toHaveAttribute('data-analytics-asset-action', 'impression');
		}

		await expect(
			page.locator(
				`[data-analytics-asset-title="${title}"][data-analytics-asset-action="view"]`
			)
		).not.toHaveCount(0);

		await page
			.locator(
				`div.asset-abstract p.component-title[data-analytics-asset-title="${title}"] a.asset-title`
			)
			.click();

		await expect(
			page.getByRole('heading', {exact: true, name: title})
		).toBeVisible();

		await expect(
			page.locator(
				`[data-analytics-asset-title="${title}"][data-analytics-asset-action="impression"]`
			)
		).not.toHaveCount(0);

		await expect(
			page.locator(
				`[data-analytics-asset-title="${title}"][data-analytics-asset-action="view"]`
			)
		).not.toHaveCount(0);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(
			page
				.locator('div.asset-abstract p.component-title')
				.filter({hasText: title})
		).toBeVisible();

		for (const [name, titleElement] of Object.entries(displayTemplates)) {
			await expect(
				page.locator(
					`${titleElement}[data-analytics-asset-title="${title}"]`
				),
				`${name}: no analytics attributes in edit mode`
			).toHaveCount(0);
		}
	}
);

test(
	'Emits object entry analytics attributes for CMS basic web content',
	{
		tag: '@LPD-93309',
	},
	async ({
		apiHelpers,
		assetPublisherPage,
		collectionsPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.slow();

		// Create a space and connect it to the site

		let space;

		await expect(async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				externalReferenceCode: getRandomString(),
				name: `Space ${getRandomString()}`,
				settings: {},
				type: 'Space',
			});
		}).toPass();

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		// Add a CMS basic web content entry to the space

		const title = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{title},
			'cms/basic-web-contents',
			space.externalReferenceCode
		);

		// Create a dynamic collection on the site scoped to the space

		const collectionName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);

		await collectionsPage.addNewDynamicCollection(collectionName);

		await page
			.getByLabel('Item Type')
			.selectOption({label: 'Basic Web Content (CMS)'});

		await page.getByRole('button', {name: 'Scope'}).click();

		await page.getByRole('button', {name: 'Select Site'}).click();

		await page
			.getByRole('menuitem', {name: 'Other Site, Asset Library, or'})
			.click();

		const scopeIframe = page
			.locator('iframe[title="Scope"]')
			.contentFrame();

		await scopeIframe.getByRole('link', {name: 'Spaces'}).click();

		await scopeIframe
			.getByRole('link', {exact: true, name: space.name})
			.click();

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, undefined, {autoClose: false, first: true});

		// Add an Asset Publisher widget to a site page

		const widgetId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: widgetId,
					widgetName:
						'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Configure the widget to display the dynamic collection

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToWidgetConfiguration(widgetId);

		await assetPublisherPage.openAssetSelectionTab();

		await assetPublisherPage.configurationIframe
			.getByRole('button', {exact: true, name: 'Select Collection'})
			.click();

		await assetPublisherPage.configurationIframe
			.frameLocator('iframe[title="Select Collection"]')
			.getByRole('button', {name: `Select ${collectionName}`})
			.click();

		await assetPublisherPage.saveConfiguration();

		await assetPublisherPage.closeConfiguration();

		// Publish the page and assert the rendered analytics attributes

		await pageEditorPage.publishPage();

		const titleLocator = page
			.locator(
				`[data-analytics-asset-title="${title}"][data-analytics-asset-type]`
			)
			.first();

		await expect(async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(titleLocator).toBeVisible({timeout: 3000});

			await expect(titleLocator).toHaveAttribute(
				'data-analytics-asset-type',
				'object-entry'
			);

			await expect(titleLocator).toHaveAttribute(
				'data-analytics-external-reference-code',
				/.+/
			);

			await expect(titleLocator).toHaveAttribute(
				'data-analytics-object-definition-name',
				'cms-basic-web-content'
			);
		}).toPass();
	}
);

test(
	'Exposes assetAnalyticsAttributesHelper in the widget template designer',
	{
		tag: '@LPD-83537',
	},
	async ({page, site, templatesPage}) => {
		const templateName = getRandomString();

		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		await templatesPage.createWidgetTemplate(
			templateName,
			'Asset Publisher Template'
		);

		await templatesPage.editTemplate(templateName);

		const helperButton = page.getByRole('button', {
			exact: true,
			name: 'Asset Analytics Attributes Helper',
		});

		await expect(helperButton).toBeVisible();

		await helperButton.locator('.preview-icon').hover();

		await expect(
			page.getByText(
				'Build the data-analytics-asset-* attributes for an asset entry.'
			)
		).toBeVisible();

		await helperButton.click();

		const codeMirror = page.locator('.CodeMirror-code');

		await expect(codeMirror).toContainText('entry?has_content');
		await expect(codeMirror).toContainText(
			'assetAnalyticsAttributesHelper.buildAttributes(entry, "impression", "title", locale)'
		);
		await expect(codeMirror).toContainText('entry.getTitle(locale)');
	}
);
