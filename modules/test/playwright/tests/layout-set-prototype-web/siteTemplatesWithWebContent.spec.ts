/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../fixtures/productMenuPageTest';
import {serverAdministrationPageTest} from '../../fixtures/serverAdministrationPageTest';
import {sitesPageTest} from '../../fixtures/sitesPageTest';
import {uiElementsPageTest} from '../../fixtures/uiElementsTest';
import {webContentDisplayPageTest} from '../../fixtures/webContentDisplayPageTest';
import {LayoutSetPrototype} from '../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import getRandomString from '../../utils/getRandomString';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';
import {pagesPagesTest} from '../layout-admin-web/fixtures/pagesPagesTest';
import {layoutSetPrototypePageTest} from './fixtures/layoutSetPrototypePageTest';

import createSiteTemplateWithContentPageAndAssetPublisher from './utils/createSiteTemplateWithContentPageAndAssetPublisher';
import deleteSites from './utils/deleteSites';
import getLayoutTemplateByName from './utils/getLayoutTemplateByName';
import deleteLayoutSetPrototype from './utils/deleteLayoutSetPrototype';
import createSiteTemplateWithWebContentOnWidgetPage from './utils/createSiteTemplateWithWebContentOnWidgetPage';
import deleteSiteAndLayoutSetPrototypes from './utils/deleteSiteAndLayoutSetPrototypes';
import createSiteTemplateWithWebContentOnContentPage from './utils/createSiteTemplateWithWebContentOnContentPage';
import createSiteTemplateWithWebContentOnHomePage from './utils/createSiteTemplateWithWebContentOnHomePage';

export const test = mergeTests(
	applicationsMenuPageTest,
	journalPagesTest,
	apiHelpersTest,
	isolatedSiteTest,
	layoutSetPrototypePageTest,
	productMenuPageTest,
	uiElementsPageTest,
	pagesPagesTest,
	pageViewModePagesTest,
	webContentDisplayPageTest,
	pageEditorPagesTest,
	sitesPageTest,
	serverAdministrationPageTest,
	loginTest(),
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	}),
	pagesAdminPagesTest
);

const testWithPrivatePages = mergeTests(
	test,
	featureFlagsTest({
		'LPD-38869': {enabled: true},
		'LPD-39304': {enabled: true},
	})
);

const webContentName1: string = getRandomString();
const webContentName2: string = getRandomString();
const webContentText1: string = getRandomString();
const webContentText2: string = getRandomString();

testWithPrivatePages(
	'Editing global web contents does not trigger site template propagation',
	{tag: '@LPD-21445'},
	async ({
		apiHelpers,
		applicationsMenuPage,
		journalPage,
		layoutSetPrototypePage,
		page,
		pageEditorPage,
		pagesAdminPage,
		productMenuPage,
		sitesPage,
		uiElementsPage,
	}) => {
		const siteTemplateName: string = getRandomString();
		const siteName1: string = getRandomString();
		const siteName2: string = getRandomString();
		const webContentName: string = getRandomString();
		const text: string = getRandomString();
		const secondPageNameOnSiteTemplate = getRandomString();

		let site1Id: string | undefined;
		let site2Id: string | undefined;

		try {
			await applicationsMenuPage.goToGlobalSite();
			await productMenuPage.checkIfAdecuateProductMenu('Global');
			await productMenuPage.openProductMenuIfClosed();

			await productMenuPage.goToWebContent();
			await journalPage.goToCreateArticle();
			await journalPage.fillArticleDataSiteTemplate(webContentName, text);
			await journalPage.publishArticle();

			await createSiteTemplateWithContentPageAndAssetPublisher({
				applicationsMenuPage,
				layoutSetPrototypePage,
				page,
				pageEditorPage,
				pagesAdminPage,
				productMenuPage,
				templateName: siteTemplateName,
				uiElementsPage,
			});

			await applicationsMenuPage.goToSites();
			site1Id = await sitesPage.createSiteFromTemplate(
				siteTemplateName,
				siteName1
			);

			await applicationsMenuPage.goToSites();
			site2Id = await sitesPage.createSiteFromTemplate(
				siteTemplateName,
				siteName2
			);

			await applicationsMenuPage.goToSiteTemplates();
			const siteTemplateUrl =
				await layoutSetPrototypePage.getSiteTemplateUrl(
					siteTemplateName
				);
			await page.goto(siteTemplateUrl);

			await productMenuPage.checkIfAdecuateProductMenu(siteTemplateName);
			await productMenuPage.openProductMenuIfClosed();

			await productMenuPage.goToPages();
			await pagesAdminPage.newButton.click();
			await layoutSetPrototypePage.addTemplatePageButton.waitFor({
				state: 'visible',
			});
			await layoutSetPrototypePage.addTemplatePageButton.click();
			await pagesAdminPage.addPage({
				name: secondPageNameOnSiteTemplate,
			});

			await journalPage.goto('/global');

			await page.getByTestId('row').first().locator('a').click();

			await page.waitForTimeout(2000);

			const layoutsCountOnSite1 =
				await apiHelpers.jsonWebServicesLayout.getLayoutsCount(
					Number(site1Id),
					true
				);

			await expect(layoutsCountOnSite1).toBe(2);
		}
		finally {
			if (site1Id || site2Id) {
				await deleteSites(apiHelpers, site1Id, site2Id);
			}

			const layoutSetPrototypes: LayoutSetPrototype[] =
				await apiHelpers.jsonWebServicesLayoutSetPrototype.getLayoutSetPrototypes();
			const layoutSetPrototype = await getLayoutTemplateByName(
				layoutSetPrototypes,
				siteTemplateName
			);

			if (layoutSetPrototypes) {
				await deleteLayoutSetPrototype(
					apiHelpers,
					layoutSetPrototype.layoutSetPrototypeId.toString()
				);
			}

			await applicationsMenuPage.goToGlobalSite();
			await productMenuPage.checkIfAdecuateProductMenu('Global');
			await productMenuPage.openProductMenuIfClosed();
			await productMenuPage.goToWebContent();
			const checkbox = page
				.getByTestId('row')
				.first()
				.locator('input[type="checkbox"]');
			await checkbox.check();

			const deleteButton = page.getByRole('button', {name: 'Delete'});
			await deleteButton.click();
		}
	}
);

testWithPrivatePages(
	'Can switch template with web content on widget page.',
	async ({
		apiHelpers,
		applicationsMenuPage,
		journalPage,
		layoutSetPrototypePage,
		page,
		pagesAdminPage,
		productMenuPage,
		serverAdministrationPage,
		uiElementsPage,
		webContentDisplayPage,
		widgetPagePage,
	}) => {
		const widgetTemplateName1: string = getRandomString();
		const widgetTemplateName2: string = getRandomString();
		const siteName: string = getRandomString();

		await createSiteTemplateWithWebContentOnWidgetPage({
			apiHelpers,
			journalPage,
			page,
			pagesAdminPage,
			productMenuPage,
			templateName: widgetTemplateName1,
			text: `${webContentText1} `,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName1,
			widgetPagePage,
		});

		await createSiteTemplateWithWebContentOnWidgetPage({
			apiHelpers,
			journalPage,
			page,
			pagesAdminPage,
			productMenuPage,
			templateName: widgetTemplateName2,
			text: `${webContentText2} `,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName2,
			widgetPagePage,
		});
		const layoutSetPrototypes: LayoutSetPrototype[] =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.getLayoutSetPrototypes();
		const layoutSetPrototype1 = await getLayoutTemplateByName(
			layoutSetPrototypes,
			widgetTemplateName1
		);
		const layoutSetPrototype2 = await getLayoutTemplateByName(
			layoutSetPrototypes,
			widgetTemplateName2
		);
		await applicationsMenuPage.goToSites();

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
			templateKey: layoutSetPrototype1.layoutSetPrototypeId,
			templateType: 'site-template',
		});

		await applicationsMenuPage.goToServerAdministration();

		const script = `
    import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
    String siteTemplateUUID = "${layoutSetPrototype2.uuid}";
    long siteId = ${site.id};
    LayoutSetLocalServiceUtil.updateLayoutSetPrototypeLinkEnabled(siteId, true, true, siteTemplateUUID);
    `;
		await serverAdministrationPage.executeScript(script);

		await applicationsMenuPage.goToSites();

		await layoutSetPrototypePage.checkIfWebContentAdded(
			siteName,
			widgetTemplateName2,
			webContentText2
		);

		// tearDown

		await deleteSiteAndLayoutSetPrototypes(
			apiHelpers,
			site.id,
			layoutSetPrototype1.layoutSetPrototypeId.toString(),
			layoutSetPrototype2.layoutSetPrototypeId.toString()
		);
	}
);

testWithPrivatePages(
	'Can switch template with web content on content page.',
	async ({
		apiHelpers,
		applicationsMenuPage,
		journalPage,
		layoutSetPrototypePage,
		page,
		pageEditorPage,
		pagesAdminPage,
		productMenuPage,
		serverAdministrationPage,
		uiElementsPage,
		webContentDisplayPage,
	}) => {
		const contentTemplateName1: string = getRandomString();
		const contentTemplateName2: string = getRandomString();
		const siteName: string = getRandomString();

		await createSiteTemplateWithWebContentOnContentPage({
			apiHelpers,
			journalPage,
			layoutSetPrototypePage,
			page,
			pageEditorPage,
			pagesAdminPage,
			productMenuPage,
			templateName: contentTemplateName1,
			text: `${webContentText1} `,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName1,
		});

		await createSiteTemplateWithWebContentOnContentPage({
			apiHelpers,
			journalPage,
			layoutSetPrototypePage,
			page,
			pageEditorPage,
			pagesAdminPage,
			productMenuPage,
			templateName: contentTemplateName2,
			text: `${webContentText2} `,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName2,
		});

		const layoutSetPrototypes: LayoutSetPrototype[] =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.getLayoutSetPrototypes();
		const layoutSetPrototype1 = await getLayoutTemplateByName(
			layoutSetPrototypes,
			contentTemplateName1
		);
		const layoutSetPrototype2 = await getLayoutTemplateByName(
			layoutSetPrototypes,
			contentTemplateName2
		);

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
			templateKey: layoutSetPrototype1.layoutSetPrototypeId,
			templateType: 'site-template',
		});

		await layoutSetPrototypePage.checkIfWebContentAdded(
			siteName,
			contentTemplateName1,
			webContentText1
		);

		await applicationsMenuPage.goToServerAdministration();

		const script = `
		import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
		String siteTemplateUUID = "${layoutSetPrototype2.uuid}";
		long siteId = ${site.id};
		LayoutSetLocalServiceUtil.updateLayoutSetPrototypeLinkEnabled(siteId, true, true, siteTemplateUUID);
		`;
		await serverAdministrationPage.executeScript(script);

		await layoutSetPrototypePage.checkIfWebContentAdded(
			siteName,
			contentTemplateName2,
			webContentText2
		);

		// tearDown

		await deleteSiteAndLayoutSetPrototypes(
			apiHelpers,
			site.id,
			layoutSetPrototype1.layoutSetPrototypeId.toString(),
			layoutSetPrototype2.layoutSetPrototypeId.toString()
		);
	}
);

testWithPrivatePages(
	'Can switch template with web content on home page.',
	async ({
		apiHelpers,
		applicationsMenuPage,
		journalPage,
		layoutSetPrototypePage,
		page,
		pageEditorPage,
		productMenuPage,
		serverAdministrationPage,
		uiElementsPage,
		webContentDisplayPage,
	}) => {
		const contentTemplateName1: string = getRandomString();
		const contentTemplateName2: string = getRandomString();
		const siteName: string = getRandomString();

		await createSiteTemplateWithWebContentOnHomePage({
			apiHelpers,
			applicationsMenuPage,
			journalPage,
			layoutSetPrototypePage,
			page,
			pageEditorPage,
			productMenuPage,
			templateName: contentTemplateName1,
			text: `${webContentText1} `,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName1,
		});

		await createSiteTemplateWithWebContentOnHomePage({
			apiHelpers,
			applicationsMenuPage,
			journalPage,
			layoutSetPrototypePage,
			page,
			pageEditorPage,
			productMenuPage,
			templateName: contentTemplateName2,
			text: `${webContentText2} `,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName2,
		});

		const layoutSetPrototypes: LayoutSetPrototype[] =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.getLayoutSetPrototypes();
		const layoutSetPrototype1 = await getLayoutTemplateByName(
			layoutSetPrototypes,
			contentTemplateName1
		);
		const layoutSetPrototype2 = await getLayoutTemplateByName(
			layoutSetPrototypes,
			contentTemplateName2
		);

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
			templateKey: layoutSetPrototype1.layoutSetPrototypeId,
			templateType: 'site-template',
		});

		await layoutSetPrototypePage.checkIfWebContentAddedToHome(
			siteName,
			webContentText1
		);

		await applicationsMenuPage.goToServerAdministration();

		const script = `
		import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
		String siteTemplateUUID = "${layoutSetPrototype2.uuid}";
		long siteId = ${site.id};
		LayoutSetLocalServiceUtil.updateLayoutSetPrototypeLinkEnabled(siteId, true, true, siteTemplateUUID);
		`;
		await serverAdministrationPage.executeScript(script);

		await layoutSetPrototypePage.checkIfWebContentAddedToHome(
			siteName,
			webContentText1
		);

		// tearDown

		await deleteSiteAndLayoutSetPrototypes(
			apiHelpers,
			site.id,
			layoutSetPrototype1.layoutSetPrototypeId.toString(),
			layoutSetPrototype2.layoutSetPrototypeId.toString()
		);
	}
);
