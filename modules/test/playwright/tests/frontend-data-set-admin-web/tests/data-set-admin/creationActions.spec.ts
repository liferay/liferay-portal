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
import {ECreationActionType, EModalActionVariant} from '../../utils/types';
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

	await test.step('Create a data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test(
	'Check interactive options in creation action form',
	{tag: '@LPD-11245'},
	async ({actionsPage, page}) => {
		const form = actionsPage.actionForm;

		await test.step('Go to creation actions tab', async () => {
			await actionsPage.gotoCreationActionsTab({dataSetLabel});
		});

		await test.step('Assert message exists informing that there are no actions created', async () => {
			await expect(actionsPage.noActionsWereCreatedMessage).toContainText(
				'No actions were created.'
			);
		});

		await test.step('Open new creation actions form', async () => {
			await actionsPage.newCreationActionPlusButton.click();

			await expect(
				actionsPage.page.getByText('Display Options')
			).toBeInViewport();
		});

		await test.step('Check options of selections', async () => {
			expect(await getSelectOptionLabels(form.typeSelect)).toEqual([
				'Link',
				'Modal',
				'Side Panel',
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
				formElements: [form.labelInput],
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
		});

		await test.step('Validate entire form on save', async () => {
			await form.typeSelect.selectOption('Link');

			await form.saveButton.click();

			await checkRequired({
				formElements: [form.labelInput, form.urlInput],
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
		});
	}
);

test(
	'Create and edit a creation action of type "Link"',
	{tag: '@LPD-11245'},
	async ({actionsPage, page}) => {
		let headlessActionKey: string = getRandomString();
		let icon: string = 'arrow-right-full';
		let label: string = getRandomString();
		const type: ECreationActionType = ECreationActionType.LINK;
		let url: string = getRandomString();

		await test.step('Go to creation actions tab', async () => {
			await actionsPage.gotoCreationActionsTab({dataSetLabel});
		});

		await test.step('Create a creation action of type "Link"', async () => {
			await actionsPage.createCreationAction({
				headlessActionKey,
				icon,
				label,
				type,
				url,
			});
		});

		let actionRow: Locator;

		await test.step('Check that the creation action is in the list', async () => {
			await expect(actionsPage.creationActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.creationActionsTable,
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

			await expect(form.labelInput).toHaveValue(label);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.typeSelect).toHaveValue(type);
			await expect(form.urlInput).toHaveValue(url);
			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
		});

		await test.step('Assert type cannot be changed', async () => {
			await expect(actionsPage.actionForm.typeSelect).toBeDisabled();
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			headlessActionKey = '';
			icon = '';
			label = getRandomString();
			url = getRandomString();

			await actionsPage.fillCreationActionFormValues({
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
				table: actionsPage.creationActionsTable,
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
	'Create and edit a creation action of type "Modal"',
	{tag: '@LPD-11245'},
	async ({actionsPage, page}) => {
		let headlessActionKey: string = getRandomString();
		let icon: string = 'check';
		let label: string = getRandomString();
		let title: string = getRandomString();
		const type: ECreationActionType = ECreationActionType.MODAL;
		let url: string = getRandomString();
		let variant = EModalActionVariant.LARGE;

		await test.step('Go to creation actions tab', async () => {
			await actionsPage.gotoCreationActionsTab({dataSetLabel});
		});

		await test.step('Create a creation action of type "Modal"', async () => {
			await actionsPage.createCreationAction({
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

		await test.step('Check that the creation action is in the list', async () => {
			await expect(actionsPage.creationActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.creationActionsTable,
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

			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.labelInput).toHaveValue(label);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.titleInput).toHaveValue(title);
			await expect(form.typeSelect).toHaveValue(type);
			await expect(form.urlInput).toHaveValue(url);
			await expect(form.variantSelect).toHaveValue(variant);
		});

		await test.step('Assert type cannot be changed', async () => {
			await expect(actionsPage.actionForm.typeSelect).toBeDisabled();
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			headlessActionKey = '';
			icon = '';
			label = getRandomString();
			title = '';
			url = getRandomString();
			variant = EModalActionVariant.FULL_SCREEN;

			await actionsPage.fillCreationActionFormValues({
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
				table: actionsPage.creationActionsTable,
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
	'Create and edit a creation action of type "Side Panel"',
	{tag: '@LPD-11245'},
	async ({actionsPage, page}) => {
		let headlessActionKey: string = getRandomString();
		let icon: string = 'check';
		let label: string = getRandomString();
		let title: string = getRandomString();
		const type: ECreationActionType = ECreationActionType.MODAL;
		let url: string = getRandomString();

		await test.step('Go to creation actions tab', async () => {
			await actionsPage.gotoCreationActionsTab({dataSetLabel});
		});

		await test.step('Create a creation action of type "Side Panel"', async () => {
			await actionsPage.createCreationAction({
				headlessActionKey,
				icon,
				label,
				title,
				type,
				url,
			});
		});

		let actionRow: Locator;

		await test.step('Check that the creation action is in the list', async () => {
			await expect(actionsPage.creationActionsTab).toBeInViewport();

			actionRow = await getRowByText({
				page,
				table: actionsPage.creationActionsTable,
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

			await expect(form.headlessActionKeyInput).toHaveValue(
				headlessActionKey
			);
			await expect(form.labelInput).toHaveValue(label);
			await expect(form.iconInput).toHaveValue(icon);
			await expect(form.titleInput).toHaveValue(title);
			await expect(form.typeSelect).toHaveValue(type);
			await expect(form.urlInput).toHaveValue(url);
		});

		await test.step('Assert type cannot be changed', async () => {
			await expect(actionsPage.actionForm.typeSelect).toBeDisabled();
		});

		await test.step('Change form values to minimum requirements and save', async () => {
			headlessActionKey = '';
			icon = '';
			label = getRandomString();
			title = '';
			url = getRandomString();

			await actionsPage.fillCreationActionFormValues({
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
				table: actionsPage.creationActionsTable,
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
	'Cancel creating a creation action',
	{tag: '@LPD-11245'},
	async ({actionsPage}) => {
		await test.step('Go to creation actions tab', async () => {
			await actionsPage.gotoCreationActionsTab({dataSetLabel});
		});

		await test.step('Open new creation actions form', async () => {
			await actionsPage.newCreationActionPlusButton.click();

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
	'Delete a creation action',
	{tag: '@LPD-11245'},
	async ({actionsPage, dataSetManagerApiHelpers, page}) => {
		const actionLabel = getRandomString();

		await test.step('Create an item action with API', async () => {
			await dataSetManagerApiHelpers.createDataSetCreationAction({
				dataSetERC,
				label_i18n: {en_US: actionLabel},
				type: ECreationActionType.LINK,
			});
		});

		await test.step('Go to creation actions tab', async () => {
			await actionsPage.gotoCreationActionsTab({dataSetLabel});
		});

		let actionRow: Locator;

		await test.step('Cancel deletion by not confirming it', async () => {
			actionRow = await getRowByText({
				page,
				table: actionsPage.creationActionsTable,
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
