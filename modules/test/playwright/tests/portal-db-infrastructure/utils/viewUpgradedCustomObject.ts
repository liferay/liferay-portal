/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

const OBJECT_DEFINITIONS_PATH =
	'/group/guest/~/control_panel/manage?p_p_id=com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet';

/**
 * Asserts that the custom object one virtual instance defines survived the
 * legacy database upgrade, keeping the label and plural label that identify it,
 * and that the neighbouring instance's object is not visible from here. Only
 * the 7.4.13.u33 archive carries custom objects, so this runs for the project
 * whose config sets assertCustomObjects.
 */
export async function viewUpgradedCustomObject({
	absentNameSuffix,
	instanceURL,
	nameSuffix,
	page,
}: {
	absentNameSuffix: string;
	instanceURL: string;
	nameSuffix: string;
	page: Page;
}) {
	await page.goto(`${instanceURL}${OBJECT_DEFINITIONS_PATH}`);

	const objectLink = page.getByRole('link', {
		exact: true,
		name: `Custom Object${nameSuffix}`,
	});

	await expect(objectLink).toBeVisible();

	await expect(
		page.getByRole('link', {
			exact: true,
			name: `Custom Object${absentNameSuffix}`,
		})
	).toBeHidden();

	await objectLink.click();

	// The form labels its inputs "Label Mandatory" and "Plural Label Mandatory",
	// and Scope is a disabled combobox rather than text.

	await expect(
		page.getByRole('textbox', {exact: true, name: 'Label Mandatory'})
	).toHaveValue(`Custom Object${nameSuffix}`);

	await expect(
		page.getByRole('textbox', {exact: true, name: 'Plural Label Mandatory'})
	).toHaveValue(`Custom Objects${nameSuffix}`);
}
