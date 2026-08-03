/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, BrowserContext, Page} from '@playwright/test';

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {addCMSAdministrator} from '../../../../utils/addCMSAdministrator';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';

// Provisions a second CMS Administrator as a member of the given Space and
// signs them in on a fresh browser context, so a spec can drive two editors
// concurrently. The caller owns the returned context and closes it.

export async function openSecondCmsEditor(
	apiHelpers: DataApiHelpers,
	browser: Browser,
	spaceExternalReferenceCode: string
): Promise<{contentsPage: ContentsPage; context: BrowserContext; page: Page}> {
	const user = await addCMSAdministrator(apiHelpers);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		spaceExternalReferenceCode,
		user.externalReferenceCode
	);

	const context = await browser.newContext();
	const page = await context.newPage();

	await performLoginViaApi({page, screenName: user.alternateName});

	return {contentsPage: new ContentsPage(page), context, page};
}
