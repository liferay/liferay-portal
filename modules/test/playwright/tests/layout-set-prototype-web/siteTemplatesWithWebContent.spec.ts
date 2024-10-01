/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../fixtures/productMenuPageTest';
import {serverAdministrationPageTest} from '../../fixtures/serverAdministrationPageTest';
import {sitesPageTest} from '../../fixtures/sitesPageTest';
import {systemSettingsPageTest} from '../../fixtures/systemSettingsPageTest';
import {uiElementsPageTest} from '../../fixtures/uiElementsTest';
import {webContentDisplayPageTest} from '../../fixtures/webContentDisplayPageTest';
import {ApiHelpers} from '../../helpers/ApiHelpers';
import {LayoutSetPrototype} from '../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {WebContentDisplayPage} from '../../pages/journal-content-web/WebContentDisplayPage';
import {PagesAdminPage} from '../../pages/layout-admin-web/PagesAdminPage';
import {WidgetPagePage} from '../../pages/layout-admin-web/WidgetPagePage';
import {PageEditorPage} from '../../pages/layout-content-page-editor-web/PageEditorPage';
import {ApplicationsMenuPage} from '../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {ProductMenuPage} from '../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {UIElementsPage} from '../../pages/uielements/UIElementsPage';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';
import {JournalPage} from '../journal-web/pages/JournalPage';
import {pagesPagesTest} from '../layout-admin-web/fixtures/pagesPagesTest';
import {layoutSetPrototypePageTest} from './fixtures/layoutSetPrototypePageTest';
import {LayoutSetPrototypePage} from './pages/LayoutSetPrototypePage';

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
	systemSettingsPageTest,
	loginTest(),
	pagesAdminPagesTest
);

const webContentName1: string = getRandomString();
const webContentName2: string = getRandomString();
const webContentText1: string = getRandomString();
const webContentText2: string = getRandomString();

test(
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
		systemSettingsPage,
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
			await systemSettingsPage.disablePrivatePages();

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
				successMessage: 'Success:The page was created successfully.',
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

test('Can switch template with web content on widget page.', async ({
	apiHelpers,
	applicationsMenuPage,
	journalPage,
	layoutSetPrototypePage,
	page,
	pagesAdminPage,
	productMenuPage,
	serverAdministrationPage,
	systemSettingsPage,
	uiElementsPage,
	webContentDisplayPage,
	widgetPagePage,
}) => {
	const widgetTemplateName1: string = getRandomString();
	const widgetTemplateName2: string = getRandomString();
	const siteName: string = getRandomString();

	await systemSettingsPage.disablePrivatePages();

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
});

test('Can switch template with web content on content page.', async ({
	apiHelpers,
	applicationsMenuPage,
	journalPage,
	layoutSetPrototypePage,
	page,
	pageEditorPage,
	pagesAdminPage,
	productMenuPage,
	serverAdministrationPage,
	systemSettingsPage,
	uiElementsPage,
	webContentDisplayPage,
}) => {
	await systemSettingsPage.disablePrivatePages();

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
});

test('Can switch template with web content on home page.', async ({
	apiHelpers,
	applicationsMenuPage,
	journalPage,
	layoutSetPrototypePage,
	page,
	pageEditorPage,
	productMenuPage,
	serverAdministrationPage,
	systemSettingsPage,
	uiElementsPage,
	webContentDisplayPage,
}) => {
	await systemSettingsPage.disablePrivatePages();

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
});

async function deleteSiteAndLayoutSetPrototypes(
	apiHelpers: ApiHelpers,
	siteId: string,
	...layoutSetPrototypeIds: string[]
) {
	let response = await apiHelpers.headlessSite.deleteSite(siteId);
	if (!response.ok()) {
		response = await apiHelpers.headlessSite.deleteSite(siteId);
	}
	expect(response.ok()).toBe(true);
	for (const prototypeId of layoutSetPrototypeIds) {
		await apiHelpers.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
			prototypeId
		);
	}
}

async function deleteSites(apiHelpers: ApiHelpers, ...siteIds: string[]) {
	for (const siteId of siteIds) {
		let response = await apiHelpers.headlessSite.deleteSite(siteId);
		if (!response.ok()) {
			response = await apiHelpers.headlessSite.deleteSite(siteId);
		}
		expect(response.ok()).toBe(true);
	}
}

async function deleteLayoutSetPrototype(
	apiHelpers: ApiHelpers,
	layoutSetPrototypeId: string
) {
	await apiHelpers.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
		layoutSetPrototypeId
	);
}

async function getLayoutTemplateByName(
	layoutSetPrototypes: LayoutSetPrototype[],
	targetName: string
): Promise<LayoutSetPrototype> {
	const targetLayout = layoutSetPrototypes.find(
		(layoutSetPrototype) =>
			layoutSetPrototype.nameCurrentValue === targetName
	);

	if (targetLayout) {
		return {
			layoutSetPrototypeId: targetLayout.layoutSetPrototypeId,
			nameCurrentValue: targetLayout.nameCurrentValue,
			uuid: targetLayout.uuid,
		};
	}
	else {
		return {
			layoutSetPrototypeId: undefined,
			nameCurrentValue: undefined,
			uuid: undefined,
		};
	}
}

async function createSiteTemplateWithContentPageAndAssetPublisher({
	applicationsMenuPage,
	layoutSetPrototypePage,
	page,
	pageEditorPage,
	pagesAdminPage,
	productMenuPage,
	templateName,
}: {
	applicationsMenuPage: ApplicationsMenuPage;
	layoutSetPrototypePage: LayoutSetPrototypePage;
	page: Page;
	pageEditorPage: PageEditorPage;
	pagesAdminPage: PagesAdminPage;
	productMenuPage: ProductMenuPage;
	templateName: string;
	uiElementsPage: UIElementsPage;
}): Promise<void> {
	await applicationsMenuPage.goToSiteTemplates();
	await layoutSetPrototypePage.addSiteTemplate(templateName);
	await applicationsMenuPage.goToSiteTemplates();
	const siteTemplateUrl =
		await layoutSetPrototypePage.getSiteTemplateUrl(templateName);

	await page.goto(siteTemplateUrl);
	await productMenuPage.checkIfAdecuateProductMenu(templateName);
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPages();
	await pagesAdminPage.newButton.click();
	await layoutSetPrototypePage.addTemplatePageButton.waitFor({
		state: 'visible',
	});
	await layoutSetPrototypePage.addTemplatePageButton.click();
	await pagesAdminPage.addPage({
		name: templateName,
		successMessage: 'Success:The page was created successfully.',
	});
	await pageEditorPage.addWidget('Content Management', 'Asset Publisher');

	const widgetId = await pageEditorPage.getFragmentId('Asset Publisher');

	const topper = pageEditorPage.getTopper(widgetId);
	await topper.hover();
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		}),
		trigger: topper.locator('.portlet-options'),
	});

	const configurationModal = await page.frameLocator(
		'iframe[title*="Asset Publisher"][title*="Configuration"]'
	);
	await configurationModal.locator('.portlet-body').waitFor();

	const configurationManualInput = await configurationModal.getByLabel(
		'Manual',
		{exact: true}
	);

	if (await configurationManualInput.isHidden()) {
		await configurationModal
			.getByRole('link', {name: 'Asset Selection'})
			.click();
	}
	if (!(await configurationManualInput.isChecked())) {
		await configurationManualInput.click();

		await waitForAlert(
			configurationModal,
			'Success:You have successfully updated the setup.'
		);
	}

	const scopeSection = configurationModal.locator('#scopeContent');
	if (await scopeSection.isHidden()) {
		await configurationModal.getByRole('link', {name: 'Scope'}).click();
	}
	await scopeSection.waitFor();

	const selectButton = scopeSection.locator('button.dropdown-toggle', {
		hasText: 'Select',
	});
	await selectButton.click();

	const globalOption = configurationModal.getByRole('menuitem', {
		name: 'Global',
	});
	await globalOption.click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	const currentSiteDeleteButton = scopeSection
		.getByRole('row', {name: /^Current Site/})
		.getByLabel('Delete');
	await currentSiteDeleteButton.click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	const assetEntriesSection = configurationModal.locator(
		'#assetEntriesContent'
	);
	if (await assetEntriesSection.isHidden()) {
		await configurationModal
			.getByRole('link', {name: 'Asset Entries'})
			.click();
	}
	await assetEntriesSection.waitFor();

	const selectAssetEntriesButton = assetEntriesSection.locator(
		'button.dropdown-toggle',
		{hasText: 'Select'}
	);
	await selectAssetEntriesButton.click();

	const basicWebContentOption = configurationModal.getByRole('menuitem', {
		name: 'Basic Web Content',
	});
	await basicWebContentOption.click();

	const selectWebContentModal = await configurationModal.frameLocator(
		'iframe[title*="Select Basic Web Content"]'
	);

	await selectWebContentModal.locator('#main-content').waitFor();

	const checkbox = selectWebContentModal
		.getByTestId('row')
		.first()
		.locator('input[type="checkbox"]');
	await checkbox.check();

	const addButton = configurationModal.getByRole('button', {name: 'Add'});
	await addButton.click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	await configurationModal.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	await page.getByLabel('close', {exact: true}).click();

	await pageEditorPage.publishPage();
}

async function createSiteTemplateWithWebContentOnWidgetPage({
	apiHelpers,
	journalPage,
	page,
	pagesAdminPage,
	productMenuPage,
	templateName,
	text,
	uiElementsPage,
	webContentDisplayPage,
	webContentName,
	widgetPagePage,
}: {
	apiHelpers: ApiHelpers;
	journalPage: JournalPage;
	page: Page;
	pagesAdminPage: PagesAdminPage;
	productMenuPage: ProductMenuPage;
	templateName: string;
	text: string;
	uiElementsPage: UIElementsPage;
	webContentDisplayPage: WebContentDisplayPage;
	webContentName: string;
	widgetPagePage: WidgetPagePage;
}): Promise<void> {
	const layoutSetPrototype: LayoutSetPrototype =
		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			templateName
		);
	await page.goto(
		'group/template-' + layoutSetPrototype.layoutSetPrototypeId
	);

	await productMenuPage.checkIfAdecuateProductMenu(templateName);
	await productMenuPage.openProductMenuIfClosed();
	await productMenuPage.goToWebContent();
	await journalPage.goToCreateArticle();
	await journalPage.fillArticleDataSiteTemplate(webContentName, text);
	await journalPage.publishArticle();

	await productMenuPage.goToPages();

	await page
		.locator('.control-menu-level-1-heading')
		.filter({hasText: 'Pages'})
		.waitFor();

	await pagesAdminPage.addWidgetPage({
		addButtonLabel: 'Add Site Template Page',
		name: templateName,
	});

	await productMenuPage.clickSpecificPage(templateName);
	await widgetPagePage.addButton.click();
	await webContentDisplayPage.addWebContentWithWidget();
	await uiElementsPage.setupUpdatedAlert.waitFor({state: 'hidden'});
	await uiElementsPage.closeClickable.click();
	await uiElementsPage.closeClickable.waitFor({
		state: 'hidden',
	});
}

async function createSiteTemplateWithWebContentOnContentPage({
	apiHelpers,
	journalPage,
	layoutSetPrototypePage,
	page,
	pageEditorPage,
	pagesAdminPage,
	productMenuPage,
	templateName,
	text,
	uiElementsPage,
	webContentDisplayPage,
	webContentName,
}: {
	apiHelpers: ApiHelpers;
	journalPage: JournalPage;
	layoutSetPrototypePage: LayoutSetPrototypePage;
	page: Page;
	pageEditorPage: PageEditorPage;
	pagesAdminPage: PagesAdminPage;
	productMenuPage: ProductMenuPage;
	templateName: string;
	text: string;
	uiElementsPage: UIElementsPage;
	webContentDisplayPage: WebContentDisplayPage;
	webContentName: string;
}): Promise<void> {
	const layoutSetPrototype: LayoutSetPrototype =
		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			templateName
		);
	await page.goto(
		'group/template-' + layoutSetPrototype.layoutSetPrototypeId
	);
	await productMenuPage.checkIfAdecuateProductMenu(templateName);
	await productMenuPage.openProductMenuIfClosed();
	await productMenuPage.goToWebContent();
	await journalPage.goToCreateArticle();
	await journalPage.fillArticleDataSiteTemplate(webContentName, text);
	await journalPage.publishArticle();

	await productMenuPage.goToPages();
	await pagesAdminPage.newButton.click();
	await layoutSetPrototypePage.addTemplatePageButton.waitFor({
		state: 'visible',
	});
	await layoutSetPrototypePage.addTemplatePageButton.click();
	await pagesAdminPage.addPage({
		name: templateName,
		successMessage: 'Success:The page was created successfully.',
	});

	await pageEditorPage.addWidget('Content Management', 'Web Content Display');
	await webContentDisplayPage.addWebContentWithDisplay();
	await uiElementsPage.publishButton.click();
}

async function createSiteTemplateWithWebContentOnHomePage({
	apiHelpers,
	journalPage,
	layoutSetPrototypePage,
	page,
	pageEditorPage,
	productMenuPage,
	templateName,
	text,
	uiElementsPage,
	webContentDisplayPage,
	webContentName,
}: {
	apiHelpers: ApiHelpers;
	applicationsMenuPage: ApplicationsMenuPage;
	journalPage: JournalPage;
	layoutSetPrototypePage: LayoutSetPrototypePage;
	page: Page;
	pageEditorPage: PageEditorPage;
	productMenuPage: ProductMenuPage;
	templateName: string;
	text: string;
	uiElementsPage: UIElementsPage;
	webContentDisplayPage: WebContentDisplayPage;
	webContentName: string;
}): Promise<void> {
	const layoutSetPrototype: LayoutSetPrototype =
		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			templateName
		);
	await page.goto(
		'group/template-' + layoutSetPrototype.layoutSetPrototypeId
	);
	await productMenuPage.checkIfAdecuateProductMenu(templateName);
	await productMenuPage.openProductMenuIfClosed();
	await productMenuPage.goToWebContent();
	await journalPage.goToCreateArticle();
	await journalPage.fillArticleDataSiteTemplate(webContentName, text);
	await journalPage.publishArticle();

	await productMenuPage.goToPages();
	await layoutSetPrototypePage.homePageLink.click();
	await pageEditorPage.addWidget('Content Management', 'Web Content Display');
	await webContentDisplayPage.addWebContentWithDisplay();
	await uiElementsPage.publishButton.click();
}
