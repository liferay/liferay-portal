/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {mcpServerWebPagesTest} from '../../../fixtures/mcpServerWebPagesTest';
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

const PROMPTS_API = 'mcp/server-prompts';

function promptName() {
	return `pwprompt-${getRandomString()}`;
}

const test = baseTest.extend<{
	createFDSItem: () => Promise<string>;
	fdsTablePage: FDSTablePage;
}>({
	createFDSItem: async ({apiHelpers}, use) => {
		await use(async () => {
			const name = promptName();

			const prompt = await apiHelpers.objectEntry.postObjectEntry(
				{
					description: `Created by Playwright ${name}`,
					name,
					prompt: 'Prompt body created by Playwright',
				},
				PROMPTS_API
			);

			apiHelpers.data.push({
				applicationName: PROMPTS_API,
				id: prompt.id,
				type: 'objectEntry',
			});

			return name;
		});
	},
	fdsTablePage: async ({promptsPage}, use) => {
		await use(promptsPage);
	},
});

createFDSTableTests(test, {
	columns: ['Name', 'Description', 'Last Modified'],
	rowActions: ['Edit', 'Duplicate', 'Delete'],
	sortOptions: ['Name', 'Last Modified'],
	tag: '@LPD-98309',
});

test.describe('Prompts - List View', () => {
	test(
		'Opens a prompt in edit when clicking its name',
		{tag: '@LPD-98309'},
		async ({createFDSItem, promptsPage}) => {
			const name = await createFDSItem();

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.titleLink(name).click();

			await expect(promptsPage.formHeading).toHaveText('Edit Prompt');
			await expect(promptsPage.nameInput).toHaveValue(name);
		}
	);

	test(
		'Edits a prompt from the three-dot menu',
		{tag: '@LPD-98309'},
		async ({createFDSItem, promptsPage}) => {
			const name = await createFDSItem();

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Edit');

			await expect(promptsPage.formHeading).toHaveText('Edit Prompt');
			await expect(promptsPage.nameInput).toHaveValue(name);
		}
	);

	test(
		'Duplicates a prompt into a Copy of prompt from the three-dot menu',
		{tag: '@LPD-98309'},
		async ({apiHelpers, createFDSItem, promptsPage}) => {
			const name = await createFDSItem();

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Duplicate');

			await promptsPage.search(`Copy of ${name}`);

			await expect(promptsPage.row(`Copy of ${name}`)).toBeVisible();

			const copy = await apiHelpers.objectEntry.getObjectEntryByName(
				PROMPTS_API,
				`Copy of ${name}`
			);

			apiHelpers.data.push({
				applicationName: PROMPTS_API,
				id: copy.id,
				type: 'objectEntry',
			});
		}
	);

	test(
		'Asks for confirmation with the prompt name before deleting',
		{tag: '@LPD-98309'},
		async ({createFDSItem, promptsPage}) => {
			const name = await createFDSItem();

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Delete');

			await expect(promptsPage.dialog).toBeVisible();
			await expect(promptsPage.dialog).toContainText('Delete MCP Prompt');
			await expect(promptsPage.dialog).toContainText(
				`This will permanently delete "${name}" prompt from your MCP server configuration.`
			);
			await expect(promptsPage.dialog).toContainText(
				'Do you want to proceed?'
			);
		}
	);

	test(
		'Keeps the prompt when the delete confirmation is cancelled',
		{tag: '@LPD-98309'},
		async ({createFDSItem, promptsPage}) => {
			const name = await createFDSItem();

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Delete');

			await promptsPage.dialog
				.getByRole('button', {name: 'Cancel'})
				.click();

			await expect(promptsPage.dialog).toBeHidden();
			await expect(promptsPage.row(name)).toBeVisible();
		}
	);

	test(
		'Deletes a prompt after confirming in the modal',
		{tag: '@LPD-98309'},
		async ({createFDSItem, promptsPage}) => {
			const name = await createFDSItem();

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Delete');

			await expect(promptsPage.dialog).toBeVisible();
			await promptsPage.dialog
				.getByRole('button', {name: 'Delete'})
				.click();

			await expect(promptsPage.dialog).toBeHidden();
			await expect(promptsPage.row(name)).toBeHidden();
		}
	);
});

test.describe('Prompts - Detail (Create / Edit)', () => {
	test.beforeEach(async ({createFDSItem}) => {
		await createFDSItem();
	});

	test(
		'Creates a prompt from the New Prompt button',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();

			await promptsPage.goto();
			await promptsPage.newPromptButton.click();

			await expect(promptsPage.formHeading).toHaveText('New Prompt');

			await promptsPage.nameInput.fill(name);
			await promptsPage.descriptionInput.fill('Created from the UI');
			await promptsPage.promptInput.fill('Answer as a friendly robot.');
			await promptsPage.saveButton.click();

			await expect(promptsPage.row(name)).toBeVisible();

			const prompt = await apiHelpers.objectEntry.getObjectEntryByName(
				PROMPTS_API,
				name
			);

			apiHelpers.data.push({
				applicationName: PROMPTS_API,
				id: prompt.id,
				type: 'objectEntry',
			});

			expect(prompt.prompt).toBe('Answer as a friendly robot.');
		}
	);

	test(
		'Shows a required-field error on Name, Description, and Prompt',
		{tag: '@LPD-98309'},
		async ({promptsPage}) => {
			await promptsPage.goto();
			await promptsPage.newPromptButton.click();

			await promptsPage.saveButton.click();

			await expect(promptsPage.nameInput).toHaveAccessibleDescription(
				/This field is required\./
			);
			await expect(
				promptsPage.descriptionInput
			).toHaveAccessibleDescription(/This field is required\./);
			await expect(promptsPage.promptInput).toHaveAccessibleDescription(
				/This field is required\./
			);
		}
	);

	test(
		'Clears the required error once the field is filled',
		{tag: '@LPD-98309'},
		async ({promptsPage}) => {
			await promptsPage.goto();
			await promptsPage.newPromptButton.click();

			await promptsPage.saveButton.click();

			await expect(promptsPage.nameInput).toHaveAccessibleDescription(
				/This field is required\./
			);

			await promptsPage.nameInput.fill(promptName());

			await expect(promptsPage.nameInput).not.toHaveAccessibleDescription(
				/This field is required\./
			);
		}
	);

	test(
		'Edits a prompt and persists the change',
		{tag: '@LPD-98309'},
		async ({apiHelpers, createFDSItem, promptsPage}) => {
			const name = await createFDSItem();

			await promptsPage.goto();
			await promptsPage.search(name);
			await promptsPage.clickAction(name, 'Edit');

			await promptsPage.promptInput.fill('Edited by Playwright');
			await promptsPage.saveButton.click();

			await expect(promptsPage.row(name)).toBeVisible();

			const response = await apiHelpers.get(
				`${apiHelpers.baseUrl}${PROMPTS_API}?search=${name}&pageSize=5`
			);
			expect(response.items[0]?.prompt).toBe('Edited by Playwright');
		}
	);

	test(
		'Returns to the prompts list when cancelling the form',
		{tag: '@LPD-98309'},
		async ({promptsPage}) => {
			await promptsPage.goto();
			await promptsPage.newPromptButton.click();

			await expect(promptsPage.formHeading).toHaveText('New Prompt');

			await promptsPage.cancelButton.click();

			await promptsPage.waitForTable();
			await expect(promptsPage.newPromptButton).toBeVisible();
		}
	);
});
