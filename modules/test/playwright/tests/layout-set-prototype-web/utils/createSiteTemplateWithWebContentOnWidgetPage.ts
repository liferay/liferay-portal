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
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export default async function createSiteTemplateWithWebContentOnWidgetPage({
	apiHelpers,
	layoutSetPrototype,
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
	layoutSetPrototype: LayoutSetPrototype;
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
	await pagesAdminPage.addWidgetPage({
		addButtonLabel: 'Add Site Template Page',
		name: templateName,
	});
	
	await page.goto(
		`group/template-${layoutSetPrototype.layoutSetPrototypeId}/${templateName}`
	);

	await widgetPagePage.addButton.click();
	await webContentDisplayPage.addWebContentWithWidget(webContentName);
	await uiElementsPage.setupUpdatedAlert.waitFor({state: 'hidden'});
	await uiElementsPage.closeClickable.click();
	await uiElementsPage.closeClickable.waitFor({
		state: 'hidden',
	});
}
