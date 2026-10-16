/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {LayoutSetPrototype} from '../../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from '../../../../utils/getRandomString';

export default async function createSiteTemplateWithMessageBoards({
	apiHelpers,
	page,
	pageEditorPage,
}: {
	apiHelpers: DataApiHelpers;
	page: Page;
	pageEditorPage: PageEditorPage;
}): Promise<{
	layout: Layout;
	layoutSetPrototype: LayoutSetPrototype;
}> {
	const layoutSetPrototype =
		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			{
				name: 'SiteTemplate-' + getRandomString(),
			}
		);

	apiHelpers.data.push({
		id: layoutSetPrototype.layoutSetPrototypeId,
		type: 'layoutSetPrototype',
	});

	const layoutSetPrototypeGroup =
		await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			layoutSetPrototype.companyId,
			layoutSetPrototype.layoutSetPrototypeId
		);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: layoutSetPrototypeGroup.groupId,
		options: {type: 'content'},
		privateLayout: 'true',
		title: 'page-' + getRandomString().toLowerCase(),
	});

	await page.goto(
		`/group/template-${layoutSetPrototype.layoutSetPrototypeId}${layout.friendlyURL}?p_l_mode=edit`
	);

	await pageEditorPage.addWidget('Collaboration', 'Message Boards');

	await pageEditorPage.publishPage();

	return {
		layout,
		layoutSetPrototype,
	};
}
