/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {GlobalMenuPage} from '../../product-navigation-applications-menu/GlobalMenuPage';
import {searchTableRowByValue} from '../commerceDNDTablePage';

export class CommerceAdminChannelDetailsPage {
	readonly accountEntryValidationModeSelect: Locator;
	readonly activeToggle: (tableName: string) => Promise<Locator>;
	readonly addTaxRateButton: (tableName: string) => Promise<Locator>;
	readonly addTaxRateSettingButton: (tableName: string) => Promise<Locator>;
	readonly addTaxRateFrame: FrameLocator;
	readonly addTaxRateSettingsFrame: FrameLocator;
	readonly allowMultishippingToggle: Locator;
	readonly allowRequestAQuote: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly byAddressCountryChoiceBox: Locator;
	readonly byAddressRegionChoiceBox: Locator;
	readonly byAddressTaxCategoryChoiceBox: Locator;
	readonly categoryDisplayPageTab: Locator;
	readonly channelCurrencySelect: Locator;
	readonly channelId: Locator;
	readonly channelNameLink: (channelName: string) => Locator;
	readonly closeSidePanelFrame: (
		isNestedFrame: boolean,
		tableName: string,
		depth?: number
	) => Promise<Locator>;
	readonly commerceChannelHealthChecksTable: Locator;
	readonly commerceChannelHealthChecksTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly commerceChannelHealthChecksTableRowAction: (
		action: string,
		pageName: string
	) => Promise<Locator>;
	readonly countryTab: Locator;
	readonly currencyTab: Locator;
	readonly detailsButton: (tableName: string) => Promise<Locator>;
	readonly eligibilityTab: (
		isNestedFrame: boolean,
		tableName: string
	) => Promise<Locator>;
	readonly eligibilityOptionButton: (
		eligibilityOption: string,
		isNestedFrame: boolean,
		tableName: string
	) => Promise<Locator>;
	readonly errorMessage: (text: string) => Locator;
	readonly frameSaveButton: (
		isNestedFrame: boolean,
		tableName: string,
		depth?: number
	) => Promise<Locator>;
	readonly generalCommerceAdminChannelTableLink: (
		name: string
	) => Promise<Locator>;
	readonly getRowByTextFromSidePanelTable: (
		tableName: string,
		text: string
	) => Promise<Locator>;
	readonly guestCheckoutToggle: Locator;
	readonly isActive: Locator;
	readonly linkSupplierSelect: Locator;
	readonly linkTab: (tabName: string) => Locator;
	readonly maxOpenOrderAccountInput: Locator;
	readonly page: Page;
	readonly placeHolderTerm: (
		isNestedFrame: boolean,
		tableName: string,
		text: string
	) => Promise<Locator>;
	readonly saveButton: Locator;
	readonly searchedEntry: (name: string) => Locator;
	readonly selectButton: (
		isNestedFrame: boolean,
		tableName: string
	) => Promise<Locator>;
	readonly shippingOptionsTab: (tableName: string) => Promise<Locator>;
	readonly shippingOptionSettingsTab: (tableName: string) => Promise<Locator>;
	readonly shippingOptionsSettingsTableLink: (
		shippingOptionName: string,
		tableName: string
	) => Promise<Locator>;
	readonly shippingOptionsTableLink: (
		shippingOptionName: string,
		tableName: string
	) => Promise<Locator>;
	readonly showSeparateOrderItemsToggle: Locator;
	readonly sidePanelCloseButton: Locator;
	readonly sidePanelFrame: (tableName: string) => Promise<FrameLocator>;
	readonly sidePanelFrameActionsButton: (
		tableName: string,
		name: string
	) => Promise<Locator>;
	readonly sidePanelFrameButton: (
		buttonName: string,
		tableName: string
	) => Promise<Locator>;
	readonly sidePanelFrameBodyHTML: (tableName: string) => Promise<string>;
	readonly sidePanelFrameInput: (
		input: string,
		tableName: string
	) => Promise<Locator>;
	readonly sidePanelFrameNavLink: (
		navItem: string,
		tableName: string
	) => Promise<Locator>;
	readonly sidePanelFrameDeleteMenuItem: (
		tableName: string
	) => Promise<Locator>;
	readonly sidePanelFrameEditMenuItem: (
		tableName: string
	) => Promise<Locator>;
	readonly sidePanelFrameAddButton: (tableName: string) => Promise<Locator>;
	readonly sidePanelNestedFrame: (
		tableName: string,
		depth?: number
	) => Promise<FrameLocator>;
	readonly sidePanelNestedFrameAmountInput: (
		tableName: string
	) => Promise<Locator>;
	readonly sidePanelNestedFrameInput: (
		label: string,
		tableName: string,
		depth?: number
	) => Promise<Locator>;
	readonly sidePanelFrameLocator: FrameLocator;
	readonly sidePanelNestedCloseButton: Locator;
	readonly sidePanelSaveButton: Locator;
	readonly taxCategoryChoiceBox: Locator;
	readonly taxCategoryInput: Locator;
	readonly taxRateFrameSettingsSubmitButton: Locator;
	readonly taxRateFrameSubmitButton: Locator;
	readonly taxRateSettingsTab: (tableName: string) => Promise<Locator>;
	readonly taxRatesTab: (tableName: string) => Promise<Locator>;

	constructor(page: Page) {
		this.activeToggle = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByRole(
				'checkbox',
				{
					name: 'Active',
				}
			);
		};
		this.addTaxRateButton = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByTitle(
				'Add Tax Rate'
			);
		};
		this.addTaxRateSettingButton = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByTitle(
				'Add Tax Rate Setting'
			);
		};
		this.addTaxRateFrame = page
			.locator('div.fds-modal-body.modal-body-iframe')
			.frameLocator('iframe');
		this.addTaxRateSettingsFrame = page
			.locator('div.fds-modal-body.modal-body-iframe')
			.frameLocator('iframe');
		this.allowMultishippingToggle = page.getByLabel('Allow Multishipping');
		this.allowRequestAQuote = page.getByLabel(
			'Allow Request a Quote on a Fully Priced Cart'
		);
		this.globalMenuPage = new GlobalMenuPage(page);
		this.byAddressTaxCategoryChoiceBox =
			this.addTaxRateSettingsFrame.getByText('Tax Category');
		this.byAddressCountryChoiceBox =
			this.addTaxRateSettingsFrame.getByLabel('Country');
		this.byAddressRegionChoiceBox =
			this.addTaxRateSettingsFrame.getByLabel('Region');
		this.categoryDisplayPageTab = page.getByRole('link', {
			name: 'Category Display Pages',
		});
		this.accountEntryValidationModeSelect = page.locator(
			'select[name$="validationMode--"]'
		);
		this.channelCurrencySelect = page.locator("select[title='Currency']");
		this.channelId = page.locator('span:has-text("ID")+strong');
		this.channelNameLink = (channelName: string) =>
			page.getByRole('link', {
				exact: true,
				name: channelName,
			});
		this.commerceChannelHealthChecksTable = page
			.locator(
				'#_com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet_editChannelContainer .fds table'
			)
			.filter({hasText: 'Fix Issue'});
		this.commerceChannelHealthChecksTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.commerceChannelHealthChecksTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.commerceChannelHealthChecksTableRowAction = async (
			action: string,
			pageName: string
		) => {
			const pagesTableRow =
				await this.commerceChannelHealthChecksTableRow(
					0,
					pageName,
					true
				);

			if (pagesTableRow && pagesTableRow.column) {
				return pagesTableRow.row.getByRole('link', {
					name: action,
				});
			}

			throw new Error(`Cannot locate table row with name ${pageName}`);
		};
		this.countryTab = page.getByRole('link', {name: 'Countries'});
		this.currencyTab = page.getByRole('link', {name: 'Currencies'});
		this.generalCommerceAdminChannelTableLink = async (name: string) => {
			return page.getByRole('link', {exact: true, name});
		};
		this.guestCheckoutToggle = page.getByLabel('Guest Checkout');
		this.closeSidePanelFrame = async (
			isNestedFrame: boolean,
			tableName: string,
			depth: number = 1
		) => {
			if (isNestedFrame) {
				return (await this.sidePanelNestedFrame(tableName, depth))
					.locator('.btn')
					.first();
			}

			return (await this.sidePanelFrame(tableName))
				.getByRole('button')
				.first();
		};
		this.detailsButton = async (tableName: string) => {
			return (await this.sidePanelNestedFrame(tableName)).getByRole(
				'button',
				{name: 'Details'}
			);
		};
		this.eligibilityTab = async (
			isNestedFrame: boolean,
			tableName: string
		) => {
			if (isNestedFrame) {
				return (await this.sidePanelNestedFrame(tableName)).getByRole(
					'link',
					{name: 'Eligibility'}
				);
			}

			return (await this.sidePanelFrame(tableName)).getByRole('link', {
				name: 'Eligibility',
			});
		};
		this.eligibilityOptionButton = async (
			eligibilityOption: string,
			isNestedFrame: boolean,
			tableName: string
		) => {
			if (isNestedFrame) {
				return (await this.sidePanelNestedFrame(tableName)).getByLabel(
					eligibilityOption
				);
			}

			return (await this.sidePanelFrame(tableName)).getByLabel(
				eligibilityOption
			);
		};
		this.errorMessage = (text: string) =>
			page.locator('.alert-danger').filter({hasText: text});
		this.frameSaveButton = async (
			isNestedFrame: boolean,
			tableName: string,
			depth: number = 1
		) => {
			if (isNestedFrame) {
				return (
					await this.sidePanelNestedFrame(tableName, depth)
				).getByRole('button', {name: 'Save'});
			}

			return (await this.sidePanelFrame(tableName)).getByRole('button', {
				name: 'Save',
			});
		};
		this.getRowByTextFromSidePanelTable = async (
			tableName: string,
			text: string
		) => {
			return (await this.sidePanelFrame(tableName))
				.locator('tbody')
				.locator('tr')
				.filter({
					has: (await this.sidePanelFrame(tableName))
						.getByText(text, {exact: true})
						.first(),
				});
		};
		this.sidePanelFrameLocator = page.frameLocator('.is-visible iframe');
		this.isActive = this.sidePanelFrameLocator.getByLabel('Active');
		this.linkSupplierSelect = page.locator(
			'select[name$="accountEntryId"]'
		);
		this.linkTab = (tabName) =>
			page.getByRole('link', {exact: true, name: tabName});
		this.maxOpenOrderAccountInput = page.getByLabel(
			'Maximum Number of Open Orders per Account'
		);
		this.saveButton = page.getByRole('link', {name: 'Save'});
		this.searchedEntry = (name) => {
			return page.getByRole('menuitem', {name: new RegExp(name)});
		};
		this.selectButton = async (
			isNestedFrame: boolean,
			tableName: string
		) => {
			if (isNestedFrame) {
				return (await this.sidePanelNestedFrame(tableName)).getByRole(
					'button',
					{name: 'Select'}
				);
			}

			return (await this.sidePanelFrame(tableName)).getByRole('button', {
				name: 'Select',
			});
		};
		this.shippingOptionsTab = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByRole('link', {
				name: 'Shipping Options',
			});
		};
		this.shippingOptionSettingsTab = async (tableName: string) => {
			return (await this.sidePanelNestedFrame(tableName)).getByRole(
				'link',
				{
					name: 'Shipping Option Settings',
				}
			);
		};
		this.shippingOptionsSettingsTableLink = async (
			shippingOptionName: string,
			tableName: string
		) => {
			return (await this.sidePanelNestedFrame(tableName)).getByRole(
				'link',
				{
					name: shippingOptionName,
				}
			);
		};
		this.shippingOptionsTableLink = async (
			shippingOptionName: string,
			tableName: string
		) => {
			return (await this.sidePanelFrame(tableName)).getByRole('link', {
				name: shippingOptionName,
			});
		};
		this.showSeparateOrderItemsToggle = page.getByLabel(
			'Show Separate Order Items'
		);
		this.sidePanelCloseButton = this.sidePanelFrameLocator
			.locator('.side-panel-iframe-header')
			.getByRole('button');
		this.sidePanelNestedCloseButton = this.sidePanelFrameLocator
			.frameLocator('.is-visible iframe')
			.locator('.side-panel-iframe-header')
			.getByRole('button');
		this.sidePanelFrame = async (tableName: string) => {
			switch (tableName) {
				case 'Payment Methods':
					return this.page.frameLocator('iframe >> nth=1');
				case 'Shipping Methods':
					return this.page.frameLocator('iframe >> nth=2');
				case 'Tax Calculations':
					return this.page
						.locator('div.fds-side-panel.is-visible')
						.frameLocator('iframe');
				default:
					break;
			}
		};
		this.sidePanelFrameActionsButton = async (
			tableName: string,
			name: string
		) => {
			return (
				await this.getRowByTextFromSidePanelTable(tableName, name)
			).getByRole('button', {name: 'Actions'});
		};
		this.sidePanelFrameButton = async (
			buttonName: string,
			tableName: string
		) => {
			return (await this.sidePanelNestedFrame(tableName)).getByRole(
				'button',
				{exact: true, name: buttonName}
			);
		};
		this.sidePanelFrameBodyHTML = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName))
				.locator('body')
				.innerHTML();
		};
		this.sidePanelFrameInput = async (
			inputName: string,
			tableName: string
		) => {
			return (await this.sidePanelFrame(tableName)).getByLabel(inputName);
		};
		this.sidePanelFrameNavLink = async (
			navItem: string,
			tableName: string
		) => {
			return (await this.sidePanelFrame(tableName)).getByRole('link', {
				exact: true,
				name: navItem,
			});
		};
		this.sidePanelFrameDeleteMenuItem = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByRole(
				'menuitem',
				{name: 'Delete'}
			);
		};
		this.sidePanelFrameEditMenuItem = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByRole(
				'menuitem',
				{name: 'Edit'}
			);
		};
		this.sidePanelNestedFrame = async (
			tableName: string,
			depth: number = 1
		) => {
			let frame = await this.sidePanelFrame(tableName);

			for (let i = 0; i < depth; i++) {
				frame = frame.frameLocator('iframe');
			}

			return frame;
		};
		this.sidePanelFrameAddButton = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName))
				.getByTestId('managementToolbar')
				.locator('[data-testid="fdsCreationActionButton"]');
		};
		this.sidePanelNestedFrameInput = async (
			label: string,
			tableName: string,
			depth: number = 1
		) => {
			return (
				await this.sidePanelNestedFrame(tableName, depth)
			).getByLabel(label);
		};
		this.sidePanelNestedFrameAmountInput = async (tableName: string) => {
			return this.sidePanelNestedFrameInput('Amount', tableName);
		};
		this.page = page;
		this.placeHolderTerm = async (
			isNestedFrame: boolean,
			tableName: string,
			text: string
		) => {
			if (isNestedFrame) {
				return (
					await this.sidePanelNestedFrame(tableName)
				).getByPlaceholder(text);
			}

			return (await this.sidePanelFrame(tableName)).getByPlaceholder(
				text
			);
		};
		this.sidePanelSaveButton = this.sidePanelFrameLocator.getByRole(
			'button',
			{name: 'Save'}
		);
		this.taxCategoryChoiceBox =
			this.addTaxRateFrame.getByText('Tax Category');
		this.taxCategoryInput = page
			.locator('#shippingTaxCategoryId')
			.locator('..')
			.locator('.form-control');
		this.taxRateFrameSettingsSubmitButton =
			this.addTaxRateSettingsFrame.getByRole('button', {name: 'Submit'});
		this.taxRateFrameSubmitButton = this.addTaxRateFrame.getByRole(
			'button',
			{name: 'Submit'}
		);
		this.taxRateSettingsTab = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByRole('link', {
				name: 'Tax Rate Settings',
			});
		};
		this.taxRatesTab = async (tableName: string) => {
			return (await this.sidePanelFrame(tableName)).getByRole('link', {
				name: 'Tax Rates',
			});
		};
	}

	async activateChannelConfiguration(name: string, tableName: string) {
		const isActiveCheckbox = this.isActive;

		await (await this.generalCommerceAdminChannelTableLink(name)).click();

		if (!(await isActiveCheckbox.isChecked())) {
			await isActiveCheckbox.check();
		}

		await (await this.frameSaveButton(false, tableName)).click();
		await waitForAlert(await this.sidePanelFrame(tableName));
		await (await this.closeSidePanelFrame(false, tableName)).click();
	}

	async activatePaymentMethod(name: string, description: string) {
		const tableName = 'Payment Methods';

		await (await this.generalCommerceAdminChannelTableLink(name)).click();

		await this.sidePanelFrameLocator
			.getByLabel('Description', {exact: true})
			.fill(description);

		if (!(await this.isActive.isChecked())) {
			await this.isActive.check();
		}

		await (await this.frameSaveButton(false, tableName)).click();

		await waitForAlert(await this.sidePanelFrame(tableName));

		await (await this.closeSidePanelFrame(false, tableName)).click();
	}

	async deactivateChannelConfiguration(name: string, tableName: string) {
		const isActiveCheckbox = await this.isActive;

		await (await this.generalCommerceAdminChannelTableLink(name)).click();

		if (await isActiveCheckbox.isChecked()) {
			await isActiveCheckbox.uncheck();
		}

		await (await this.frameSaveButton(false, tableName)).click();
		await waitForAlert(await this.sidePanelFrame(tableName));
		await (await this.closeSidePanelFrame(false, tableName)).click();
	}

	async addFlatRateShippingOption(
		name: string,
		amount?: string,
		description?: string,
		priority?: string
	) {
		const tableName = 'Shipping Methods';
		await (
			await this.generalCommerceAdminChannelTableLink('Flat Rate')
		).click();
		await (await this.shippingOptionsTab(tableName)).click();
		await (await this.sidePanelFrameAddButton(tableName)).click();
		await (
			await this.sidePanelNestedFrameInput('Name', tableName)
		).fill(name);
		if (description) {
			await (
				await this.sidePanelNestedFrameInput('Description', tableName)
			).fill(description);
		}
		if (amount) {
			await (
				await this.sidePanelNestedFrameInput('Amount', tableName)
			).fill(amount);
		}
		if (priority) {
			await (
				await this.sidePanelNestedFrameInput('Priority', tableName)
			).fill(priority);
		}
		await (
			await this.sidePanelNestedFrameInput('Key', tableName)
		).fill(name);
		await (await this.frameSaveButton(true, tableName)).click();
		await waitForAlert(await this.sidePanelNestedFrame('Shipping Methods'));
		await (
			await this.closeSidePanelFrame(true, 'Shipping Methods')
		).click();
		await (
			await this.closeSidePanelFrame(false, 'Shipping Methods')
		).click();
	}

	async addVariableRateShippingOption(name: string) {
		const tableName = 'Shipping Methods';
		await (
			await this.generalCommerceAdminChannelTableLink('Variable Rate')
		).click();
		(await this.shippingOptionsTab(tableName)).click();
		await (await this.sidePanelFrameAddButton(tableName)).click();
		await (
			await this.sidePanelNestedFrameInput('Name', tableName)
		).fill(name);
		await (
			await this.sidePanelNestedFrameInput('Key', tableName)
		).fill(name);
		await (await this.frameSaveButton(true, tableName)).click();
		await waitForAlert(await this.sidePanelNestedFrame('Shipping Methods'));
		await (
			await this.closeSidePanelFrame(true, 'Shipping Methods')
		).click();
		await (
			await this.closeSidePanelFrame(false, 'Shipping Methods')
		).click();
	}

	async addVariableRateShippingOptionSetting(
		optionName: string,
		subtotalPercentagePrice?: string,
		fixedPrice?: string,
		weightTo?: string
	) {
		const tableName = 'Shipping Methods';
		await (
			await this.generalCommerceAdminChannelTableLink('Variable Rate')
		).click();
		await (await this.shippingOptionsTab(tableName)).click();
		await (
			await this.shippingOptionsTableLink(optionName, tableName)
		).click();
		await (await this.detailsButton(tableName)).click();
		await (await this.shippingOptionSettingsTab(tableName)).click();
		await (await this.sidePanelNestedFrame(tableName))
			.getByText('Add Shipping Option Setting')
			.click();

		if (subtotalPercentagePrice) {
			await (
				await this.sidePanelNestedFrameInput(
					'Subtotal Percentage Price',
					tableName,
					2
				)
			).fill(subtotalPercentagePrice);
		}

		if (fixedPrice) {
			await (await this.sidePanelNestedFrame(tableName, 2))
				.getByLabel('Fixed Price', {exact: true})
				.fill(fixedPrice);
		}

		if (weightTo) {
			await (
				await this.sidePanelNestedFrameInput('Weight To', tableName, 2)
			).fill(weightTo);
		}

		await (await this.frameSaveButton(true, tableName, 2)).click();
		await waitForAlert(
			await this.sidePanelNestedFrame('Shipping Methods', 2)
		);
		await (
			await this.closeSidePanelFrame(true, 'Shipping Methods', 2)
		).click();
	}

	async addFixedTaxRate(amount: string, name: string, percentage = false) {
		const tableName = 'Tax Calculations';

		await (
			await this.generalCommerceAdminChannelTableLink('Fixed Tax Rate')
		).click();

		await expect(await this.activeToggle(tableName)).toBeVisible();

		await (await this.activeToggle(tableName)).check();

		if (percentage) {
			await (await this.sidePanelFrame(tableName))
				.getByLabel('Percentage')
				.check();
		}

		await (await this.frameSaveButton(false, tableName)).click();

		await (await this.taxRatesTab(tableName)).click();
		await (await this.addTaxRateButton(tableName)).click();

		await expect(this.taxCategoryChoiceBox).toBeVisible();

		await this.taxCategoryChoiceBox.selectOption(name);
		await this.addTaxRateFrame
			.getByLabel(percentage ? 'Rate' : 'Amount')
			.fill(amount);
		await this.taxRateFrameSubmitButton.click();

		await this.page.reload();

		await expect(
			await this.generalCommerceAdminChannelTableLink('Fixed Tax Rate')
		).toBeVisible();
	}

	async addByAddressTaxRate(
		amount: string,
		country: string,
		name: string,
		region: string,
		zip: string
	) {
		const tableName = 'Tax Calculations';

		await (
			await this.generalCommerceAdminChannelTableLink('By Address')
		).click();

		await expect(await this.activeToggle(tableName)).toBeVisible();

		await (await this.activeToggle(tableName)).check();
		await (await this.frameSaveButton(false, tableName)).click();

		await waitForAlert(this.sidePanelFrameLocator);

		await (await this.taxRateSettingsTab(tableName)).click();
		await (await this.addTaxRateSettingButton(tableName)).click();

		await expect(this.byAddressTaxCategoryChoiceBox).toBeVisible();

		await this.byAddressTaxCategoryChoiceBox.selectOption(name);
		const amountField = this.addTaxRateSettingsFrame.getByLabel('Amount');

		await amountField.fill(amount);

		await this.byAddressCountryChoiceBox.focus();

		await expect(this.byAddressCountryChoiceBox).toBeFocused();

		await this.byAddressCountryChoiceBox.selectOption({label: country});

		await this.byAddressRegionChoiceBox.focus();

		await expect(this.byAddressRegionChoiceBox).toBeFocused();

		await this.byAddressRegionChoiceBox.selectOption({label: region});

		const zipField = this.addTaxRateSettingsFrame.getByLabel('Zip');

		await zipField.fill(zip);

		await this.taxRateFrameSettingsSubmitButton.click();

		await expect(
			await this.generalCommerceAdminChannelTableLink('By Address')
		).toBeVisible();
	}

	async setValidationMode(validationMode: string) {
		await this.accountEntryValidationModeSelect.selectOption(
			validationMode
		);

		await expect(this.accountEntryValidationModeSelect).toHaveValue(
			validationMode
		);

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async setValidationModeAsAdmin(
		channelName: string,
		validationMode: string
	) {
		await performLogout(this.page);
		await performLoginViaApi({page: this.page, screenName: 'test'});

		await this.goto();
		await this.channelNameLink(channelName).click();

		await this.setValidationMode(validationMode);
	}

	async changeChannelDefaultCurrency(currency: string) {
		await this.channelCurrencySelect.selectOption(currency);

		await expect(this.channelCurrencySelect).toHaveValue(currency);

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async editFixedTaxRate(newAmount: string, name: string) {
		const tableName = 'Tax Calculations';

		await (
			await this.generalCommerceAdminChannelTableLink('Fixed Tax Rate')
		).click();
		await (await this.taxRatesTab(tableName)).click();

		await (await this.sidePanelFrameActionsButton(tableName, name)).click();
		await (await this.sidePanelFrameEditMenuItem(tableName)).click();

		await (
			await this.sidePanelNestedFrameAmountInput(tableName)
		).fill(newAmount);
		await (await this.frameSaveButton(true, tableName)).click();

		await this.page.reload();

		await expect(
			await this.generalCommerceAdminChannelTableLink('Fixed Tax Rate')
		).toBeVisible();
	}

	async editByAddressTaxRate(newAmount: string, name: string) {
		const tableName = 'Tax Calculations';

		await (
			await this.generalCommerceAdminChannelTableLink('By Address')
		).click();
		await (await this.taxRateSettingsTab(tableName)).click();

		await (await this.sidePanelFrameActionsButton(tableName, name)).click();
		await (await this.sidePanelFrameEditMenuItem(tableName)).click();

		await (
			await this.sidePanelNestedFrameAmountInput(tableName)
		).fill(newAmount);
		await (await this.frameSaveButton(true, tableName)).click();

		await this.page.reload();

		await expect(
			await this.generalCommerceAdminChannelTableLink('By Address')
		).toBeVisible();
	}

	async setEntryEligibility(
		eligibilityOption: string,
		entryName: string,
		tableName: string,
		shippingOption?: string
	) {
		let isNestedFrame: boolean;

		if (tableName === 'Payment Methods') {
			isNestedFrame = false;

			await (await this.eligibilityTab(isNestedFrame, tableName)).click();

			const eligibilityButton = await this.eligibilityOptionButton(
				eligibilityOption,
				isNestedFrame,
				tableName
			);

			await expect(eligibilityButton).toBeVisible();

			await eligibilityButton.click();

			const placeholderInput = await this.placeHolderTerm(
				isNestedFrame,
				tableName,
				'Find a Payment Term'
			);

			await expect(placeholderInput).toBeVisible();

			await placeholderInput.fill(entryName);

			await (await this.selectButton(isNestedFrame, tableName)).click();
			await (
				await this.frameSaveButton(isNestedFrame, tableName)
			).click();
			await waitForAlert(await this.sidePanelFrame(tableName));
			await (
				await this.closeSidePanelFrame(isNestedFrame, tableName)
			).click();
		}
		else if (tableName === 'Shipping Methods') {
			isNestedFrame = true;

			await (await this.shippingOptionsTab(tableName)).click();
			await (
				await this.shippingOptionsTableLink(shippingOption, tableName)
			).click();
			await (await this.detailsButton(tableName)).click();
			await (await this.eligibilityTab(isNestedFrame, tableName)).click();

			const eligibilityButton = await this.eligibilityOptionButton(
				eligibilityOption,
				isNestedFrame,
				tableName
			);

			await expect(eligibilityButton).toBeVisible();

			await eligibilityButton.click();

			const placeholderText =
				eligibilityOption === 'Specific Order Types'
					? 'Find an Order Type'
					: 'Find a Delivery Term';

			const placeholderInput = await this.placeHolderTerm(
				isNestedFrame,
				tableName,
				placeholderText
			);

			await expect(placeholderInput).toBeVisible();

			await placeholderInput.fill(entryName);

			await (await this.selectButton(isNestedFrame, tableName)).click();
			await (
				await this.frameSaveButton(isNestedFrame, tableName)
			).click();
			await waitForAlert(await this.sidePanelNestedFrame(tableName));
			await (
				await this.closeSidePanelFrame(isNestedFrame, tableName)
			).click();
		}
	}

	async goto() {
		await this.globalMenuPage.goToCommerce('Channels');
	}

	async goToCategoryDisplayPages() {
		await this.categoryDisplayPageTab.click();
	}

	async goToTab(tabName: string) {
		await this.linkTab(tabName).click();
	}

	async goToCountries() {
		await this.countryTab.click();
	}

	async goToCurrencies() {
		await this.currencyTab.click();
	}

	async setShippingMethodTrackingURL(
		name: string,
		trackingURL: string,
		tableName: string
	) {
		await (await this.generalCommerceAdminChannelTableLink(name)).click();
		await (
			await this.sidePanelFrameInput('Tracking URL', tableName)
		).fill(trackingURL);
		await (await this.frameSaveButton(false, tableName)).click();

		await waitForAlert(await this.sidePanelFrame(tableName));

		await (await this.closeSidePanelFrame(false, tableName)).click();
	}
}
