/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../../../pages/layout-content-page-editor-web/PageEditorPage';
import {DisplayPageTemplatesPage} from '../../../../../pages/layout-page-template-admin-web/DisplayPageTemplatesPage';
import getRandomString from '../../../../../utils/getRandomString';

/**
 * Creates the default display page template of a CMS object definition and
 * maps a Heading fragment to one of its fields, so an entry of that definition
 * renders its own value at its friendly URL.
 *
 * The template itself is created and marked as default through the API. Only
 * the fragment mapping goes through the page editor, because no API exposes it.
 */
export async function createDefaultDisplayPageTemplate({
	apiHelpers,
	displayPageTemplatesPage,
	mappedField = 'Title',
	objectDefinitionName,
	page,
	pageEditorPage,
	site,
}: {
	apiHelpers: ApiHelpers;
	displayPageTemplatesPage: DisplayPageTemplatesPage;
	mappedField?: string;
	objectDefinitionName: string;
	page: Page;
	pageEditorPage: PageEditorPage;
	site: Site;
}) {
	const name = `DPT ${getRandomString()}`;

	const objectDefinition =
		await apiHelpers.objectAdmin.getObjectDefinitionByName(
			objectDefinitionName
		);

	const className = await apiHelpers.jsonWebServicesClassName.fetchClassName(
		objectDefinition.className
	);

	const layoutPageTemplateEntry =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				groupId: String(site.id),
				name,
			}
		);

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
		{
			layoutPageTemplateEntryId:
				layoutPageTemplateEntry.layoutPageTemplateEntryId,
		}
	);

	await displayPageTemplatesPage.goto(site.friendlyUrlPath);

	await displayPageTemplatesPage.editTemplate(name);

	await pageEditorPage.addFragment(
		'Basic Components',
		'Heading',
		page.getByText('Drag and drop fragments or widgets here')
	);

	const headingId = await pageEditorPage.getFragmentId('Heading');

	await pageEditorPage.selectEditable(headingId, 'element-text');

	await pageEditorPage.changeConfiguration({
		fieldLabel: 'Field',
		tab: 'Mapping',
		value: mappedField,
	});

	await pageEditorPage.publishPage();

	return name;
}
