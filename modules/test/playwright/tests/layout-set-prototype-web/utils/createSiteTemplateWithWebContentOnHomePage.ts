/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {LayoutSetPrototype} from '../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {WebContentDisplayPage} from '../../../pages/journal-content-web/WebContentDisplayPage';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {ApplicationsMenuPage} from '../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {ProductMenuPage} from '../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {UIElementsPage} from '../../../pages/uielements/UIElementsPage';
import {JournalPage} from '../../journal-web/pages/JournalPage';
import {LayoutSetPrototypePage} from '../pages/LayoutSetPrototypePage';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export default async function createSiteTemplateWithWebContentOnHomePage({
	apiHelpers,
	journalPage,
	layoutSetPrototype,
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
	layoutSetPrototype: LayoutSetPrototype;
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
	await page.goto(
		'group/template-' + layoutSetPrototype.layoutSetPrototypeId
	);
	
	const siteId = await page.evaluate(() => {
		return String(Liferay.ThemeDisplay.getSiteGroupId());
	});

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		content: text,
		ddmStructureId: basicWebContentStructureId,
		groupId: siteId,
		titleMap: {en_US: webContentName},
	});

	await productMenuPage.checkIfAdecuateProductMenu(templateName);
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPages();
	await layoutSetPrototypePage.homePageLink.click();
	await pageEditorPage.addWidget('Content Management', 'Web Content Display');
	await webContentDisplayPage.addWebContentWithDisplay(webContentName);
	await uiElementsPage.publishButton.click();
}
