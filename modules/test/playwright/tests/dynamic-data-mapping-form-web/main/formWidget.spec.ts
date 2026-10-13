/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {contentSecurityPolicyPagesTest} from '../../../fixtures/contentSecurityPolicyPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import getLoremIpsumText from '../../../utils/lorem-ipsum-util/getLoremIpsumText';
import {performLoginViaApi} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getGridDefinition from '../../layout-content-page-editor-web/main/utils/getGridDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import evaluateKeepCheckingAfterFound from '../../object-web/utils/keepCheckingAfterFound';
import {deleteItems} from './utils/deleteItems';

const NONCE_CONTENT_SECURITY_POLICY = `default-src 'self'; script-src '[$NONCE$]' 'strict-dynamic'; script-src-attr 'none'; style-src-elem 'self' 'unsafe-inline'; style-src-attr 'self' 'unsafe-inline'; connect-src 'self'; img-src 'self'; object-src 'none'; frame-ancestors 'self'; frame-src 'self';`;

const test = mergeTests(
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	contentSecurityPolicyPagesTest,
	dataApiHelpersTest,
	formsPagesTest,
	isolatedSiteTest,
	loginTest(),
	productMenuPageTest
);

test.use({
	permissions: ['clipboard-write'],
});

test.beforeEach(({page}) => {
	page.setViewportSize({height: 1080, width: 1920});
});

test.afterEach(async ({formsPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.describe('Forms widget', () => {
	test('renders in the page builder phone preview and automatically scrolls to the first field when navigating to the next form page', async ({
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
			columns: [{pageElements: [], size: 1}],
			id: gridId,
		});

		const formWidgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([grid, formWidgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		await formBuilderPage.goToNew(site.friendlyUrlPath);

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		const formName = 'Form' + getRandomInt();

		await formBuilderPage.fillFormTitle(formName);

		const loremIpsumText = getLoremIpsumText([50, 100, 200]);

		for (let index = 0; index < 8; index++) {
			await formBuilderSidePanelPage.addFieldByDoubleClick('Paragraph');

			await formBuilderSidePanelPage.fillParagraphField(
				apiHelpers,
				loremIpsumText
			);

			await formBuilderSidePanelPage.paragraphFieldTitle.fill(
				`Paragraph ${index}`
			);

			await formBuilderSidePanelPage.clickBackButton();

			if (index === 3) {
				await formBuilderPage.newPageButton.click();
			}
		}

		await formBuilderPage.clickPublishFormButton();

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await formWidgetPage.setFormWidgetConfiguration(formName);

		await page.waitForLoadState();

		await page.reload();

		await expect(page.getByText(formName)).toBeVisible();

		await page.getByLabel('Portrait Phone').click();

		await page.waitForLoadState('networkidle');

		const keepsAttached = await evaluateKeepCheckingAfterFound({
			duration: 4000,
			iframeSelector: '.page-editor__global-context-iframe',
			page,
			selector:
				'form .lfr-ddm-form-pagination-controls button.lfr-ddm-form-pagination-next',
		});

		expect(keepsAttached).toBe(true);

		await pageEditorPage.publishPage();

		await page.goto('/web' + site.friendlyUrlPath);

		await expect(formWidgetPage.nextButton).toBeVisible();

		await formWidgetPage.nextButton.click();

		await expect(
			page.getByLabel('Paragraph 4<p>Nam tincidunt')
		).toBeInViewport();
	});
});

test.describe('Forms widget setup', () => {
	test.afterEach(async ({contentSecurityPolicyPage, page}) => {
		await page.goto('/');

		if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
			await performLoginViaApi({page, screenName: 'test'});
		}

		await contentSecurityPolicyPage.goto();

		await contentSecurityPolicyPage.resetCSPConfiguration();
	});

	test(
		'selects a form and confirms every save',
		{tag: ['@LPD-109760']},
		async ({
			apiHelpers,
			formBuilderPage,
			formWidgetPage,
			page,
			pageEditorPage,
			site,
		}) => {
			const firstFormName = 'Form' + getRandomInt();

			await formBuilderPage.publishNewForm({
				fieldTypes: ['Text'],
				siteUrl: site.friendlyUrlPath,
				title: firstFormName,
			});

			const secondFormName = 'Form' + getRandomInt();

			await formBuilderPage.publishNewForm({
				fieldTypes: ['Text'],
				siteUrl: site.friendlyUrlPath,
				title: secondFormName,
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await test.step('select the first form and save', async () => {
				await formWidgetPage.openConfiguration();

				await expect(
					formWidgetPage.getFormLink(firstFormName)
				).toBeVisible();

				await expect(formWidgetPage.javascriptLinks).toHaveCount(0);

				await formWidgetPage.selectForm(firstFormName);

				await formWidgetPage.saveConfiguration();

				await waitForAlert(
					formWidgetPage.configurationFrame,
					'Success:You have successfully updated the setup.'
				);

				await formWidgetPage.closeConfiguration();
			});

			await test.step('reopen the setup, select the second form and save again', async () => {
				await formWidgetPage.openConfiguration();

				await formWidgetPage.selectForm(secondFormName);

				await formWidgetPage.saveConfiguration();

				await waitForAlert(
					formWidgetPage.configurationFrame,
					'Success:You have successfully updated the setup.'
				);

				await formWidgetPage.closeConfiguration();
			});

			await page.waitForLoadState();

			await page.reload();

			await expect(page.getByText(secondFormName)).toBeVisible();
		}
	);

	test(
		'selects a form under a nonce based content security policy',
		{tag: ['@LPD-109760']},
		async ({
			apiHelpers,
			contentSecurityPolicyPage,
			formBuilderPage,
			formWidgetPage,
			page,
			pageEditorPage,
			site,
		}) => {
			const formName = 'Form' + getRandomInt();

			await formBuilderPage.publishNewForm({
				fieldTypes: ['Text'],
				siteUrl: site.friendlyUrlPath,
				title: formName,
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await contentSecurityPolicyPage.gotoAndConfigurePolicy(
				NONCE_CONTENT_SECURITY_POLICY
			);

			const errors: string[] = [];

			page.on('console', (message) => {
				if (
					message.type() === 'error' &&
					message
						.text()
						.includes(
							'Content Security Policy directive: "script-src'
						)
				) {
					errors.push(message.text());
				}
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await formWidgetPage.openConfiguration();

			await formWidgetPage.selectForm(formName);

			await formWidgetPage.saveConfiguration();

			await waitForAlert(
				formWidgetPage.configurationFrame,
				'Success:You have successfully updated the setup.'
			);

			expect(errors).toHaveLength(0);
		}
	);
});
