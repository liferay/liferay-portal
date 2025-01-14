/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../fixtures/pageManagementSiteTest';
import {pageViewModePagesTest} from '../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../fixtures/productMenuPageTest';
import {serverAdministrationPageTest} from '../../fixtures/serverAdministrationPageTest';
import {sitesPageTest} from '../../fixtures/sitesPageTest';
import {uiElementsPageTest} from '../../fixtures/uiElementsTest';
import {webContentDisplayPageTest} from '../../fixtures/webContentDisplayPageTest';
import {LayoutSetPrototype} from '../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import getGlobalSiteId from '../../utils/getGlobalSiteId';
import getRandomString from '../../utils/getRandomString';
import getBasicWebContentStructureId from '../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';
import {pagesPagesTest} from '../layout-admin-web/fixtures/pagesPagesTest';
import {layoutSetPrototypePageTest} from './fixtures/layoutSetPrototypePageTest';
import createSiteTemplateWithContentPageAndAssetPublisher from './utils/createSiteTemplateWithContentPageAndAssetPublisher';
import createSiteTemplateWithWebContentOnContentPage from './utils/createSiteTemplateWithWebContentOnContentPage';
import createSiteTemplateWithWebContentOnHomePage from './utils/createSiteTemplateWithWebContentOnHomePage';
import createSiteTemplateWithWebContentOnWidgetPage from './utils/createSiteTemplateWithWebContentOnWidgetPage';
import deleteSiteAndLayoutSetPrototypes from './utils/deleteSiteAndLayoutSetPrototypes';
import getLayoutTemplateByName from './utils/getLayoutTemplateByName';

export const test = mergeTests(
	applicationsMenuPageTest,
	apiHelpersTest,
	dataApiHelpersTest,
	journalPagesTest,
	isolatedSiteTest,
	pageManagementSiteTest,
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

		await applicationsMenuPage.goToGlobalSite();
		await productMenuPage.checkIfAdecuateProductMenu('Global');
		await productMenuPage.openProductMenuIfClosed();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const globalSiteId = await getGlobalSiteId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: text,
				ddmStructureId: basicWebContentStructureId,
				groupId: globalSiteId,
				titleMap: {en_US: webContentName},
			});

		apiHelpers.data.push({
			id: `${globalSiteId}_${webContent.articleId}`,
			type: 'webContent',
		});

		const layoutSetPrototype =
			await createSiteTemplateWithContentPageAndAssetPublisher({
				apiHelpers,
				applicationsMenuPage,
				layoutSetPrototypePage,
				page,
				pageEditorPage,
				pagesAdminPage,
				productMenuPage,
				templateName: siteTemplateName,
				uiElementsPage,
			});

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await applicationsMenuPage.goToSites();
		const site1Id = await sitesPage.createSiteFromTemplate(
			siteTemplateName,
			siteName1
		);

		apiHelpers.data.push({id: site1Id, type: 'site'});

		await applicationsMenuPage.goToSites();
		const site2Id = await sitesPage.createSiteFromTemplate(
			siteTemplateName,
			siteName2
		);

		apiHelpers.data.push({id: site2Id, type: 'site'});

		await applicationsMenuPage.goToSiteTemplates();
		const siteTemplateUrl =
			await layoutSetPrototypePage.getSiteTemplateUrl(siteTemplateName);
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
);

testWithPrivatePages(
	'Can switch template with web content on widget page.',
	async ({
		apiHelpers,
		applicationsMenuPage,
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

		const layoutSetPrototype1: LayoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				widgetTemplateName1
			);

		apiHelpers.data.push({
			id: layoutSetPrototype1.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await createSiteTemplateWithWebContentOnWidgetPage({
			apiHelpers,
			layoutSetPrototype: layoutSetPrototype1,
			page,
			pagesAdminPage,
			productMenuPage,
			templateName: widgetTemplateName1,
			text: webContentText1,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName1,
			widgetPagePage,
		});

		const layoutSetPrototype2: LayoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				widgetTemplateName2
			);

		apiHelpers.data.push({
			id: layoutSetPrototype2.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await createSiteTemplateWithWebContentOnWidgetPage({
			apiHelpers,
			layoutSetPrototype: layoutSetPrototype2,
			page,
			pagesAdminPage,
			productMenuPage,
			templateName: widgetTemplateName2,
			text: webContentText2,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName2,
			widgetPagePage,
		});

		await applicationsMenuPage.goToSites();

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
			templateKey: layoutSetPrototype1.layoutSetPrototypeId,
			templateType: 'site-template',
		});

		apiHelpers.data.push({
			id: site.id,
			type: 'site',
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
			webContentName2
		);
	}
);

testWithPrivatePages(
	'Can switch template with web content on content page.',
	async ({
		apiHelpers,
		applicationsMenuPage,
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

		const layoutSetPrototype1: LayoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				contentTemplateName1
			);

		apiHelpers.data.push({
			id: layoutSetPrototype1.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await createSiteTemplateWithWebContentOnContentPage({
			apiHelpers,
			layoutSetPrototype: layoutSetPrototype1,
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

		const layoutSetPrototype2: LayoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				contentTemplateName2
			);

		apiHelpers.data.push({
			id: layoutSetPrototype2.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await createSiteTemplateWithWebContentOnContentPage({
			apiHelpers,
			layoutSetPrototype: layoutSetPrototype2,
			layoutSetPrototypePage,
			page,
			pageEditorPage,
			pagesAdminPage,
			productMenuPage,
			templateName: contentTemplateName2,
			text: webContentText2,
			uiElementsPage,
			webContentDisplayPage,
			webContentName: webContentName2,
		});

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
			templateKey: layoutSetPrototype1.layoutSetPrototypeId,
			templateType: 'site-template',
		});

		apiHelpers.data.push({
			id: site.id,
			type: 'site',
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
