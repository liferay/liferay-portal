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
import {FDSTablePage} from '../../../pages/mcp-server-web/FDSTablePage';
import getRandomString from '../../../utils/getRandomString';
import {createFDSTableTests} from './utils/createFDSTableTests';

const baseTest = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-63311': {enabled: true},
		'LPD-89575': {enabled: true},
	}),
	loginTest(),
	mcpServerWebPagesTest
);

const DATA_MASKS_API = 'data-masks';

const PROFILE_DATA_MASKS_API = 'mcp/server-profile-data-masks';

const SYSTEM_MASK = 'Email Address';

function maskName() {
	return `pwmask-${getRandomString()}`;
}

async function createCustomMask(
	apiHelpers: DataApiHelpers,
	name: string
): Promise<ObjectEntry> {
	const dataMask = await apiHelpers.objectEntry.postObjectEntry(
		{
			description: 'Created by Playwright',
			detectionRegex: '\\bzz\\b',
			maskType: {key: 'custom'},
			name,
			replacementValue: '[ZZ]',
		},
		DATA_MASKS_API
	);

	apiHelpers.data.push({
		applicationName: DATA_MASKS_API,
		id: dataMask.id,
		type: 'objectEntry',
	});

	return dataMask;
}

async function associateMaskWithProfile(
	apiHelpers: DataApiHelpers,
	dataMaskExternalReferenceCode: string
) {
	const profiles = await apiHelpers.get(
		`${apiHelpers.baseUrl}mcp/server-profiles?pageSize=1`
	);

	const association = await apiHelpers.objectEntry.postObjectEntry(
		{
			dataMaskExternalReferenceCode,
			mcpServerProfileExternalReferenceCode:
				profiles.items[0].externalReferenceCode,
		},
		PROFILE_DATA_MASKS_API
	);

	apiHelpers.data.push({
		applicationName: PROFILE_DATA_MASKS_API,
		id: association.id,
		type: 'objectEntry',
	});

	return association;
}

const test = baseTest.extend<{
	createFDSItem: () => Promise<string>;
	fdsTablePage: FDSTablePage;
}>({
	createFDSItem: async ({apiHelpers}, use) => {
		await use(async () => {
			const name = maskName();

			await createCustomMask(apiHelpers, name);

			return name;
		});
	},
	fdsTablePage: async ({dataMasksPage}, use) => {
		await use(dataMasksPage);
	},
});

createFDSTableTests(test, {
	columns: ['Title', 'Type', 'Description', 'Last Modified'],
	rowActions: ['Edit', 'Duplicate', 'Delete'],
	sortOptions: ['Title', 'Last Modified'],
	tag: '@LPD-90205',
});

test.describe('Data Masks - List View', () => {
	test(
		'Opens a system mask read-only and a custom mask in edit when clicking its title',
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
		'Views a system mask read-only from the three-dot menu',
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
		'Duplicates a system mask into a custom Copy of mask from the three-dot menu',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			await dataMasksPage.goto();

			await dataMasksPage.clickAction(SYSTEM_MASK, 'Duplicate');

			await expect(
				dataMasksPage.row(`Copy of ${SYSTEM_MASK}`)
			).toBeVisible();

			const copy = await apiHelpers.objectEntry.getObjectEntryByName(
				DATA_MASKS_API,
				`Copy of ${SYSTEM_MASK}`
			);

			apiHelpers.data.push({
				applicationName: DATA_MASKS_API,
				id: copy.id,
				type: 'objectEntry',
			});

			expect(copy.maskType?.key).toBe('custom');
		}
	);

	test(
		'Filters the data masks list by type',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();

			await dataMasksPage.applySelectionFilter('Type', 'System');

			await expect(dataMasksPage.row(SYSTEM_MASK)).toBeVisible();
			await expect(dataMasksPage.row(name)).toBeHidden();
		}
	);

	test(
		'Edits a custom mask from the three-dot menu',
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
		'Deletes a custom mask with no associations after confirming in the modal',
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
		'Warns about profile associations when deleting a custom mask used by a profile',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			const mask = await createCustomMask(apiHelpers, name);

			await associateMaskWithProfile(
				apiHelpers,
				mask.externalReferenceCode ?? ''
			);

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
		'Duplicates a custom mask into a Copy of mask from the three-dot menu',
		{tag: '@LPD-90205'},
		async ({apiHelpers, dataMasksPage}) => {
			const name = maskName();
			await createCustomMask(apiHelpers, name);

			await dataMasksPage.goto();
			await dataMasksPage.search(name);

			await dataMasksPage.clickAction(name, 'Duplicate');

			await expect(dataMasksPage.row(`Copy of ${name}`)).toBeVisible();

			const copy = await apiHelpers.objectEntry.getObjectEntryByName(
				DATA_MASKS_API,
				`Copy of ${name}`
			);

			apiHelpers.data.push({
				applicationName: DATA_MASKS_API,
				id: copy.id,
				type: 'objectEntry',
			});
		}
	);

	test(
		'Creates a custom mask from the New Data Mask button',
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

			const dataMask = await apiHelpers.objectEntry.getObjectEntryByName(
				DATA_MASKS_API,
				name
			);

			apiHelpers.data.push({
				applicationName: DATA_MASKS_API,
				id: dataMask.id,
				type: 'objectEntry',
			});

			expect(dataMask.maskType?.key).toBe('custom');
		}
	);

	test(
		'Cannot delete a system mask',
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
		'Shows system masks as read-only in the form',
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
		'Tests a system mask and previews the masked output',
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
		'Requires Title, Regex Pattern, and Replacement when creating a mask',
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
		'Shows a required-field error on Title, Regex Pattern, and Replacement',
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
		'Clears the required error once the field is filled',
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
		'Links field help text to its input as an accessible description',
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
		'Edits a custom mask and persists the change',
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
