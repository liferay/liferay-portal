/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getGridDefinition from '../../layout-content-page-editor-web/main/utils/getGridDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	formsPagesTest,
	isolatedSiteTest,
	loginTest()
);

const pageFields: {
	fieldTitle: FormFieldTypeTitle;
}[] = [
	{
		fieldTitle: 'Text',
	},
	{
		fieldTitle: 'Text',
	},
	{
		fieldTitle: 'Text',
	},
];

test.afterEach(async ({formsPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.describe('Can render forms with multiple pages through page templates', () => {
	test('check that form with multiple pages does not scroll the page when navigating between pages', async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formWidgetPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const gridId = getRandomString();

		const grid = getGridDefinition({
			id: gridId,
		});

		const formWidgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet',
		});

		const sliderFragment = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-slider',
		});

		const imageFragment = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-image',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				sliderFragment,
				grid,
				formWidgetDefinition,
				imageFragment,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		await formBuilderPage.goToNew(site.friendlyUrlPath);

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		const formTitle = 'Form' + getRandomInt();

		await formBuilderPage.fillFormTitle(formTitle);

		for (const formField of pageFields) {
			await formBuilderSidePanelPage.addFieldByDoubleClick(
				formField.fieldTitle
			);

			await formBuilderSidePanelPage.clickBackButton();
		}

		await formBuilderPage.newPageButton.click();

		for (const formField of pageFields) {
			await formBuilderSidePanelPage.addFieldByDoubleClick(
				formField.fieldTitle
			);

			await formBuilderSidePanelPage.clickBackButton();
		}

		await formBuilderPage.clickPublishFormButton();

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await formWidgetPage.setFormWidgetConfiguration(formTitle);

		await pageEditorPage.publishPage();

		await page.goto('/web' + site.friendlyUrlPath);

		await page.waitForLoadState('domcontentloaded');

		await page.evaluate(() => {
			const element = document.querySelector(
				'button.lfr-ddm-form-pagination-next'
			);
			if (element) {
				const elementRect = element.getBoundingClientRect();
				const absoluteElementTop = elementRect.top + window.scrollY;
				const middle =
					absoluteElementTop -
					window.innerHeight / 2 +
					elementRect.height / 2;
				window.scrollTo({
					behavior: 'smooth',
					top: middle,
				});
			}
		});

		await expect(formWidgetPage.nextButton).toBeVisible();

		await formWidgetPage.nextButton.click();

		const scrollYBefore = await page.evaluate(() => window.scrollY);

		await expect(formWidgetPage.previousButton).toBeVisible();

		await formWidgetPage.previousButton.click();

		await formWidgetPage.nextButton.click();

		const scrollYAfter = await page.evaluate(() => window.scrollY);

		expect(scrollYAfter).toBe(scrollYBefore);
	});
});
