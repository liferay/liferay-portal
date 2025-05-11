/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {portletConfigurationPermissionsPageTest} from '../../../fixtures/portletConfigurationPermissionsPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {stagingPageTest} from '../../export-import-web/main/fixtures/stagingPageTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {stagingConfigurationPageTest} from './fixtures/stagingConfigurationPageTest';

export const test = mergeTests(
	applicationsMenuPageTest,
	dataApiHelpersTest,
	loginTest(),
	pageViewModePagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	pageEditorPagesTest,
	stagingConfigurationPageTest,
	webContentDisplayPageTest,
	uiElementsPageTest,
	journalPagesTest
);

export const testFlagsEnabled = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11131': {enabled: true},
		'LPD-35013': {enabled: true},
		'LPD-39304': {enabled: true},
	}),
	dataApiHelpersTest,
	loginTest(),
	portletConfigurationPermissionsPageTest,
	stagingPageTest,
	test,
	webContentDisplayPageTest
);

test('Check if local staging can be enabled', async ({
	apiHelpers,
	applicationsMenuPage,
	stagingConfigurationPage,
}) => {
	const siteName: string = getRandomString();

	await applicationsMenuPage.goToSites();

	const site = await apiHelpers.headlessSite.createSite({
		name: siteName,
	});

	await stagingConfigurationPage.gotoStagingConfiguration(
		site.friendlyUrlPath
	);

	await stagingConfigurationPage.enableLocalStaging({});
});

test(
	'Validate friendlyURL with special characters',
	{tag: ['@LPS-89116']},
	async ({
		apiHelpers,
		applicationsMenuPage,
		journalPage,
		page,
		pagesAdminPage,
		productMenuPage,
		stagingConfigurationPage,
	}) => {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await applicationsMenuPage.goToSite(site.name);
		await productMenuPage.goToPages();

		await pagesAdminPage.createNewPage({
			name: getRandomString(),
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentName = 'Special Char aŐb';
		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: 'Web Content Content',
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: webContentName},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {
				exact: true,
				name: webContentName,
			})
		).toBeVisible();

		await page.getByRole('link', {name: webContentName}).click();

		await expect(
			page.getByLabel('Friendly URL', {exact: true})
		).toHaveValue('special-char-aőb');

		await apiHelpers.jsonWebServicesJournal.addWebContentDetailed({
			content: 'Web Content Content2',
			ddmStructureId: basicWebContentStructureId,
			friendlyURLMap: {en_US: 'special-char-a-c5-90b'},
			groupId: site.id,
			titleMap: {en_US: 'Web Content Title'},
		});

		await stagingConfigurationPage.gotoStagingConfiguration(
			site.friendlyUrlPath
		);

		await stagingConfigurationPage.enableLocalStaging({});
	}
);

testFlagsEnabled(
	'Check if local staging with page-scoped Web Content can be enabled',
	{tag: ['@LPS-83147']},
	async ({apiHelpers, page, webContentDisplayPage, widgetPagePage}) => {
		const siteName = getRandomString();
		const layoutName = getRandomString();
		const webContentName = getRandomString();

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'portlet'},
			title: layoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await page.getByLabel('Add', {exact: true}).click();

		await widgetPagePage.addPortlet(
			'Web Content Display',
			'Content Management'
		);

		await webContentDisplayPage.goToConfiguration();
		await webContentDisplayPage.setScope(layout.uuid);
		await webContentDisplayPage.saveConfigurationFrameOptions();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			company.companyId,
			layout.plid
		);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: getRandomString(),
				ddmStructureId: basicWebContentStructureId,
				groupId: group.groupId,
				titleMap: {en_US: webContentName},
			});

		apiHelpers.data.push({
			id: `${group.groupId}_${webContent.articleId}`,
			type: 'webContent',
		});

		await webContentDisplayPage.addWebContentWithDisplay({
			pageType: 'widget',
			webContentName,
		});

		await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
			groupId: site.id,
		});

		await webContentDisplayPage.gotoWebContentAdmin(layout.plid);
		await page
			.getByRole('link', {name: webContentName})
			.waitFor({state: 'visible'});
	}
);
