/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {SystemSettingsPage} from '../../../pages/configuration-admin-web/SystemSettingsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest()
);

async function addDisplayWidgetPage(apiHelpers: ApiHelpers, site: Site) {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
		typeSettings:
			'layout-template-id=1_column\ncolumn-1=com_liferay_knowledge_base_web_portlet_DisplayPortlet\n',
	});

	return `/web${site.friendlyUrlPath}${layout.friendlyURL}`;
}

const customFieldTest = test.extend<{customFieldName: string}>({
	customFieldName: [
		async ({page}, use) => {
			const customFieldName = getRandomString().replace(/-/g, '');

			const addCustomFieldLink = page.getByRole('link', {
				name: 'Add Custom Field',
			});

			const gotoKnowledgeBaseArticleCustomFields = async () => {
				await page.goto(
					'/group/control_panel/manage?p_p_id=com_liferay_expando_web_portlet_ExpandoPortlet'
				);

				await clickAndExpectToBeVisible({
					target: addCustomFieldLink,
					trigger: page.getByRole('link', {
						exact: true,
						name: 'Knowledge Base Article',
					}),
				});
			};

			await gotoKnowledgeBaseArticleCustomFields();

			const inputFieldLink = page.getByRole('link', {
				name: 'Input Field',
			});

			await clickAndExpectToBeVisible({
				target: inputFieldLink,
				trigger: addCustomFieldLink,
			});

			const fieldNameInput = page.getByText('Field Name Required');

			await clickAndExpectToBeVisible({
				target: fieldNameInput,
				trigger: inputFieldLink,
			});

			await fieldNameInput.fill(customFieldName);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);

			try {
				await use(customFieldName);
			}
			finally {
				await gotoKnowledgeBaseArticleCustomFields();

				page.once('dialog', (dialog) => dialog.accept());

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('link', {name: 'Delete'}),
					trigger: page
						.getByRole('row')
						.filter({hasText: customFieldName})
						.locator('.dropdown-toggle'),
				});

				await waitForAlert(page);
			}
		},
		{timeout: 60000},
	],
});

customFieldTest(
	'Custom field values of an article are shown and updated in the Knowledge Base Display widget',
	{tag: ['@LPD-110921', '@LRQA-41349']},
	async ({
		apiHelpers,
		customFieldName,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		page,
		site,
	}) => {
		const displayURL = await addDisplayWidgetPage(apiHelpers, site);

		const title = getRandomString();

		const customFieldInput = page.getByLabel(customFieldName);
		const displayPortlet = page.locator(
			'[id^="portlet_com_liferay_knowledge_base_web_portlet_DisplayPortlet"]'
		);

		await customFieldTest.step('EditKBArticleWithCustomField', async () => {
			const content = getRandomString();
			const customFieldValue = getRandomString();

			await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(content);

			await knowledgeBaseEditArticlePage.expandPanel('Custom Fields');

			await customFieldInput.fill(customFieldValue);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await page.goto(displayURL);

			await expect(
				displayPortlet.getByRole('heading', {name: title})
			).toBeVisible();
			await expect(displayPortlet).toContainText(content);
			await expect(displayPortlet).toContainText(customFieldName, {
				ignoreCase: true,
			});
			await expect(displayPortlet).toContainText(customFieldValue);
		});

		await customFieldTest.step('EditKBArticleWithCustomField', async () => {
			const editedContent = getRandomString();
			const editedCustomFieldValue = getRandomString();
			const editedTitle = getRandomString();

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit');

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(
				editedTitle
			);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				editedContent
			);

			await knowledgeBaseEditArticlePage.expandPanel('Custom Fields');

			await customFieldInput.fill(editedCustomFieldValue);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${editedTitle} was successfully published.`
			);

			await page.goto(displayURL);

			await expect(
				displayPortlet.getByRole('heading', {name: editedTitle})
			).toBeVisible();
			await expect(displayPortlet).toContainText(editedContent);
			await expect(displayPortlet).toContainText(editedCustomFieldValue);
		});
	}
);

test(
	'Articles show the source URL link in the Knowledge Base Display widget when it is enabled',
	{tag: ['@LPD-110921', '@LRQA-41349']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		page,
		site,
	}) => {
		const systemSettingsPage = new SystemSettingsPage(page);

		await systemSettingsPage.goToConfiguration(
			'com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration'
		);

		const defaultConfiguration = await page
			.getByText('This configuration is not saved yet.')
			.isVisible();

		const sourceURLEnabledCheckbox = page.getByLabel('Source URL Enabled');

		const sourceURLEnabled = await sourceURLEnabledCheckbox.isChecked();

		await sourceURLEnabledCheckbox.check();

		await systemSettingsPage.saveAndWaitForAlert();

		try {
			await test.step('ViewKBArticleGitHubSourceURL', async () => {
				const sourceURL = `https://www.liferay.com/${getRandomString()}`;
				const sourceURLInput = page.getByLabel('Source URL', {
					exact: true,
				});
				const title = getRandomString();

				await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

				await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
				await knowledgeBaseEditArticlePage.contentTextBox.fill(
					getRandomString()
				);

				await knowledgeBaseEditArticlePage.expandPanel('Configuration');

				await sourceURLInput.fill(sourceURL);

				await knowledgeBaseEditArticlePage.publish();

				await waitForAlert(
					page,
					`Success:${title} was successfully published.`
				);

				await knowledgeBasePage.goto(site.friendlyUrlPath);

				await knowledgeBasePage.clickRowAction(title, 'Edit');

				await knowledgeBaseEditArticlePage.expandPanel('Configuration');

				await expect(sourceURLInput).toHaveValue(sourceURL);

				await page.goto(await addDisplayWidgetPage(apiHelpers, site));

				await expect(
					page.getByRole('link', {name: 'Edit on GitHub'})
				).toHaveAttribute('href', sourceURL);

				await clickAndExpectToBeVisible({
					target: page.getByRole('menu'),
					trigger: page
						.locator(
							'[id^="portlet_com_liferay_knowledge_base_web_portlet_DisplayPortlet"]'
						)
						.getByRole('button', {name: 'Show Actions'}),
				});

				for (const menuItem of ['History', 'Print', 'Subscribe']) {
					await expect(
						page.getByRole('menuitem', {
							exact: true,
							name: menuItem,
						})
					).toBeVisible();
				}
			});
		}
		finally {
			await systemSettingsPage.goToConfiguration(
				'com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration'
			);

			if (defaultConfiguration) {
				if (
					!(await page
						.getByText('This configuration is not saved yet.')
						.isVisible())
				) {
					await systemSettingsPage.resetToDefaultValues();
				}
			}
			else {
				await sourceURLEnabledCheckbox.setChecked(sourceURLEnabled);

				await systemSettingsPage.saveAndWaitForAlert();
			}
		}
	}
);
