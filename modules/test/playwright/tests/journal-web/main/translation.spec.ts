/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {changeManagementToolbarView} from '../../../utils/changeManagementToolbarView';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getGlobalSiteId from '../../../utils/getGlobalSiteId';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {watchForDialog} from '../../../utils/watchForDialog';
import {journalPagesTest} from './fixtures/journalPagesTest';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	siteSettingsPagesTest
);

test(
	'Stored XSS payload in a Web Content is sanitized in the translation editor and processes list',
	{
		tag: '@LPD-77709',
	},
	async ({
		apiHelpers,
		journalEditArticleTranslationsPage,
		journalPage,
		page,
		site,
	}) => {
		const watcher = watchForDialog(page);

		try {
			const xssPayload = '<script>alert(123)</script>';
			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: xssPayload,
				ddmStructureId: contentStructureId,
				descriptionMap: {en_US: xssPayload},
				groupId: site.id,
				titleMap: {en_US: xssPayload},
			});

			await journalPage.goto(site.friendlyUrlPath);

			await changeManagementToolbarView(page, 'List');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Translate',
				}),
				trigger: page.locator('[aria-label^="Actions for"]').first(),
			});

			await journalEditArticleTranslationsPage.titleInput.fill(
				xssPayload
			);

			await journalEditArticleTranslationsPage.publishButton.click();

			await waitForAlert(page);

			await page.goto(
				`/group${site.friendlyUrlPath}${PORTLET_URLS.translation}`
			);

			watcher.assertNoDialog();
		}
		finally {
			watcher.dispose();
		}
	}
);

test(
	'Language selector remains when editing Global web content from a site with limited languages',
	{
		tag: '@LPD-94459',
	},
	async ({
		apiHelpers,
		journalEditArticlePage,
		page,
		site,
		siteSettingsLocalizationPage,
	}) => {
		const globalSiteId = await getGlobalSiteId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: globalSiteId,
			});

		try {
			await siteSettingsLocalizationPage.setCustomDefaultLanguage(
				'Spanish (Spain)',
				site.friendlyUrlPath
			);

			await siteSettingsLocalizationPage.disableAllLanguagesExceptSp(
				site.friendlyUrlPath
			);

			await page.goto(
				`/group${site.friendlyUrlPath}${PORTLET_URLS.journal}&_com_liferay_journal_web_portlet_JournalPortlet_mvcRenderCommandName=%2Fjournal%2Fedit_article&_com_liferay_journal_web_portlet_JournalPortlet_groupId=${globalSiteId}&_com_liferay_journal_web_portlet_JournalPortlet_articleId=${webContent.articleId}`
			);

			const languageSelector = page.getByRole('combobox', {
				name: 'Select a language',
			});

			await expect(languageSelector).toBeVisible();

			await journalEditArticlePage.changeLanguage('de_DE');

			await expect(languageSelector).toBeVisible();

			await expect(languageSelector).toContainText('de-DE');
		}
		finally {
			await apiHelpers.jsonWebServicesJournal.deleteArticle(
				globalSiteId,
				webContent.articleId
			);
		}
	}
);
