/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {LayoutSetPrototype} from '../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {WebContentDisplayPage} from '../../../pages/journal-content-web/WebContentDisplayPage';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {WidgetPagePage} from '../../../pages/layout-admin-web/WidgetPagePage';
import {ProductMenuPage} from '../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {UIElementsPage} from '../../../pages/uielements/UIElementsPage';
import {JournalPage} from '../../journal-web/pages/JournalPage';

export default async function createSiteTemplateWithWebContentOnWidgetPage({
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
