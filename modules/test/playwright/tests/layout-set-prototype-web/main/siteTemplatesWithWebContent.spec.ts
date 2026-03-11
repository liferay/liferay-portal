/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {sitesPageTest} from '../../../fixtures/sitesPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import createSiteTemplate from './utils/createSiteTemplate';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	sitesPageTest,
	uiElementsPageTest,
	webContentDisplayPageTest
);

test(
	'Page links on sites which were created in site templates should redirect correctly to other pages.',
	{tag: ['@LPD-46415']},
	async ({
		apiHelpers,
		applicationsMenuPage,
		page,
		pageEditorPage,
		pagesAdminPage,
		productMenuPage,
		sitesPage,
		uiElementsPage,
		webContentDisplayPage,
	}) => {
		const siteTemplateName: string = 'Template-' + getRandomString();

		const layoutSetPrototype = await createSiteTemplate({
			apiHelpers,
			page,
			productMenuPage,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await productMenuPage.goToPages();

		const page1Name = getRandomString();
		await pagesAdminPage.createNewPage({
			name: page1Name,
		});
		const page2Name = getRandomString();
		await pagesAdminPage.createNewPage({
			name: page2Name,
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentName = getRandomString();

		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				`Template-${layoutSetPrototype.layoutSetPrototypeId}`
			);

		const baseURL = liferayConfig.environment.baseUrl;
		const baseURLNoProtocol = baseURL.replace('http://', '');

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: `<a href="${baseURLNoProtocol}/group/template-${layoutSetPrototype.layoutSetPrototypeId}/${page2Name}">${baseURL}/group/template-${layoutSetPrototype.layoutSetPrototypeId}/${page2Name}</a>`,
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: webContentName},
		});

		await applicationsMenuPage.goToSites();

		const siteName = getRandomString();

		const siteId = await sitesPage.createSite({
			isCustom: true,
			siteName,
			templateName: layoutSetPrototype.nameCurrentValue,
		});

		apiHelpers.data.push({
			id: siteId,
			type: 'site',
		});

		await applicationsMenuPage.goToSite(siteName);
		await productMenuPage.goToPages();
		await page.getByLabel(`${page1Name}`, {exact: true}).click();
		await pageEditorPage.addWidget(
			'Content Management',
			'Web Content Display'
		);
		await webContentDisplayPage.addWebContentWithDisplay({
			webContentName,
		});
		await uiElementsPage.publishButton.click();

		await page.goto(`web/${siteName}/${page1Name}`);

		await expect(
			page.getByRole('link', {
				name: `template-${layoutSetPrototype.layoutSetPrototypeId}/${page2Name}`,
			})
		).toBeVisible();

		expect(
			await page
				.getByRole('link', {
					name: `template-${layoutSetPrototype.layoutSetPrototypeId}/${page2Name}`,
				})
				.getAttribute('href')
		).toEqual(
			`${baseURLNoProtocol}/group/template-${layoutSetPrototype.layoutSetPrototypeId}/${page2Name}`
		);
	}
);
