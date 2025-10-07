/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import fillAndClickOutside from '../../../../utils/fillAndClickOutside';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export async function createSegmentsEntry(apiHelpers, name: string) {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');
	const userName = getRandomString();

	return await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
		criteria: {
			criteria: {
				user: {
					conjunction: 'and',
					filterString: `(firstName eq '${userName}')`,
					typeValue: 'model',
				},
			},
			filterString: {
				model: `(firstName eq '${userName}')`,
			},
		},
		groupId: site.id,
		name,
	});
}

export async function editSegmentsEntry(
	name: string,
	page: Page,
	editedName?: string,
	locale?: boolean
) {
	await goToSegmentsAdmin(page);

	await page.getByLabel('Show More Options for ' + name).click();

	await page.getByRole('menuitem', {name: 'Edit'}).click();

	await page.getByText('Conditions');

	if (locale) {
		await switchSegmentsLanguage('es-ES', page);
	}

	if (editedName) {
		await fillAndClickOutside(
			page,
			page.locator('[data-testid="localized-input-button"]'),
			editedName
		);
	}

	await saveSegmentsEntry(page);
}

export async function goToSegmentsAdmin(
	page,
	siteUrl?: Site['friendlyUrlPath']
) {
	await page.goto(`/group${siteUrl || '/guest'}${PORTLET_URLS.segments}`);
}

export async function saveSegmentsEntry(page) {
	await page.getByText('Save').click();

	await waitForAlert(page, 'Success:Your request completed successfully.');
}

export async function switchSegmentsLanguage(language: string, page) {
	await page.getByTitle('en-US').click();

	await page
		.getByRole('option', {
			name: `${language} language: Untranslated`,
		})
		.click();
}
