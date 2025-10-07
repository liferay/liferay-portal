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
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {pagesPagesTest} from './fixtures/pagesPagesTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pagesPagesTest,
	pageEditorPagesTest
);

test.describe('Translation of editables with experiences', () => {
	test('Can add translation for an editable for default experience in draft and then publish it', async ({
		apiHelpers,
		contentPageTranslationPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with a Heading fragment

		const title = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title,
		});

		// Translate the editable to spanish for default experience and save as draft

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Manzana',
		});

		await contentPageTranslationPage.saveAsDraft();

		// Go to page editor and check the translation is not applied since it's in draft

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(page.getByText('Heading Example')).toBeVisible();

		await pageEditorPage.switchLanguage('es-ES');

		await expect(page.getByText('Heading Example')).toBeVisible();

		await expect(page.getByText('Manzana')).not.toBeVisible();

		// Check translation was correctly saved in draft and publish it

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchLanguage({to: 'es-ES'});

		await expect(page.getByLabel('element-text')).toHaveValue('Manzana');

		await contentPageTranslationPage.publish();

		// Check it's now applied to edit mode

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(page.getByText('Heading Example')).toBeVisible();

		await pageEditorPage.switchLanguage('es-ES');

		await expect(page.getByText('Manzana')).toBeVisible();

		// Check it not applied in view mode since we haven't published the page

		await page.goto(
			`/es/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.getByText('Heading Example')).toBeVisible();

		// Set english language, otherwise the portal keeps spanish language

		await page.goto(
			`/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Now publish the page and check the translation is applied to view mode

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		await page.goto(
			`/es/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.getByText('Manzana')).toBeVisible();

		// Set english language, otherwise the portal keeps spanish language

		await page.goto(
			`/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);
	});

	test('Can add translations for different experiences', async ({
		apiHelpers,
		contentPageTranslationPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with a Heading fragment

		const title = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title,
		});

		// Go to edit mode and create two new experiences

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.createExperience('Experience 1');

		await expect(page.locator('.alert-success')).toBeHidden();

		await pageEditorPage.createExperience('Experience 2');

		await pageEditorPage.publishPage();

		// Translate the editable for Experience 1

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchExperience('Experience 1');

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Manzana',
		});

		await contentPageTranslationPage.publish();

		// Translate the editable for Experience 2

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchExperience('Experience 2');

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Pera',
		});

		await contentPageTranslationPage.publish();

		// Go to page editor and check original value is shown for default experience

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(page.getByText('Heading Example')).toBeVisible();

		// Change to new experiences and check correct value is shown

		await pageEditorPage.switchLanguage('es-ES');

		await pageEditorPage.switchExperience('Experience 1');

		await expect(page.getByText('Manzana')).toBeVisible();

		await pageEditorPage.switchExperience('Experience 2');

		await expect(page.getByText('Pera')).toBeVisible();

		// Publish the page and check it applies to view mode

		await pageEditorPage.publishPage();

		await page.goto(
			`/es/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Experience 1'}),
			trigger: page.getByLabel('Selector de experiencia'),
		});

		await expect(page.getByText('Manzana')).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Experience 2'}),
			trigger: page.getByLabel('Selector de experiencia'),
		});

		await expect(page.getByText('Pera')).toBeVisible();

		// Set english language, otherwise the portal keeps spanish language

		await page.goto(
			`/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);
	});

	test('New experience inherites translations from Default', async ({
		apiHelpers,
		contentPageTranslationPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with a Heading fragment

		const title = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title,
		});

		// Translate the editable for Default

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Manzana',
		});

		await contentPageTranslationPage.publish();

		// Go to edit mode and check translation is applied

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.switchLanguage('es-ES');

		await expect(page.getByText('Manzana')).toBeVisible();

		await pageEditorPage.switchLanguage('en-US');

		await expect(page.getByText('Manzana')).not.toBeVisible();

		// Create new experience and check the translation is kept

		await pageEditorPage.createExperience('Experience 1');

		await pageEditorPage.switchLanguage('es-ES');

		await expect(page.getByText('Manzana')).toBeVisible();
	});
});

test.describe('Export and import of translations', () => {
	test('Can export the translation for a page with experiences and then import it', async ({
		apiHelpers,
		contentPageTranslationPage,
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
	}) => {

		// Create a page with a Heading fragment

		const title = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title,
		});

		// Go to edit mode and create new experience

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.createExperience('Experience 1');

		await expect(page.locator('.alert-success')).toBeHidden();

		await pageEditorPage.publishPage();

		// Translate the editable to spanish for default experience and publish

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Manzana',
		});

		await contentPageTranslationPage.publish();

		// Translate the editable to spanish for Experience 1 and publish

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchExperience('Experience 1');

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Pera',
		});

		await contentPageTranslationPage.publish();

		// Export the translation with single export

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const file1 = await contentPageTranslationPage.exportTranslations({
			languages: ['Spanish (Spain)'],
			pageName: title,
		});

		// Change the translations for both Default and Experience 1

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Mango',
		});

		await contentPageTranslationPage.publish();

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchExperience('Experience 1');

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Naranja',
		});

		await contentPageTranslationPage.publish();

		// Now import the translations exported previously

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await contentPageTranslationPage.importTranslations({filePath: file1});

		// Now check the import worked

		await contentPageTranslationPage.goto({
			pageName: title,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchLanguage({to: 'es-ES'});

		await expect(page.getByLabel('element-text')).toHaveValue('Manzana');

		await contentPageTranslationPage.switchExperience('Experience 1');

		await expect(page.getByLabel('element-text')).toHaveValue('Pera');
	});

	test('Can export and import translations for several pages at once', async ({
		apiHelpers,
		contentPageTranslationPage,
		page,
		pagesAdminPage,
		site,
	}) => {

		// Create a page with a Heading fragment

		const title1 = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: title1,
		});

		// Create another page with a Heading fragment

		const title2 = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: title2,
		});

		// Translate the editable to spanish for the first page

		await contentPageTranslationPage.goto({
			pageName: title1,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Manzana',
		});

		await contentPageTranslationPage.publish();

		// Translate the editable to spanish for the second page

		await contentPageTranslationPage.goto({
			pageName: title2,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Pera',
		});

		await contentPageTranslationPage.publish();

		// Export translations

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const file = await contentPageTranslationPage.bulkExportTranslations({
			languages: ['Spanish (Spain)'],
			pageNames: [title1, title2],
		});

		// Now change translations

		await contentPageTranslationPage.goto({
			pageName: title1,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Mango',
		});

		await contentPageTranslationPage.publish();

		await contentPageTranslationPage.goto({
			pageName: title2,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.translateEditable({
			editableId: 'element-text',
			to: 'es-ES',
			value: 'Naranja',
		});

		await contentPageTranslationPage.publish();

		// Now import translations and check they are applied

		await contentPageTranslationPage.importTranslations({filePath: file});

		// Now check the import worked

		await contentPageTranslationPage.goto({
			pageName: title1,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchLanguage({to: 'es-ES'});

		await expect(page.getByLabel('element-text')).toHaveValue('Manzana');

		await contentPageTranslationPage.goto({
			pageName: title2,
			siteUrl: site.friendlyUrlPath,
		});

		await contentPageTranslationPage.switchLanguage({to: 'es-ES'});

		await expect(page.getByLabel('element-text')).toHaveValue('Pera');
	});
});
