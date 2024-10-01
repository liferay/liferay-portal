/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../../../../../../utils/waitForAlert';
import {ICreationAction, IItemAction} from '../../../../../utils/types';
import {DataSetPage} from '../DataSetPage';

export class ActionsPage {
	readonly actionForm: {
		addIconButton: Locator;
		cancelButton: Locator;
		changeIconButton: Locator;
		confirmationMessageInput: Locator;
		confirmationMessageTypeSelect: Locator;
		deleteIconButton: Locator;
		errorStatusMessageInput: Locator;
		headlessActionKeyInput: Locator;
		iconInput: Locator;
		labelInput: Locator;
		methodSelect: Locator;
		requestBodyInput: Locator;
		saveButton: Locator;
		selectIconModal: Locator;
		successStatusMessageInput: Locator;
		titleInput: Locator;
		typeSelect: Locator;
		urlInput: Locator;
		variantSelect: Locator;
	};
	readonly creationActionsTab: Locator;
	readonly creationActionsTable: Locator;
	readonly dataSetPage: DataSetPage;
	readonly deletionConfirmationModal: Locator;
	readonly itemActionsTab: Locator;
	readonly itemActionsTable: Locator;
	readonly newItemActionPlusButton: Locator;
	readonly newCreationActionPlusButton: Locator;
	readonly newCreationActionButton: Locator;
	readonly newItemActionButton: Locator;
	readonly noActionsWereCreatedMessage: Locator;
	readonly page: Page;
	readonly statusMessagesTabs: Locator;
	private readonly actionsTabs: Locator;

	constructor(page: Page) {
		this.actionForm = {
			addIconButton: page.getByLabel('Add Icon'),
			cancelButton: page.getByRole('button', {name: 'Cancel'}),
			changeIconButton: page.getByLabel('Change Icon'),
			confirmationMessageInput: page.getByTestId(
				'confirmationMessageInput'
			),
			confirmationMessageTypeSelect: page.getByLabel('Message Type', {
				exact: true,
			}),
			deleteIconButton: page.getByLabel('Remove Icon'),
			errorStatusMessageInput: page.getByTestId(
				'errorStatusMessageInput'
			),
			headlessActionKeyInput: page.getByPlaceholder('Add a value here.'),
			iconInput: page.getByPlaceholder('No Icon Selected'),
			labelInput: page.getByPlaceholder('Action Name'),
			methodSelect: page.getByLabel('MethodRequired', {exact: true}),
			requestBodyInput: page.getByPlaceholder('Add a request body here'),
			saveButton: page.getByRole('button', {name: 'Save'}),
			selectIconModal: page.locator('.dsm-actions-icon-selection-modal'),
			successStatusMessageInput: page.getByTestId(
				'successStatusMessageInput'
			),
			titleInput: page.getByLabel('Title', {exact: true}),
			typeSelect: page.getByLabel('TypeRequired', {exact: true}),
			urlInput: page.getByPlaceholder('Add a URL here.'),
			variantSelect: page.getByLabel('VariantRequired', {exact: true}),
		};
		this.creationActionsTab = page.getByRole('tab', {
			name: 'Creation Actions',
		});
		this.creationActionsTable = page.locator(
			'.creation-actions-tab-pane table'
		);
		this.dataSetPage = new DataSetPage(page);
		this.deletionConfirmationModal = page
			.getByRole('dialog')
			.and(page.getByLabel('Delete Action'));
		this.itemActionsTab = page.getByRole('tab', {name: 'Item Actions'});
		this.itemActionsTable = page.locator('.item-actions-tab-pane table');
		this.newItemActionPlusButton = page.getByTitle('New Item Action');
		this.newCreationActionPlusButton = page.getByText(
			'New Creation Action'
		);
		this.newCreationActionButton = page.getByText('New Creation Action');
		this.newItemActionButton = page.getByText('New Item Action');
		this.noActionsWereCreatedMessage = page
			.getByRole('tabpanel')
			.nth(0)
			.locator('.c-empty-state-title');
		this.page = page;
		this.statusMessagesTabs = page.locator('.status-messages-tabs');
		this.actionsTabs = page.locator('.actions-tabs');
	}

	async goto({dataSetLabel}: {dataSetLabel: string}) {
		await this.dataSetPage.goto({
			dataSetLabel,
		});

		await this.dataSetPage.selectTab('Actions');
	}

	async gotoCreationActionsTab({dataSetLabel}: {dataSetLabel: string}) {
		await this.goto({
			dataSetLabel,
		});

		await this.selectTab({
			container: this.actionsTabs,
			label: 'Creation Actions',
		});
	}

	async gotoItemActionsTab({dataSetLabel}: {dataSetLabel: string}) {
		await this.goto({
			dataSetLabel,
		});

		await this.selectTab({
			container: this.actionsTabs,
			label: 'Item Actions',
		});
	}

	async createCreationAction(creationActionProps: ICreationAction) {
		await this.newCreationActionPlusButton.click();

		await this.fillCreationActionFormValues({...creationActionProps});

		await this.actionForm.saveButton.click();

		await waitForAlert(this.page);
	}

	async createItemAction(itemActionProps: IItemAction) {
		await this.newItemActionPlusButton.click();

		await this.fillItemActionFormValues({...itemActionProps});

		await this.actionForm.saveButton.click();

		await waitForAlert(this.page);
	}

	async fillCreationActionFormValues(creationActionProps: ICreationAction) {
		const typeDisabled = await this.actionForm.typeSelect.isDisabled();

		if (!typeDisabled) {
			await this.actionForm.typeSelect.selectOption(
				creationActionProps.type
			);
		}

		await this.fillActionFormValues({...creationActionProps});
	}

	async fillItemActionFormValues(itemActionProps: IItemAction) {
		const {
			confirmationMessage,
			confirmationMessageType,
			errorStatusMessage,
			method,
			requestBody,
			successStatusMessage,
			type,
		} = itemActionProps;

		const typeDisabled = await this.actionForm.typeSelect.isDisabled();

		if (!typeDisabled) {
			await this.actionForm.typeSelect.selectOption(type);
		}

		if (confirmationMessage) {
			this.actionForm.confirmationMessageInput.fill(confirmationMessage);
		}

		if (confirmationMessageType) {
			this.actionForm.confirmationMessageTypeSelect.selectOption(
				confirmationMessageType
			);
		}

		if (method) {
			await this.actionForm.methodSelect.selectOption(method);
		}

		if (requestBody) {
			await this.actionForm.requestBodyInput.fill(requestBody);
		}

		if (successStatusMessage) {
			await this.actionForm.successStatusMessageInput.fill(
				successStatusMessage
			);
		}

		if (errorStatusMessage) {
			await this.selectTab({
				container: this.statusMessagesTabs,
				label: 'Error',
			});

			await this.actionForm.errorStatusMessageInput.fill(
				errorStatusMessage
			);
		}

		await this.fillActionFormValues({...itemActionProps});
	}

	private async fillActionFormValues(
		actionProps: ICreationAction | IItemAction
	) {
		const {headlessActionKey, icon, label, title, url, variant} =
			actionProps;

		if (headlessActionKey) {
			await this.actionForm.headlessActionKeyInput.fill(
				headlessActionKey
			);
		}

		await this.actionForm.labelInput.fill(label);

		const iconSelected = Boolean(
			await this.actionForm.iconInput.inputValue()
		);

		if (icon) {
			if (iconSelected) {
				await this.actionForm.changeIconButton.click();
			}
			else {
				await this.actionForm.addIconButton.click();
			}

			await this.actionForm.selectIconModal
				.getByRole('listitem')
				.getByText(icon, {exact: true})
				.click();
		}
		else if (iconSelected) {
			await this.actionForm.deleteIconButton.click();
		}

		if (title) {
			await this.actionForm.titleInput.fill(title);
		}

		if (url) {
			await this.actionForm.urlInput.fill(url);
		}

		if (variant) {
			await this.actionForm.variantSelect.waitFor({state: 'visible'});

			await this.actionForm.variantSelect.selectOption(variant);
		}
	}

	async selectTab({container, label}: {container: Locator; label: string}) {
		const tabButton = container.getByRole('tab', {
			exact: true,
			name: label,
		});

		await tabButton.click();

		await tabButton.and(this.page.locator('.active')).waitFor();

		await expect(
			this.page.locator('.loading-animation')
		).not.toBeInViewport();
	}
}
