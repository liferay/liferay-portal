/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {captchaConfigPageTest} from '../../../fixtures/captchaConfigPageTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performLogout} from '../../../utils/performLogin';

const test = mergeTests(
	apiHelpersTest,
	captchaConfigPageTest,
	formsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test('LPD-47067 check that two forms on same page with simplecaptcha could refresh both captchas', async ({
	apiHelpers,
	captchaConfigPage,
	formBuilderPage,
	formBuilderSidePanelPage,
	formSettingsModalPage,
	formWidgetPage,
	page,
	site,
	widgetPagePage,
}) => {
	await captchaConfigPage.goTo();

	await captchaConfigPage.resetCaptchaConfiguration();

	await formBuilderPage.goToNew(site.friendlyUrlPath);

	await expect(formBuilderPage.newFormHeading).toBeVisible();

	const formName = 'Form' + getRandomInt();

	await formBuilderPage.fillFormTitle(formName);

	await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

	await formBuilderPage.formSettingsButton.click();

	await formBuilderPage.requireCaptchaToggle.click();

	await formSettingsModalPage.clickDoneButton();

	await formBuilderPage.publishButton.click();

	await expect(
		page.getByText('Your request completed successfully')
	).toBeVisible();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {
			type: 'portlet',
		},
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await addAndConfigureForms(formName, formWidgetPage, widgetPagePage);

	await refreshAndCheckCaptcha(2, page);

	await performLogout(page);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await refreshAndCheckCaptcha(2, page);

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

test('LPD-66742 Check if image refresh works when multiple elements have the modal-body class', async ({
	apiHelpers,
	captchaConfigPage,
	page,
	pageEditorPage,
	site,
}) => {
	await captchaConfigPage.goTo();

	await captchaConfigPage.resetCaptchaConfiguration();

	const fragmentCollectionName = getRandomString();

	const {fragmentCollectionId} =
		await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
			{
				groupId: site.id,
				name: fragmentCollectionName,
			}
		);

	const fragmentEntryName = getRandomString();

	await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
		fragmentCollectionId,
		groupId: site.id,
		html: `<div class="modal-body">
					This is modal body div
				</div>
				[@liferay_captcha["captcha"]/]`,
		name: fragmentEntryName,
		type: 'component',
	});

	// Add a layout and add custom fragment

	const layoutTitle = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'content'},
		title: layoutTitle,
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.addFragment(fragmentCollectionName, fragmentEntryName);

	await pageEditorPage.waitForChangesSaved();

	await pageEditorPage.publishPage();

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	refreshAndCheckCaptcha(1, page);

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

async function addAndConfigureForms(formName, formWidgetPage, widgetPagePage) {
	for (let count = 2; count < 4; count++) {
		await widgetPagePage.addPortlet('Form');

		await formWidgetPage.dropdownButton.nth(count).click();
		await formWidgetPage.configurationDropdownButton.first().click();

		const formLink = formWidgetPage.getFormLink(formName);
		await formLink.click();

		await formWidgetPage.saveButton.click();
		await formWidgetPage.page.keyboard.press('Escape');

		await widgetPagePage.page.waitForLoadState();
		await widgetPagePage.page.reload();
	}
}

async function refreshAndCheckCaptcha(captchaCount, page) {
	for (let count = 0; count < captchaCount; count++) {
		const captchaImgSource = await page
			.getByAltText('Text to Identify')
			.nth(count)
			.getAttribute('src');

		await page.getByTitle('Refresh CAPTCHA').nth(count).click();

		const refreshedCaptchaImgSource = await page
			.getByAltText('Text to Identify')
			.nth(count)
			.getAttribute('src');

		await expect(captchaImgSource).not.toEqual(refreshedCaptchaImgSource);
	}
}
