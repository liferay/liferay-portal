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
import {
	expectFDSTableColumns,
	expectFDSTableRowActions,
	expectFDSTableSearchEmptyResult,
	expectFDSTableSearchFindsItem,
	expectFDSTableSortOptions,
} from './utils/fdsTable';

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

const PROFILES_API = 'mcp/server-profiles';

function profileName() {
	return `pwprofile-${getRandomString()}`;
}

async function createProfile(
	apiHelpers: DataApiHelpers,
	name: string
): Promise<ObjectEntry> {
	const profile = await apiHelpers.objectEntry.postObjectEntry(
		{
			description: `Created by Playwright ${name}`,
			name,
			tools: 'mcp-server-v1.0 getToolSetsPage',
		},
		PROFILES_API
	);

	apiHelpers.data.push({
		applicationName: PROFILES_API,
		id: profile.id,
		type: 'objectEntry',
	});

	return profile;
}

const test = baseTest.extend<{
	createFDSItem: () => Promise<string>;
	fdsTablePage: FDSTablePage;
}>({
	createFDSItem: async ({apiHelpers}, use) => {
		await use(async () => {
			const name = profileName();

			await createProfile(apiHelpers, name);

			return name;
		});
	},
	fdsTablePage: async ({profilesPage}, use) => {
		await use(profilesPage);
	},
});

async function createDataMask(
	apiHelpers: DataApiHelpers,
	name: string
): Promise<ObjectEntry> {
	const dataMask = await apiHelpers.objectEntry.postObjectEntry(
		{
			description: `Created by Playwright ${name}`,
			detectionRegex: 'a+',
			maskType: {key: 'custom'},
			name,
			replacementValue: '***',
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

test.describe('Profiles - FDS Table', () => {
	let seededItemName: string;

	test.beforeEach(async ({createFDSItem}) => {
		seededItemName = await createFDSItem();
	});

	test(
		'Shows the table columns',
		{tag: '@LPD-99230'},
		async ({fdsTablePage}) => {
			await expectFDSTableColumns(fdsTablePage, [
				'Title',
				'Path',
				'Description',
				'Last Modified',
			]);
		}
	);

	test(
		'Offers the sort options',
		{tag: '@LPD-99230'},
		async ({fdsTablePage}) => {
			await expectFDSTableSortOptions(fdsTablePage, [
				'Title',
				'Last Modified',
			]);
		}
	);

	test(
		'Searches the table by item name',
		{tag: '@LPD-99230'},
		async ({createFDSItem, fdsTablePage}) => {
			await expectFDSTableSearchFindsItem(
				createFDSItem,
				fdsTablePage,
				seededItemName
			);
		}
	);

	test(
		'Shows an empty result when searching for a missing item',
		{tag: '@LPD-99230'},
		async ({fdsTablePage}) => {
			await expectFDSTableSearchEmptyResult(fdsTablePage);
		}
	);

	test(
		'Offers the row actions',
		{tag: '@LPD-99230'},
		async ({fdsTablePage, page}) => {
			await expectFDSTableRowActions(fdsTablePage, page, seededItemName, [
				'Edit',
				'Delete',
			]);
		}
	);
});

test.describe('Profiles - List View', () => {
	test(
		'Opens a profile in edit when clicking its title',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);

			await profilesPage.titleLink(name).click();

			await expect(profilesPage.formHeading).toHaveText('Edit Profile');
			await expect(profilesPage.nameInput).toHaveValue(name);
		}
	);

	test(
		'Edits a profile from the three-dot menu',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);

			await profilesPage.clickAction(name, 'Edit');

			await expect(profilesPage.formHeading).toHaveText('Edit Profile');
			await expect(profilesPage.nameInput).toHaveValue(name);
		}
	);

	test(
		'Shows the path derived from the profile title',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			const profile = await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);

			await expect(profilesPage.row(name)).toContainText(
				profile.friendlyUrlPath ?? name
			);
		}
	);

	test(
		'Asks for confirmation with the profile name before deleting',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);

			await profilesPage.clickAction(name, 'Delete');

			await expect(profilesPage.dialog).toBeVisible();
			await expect(profilesPage.dialog).toContainText(
				'Delete MCP Profile'
			);
			await expect(profilesPage.dialog).toContainText(
				`This will permanently delete "${name}" profile from your MCP server configuration.`
			);
			await expect(profilesPage.dialog).toContainText(
				'Do you want to proceed?'
			);
		}
	);

	test(
		'Keeps the profile when the delete confirmation is cancelled',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);

			await profilesPage.clickAction(name, 'Delete');

			await profilesPage.dialog
				.getByRole('button', {name: 'Cancel'})
				.click();

			await expect(profilesPage.dialog).toBeHidden();
			await expect(profilesPage.row(name)).toBeVisible();
		}
	);

	test(
		'Deletes a profile after confirming in the modal',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);

			await profilesPage.clickAction(name, 'Delete');

			await expect(profilesPage.dialog).toBeVisible();
			await profilesPage.dialog
				.getByRole('button', {name: 'Delete'})
				.click();

			await expect(profilesPage.dialog).toBeHidden();
			await expect(profilesPage.row(name)).toBeHidden();
		}
	);
});

test.describe('Profiles - Detail (Create / Edit)', () => {
	test.skip(
		'Creates a profile from the New Profile button',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();

			await profilesPage.goto();
			await profilesPage.newProfileButton.click();

			await expect(profilesPage.formHeading).toHaveText('New Profile');

			await profilesPage.nameInput.fill(name);
			await profilesPage.descriptionInput.fill('Created from the UI');
			await profilesPage.toolsInput.fill(
				'mcp-server-v1.0 getToolSetsPage'
			);
			await profilesPage.saveButton.click();

			// The form stays in place after the first save

			await expect(profilesPage.page).toHaveURL(/profileId=/);
			await expect(profilesPage.formHeading).toHaveText('Edit Profile');
			await expect(profilesPage.dataMasksTabLink).toBeVisible();

			const response = await apiHelpers.get(
				`${apiHelpers.baseUrl}${PROFILES_API}?search=${name}&pageSize=5`
			);
			expect(response.items[0]?.tools).toBe(
				'mcp-server-v1.0 getToolSetsPage'
			);

			await profilesPage.goto();
			await profilesPage.search(name);

			await expect(profilesPage.row(name)).toBeVisible();
		}
	);

	test(
		'Shows a required-field error on Title, Description, and Tools',
		{tag: '@LPD-99230'},
		async ({profilesPage}) => {
			await profilesPage.goto();
			await profilesPage.newProfileButton.click();

			await profilesPage.saveButton.click();

			await expect(profilesPage.nameInput).toHaveAccessibleDescription(
				/This field is required\./
			);
			await expect(
				profilesPage.descriptionInput
			).toHaveAccessibleDescription(/This field is required\./);
			await expect(profilesPage.toolsInput).toHaveAccessibleDescription(
				/This field is required\./
			);
		}
	);

	test(
		'Clears the required error once the field is filled',
		{tag: '@LPD-99230'},
		async ({profilesPage}) => {
			await profilesPage.goto();
			await profilesPage.newProfileButton.click();

			await profilesPage.saveButton.click();

			await expect(profilesPage.nameInput).toHaveAccessibleDescription(
				/This field is required\./
			);

			await profilesPage.nameInput.fill(profileName());

			await expect(
				profilesPage.nameInput
			).not.toHaveAccessibleDescription(/This field is required\./);
		}
	);

	test.skip(
		'Edits a profile and persists the change',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');

			await profilesPage.toolsInput.fill('mcp-server-v1.0 editedTool');
			await profilesPage.saveButton.click();

			await expect(profilesPage.row(name)).toBeVisible();

			const response = await apiHelpers.get(
				`${apiHelpers.baseUrl}${PROFILES_API}?search=${name}&pageSize=5`
			);
			expect(response.items[0]?.tools).toBe('mcp-server-v1.0 editedTool');
		}
	);

	test(
		'Returns to the profiles list when cancelling the form',
		{tag: '@LPD-99230'},
		async ({profilesPage}) => {
			await profilesPage.goto();
			await profilesPage.newProfileButton.click();

			await expect(profilesPage.formHeading).toHaveText('New Profile');

			await profilesPage.cancelButton.click();

			await profilesPage.table.waitFor({state: 'visible'});
			await expect(profilesPage.newProfileButton).toBeVisible();
		}
	);
});

test.describe('Profiles - Data Masks tab', () => {
	test(
		'Disables the Data Masks tab until the profile is saved',
		{tag: '@LPD-99230'},
		async ({profilesPage}) => {
			await profilesPage.goto();
			await profilesPage.newProfileButton.click();

			await expect(profilesPage.nameInput).toBeVisible();
			await expect(profilesPage.profileInfoTab).toBeVisible();
			await expect(profilesPage.dataMasksTabButton).toBeDisabled();

			await profilesPage.nameInput.fill(profileName());
			await profilesPage.descriptionInput.fill('Created from the UI');
			await profilesPage.toolsInput.fill(
				'mcp-server-v1.0 getToolSetsPage'
			);
			await profilesPage.saveButton.click();

			await expect(profilesPage.dataMasksTabLink).toBeVisible();
		}
	);

	test(
		'Enables the Data Masks tab when editing an existing profile',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');

			await expect(profilesPage.dataMasksTabLink).toBeVisible();

			await profilesPage.dataMasksTabLink.click();

			// The backend seeds every new profile with the system masks

			await expect(profilesPage.masksRows.first()).toBeVisible();
		}
	);

	test(
		'Adds a mask to the profile from the Add Masks modal',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const maskName = profileName();
			const name = profileName();
			await createDataMask(apiHelpers, maskName);
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');
			await profilesPage.dataMasksTabLink.click();
			await profilesPage.masksRows.first().waitFor({state: 'visible'});

			await profilesPage.addMasksButton.click();

			await profilesPage.maskCheckbox(maskName).check();
			await profilesPage.addMasksSubmitButton.click();

			// The new association takes the last execution order

			await expect(profilesPage.maskRow(maskName)).toBeVisible();
			await expect(profilesPage.masksRows.last()).toContainText(maskName);
		}
	);

	test(
		'Keeps the table unchanged when cancelling the Add Masks modal',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const maskName = profileName();
			const name = profileName();
			await createDataMask(apiHelpers, maskName);
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');
			await profilesPage.dataMasksTabLink.click();
			await profilesPage.masksRows.first().waitFor({state: 'visible'});

			const rowsBefore = await profilesPage.masksRows.count();

			await profilesPage.addMasksButton.click();

			await profilesPage.maskCheckbox(maskName).check();
			await profilesPage.dialog
				.getByRole('button', {name: 'Cancel'})
				.click();

			await expect(profilesPage.dialog).toBeHidden();
			await expect(profilesPage.maskRow(maskName)).toBeHidden();
			await expect(profilesPage.masksRows).toHaveCount(rowsBefore);
		}
	);

	test(
		'Deselects every mask with the Deselect All action',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const firstMaskName = profileName();
			const name = profileName();
			const secondMaskName = profileName();
			await createDataMask(apiHelpers, firstMaskName);
			await createDataMask(apiHelpers, secondMaskName);
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');
			await profilesPage.dataMasksTabLink.click();
			await profilesPage.masksRows.first().waitFor({state: 'visible'});

			await profilesPage.addMasksButton.click();

			// Checking the Custom group cascades to every custom mask

			await profilesPage.maskCheckbox('Custom').check();

			await expect(
				profilesPage.maskCheckbox(firstMaskName)
			).toBeChecked();
			await expect(
				profilesPage.maskCheckbox(secondMaskName)
			).toBeChecked();
			await expect(
				profilesPage.dialog.getByText(/Items? Selected/)
			).toBeVisible();

			await profilesPage.deselectAllButton.click();

			await expect(
				profilesPage.dialog.getByText(/Items? Selected/)
			).toBeHidden();
			await expect(
				profilesPage.maskCheckbox(firstMaskName)
			).not.toBeChecked();
			await expect(profilesPage.addMasksSubmitButton).toBeDisabled();
		}
	);

	test(
		'Requires a delete reason to remove a mask from the profile',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');
			await profilesPage.dataMasksTabLink.click();
			await profilesPage.masksRows.first().waitFor({state: 'visible'});

			const rowsBefore = await profilesPage.masksRows.count();
			const firstRowName = await profilesPage.masksRows
				.first()
				.locator('td')
				.nth(1)
				.innerText();

			await profilesPage.masksRows
				.first()
				.locator('td')
				.last()
				.getByRole('button')
				.click();
			await profilesPage.page
				.getByRole('menuitem', {name: 'Remove'})
				.click();

			const removeButton = profilesPage.dialog.getByRole('button', {
				name: 'Remove',
			});

			await expect(removeButton).toBeDisabled();

			await profilesPage.removeReasonInput.fill('Removed by Playwright');
			await removeButton.click();

			await expect(profilesPage.dialog).toBeHidden();
			await expect(profilesPage.masksRows).toHaveCount(rowsBefore - 1);
			await expect(
				profilesPage.maskRow(firstRowName.trim())
			).toBeHidden();
		}
	);

	test(
		'Keeps the mask when the remove confirmation is cancelled',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			await createProfile(apiHelpers, name);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');
			await profilesPage.dataMasksTabLink.click();
			await profilesPage.masksRows.first().waitFor({state: 'visible'});

			const rowsBefore = await profilesPage.masksRows.count();

			await profilesPage.masksRows
				.first()
				.locator('td')
				.last()
				.getByRole('button')
				.click();
			await profilesPage.page
				.getByRole('menuitem', {name: 'Remove'})
				.click();

			await profilesPage.dialog
				.getByRole('button', {name: 'Cancel'})
				.click();

			await expect(profilesPage.dialog).toBeHidden();
			await expect(profilesPage.masksRows).toHaveCount(rowsBefore);
		}
	);

	test(
		'Persists the mask order after a keyboard reorder',
		{tag: '@LPD-99230'},
		async ({apiHelpers, profilesPage}) => {
			const name = profileName();
			const profile = await createProfile(apiHelpers, name);

			const getMaskExternalReferenceCodes = async () => {
				const response = await apiHelpers.get(
					`${apiHelpers.baseUrl}${PROFILE_DATA_MASKS_API}?filter=mcpServerProfileExternalReferenceCode eq '${profile.externalReferenceCode}'&pageSize=200&sort=executionOrder:asc`
				);

				return (response?.items ?? []).map(
					(item: {dataMaskExternalReferenceCode?: string}) =>
						item.dataMaskExternalReferenceCode
				);
			};

			const maskExternalReferenceCodes =
				await getMaskExternalReferenceCodes();

			expect(maskExternalReferenceCodes.length).toBeGreaterThan(1);

			await profilesPage.goto();
			await profilesPage.search(name);
			await profilesPage.clickAction(name, 'Edit');
			await profilesPage.dataMasksTabLink.click();
			await profilesPage.masksRows.first().waitFor({state: 'visible'});

			// Pick up the first row with the keyboard handle and move it down

			const handle = profilesPage.masksRows
				.first()
				.locator('td')
				.first()
				.getByRole('button');

			await handle.focus();
			await profilesPage.page.keyboard.press('Enter');
			await profilesPage.page.keyboard.press('ArrowDown');
			await profilesPage.page.keyboard.press('Enter');

			await expect(async () => {
				expect(await getMaskExternalReferenceCodes()).toEqual([
					maskExternalReferenceCodes[1],
					maskExternalReferenceCodes[0],
					...maskExternalReferenceCodes.slice(2),
				]);
			}).toPass({timeout: 15000});
		}
	);
});
