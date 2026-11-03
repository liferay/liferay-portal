/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {captchaConfigPageTest} from '../../../fixtures/captchaConfigPageTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';

const test = mergeTests(
	captchaConfigPageTest,
	formsPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'CAPTCHA labels follow the form display language on the shared link',
	{tag: '@LPD-94997'},
	async ({
		captchaConfigPage,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		formViewPage,
		page,
		site,
	}) => {
		await captchaConfigPage.goTo();

		await captchaConfigPage.resetCaptchaConfiguration();

		// Create a form with a text field translated to Portuguese

		await formBuilderPage.goToNew(site.friendlyUrlPath);

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.label.fill('Text Field');

		await formBuilderPage.changeFormBuilderLanguage('Portuguese (Brazil)');

		await formBuilderSidePanelPage.label.fill('Campo de texto');

		// Require CAPTCHA

		await formBuilderPage.formSettingsButton.click();

		await formBuilderPage.requireCaptchaToggle.click();

		await formSettingsModalPage.clickDoneButton();

		// Publish and open the shared form link

		await formBuilderPage.clickPublishFormButton();

		await page.goto(await formBuilderPage.getFormSubmissionURL());

		// The CAPTCHA renders in the default language (English)

		await expect(page.getByText('Text Verification')).toBeVisible();

		await expect(page.getByTitle('Refresh CAPTCHA')).toBeVisible();

		// Switch the form to Portuguese

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {name: 'português-Brasil'}),
			trigger: formViewPage.languageSelector,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText('Verificação de texto'),
			trigger: page.getByRole('menuitem', {name: 'português-Brasil'}),
		});

		// The CAPTCHA labels now follow the form's display language (LPD-94997)

		await expect(page.getByTitle('Atualizar CAPTCHA')).toBeVisible();

		await expect(page.getByText('Text Verification')).toBeHidden();

		await expect(page.getByTitle('Refresh CAPTCHA')).toBeHidden();
	}
);
