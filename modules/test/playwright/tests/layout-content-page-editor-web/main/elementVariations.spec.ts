/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {audiencesPagesTest} from '../../../fixtures/audiencesPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi} from '../../../utils/performLogin';
import {waitForSPAToBeLoaded} from '../../../utils/waitForSPAToBeLoaded';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	audiencesPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-85746': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Element variations are applied in view mode for a matching audience',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		browser,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create an audience matching the browser language

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: audienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Heading and a Paragraph fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-paragraph',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Create a variation replacing the heading HTML and another one
		// hiding the paragraph

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const variationText = 'Variation ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${variationText}</span>`,
			name: 'Replace heading',
			pageElementLabel: 'Heading (element-text)',
		});

		await elementVariationsPage.createElementVariation({
			audienceName,
			hide: true,
			name: 'Hide paragraph',
			pageElementLabel: 'Paragraph (element-text)',
		});

		// Publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The variations are applied in view mode

		const paragraphDefaultText =
			'A paragraph is a self-contained unit of a discourse';

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText(variationText)).toBeVisible();

		await expect(page.getByText('Heading Example')).not.toBeVisible();

		await expect(page.getByText(paragraphDefaultText)).not.toBeVisible();

		// The page renders unchanged for a visitor matching no audience

		const nonMatchingContext = await browser.newContext({locale: 'es-ES'});

		const nonMatchingPage = await nonMatchingContext.newPage();

		await performLoginViaApi({page: nonMatchingPage, screenName: 'test'});

		await nonMatchingPage.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(
			nonMatchingPage.getByText('Heading Example')
		).toBeVisible();

		await expect(
			nonMatchingPage.getByText(paragraphDefaultText)
		).toBeVisible();

		await expect(
			nonMatchingPage.getByText(variationText)
		).not.toBeVisible();

		await nonMatchingContext.close();
	}
);

test(
	'Translated HTML and JavaScript fields follow the page language',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create an audience matching the browser name

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Browser Name',
			name: audienceName,
			operator: 'Contains',
			value: 'Chrome',
		});

		// Create a page with a Heading fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Create a variation with translated HTML and JavaScript fields

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const englishHTMLText = 'HTMLEN' + getRandomString();
		const englishJavaScriptText = 'JSEN' + getRandomString();
		const spanishHTMLText = 'HTMLES' + getRandomString();
		const spanishJavaScriptText = 'JSES' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${englishHTMLText}</span>`,
			javaScript: `element.append(' ${englishJavaScriptText}');`,
			name: 'Translated heading',
			pageElementLabel: 'Heading (element-text)',
			translations: [
				{
					html: `<span>${spanishHTMLText}</span>`,
					javaScript: `element.append(' ${spanishJavaScriptText}');`,
					languageId: 'es-ES',
				},
			],
		});

		// Publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The default page language applies the default language payloads

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText(englishHTMLText)).toBeVisible();

		await expect(page.getByText(englishJavaScriptText)).toBeVisible();

		// The translated page applies the translated payloads

		await page.goto(
			`/es/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.getByText(spanishHTMLText)).toBeVisible();

		await expect(page.getByText(spanishJavaScriptText)).toBeVisible();

		await expect(page.getByText(englishHTMLText)).not.toBeVisible();

		// Return to the default language before cleaning up

		await page.goto(
			`/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);
	}
);

test(
	'Applies the highest priority variation when a visitor matches several audiences',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create two audiences that both match the browser language. The first
		// created audience ranks higher in the definition order and therefore
		// takes precedence.

		const firstAudienceName = 'Audience ' + getRandomString();
		const secondAudienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: firstAudienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: secondAudienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Heading fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Bind a variation to each audience on the same heading element

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const firstVariationText = 'First ' + getRandomString();
		const secondVariationText = 'Second ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName: firstAudienceName,
			html: `<span>${firstVariationText}</span>`,
			name: 'First audience variation',
			pageElementLabel: 'Heading (element-text)',
		});

		await elementVariationsPage.createElementVariation({
			audienceName: secondAudienceName,
			html: `<span>${secondVariationText}</span>`,
			name: 'Second audience variation',
			pageElementLabel: 'Heading (element-text)',
		});

		// Publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The visitor matches both audiences, so only the higher priority
		// audience variation is applied

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText(firstVariationText)).toBeVisible();

		await expect(page.getByText(secondVariationText)).not.toBeVisible();
	}
);

test(
	'Applies the manually prioritized audience variation over the definition order',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create two audiences that both match the browser language

		const firstAudienceName = 'Audience ' + getRandomString();
		const secondAudienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: firstAudienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: secondAudienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Heading fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Bind a variation to each audience on the same heading element

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const firstVariationText = 'First ' + getRandomString();
		const secondVariationText = 'Second ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName: firstAudienceName,
			html: `<span>${firstVariationText}</span>`,
			name: 'First audience variation',
			pageElementLabel: 'Heading (element-text)',
		});

		await elementVariationsPage.createElementVariation({
			audienceName: secondAudienceName,
			html: `<span>${secondVariationText}</span>`,
			name: 'Second audience variation',
			pageElementLabel: 'Heading (element-text)',
		});

		// Move the later created audience to the top of the priority list. By
		// default the first created audience wins, so the manual order takes
		// precedence.

		await elementVariationsPage.prioritizeAudience(secondAudienceName);

		// Publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The manually prioritized audience variation is applied in view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText(secondVariationText)).toBeVisible();

		await expect(page.getByText(firstVariationText)).not.toBeVisible();
	}
);

test(
	'Excludes a disabled variation from the page and applies it again once re-enabled',
	{tag: '@LPD-95644'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create an audience matching the browser language

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: audienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Heading fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Create a variation replacing the heading HTML

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const variationName = 'Toggle heading';
		const variationText = 'Variation ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${variationText}</span>`,
			name: variationName,
			pageElementLabel: 'Heading (element-text)',
		});

		// Disable the variation from the actions menu

		await elementVariationsPage.setVariationActive(variationName, false);

		await expect(
			elementVariationsPage.sidebar.getByText('Disabled', {exact: true})
		).toBeVisible();

		// Publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The disabled variation is excluded from the runtime bundle

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('Heading Example')).toBeVisible();

		await expect(page.getByText(variationText)).not.toBeVisible();

		// Re-enable the variation from the actions menu

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		await elementVariationsPage.setVariationActive(variationName, true);

		await expect(
			elementVariationsPage.sidebar.getByText('Disabled', {exact: true})
		).not.toBeVisible();

		// Publish the page again

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The re-enabled variation is applied in view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText(variationText)).toBeVisible();

		await expect(page.getByText('Heading Example')).not.toBeVisible();
	}
);

test(
	'Applies a variation only after the page is published',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create an audience matching the browser language

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: audienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Heading fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Create a variation replacing the heading HTML on the draft

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const variationText = 'Variation ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${variationText}</span>`,
			name: 'Draft heading',
			pageElementLabel: 'Heading (element-text)',
		});

		// The unpublished variation is not applied in view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('Heading Example')).toBeVisible();

		await expect(page.getByText(variationText)).not.toBeVisible();

		// Publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The variation is applied once the page is published

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText(variationText)).toBeVisible();

		await expect(page.getByText('Heading Example')).not.toBeVisible();
	}
);

test(
	'Edits an existing variation and applies the updated payload',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create an audience matching the browser language

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: audienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Heading fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Create a variation replacing the heading HTML

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const variationName = 'Editable heading';
		const originalText = 'Original ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${originalText}</span>`,
			name: variationName,
			pageElementLabel: 'Heading (element-text)',
		});

		// Edit the variation payload

		const updatedText = 'Updated ' + getRandomString();

		await elementVariationsPage.editElementVariation({
			html: `<span>${updatedText}</span>`,
			name: variationName,
		});

		// Publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The updated payload is applied in view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText(updatedText)).toBeVisible();

		await expect(page.getByText(originalText)).not.toBeVisible();
	}
);

test(
	'Deletes a variation from the actions menu',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		pageEditorPage,
		site,
	}) => {

		// Create an audience matching the browser language

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: audienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Heading fragment

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Create a variation on the heading element

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const variationName = 'Removable heading';

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${getRandomString()}</span>`,
			name: variationName,
			pageElementLabel: 'Heading (element-text)',
		});

		await expect(
			elementVariationsPage.getVariationListItem(variationName)
		).toBeVisible();

		// Delete the variation from the actions menu

		await elementVariationsPage.deleteElementVariation(variationName);

		// The variation is no longer listed

		await expect(
			elementVariationsPage.sidebar.getByText(variationName)
		).not.toBeVisible();
	}
);

test(
	'Reflects the selected experience content in the preview and page element picker',
	{tag: '@LPD-101994'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		pageEditorPage,
		site,
	}) => {

		// Create an audience so element variations can be built

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: audienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create a page with a Paragraph fragment in the default experience

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-paragraph',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Create a second experience and add a Heading only to it

		const experienceName = 'Experience ' + getRandomString();

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.createExperience(experienceName);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		// Open element variations from the second experience

		await pageEditorPage.goToElementVariations();

		const paragraphDefaultText =
			'A paragraph is a self-contained unit of a discourse';

		// The default experience preview excludes the heading, so it is neither
		// rendered nor offered as a page element

		await elementVariationsPage.selectExperience('Default');

		await expect(
			elementVariationsPage.preview.getByText(paragraphDefaultText)
		).toBeVisible();

		await expect(
			elementVariationsPage.preview.getByText('Heading Example')
		).not.toBeVisible();

		await elementVariationsPage.startElementVariationDraft();

		await elementVariationsPage.openPageElementPicker();

		await expect(
			elementVariationsPage.getPageElementOption(
				'Paragraph (element-text)'
			)
		).toBeVisible();

		await expect(
			elementVariationsPage.getPageElementOption('Heading (element-text)')
		).not.toBeVisible();

		await elementVariationsPage.cancelElementVariationDraft();

		// The second experience preview renders the heading and offers it as a
		// page element

		await elementVariationsPage.selectExperience(experienceName);

		await expect(
			elementVariationsPage.preview.getByText('Heading Example')
		).toBeVisible();

		await elementVariationsPage.startElementVariationDraft();

		await elementVariationsPage.openPageElementPicker();

		await expect(
			elementVariationsPage.getPageElementOption('Heading (element-text)')
		).toBeVisible();
	}
);

test(
	'Loads each page own variations when navigating between pages',
	{tag: '@LPD-93951'},
	async ({
		apiHelpers,
		audiencesPage,
		elementVariationsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create an audience matching the browser language

		const audienceName = 'Audience ' + getRandomString();

		await audiencesPage.goto();

		await audiencesPage.createAudience({
			attributeName: 'Language',
			name: audienceName,
			value: 'English (United States)',
			valueType: 'select',
		});

		// Create two pages, each with a Heading fragment

		const firstLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const secondLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Replace the heading on the first page and publish

		await pageEditorPage.goto(firstLayout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const firstVariationText = 'First ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${firstVariationText}</span>`,
			name: 'First page heading',
			pageElementLabel: 'Heading (element-text)',
		});

		await pageEditorPage.goto(firstLayout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// Replace the heading on the second page and publish

		await pageEditorPage.goto(secondLayout, site.friendlyUrlPath);

		await pageEditorPage.goToElementVariations();

		const secondVariationText = 'Second ' + getRandomString();

		await elementVariationsPage.createElementVariation({
			audienceName,
			html: `<span>${secondVariationText}</span>`,
			name: 'Second page heading',
			pageElementLabel: 'Heading (element-text)',
		});

		await pageEditorPage.goto(secondLayout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		// The first page applies its own variation

		await page.goto(
			`/web${site.friendlyUrlPath}${firstLayout.friendlyUrlPath}`
		);

		await expect(page.getByText(firstVariationText)).toBeVisible();

		await expect(page.getByText(secondVariationText)).not.toBeVisible();

		// A client-side navigation to the second page reapplies detection and
		// loads the second page variation

		await page
			.locator(`a[href*="${secondLayout.friendlyUrlPath}"]`)
			.first()
			.click();

		await waitForSPAToBeLoaded(page);

		await expect(page.getByText(secondVariationText)).toBeVisible();

		await expect(page.getByText(firstVariationText)).not.toBeVisible();

		// Navigating back loads the first page variation again

		await page
			.locator(`a[href*="${firstLayout.friendlyUrlPath}"]`)
			.first()
			.click();

		await waitForSPAToBeLoaded(page);

		await expect(page.getByText(firstVariationText)).toBeVisible();

		await expect(page.getByText(secondVariationText)).not.toBeVisible();
	}
);
