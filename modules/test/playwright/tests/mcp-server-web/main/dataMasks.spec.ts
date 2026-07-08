/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {mcpServerWebPagesTest} from '../../../fixtures/mcpServerWebPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-63311': {enabled: true},
		'LPD-90204': {enabled: true},
	}),
	loginTest(),
	mcpServerWebPagesTest
);

const DATA_MASKS_API = 'mcp/server-data-masks';

const SYSTEM_MASK = 'Email Address';

const TOKEN = 'pwmask';

interface DataMaskEntry {
	id: number;
	maskType?: {key?: string};
	name?: string;
}

function maskName() {
	return `${TOKEN}-${getRandomString()}`;
}

async function createCustomMask(
	apiHelpers: DataApiHelpers,
	name: string
): Promise<DataMaskEntry> {
	return apiHelpers.post(`${apiHelpers.baseUrl}${DATA_MASKS_API}`, {
		data: {
			description: 'Created by Playwright',
			detectionRegex: '\\bzz\\b',
			maskType: {key: 'custom'},
			name,
			replacementValue: '[ZZ]',
		},
	});
}

async function associateMaskWithProfile(
	apiHelpers: DataApiHelpers,
	maskId: number
) {
	const profiles = await apiHelpers.get(
		`${apiHelpers.baseUrl}mcp/server-profiles?pageSize=1`
	);

	return apiHelpers.post(
		`${apiHelpers.baseUrl}mcp/server-profile-data-masks`,
		{
			data: {
				mcpServerProfileId: profiles.items[0].id,
				r_dataMaskToProfileDataMasks_mcpServerDataMaskId: maskId,
			},
		}
	);
}

test.afterEach(async ({apiHelpers}) => {
	const response = await apiHelpers.get(
		`${apiHelpers.baseUrl}${DATA_MASKS_API}?pageSize=200`
	);

	const items: DataMaskEntry[] = response?.items ?? [];

	for (const item of items) {
		const name = item.name ?? '';

		if (
			item.maskType?.key === 'custom' &&
			(name.includes(TOKEN) || name.startsWith('Copy of '))
		) {
			await apiHelpers.delete(
				`${apiHelpers.baseUrl}${DATA_MASKS_API}/${item.id}`
			);
		}
	}
});

test.describe('Data Masks - List View', () => {
	test(
		'shows the data masks list with Title, Type, Description, and Last Modified columns',
		{tag: '@LPD-90205'},
		async ({dataMasksPage, page}) => {
			await dataMasksPage.goto();

			for (const column of [
				'Title',
				'Type',
				'Description',
				'Last Modified',
			]) {
				await expect(
					page.getByRole('columnheader', {name: column})
				).toBeVisible();
			}

			await expect(dataMasksPage.row(SYSTEM_MASK)).toBeVisible();
		}
	);

	test(
		'opens a system mask read-only and a custom mask in edit when clicking its title',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();

			await dataMasksPage.titleLink(SYSTEM_MASK).click();
			await expect(dataMasksPage.formHeading).toHaveText(
				'View Data Mask'
			);
			await expect(dataMasksPage.nameInput).toBeDisabled();

			await dataMasksPage.goto();

			await dataMasksPage.titleLink(name).click();
			await expect(dataMasksPage.formHeading).toHaveText(
				'Edit Data Mask'
			);
			await expect(dataMasksPage.nameInput).toBeEnabled();
		}
	);

	test(
		'views a system mask read-only from the three-dot menu',
		{tag: '@LPD-90205'},
		async ({dataMasksPage}) => {
			await dataMasksPage.goto();

			await dataMasksPage.clickAction(SYSTEM_MASK, 'View');

			await expect(dataMasksPage.formHeading).toHaveText(
				'View Data Mask'
			);
			await expect(dataMasksPage.regexPatternInput).toBeDisabled();
		}
	);

	test(
		'duplicates a system mask into a custom Copy of mask from the three-dot menu',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			await dataMasksPage.goto();

			await dataMasksPage.clickAction(SYSTEM_MASK, 'Duplicate');

			await expect(
				dataMasksPage.row(`Copy of ${SYSTEM_MASK}`)
			).toBeVisible();

			const response = await apiHelpers.get(
				`${apiHelpers.baseUrl}${DATA_MASKS_API}?search=${encodeURIComponent(
					`Copy of ${SYSTEM_MASK}`
				)}&pageSize=5`
			);
			const copy = response.items.find(
				(item: DataMaskEntry) => item.name === `Copy of ${SYSTEM_MASK}`
			);

			expect(copy?.maskType?.key).toBe('custom');
		}
	);

	test(
		'searches the data masks list by name',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();

			await dataMasksPage.search(name);

			await expect(dataMasksPage.table.locator('tbody tr')).toHaveCount(
				1
			);
			await expect(dataMasksPage.row(name)).toBeVisible();
		}
	);

	test(
		'filters the data masks list by type',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();

			await dataMasksPage.filterByType('System');

			await expect(dataMasksPage.row(SYSTEM_MASK)).toBeVisible();
			await expect(dataMasksPage.row(name)).toBeHidden();
		}
	);

	test(
		'offers Title and Last Modified sort options',
		{tag: '@LPD-90205'},
		async ({dataMasksPage, page}) => {
			await dataMasksPage.goto();

			await dataMasksPage.orderButton.click();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Title'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Last Modified'})
			).toBeVisible();
		}
	);

	test(
		'edits a custom mask from the three-dot menu',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();
			await dataMasksPage.search(name);

			await dataMasksPage.clickAction(name, 'Edit');

			await expect(dataMasksPage.formHeading).toHaveText(
				'Edit Data Mask'
			);
			await expect(dataMasksPage.nameInput).toBeEnabled();
			await expect(dataMasksPage.nameInput).toHaveValue(name);
		}
	);

	test(
		'deletes a custom mask with no associations after confirming in the modal',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();
			await dataMasksPage.search(name);

			await dataMasksPage.clickAction(name, 'Delete');

			await expect(dataMasksPage.dialog).toBeVisible();
			await dataMasksPage.dialog
				.getByRole('button', {name: 'Delete'})
				.click();

			await expect(dataMasksPage.dialog).toBeHidden();
			await expect(dataMasksPage.row(name)).toBeHidden();
		}
	);

	test(
		'warns about profile associations when deleting a custom mask used by a profile',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			const mask = await createCustomMask(apiHelpers, name);

			await associateMaskWithProfile(apiHelpers, mask.id);

			await dataMasksPage.goto();
			await dataMasksPage.search(name);

			await dataMasksPage.clickAction(name, 'Delete');

			await expect(dataMasksPage.dialog).toBeVisible();
			await expect(dataMasksPage.dialog).toContainText(
				'This mask is currently being used in 1 profile(s).'
			);
		}
	);

	test(
		'duplicates a custom mask into a Copy of mask from the three-dot menu',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();
			await dataMasksPage.search(name);

			await dataMasksPage.clickAction(name, 'Duplicate');

			await expect(dataMasksPage.row(`Copy of ${name}`)).toBeVisible();
		}
	);

	test(
		'creates a custom mask from the New Data Mask button',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();

			await dataMasksPage.goto();
			await dataMasksPage.newDataMaskButton.click();

			await dataMasksPage.nameInput.fill(name);
			await dataMasksPage.regexPatternInput.fill('\\bzz\\b');
			await dataMasksPage.replacementInput.fill('[ZZ]');
			await dataMasksPage.saveButton.click();

			await expect(dataMasksPage.row(name)).toBeVisible();

			const response = await apiHelpers.get(
				`${apiHelpers.baseUrl}${DATA_MASKS_API}?search=${name}&pageSize=5`
			);
			expect(response.items[0]?.maskType?.key).toBe('custom');
		}
	);

	test(
		'cannot delete a system mask',
		{tag: '@LPD-90205'},
		async ({dataMasksPage, page}) => {
			await dataMasksPage.goto();

			await dataMasksPage
				.row(SYSTEM_MASK)
				.getByRole('button', {name: 'Actions'})
				.click();

			await expect(
				page.getByRole('menuitem', {name: 'Delete'})
			).toBeHidden();
		}
	);
});

test.describe('Data Masks - Detail (Create / Edit / View)', () => {
	test(
		'shows system masks as read-only in the form',
		{tag: '@LPD-90205'},
		async ({dataMasksPage}) => {
			await dataMasksPage.goto();
			await dataMasksPage.clickAction(SYSTEM_MASK, 'View');

			await expect(dataMasksPage.nameInput).toBeDisabled();
			await expect(dataMasksPage.descriptionInput).toBeDisabled();
			await expect(dataMasksPage.regexPatternInput).toBeDisabled();
			await expect(dataMasksPage.replacementInput).toBeDisabled();
			await expect(dataMasksPage.saveButton).toBeHidden();
		}
	);

	test(
		'tests a system mask and previews the masked output',
		{tag: '@LPD-90205'},
		async ({dataMasksPage}) => {
			await dataMasksPage.goto();
			await dataMasksPage.clickAction(SYSTEM_MASK, 'View');

			await dataMasksPage.sampleInput.fill(
				'reach me at john@acme.com please'
			);
			await dataMasksPage.testButton.click();

			await expect(dataMasksPage.outputInput).toHaveValue(
				'reach me at [EMAIL_ADDRESS] please'
			);
		}
	);

	test(
		'requires Title, Regex Pattern, and Replacement when creating a mask',
		{tag: '@LPD-90205'},
		async ({dataMasksPage}) => {
			await dataMasksPage.goto();
			await dataMasksPage.newDataMaskButton.click();

			await dataMasksPage.saveButton.click();

			await expect(dataMasksPage.formHeading).toHaveText('New Data Mask');
			await expect(dataMasksPage.nameInput).toHaveJSProperty(
				'validity.valueMissing',
				true
			);
		}
	);

	test(
		'shows a required-field error on Title, Regex Pattern, and Replacement',
		{tag: '@LPD-90205'},
		async ({dataMasksPage}) => {
			await dataMasksPage.goto();
			await dataMasksPage.newDataMaskButton.click();

			await dataMasksPage.saveButton.click();

			await expect(dataMasksPage.nameInput).toHaveAccessibleDescription(
				/This field is required\./
			);
			await expect(
				dataMasksPage.regexPatternInput
			).toHaveAccessibleDescription(/This field is required\./);
			await expect(
				dataMasksPage.replacementInput
			).toHaveAccessibleDescription(/This field is required\./);
		}
	);

	test(
		'clears the required error once the field is filled',
		{tag: '@LPD-90205'},
		async ({dataMasksPage}) => {
			await dataMasksPage.goto();
			await dataMasksPage.newDataMaskButton.click();

			await dataMasksPage.saveButton.click();

			await expect(dataMasksPage.nameInput).toHaveAccessibleDescription(
				/This field is required\./
			);

			await dataMasksPage.nameInput.fill(maskName());

			await expect(
				dataMasksPage.nameInput
			).not.toHaveAccessibleDescription(/This field is required\./);
		}
	);

	test(
		'links field help text to its input as an accessible description',
		{tag: '@LPD-90205'},
		async ({dataMasksPage}) => {
			await dataMasksPage.goto();
			await dataMasksPage.newDataMaskButton.click();

			await expect(
				dataMasksPage.regexPatternInput
			).toHaveAccessibleDescription(
				'Use a standard regular expression. Named capture groups are supported.'
			);
			await expect(
				dataMasksPage.matchPatternInput
			).toHaveAccessibleDescription(
				'Leave empty to replace the entire detected value with the replacement token.'
			);
		}
	);

	test(
		'edits a custom mask and persists the change',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();
			await dataMasksPage.search(name);
			await dataMasksPage.clickAction(name, 'Edit');

			await dataMasksPage.descriptionInput.fill('Edited by Playwright');
			await dataMasksPage.saveButton.click();

			await expect(dataMasksPage.row(name)).toBeVisible();

			const response = await apiHelpers.get(
				`${apiHelpers.baseUrl}${DATA_MASKS_API}?search=${name}&pageSize=5`
			);
			expect(response.items[0]?.description).toBe('Edited by Playwright');
		}
	);
});
