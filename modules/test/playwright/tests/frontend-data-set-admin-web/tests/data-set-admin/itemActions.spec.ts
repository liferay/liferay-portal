/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import checkHelperTooltip from '../../utils/checkHelperTooltip';
import checkLocalized from '../../utils/checkLocalized';
import checkRequired from '../../utils/checkRequired';
import clickRowAction from '../../utils/clickRowAction';
import getRowByText from '../../utils/getRowByText';
import getSelectOptionLabels from '../../utils/getSelectOptionLabels';
import {
	EAsyncActionMethod,
	EConfirmationMessageType,
	EItemActionType,
	EModalActionVariant,
} from '../../utils/types';
import {actionsPageTest} from './fixtures/actionsPageTest';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';

export const test = mergeTests(
	actionsPageTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': true,
		'LPS-178052': true,
	}),
	loginTest(),
	dataSetManagerSetupTest
);

let dataSetERC: string;
let dataSetLabel: string;

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await test.step('Create data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

async function assertTableCellContent({actionData, page, rowIndex = 0}) {
	await test.step('Assert table cell content', async () => {
		await page
			.locator('.orderable-table > tbody > .orderable-table-row')
			.first()
			.waitFor();

		const tableRowContent = await page
			.locator('.orderable-table-row')
			.nth(rowIndex)
			.locator('td');

		const expectedRowContent = [
			actionData.icon,
			actionData.label,
			actionData.type,
		];

		await expect(tableRowContent).toContainText(expectedRowContent);
	});
}

test(
	'Check interactive options in item action form',
	{tag: '@LPD-11300'},
	async ({actionsPage, page}) => {
		const form = actionsPage.actionForm;

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Assert message exists informing that there are no actions created', async () => {
			await expect(actionsPage.noActionsWereCreatedMessage).toContainText(
				'No actions were created.'
			);
		});

		await test.step('Open new item actions form', async () => {
			await actionsPage.newItemActionPlusButton.click();

			await expect(
				actionsPage.page.getByText('Display Options')
			).toBeInViewport();
		});

		await test.step('Check options of selections', async () => {
			expect(await getSelectOptionLabels(form.typeSelect)).toEqual([
				'Async',
				'Headless',
				'Link',
				'Modal',
				'Side Panel',
			]);

			expect(
				await getSelectOptionLabels(form.confirmationMessageTypeSelect)
			).toEqual(['Info', 'Secondary', 'Success', 'Danger', 'Warning']);

			await form.typeSelect.selectOption('Async');

			expect(await getSelectOptionLabels(form.methodSelect)).toEqual([
				'DELETE',
				'GET',
				'PATCH',
				'POST',
				'PUT',
			]);

			await form.typeSelect.selectOption('Modal');

			expect(await getSelectOptionLabels(form.variantSelect)).toEqual([
				'Full Screen',
				'Large',
				'Small',
			]);
		});

		await test.step('Check localizable inputs', async () => {
			await checkLocalized({
				formElements: [form.labelInput, form.confirmationMessageInput],
				page,
			});

			await form.typeSelect.selectOption('Async');

			await actionsPage.selectTab({
				container: actionsPage.statusMessagesTabs,
				label: 'Success',
			});

			await expect(form.successStatusMessageInput).toBeVisible();

			await checkLocalized({
				formElements: [form.successStatusMessageInput],
				page,
			});

			await actionsPage.selectTab({
				container: actionsPage.statusMessagesTabs,
				label: 'Error',
			});

			await expect(form.errorStatusMessageInput).toBeVisible();

			await checkLocalized({
				formElements: [form.errorStatusMessageInput],
				page,
			});

			await form.typeSelect.selectOption('Modal');

			await checkLocalized({
				formElements: [form.titleInput],
				page,
			});
		});

		await test.step('Check helper tooltips', async () => {
			await checkHelperTooltip({
				formElement: form.headlessActionKeyInput,
				page,
				text: "This key is used to display the action if the user has the permission for it by checking if the key is present in the data item's actions array.",
			});

			await checkHelperTooltip({
				formElement: form.confirmationMessageInput,
				page,
				text: 'The user will see this message before performing the action. No message will be displayed if the message is empty.',
			});

			await form.typeSelect.selectOption('Async');

			await expect(form.requestBodyInput).toBeVisible();

			await checkHelperTooltip({
				formElement: form.requestBodyInput,
				page,
				text: 'This field must be a valid JSON that matches the schema of the endpoint used in this action. Use it to send data to the server.',
			});

			await actionsPage.selectTab({
				container: actionsPage.statusMessagesTabs,
				label: 'Success',
			});

			await expect(form.successStatusMessageInput).toBeVisible();

			await checkHelperTooltip({
				formElement: form.successStatusMessageInput,
				page,
				text: 'The user will see this message if the action is successful.',
			});

			await actionsPage.selectTab({
				container: actionsPage.statusMessagesTabs,
				label: 'Error',
			});

			await expect(form.errorStatusMessageInput).toBeVisible();

			await checkHelperTooltip({
				formElement: form.errorStatusMessageInput,
				page,
				text: 'The user will see this message if the action fails.',
			});
		});

		await test.step('Filter icon selection ', async () => {
			await form.addIconButton.click();

			const icon: string = 'chip';

			await form.selectIconModal.getByPlaceholder('Search').fill(icon);

			const iconsList = form.selectIconModal.getByRole('list');

			const iconsListItem = iconsList.getByText(icon, {exact: true});

			await expect(iconsListItem).toBeVisible();

			await expect(iconsList.getByRole('listitem')).toHaveCount(1);

			await form.selectIconModal
				.getByRole('button', {name: 'Close'})
				.click();

			expect(form.iconInput).toHaveValue('');
		});

		await test.step('Validate entire form on save', async () => {
			await form.typeSelect.selectOption('Link');

			await form.saveButton.click();

			await checkRequired({
				formElements: [form.labelInput, form.urlInput],
				page,
			});

			await form.typeSelect.selectOption('Headless');

			await form.saveButton.click();

			await checkRequired({
				formElements: [form.labelInput, form.headlessActionKeyInput],
				page,
			});
		});

		await test.step('Validate required form elements individually', async () => {
			await form.typeSelect.selectOption('Link');

			await form.labelInput.fill(getRandomString());
			await form.labelInput.clear();

			await form.urlInput.fill(getRandomString());
			await form.urlInput.clear();

			await checkRequired({
				formElements: [form.labelInput, form.urlInput],
				page,
			});

			await form.typeSelect.selectOption('Headless');

			await form.headlessActionKeyInput.fill(getRandomString());
			await form.headlessActionKeyInput.clear();

			await checkRequired({
				formElements: [form.headlessActionKeyInput],
				page,
			});
		});

		await test.step('Validate valid JSON in request body', async () => {
			const requestBodyInput = form.requestBodyInput;

			await form.typeSelect.selectOption('Headless');

			await requestBodyInput.fill(getRandomString());

			const parent = page
				.locator('.form-group.has-error')
				.filter({has: requestBodyInput});

			expect(parent).toBeVisible();

			expect(
				parent.getByText('This field must contain a valid JSON.')
			).toBeVisible();

			await requestBodyInput.fill('{}');

			expect(
				parent.getByText('This field must contain a valid JSON.')
			).not.toBeVisible();

			await requestBodyInput.clear();
		});
	}
);

test(
	'Create and edit item action of type "Async"',
	{tag: '@LPD-11300'},
	async ({actionsPage, page}) => {
		let confirmationMessage: string = getRandomString();
		let confirmationMessageType: EConfirmationMessageType =
			EConfirmationMessageType.INFO;
		let errorStatusMessage: string = getRandomString();
		let headlessActionKey: string = getRandomString();
		let icon: string = 'catalog';
		let label: string = getRandomString();
		let method: EAsyncActionMethod = EAsyncActionMethod.GET;
		const requestBody: string = '{"Async": "async"}';
		let successStatusMessage: string = getRandomString();
		const type: EItemActionType = EItemActionType.ASYNC;
		let url: string = getRandomString();

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Create an item action of type "Async"', async () => {
			await actionsPage.createItemAction({
				confirmationMessage,
				confirmationMessageType,
				errorStatusMessage,
				headlessActionKey,
				icon,
				label,
				method,
				requestBody,
				successStatusMessage,
				type,
				url,
			});
		});

		let actionRow: Locator;

		await test.step('Check that the item action is in the list', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await expect(actionRow.getByRole('cell')).toContainText([
				icon,
				label,
				type,
			]);
		});

		await test.step('Open edit page of the saved item', async () => {
			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert saved values match values on creation', async () => {
			const form = actionsPage.actionForm;

			await expect(form.confirmationMessageInput).toHaveValue(
				confirmationMessage
			);
			await expect(form.confirmationMessageTypeSelect).toHaveValue(
				confirmationMessageType
			);
			await expect(form.errorStatusMessageInput).toHaveValue(
				errorStatusMessage
			);
			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.labelInput).toHaveValue(label);
			await expect(form.methodSelect).toHaveValue(method);
			await expect(form.requestBodyInput).toHaveValue(requestBody);
			await expect(form.successStatusMessageInput).toHaveValue(
				successStatusMessage
			);
			await expect(form.typeSelect).toHaveValue(type);
			await expect(form.urlInput).toHaveValue(url);
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			confirmationMessage = '';
			confirmationMessageType = EConfirmationMessageType.WARNING;
			errorStatusMessage = '';
			headlessActionKey = '';
			icon = '';
			label = getRandomString();
			method = EAsyncActionMethod.DELETE;
			successStatusMessage = '';
			url = getRandomString();

			await actionsPage.fillItemActionFormValues({
				confirmationMessage,
				confirmationMessageType,
				errorStatusMessage,
				headlessActionKey,
				icon,
				label,
				method,
				successStatusMessage,
				type,
				url,
			});

			await actionsPage.actionForm.requestBodyInput.clear();

			await actionsPage.actionForm.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Open edit page of the saved item', async () => {
			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert form values have changed', async () => {
			const form = actionsPage.actionForm;

			await expect(form.labelInput).toHaveValue(label);
			await expect(form.methodSelect).toHaveValue(
				EAsyncActionMethod.DELETE
			);
			await expect(form.urlInput).toHaveValue(url);
		});
	}
);

test(
	'Create and edit item action of type "Headless"',
	{tag: '@LPD-11300'},
	async ({actionsPage, page}) => {
		let confirmationMessage: string = getRandomString();
		let confirmationMessageType: EConfirmationMessageType =
			EConfirmationMessageType.SUCCESS;
		let errorStatusMessage: string = getRandomString();
		let headlessActionKey: string = getRandomString();
		let icon: string = 'heading';
		let label: string = getRandomString();
		const requestBody: string = '{"Headless": "sdfs"}';
		let successStatusMessage: string = getRandomString();
		const type: EItemActionType = EItemActionType.HEADLESS;

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Create an item action of type "Headless"', async () => {
			await actionsPage.createItemAction({
				confirmationMessage,
				confirmationMessageType,
				errorStatusMessage,
				headlessActionKey,
				icon,
				label,
				requestBody,
				successStatusMessage,
				type,
			});
		});

		let actionRow: Locator;

		await test.step('Check that the item action is in the list', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await expect(actionRow.getByRole('cell')).toContainText([
				icon,
				label,
				type,
			]);
		});

		await test.step('Open edit page of the saved item', async () => {
			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert saved values match values on creation', async () => {
			const form = actionsPage.actionForm;

			await expect(form.confirmationMessageInput).toHaveValue(
				confirmationMessage
			);
			await expect(form.confirmationMessageTypeSelect).toHaveValue(
				confirmationMessageType
			);
			await expect(form.errorStatusMessageInput).toHaveValue(
				errorStatusMessage
			);
			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.labelInput).toHaveValue(label);
			await expect(form.requestBodyInput).toHaveValue(requestBody);
			await expect(form.successStatusMessageInput).toHaveValue(
				successStatusMessage
			);
			await expect(form.typeSelect).toHaveValue(type);
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			confirmationMessage = '';
			confirmationMessageType = EConfirmationMessageType.WARNING;
			errorStatusMessage = '';
			headlessActionKey = getRandomString();
			icon = '';
			label = getRandomString();
			successStatusMessage = '';

			await actionsPage.fillItemActionFormValues({
				confirmationMessage,
				confirmationMessageType,
				errorStatusMessage,
				headlessActionKey,
				icon,
				label,
				successStatusMessage,
				type,
			});

			await actionsPage.actionForm.requestBodyInput.clear();

			await actionsPage.actionForm.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Open edit page of the saved item', async () => {
			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert form values have changed', async () => {
			const form = actionsPage.actionForm;

			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.labelInput).toHaveValue(label);
		});
	}
);

test(
	'Create and edit item action of type "Link"',
	{tag: '@LPD-11300'},
	async ({actionsPage, page}) => {
		let confirmationMessage: string = getRandomString();
		let confirmationMessageType: EConfirmationMessageType =
			EConfirmationMessageType.INFO;
		let headlessActionKey: string = getRandomString();
		let icon: string = 'arrow-right-full';
		let label: string = getRandomString();
		const type: EItemActionType = EItemActionType.LINK;
		let url: string = getRandomString();

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Create an item action of type "Link"', async () => {
			await actionsPage.createItemAction({
				confirmationMessage,
				confirmationMessageType,
				headlessActionKey,
				icon,
				label,
				type,
				url,
			});
		});

		let actionRow: Locator;

		await test.step('Check that the item action is in the list', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await expect(actionRow.getByRole('cell')).toContainText([
				icon,
				label,
				type,
			]);
		});

		await test.step('Open edit page of the saved item', async () => {
			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert saved values match values on creation', async () => {
			const form = actionsPage.actionForm;

			await expect(form.confirmationMessageInput).toHaveValue(
				confirmationMessage
			);
			await expect(form.confirmationMessageTypeSelect).toHaveValue(
				confirmationMessageType
			);
			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.labelInput).toHaveValue(label);
			await expect(form.typeSelect).toHaveValue(type);
			await expect(form.urlInput).toHaveValue(url);
		});

		await test.step('Assert type cannot be changed', async () => {
			await expect(actionsPage.actionForm.typeSelect).toBeDisabled();
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			confirmationMessage = '';
			confirmationMessageType = EConfirmationMessageType.WARNING;
			headlessActionKey = '';
			icon = '';
			label = getRandomString();
			url = getRandomString();

			await actionsPage.fillItemActionFormValues({
				confirmationMessage,
				confirmationMessageType,
				headlessActionKey,
				icon,
				label,
				type,
				url,
			});

			await actionsPage.actionForm.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Open edit page of the saved item', async () => {
			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert form values have changed', async () => {
			const form = actionsPage.actionForm;

			await expect(form.labelInput).toHaveValue(label);
			await expect(form.urlInput).toHaveValue(url);
		});
	}
);

test(
	'Create and edit item action of type "Modal"',
	{tag: '@LPD-11300'},
	async ({actionsPage, page}) => {
		let confirmationMessage: string = getRandomString();
		let confirmationMessageType: EConfirmationMessageType =
			EConfirmationMessageType.DANGER;
		let headlessActionKey: string = getRandomString();
		let icon: string = 'check';
		let label: string = getRandomString();
		let title: string = getRandomString();
		const type: EItemActionType = EItemActionType.MODAL;
		let url: string = getRandomString();
		let variant: EModalActionVariant = EModalActionVariant.SMALL;

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Create an item action of type "Modal"', async () => {
			await actionsPage.createItemAction({
				confirmationMessage,
				confirmationMessageType,
				headlessActionKey,
				icon,
				label,
				title,
				type,
				url,
				variant,
			});
		});

		let actionRow: Locator;

		await test.step('Check that the item action is in the list', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await expect(actionRow.getByRole('cell')).toContainText([
				icon,
				label,
				type,
			]);
		});

		await test.step('Open edit page of the saved item', async () => {
			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert saved values match values on creation', async () => {
			const form = actionsPage.actionForm;

			await expect(form.confirmationMessageInput).toHaveValue(
				confirmationMessage
			);
			await expect(form.confirmationMessageTypeSelect).toHaveValue(
				confirmationMessageType
			);
			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.labelInput).toHaveValue(label);
			await expect(form.typeSelect).toHaveValue(type);
			await expect(form.urlInput).toHaveValue(url);
			await expect(form.variantSelect).toHaveValue(variant);
		});

		await test.step('Assert type cannot be changed', async () => {
			await expect(actionsPage.actionForm.typeSelect).toBeDisabled();
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			confirmationMessage = '';
			confirmationMessageType = EConfirmationMessageType.WARNING;
			headlessActionKey = '';
			icon = '';
			label = getRandomString();
			title = '';
			url = getRandomString();
			variant = EModalActionVariant.LARGE;

			await actionsPage.fillItemActionFormValues({
				confirmationMessage,
				confirmationMessageType,
				headlessActionKey,
				icon,
				label,
				title,
				type,
				url,
				variant,
			});

			await actionsPage.actionForm.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Open edit page of the saved item', async () => {
			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert form values have changed', async () => {
			const form = actionsPage.actionForm;

			await expect(form.labelInput).toHaveValue(label);
			await expect(form.urlInput).toHaveValue(url);
			await expect(form.variantSelect).toHaveValue(variant);
		});
	}
);

test(
	'Create and edit item action of type "Side Panel"',
	{tag: '@LPD-11300'},
	async ({actionsPage, page}) => {
		let confirmationMessage: string = getRandomString();
		let confirmationMessageType: EConfirmationMessageType =
			EConfirmationMessageType.DANGER;
		let headlessActionKey: string = getRandomString();
		let icon: string = 'book';
		let label: string = getRandomString();
		let title: string = getRandomString();
		const type: EItemActionType = EItemActionType.SIDE_PANEL;
		let url: string = getRandomString();

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Create an item action of type "Side Panel"', async () => {
			await actionsPage.createItemAction({
				confirmationMessage,
				confirmationMessageType,
				headlessActionKey,
				icon,
				label,
				title,
				type,
				url,
			});
		});

		let actionRow: Locator;

		await test.step('Check that the item action is in the list', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await expect(actionRow.getByRole('cell')).toContainText([
				icon,
				label,
				type,
			]);
		});

		await test.step('Open edit page of the saved item', async () => {
			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert saved values match values on creation', async () => {
			const form = actionsPage.actionForm;

			await expect(form.confirmationMessageInput).toHaveValue(
				confirmationMessage
			);
			await expect(form.confirmationMessageTypeSelect).toHaveValue(
				confirmationMessageType
			);
			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.labelInput).toHaveValue(label);
			await expect(form.typeSelect).toHaveValue(type);
			await expect(form.urlInput).toHaveValue(url);
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			confirmationMessage = '';
			confirmationMessageType = EConfirmationMessageType.INFO;
			headlessActionKey = '';
			icon = '';
			label = getRandomString();
			title = '';
			url = getRandomString();

			await actionsPage.fillItemActionFormValues({
				confirmationMessage,
				confirmationMessageType,
				headlessActionKey,
				icon,
				label,
				title,
				type,
				url,
			});

			await actionsPage.actionForm.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Open edit page of the saved item', async () => {
			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: label,
			});

			await clickRowAction({
				actionLabel: 'Edit',
				page,
				row: actionRow,
			});

			await expect(page.getByText('Display Options')).toBeInViewport();
		});

		await test.step('Assert form values have changed', async () => {
			const form = actionsPage.actionForm;

			await expect(form.labelInput).toHaveValue(label);
			await expect(form.urlInput).toHaveValue(url);
		});
	}
);

test(
	'Cancel creating an item action',
	{tag: '@LPD-11300'},
	async ({actionsPage}) => {
		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Open new item actions form', async () => {
			await actionsPage.newItemActionPlusButton.click();

			await expect(
				actionsPage.page.getByText('Display Options')
			).toBeInViewport();
		});

		await test.step('Add some information in the form', async () => {
			await actionsPage.actionForm.labelInput.fill(getRandomString());
		});

		await test.step('Cancel the creation of the item action', async () => {
			await actionsPage.actionForm.cancelButton.click();
		});

		await test.step('Check that no actions were created', async () => {
			await expect(actionsPage.noActionsWereCreatedMessage).toContainText(
				'No actions were created.'
			);
		});
	}
);

test(
	'Delete an item action',
	{tag: '@LPD-11300'},
	async ({actionsPage, dataSetManagerApiHelpers, page}) => {
		const actionLabel = getRandomString();

		await test.step('Create an item action with API', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				confirmationMessage_i18n: {
					en_US: getRandomString(),
				},
				dataSetERC,
				label_i18n: {en_US: actionLabel},
				type: EItemActionType.LINK,
			});
		});

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		let actionRow: Locator;

		await test.step('Cancel deletion by not confirming it', async () => {
			actionRow = await getRowByText({
				page,
				table: actionsPage.itemActionsTable,
				text: actionLabel,
			});

			await clickRowAction({
				actionLabel: 'Delete',
				page,
				row: actionRow,
			});

			await expect(
				actionsPage.deletionConfirmationModal
			).toBeInViewport();

			await actionsPage.deletionConfirmationModal
				.getByRole('button', {
					name: 'Cancel',
				})
				.click();

			await expect(
				actionsPage.deletionConfirmationModal
			).not.toBeInViewport();

			await expect(actionRow).toBeInViewport();
		});

		await test.step('Delete item', async () => {
			await clickRowAction({
				actionLabel: 'Delete',
				page,
				row: actionRow,
			});

			await actionsPage.deletionConfirmationModal
				.getByRole('button', {
					name: 'Delete',
				})
				.click();

			await expect(
				actionsPage.deletionConfirmationModal
			).not.toBeInViewport();

			await expect(actionRow).not.toBeInViewport();
		});
	}
);

test(
	'Item actions can be reordered',
	{tag: '@LPD-11300'},
	async ({actionsPage, dataSetManagerApiHelpers, page}) => {
		const firstAction = {
			icon: 'angle-left-double',
			label: getRandomString(),
			type: EItemActionType.LINK,
		};
		const secondAction = {
			icon: 'angle-left-small',
			label: getRandomString(),
			type: EItemActionType.LINK,
		};
		const thirdAction = {
			icon: 'angle-left',
			label: getRandomString(),
			type: EItemActionType.LINK,
		};

		await test.step('Create some item actions', async () => {
			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				icon: firstAction.icon,
				label_i18n: {en_US: firstAction.label},
				type: firstAction.type,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				icon: secondAction.icon,
				label_i18n: {en_US: secondAction.label},
				type: secondAction.type,
			});

			await dataSetManagerApiHelpers.createDataSetItemAction({
				dataSetERC,
				icon: thirdAction.icon,
				label_i18n: {en_US: thirdAction.label},
				type: thirdAction.type,
			});
		});

		await test.step('Go to item actions tab', async () => {
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Check that the item action are in the list', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();

			assertTableCellContent({
				actionData: firstAction,
				page,
				rowIndex: 0,
			});
			assertTableCellContent({
				actionData: secondAction,
				page,
				rowIndex: 1,
			});
			assertTableCellContent({
				actionData: thirdAction,
				page,
				rowIndex: 2,
			});
		});

		await test.step('Move second item action to the top', async () => {
			const secondRow = actionsPage.page
				.locator('.orderable-table-row')
				.nth(2);

			const firstRow = actionsPage.page
				.locator('.orderable-table-row')
				.nth(0);

			await secondRow.dragTo(firstRow);
		});

		await test.step('Check that the item actions order has changed', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();
			assertTableCellContent({
				actionData: firstAction,
				page,
				rowIndex: 1,
			});
			assertTableCellContent({
				actionData: secondAction,
				page,
				rowIndex: 2,
			});
			assertTableCellContent({
				actionData: thirdAction,
				page,
				rowIndex: 0,
			});
		});

		await test.step('Navigate to the "Creation Actions" tab and back to "Item Actions" tab', async () => {
			await actionsPage.gotoCreationActionsTab({dataSetLabel});
			await actionsPage.gotoItemActionsTab({dataSetLabel});
		});

		await test.step('Check that the item actions keep the last order saved', async () => {
			await expect(actionsPage.itemActionsTab).toBeInViewport();

			assertTableCellContent({
				actionData: firstAction,
				page,
				rowIndex: 1,
			});
			assertTableCellContent({
				actionData: secondAction,
				page,
				rowIndex: 2,
			});
			assertTableCellContent({
				actionData: thirdAction,
				page,
				rowIndex: 0,
			});
		});
	}
);
