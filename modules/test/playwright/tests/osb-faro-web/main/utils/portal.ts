/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';

export async function acceptsCookiesBanner(page: Page) {
	const cookiesBannerButton = page.getByRole('button', {name: 'Accept All'});

	if (await cookiesBannerButton.isVisible()) {
		await cookiesBannerButton.click();
	}
}

export const createSitePage = async function ({
	apiHelpers,
	pageTitle,
	siteName = 'Guest',
}: {
	apiHelpers: ApiHelpers;
	pageTitle: string;
	siteName?: string;
}) {
	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
		company.companyId,
		siteName
	);

	return await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			}),
		]),
		siteId: group.groupId,
		title: pageTitle,
	});
};

export async function navigateToDXPandDeleteSite({
	apiHelpers,
	page,
	site,
}: {
	apiHelpers: ApiHelpers;
	page: Page;
	site: Site;
}) {
	await page.goto(liferayConfig.environment.baseUrl);

	await apiHelpers.headlessSite.deleteSite(String(site.id));
}

export async function navigateToSitePage({
	layout,
	page,
	pageName,
	siteName,
}: {
	layout?: Layout;
	page: Page;
	pageName: string;
	siteName?: string;
}) {
	const pageNameURL = pageName.replace(/ /g, '-').toLowerCase();

	if (siteName) {
		const siteNameURL = siteName.replace(/ /g, '-').toLowerCase();

		if (layout) {
			await page.goto(
				`${liferayConfig.environment.baseUrl}/web/${siteNameURL}/` +
					`${pageNameURL}?p_l_mode=edit`
			);
		}
		else {
			await page.goto(
				`${liferayConfig.environment.baseUrl}/web/${siteNameURL}/` +
					`${pageNameURL}`
			);
		}
	}
	else {
		await page.goto(
			`${liferayConfig.environment.baseUrl}/web/guest/${pageNameURL}`
		);
	}
}
