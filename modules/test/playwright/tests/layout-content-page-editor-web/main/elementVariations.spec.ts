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

		// Delete the audience, which is company scoped and does not go away
		// with the site

		await audiencesPage.goto();

		await audiencesPage.deleteAudience(audienceName);
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

		// Delete the audience, which is company scoped and does not go away
		// with the site

		await audiencesPage.goto();

		await audiencesPage.deleteAudience(audienceName);
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

		// Delete the audiences, which are company scoped and do not go away with
		// the site

		await audiencesPage.goto();

		await audiencesPage.deleteAudience(firstAudienceName);

		await audiencesPage.deleteAudience(secondAudienceName);
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

		// Delete the audiences, which are company scoped and do not go away with
		// the site

		await audiencesPage.goto();

		await audiencesPage.deleteAudience(firstAudienceName);

		await audiencesPage.deleteAudience(secondAudienceName);
	}
);
