/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {LayoutSetPrototype} from '../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {ProductMenuPage} from '../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export default async function createSiteTemplate({
	apiHelpers,
	layoutsUpdateable = true,
	page,
	productMenuPage,
	templateName,
	text,
	webContentName,
}: {
	apiHelpers: ApiHelpers;
	layoutsUpdateable?: boolean;
	page: Page;
	productMenuPage: ProductMenuPage;
	templateName: string;
	text?: string;
	webContentName?: string;
}): Promise<LayoutSetPrototype> {
	const layoutSetPrototype: LayoutSetPrototype =
		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			{
				layoutsUpdateable,
				name: templateName,
			}
		);

	await page.goto(
		'group/template-' + layoutSetPrototype.layoutSetPrototypeId
	);

	const siteId = await page.evaluate(() => {
		return String(Liferay.ThemeDisplay.getSiteGroupId());
	});

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	if (text && webContentName) {
		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: text,
			ddmStructureId: basicWebContentStructureId,
			groupId: siteId,
			titleMap: {en_US: webContentName},
		});
	}

	await productMenuPage.checkIfAdecuateProductMenu(templateName);
	await productMenuPage.openProductMenuIfClosed();

	return layoutSetPrototype;
}
