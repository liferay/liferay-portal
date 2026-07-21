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

const PROMPTS_API = 'mcp/server-prompts';

const TOKEN = 'pwprompt';

interface PromptEntry {
	description?: string;
	id: number;
	name?: string;
	prompt?: string;
}

function promptName() {
	return `${TOKEN}-${getRandomString()}`;
}

async function createPrompt(
	apiHelpers: DataApiHelpers,
	name: string
): Promise<PromptEntry> {
	return apiHelpers.post(`${apiHelpers.baseUrl}${PROMPTS_API}`, {
		data: {
			description: `Created by Playwright ${name}`,
			name,
			prompt: 'Prompt body created by Playwright',
		},
	});
}

const test = baseTest.extend<{
	createFDSItem: () => Promise<string>;
	fdsTablePage: FDSTablePage;
}>({
	createFDSItem: async ({apiHelpers}, use) => {
		await use(async () => {
			const name = promptName();

			await createPrompt(apiHelpers, name);

			return name;
		});
	},
	fdsTablePage: async ({promptsPage}, use) => {
		await use(promptsPage);
	},
});

test.afterEach(async ({apiHelpers}) => {
	const response = await apiHelpers.get(
		`${apiHelpers.baseUrl}${PROMPTS_API}?pageSize=200`
	);

	const items: PromptEntry[] = response?.items ?? [];

	for (const item of items) {
		const name = item.name ?? '';

		if (name.includes(TOKEN) || name.startsWith('Copy of ')) {
			await apiHelpers.delete(
				`${apiHelpers.baseUrl}${PROMPTS_API}/${item.id}`
			);
		}
	}
});

createFDSTableTests(test, {
	columns: ['Name', 'Description', 'Last Modified'],
	rowActions: ['Edit', 'Duplicate', 'Delete'],
	sortOptions: ['Name', 'Last Modified'],
	tag: '@LPD-98309',
});

test.describe('Prompts - List View', () => {
	test(
		'opens a prompt in edit when clicking its name',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();
			await createPrompt(apiHelpers, name);

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.titleLink(name).click();

			await expect(promptsPage.formHeading).toHaveText('Edit Prompt');
			await expect(promptsPage.nameInput).toHaveValue(name);
		}
	);

	test(
		'edits a prompt from the three-dot menu',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();
			await createPrompt(apiHelpers, name);

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Edit');

			await expect(promptsPage.formHeading).toHaveText('Edit Prompt');
			await expect(promptsPage.nameInput).toHaveValue(name);
		}
	);

	test(
		'duplicates a prompt into a Copy of prompt from the three-dot menu',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();
			await createPrompt(apiHelpers, name);

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Duplicate');

			await promptsPage.search(`Copy of ${name}`);

			await expect(promptsPage.row(`Copy of ${name}`)).toBeVisible();
		}
	);

	test(
		'asks for confirmation with the prompt name before deleting',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();
			await createPrompt(apiHelpers, name);

			await promptsPage.goto();
			await promptsPage.search(name);

			await promptsPage.clickAction(name, 'Delete');

			await expect(promptsPage.dialog).toBeVisible();
			await expect(promptsPage.dialog).toContainText('Delete MCP Prompt');
			await expect(promptsPage.dialog).toContainText(
				`This will permanently delete "${name}" prompt from your MCP Server configuration.`
			);
			await expect(promptsPage.dialog).toContainText(
				'Do you want to proceed?'
			);
		}
	);

	test(
		'keeps the prompt when the delete confirmation is cancelled',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();
			await createPrompt(apiHelpers, name);

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
		'deletes a prompt after confirming in the modal',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();
			await createPrompt(apiHelpers, name);

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
	test(
		'creates a prompt from the New Prompt button',
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

			const response = await apiHelpers.get(
				`${apiHelpers.baseUrl}${PROMPTS_API}?search=${name}&pageSize=5`
			);
			expect(response.items[0]?.prompt).toBe(
				'Answer as a friendly robot.'
			);
		}
	);

	test(
		'shows a required-field error on Name, Description, and Prompt',
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
		'clears the required error once the field is filled',
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
		'edits a prompt and persists the change',
		{tag: '@LPD-98309'},
		async ({apiHelpers, promptsPage}) => {
			const name = promptName();
			await createPrompt(apiHelpers, name);

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
		'returns to the prompts list when cancelling the form',
		{tag: '@LPD-98309'},
		async ({promptsPage}) => {
			await promptsPage.goto();
			await promptsPage.newPromptButton.click();

			await expect(promptsPage.formHeading).toHaveText('New Prompt');

			await promptsPage.cancelButton.click();

			await promptsPage.table.waitFor({state: 'visible'});
			await expect(promptsPage.newPromptButton).toBeVisible();
		}
	);
});
