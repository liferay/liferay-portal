/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../helpers/ApiHelpers';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {DataTablePage} from './DataTablePage';

export class EditAccountPage {
	readonly accountEntryAddressesTable: DataTablePage;
	readonly accountEntryAddressesTableRowRadioButton: (
		accountName: string
	) => Promise<Locator>;
	readonly accountGroupsLink: Locator;
	readonly accountIdInput: Locator;
	readonly accountNameInput: Locator;
	readonly addDomainLink: Locator;
	readonly addDomainFrame: FrameLocator;
	readonly addressesTab: Locator;
	readonly assignUserMessage: Locator;
	readonly backButton: Locator;
	readonly categoryClearAllButton: Locator;
	readonly categoryLabel: (name: string) => Locator;
	readonly changeImageButton: Locator;
	readonly channelDefaultsLink: Locator;
	readonly contactLink: Locator;
	readonly customFieldInput: (name: string) => Locator;
	readonly defaultBillingAddress: (name: string) => Locator;
	readonly defaultShippingAddress: (name: string) => Locator;
	readonly descriptionInput: Locator;
	readonly detailsTab: Locator;
	readonly domainCell: (value: string) => Locator;
	readonly domainRemoveButton: (value: string) => Locator;
	readonly externalReferenceCodeInput: Locator;
	readonly frameDomainInput: Locator;
	readonly frameSaveButton: Locator;
	readonly imageInput: Locator;
	readonly organizationsLink: Locator;
	readonly setDefaultAddressFrame: FrameLocator;
	readonly setDefaultAddressFrameSaveButton: Locator;
	readonly page: Page;
	readonly personAccountUserContainer: Locator;
	readonly personAccountUserName: (name: string) => Locator;
	readonly personAccountUserRemoveButton: Locator;
	readonly personAccountUserSelectButton: Locator;
	readonly removeBillingDefaultAddressButton: Locator;
	readonly removeShippingDefaultAddressButton: Locator;
	readonly rolesLink: Locator;
	readonly saveButton: Locator;
	readonly selectCategoriesButton: (vocabularyName: string) => Locator;
	readonly selectTagsButton: Locator;
	readonly setBillingDefaultAddressButton: Locator;
	readonly setShippingDefaultAddressButton: Locator;
	readonly tagInput: (name: string) => Locator;
	readonly taxIDInput: Locator;
	readonly typeInput: Locator;
	readonly uploadImageSelectImageButton: Locator;
	readonly uploadImageDoneButton: Locator;
	readonly usersLink: Locator;
	readonly validDomainsHeading: Locator;
	readonly vocabularyLabel: (name: string) => Locator;

	constructor(page: Page) {
		this.setDefaultAddressFrame = page.frameLocator('iframe');
		this.accountEntryAddressesTable = new DataTablePage(
			this.setDefaultAddressFrame,
			this.setDefaultAddressFrame.locator(
				'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_accountEntryAddresses'
			)
		);
		this.accountGroupsLink = page.getByRole('link', {
			name: 'Account Groups',
		});
		this.accountEntryAddressesTableRowRadioButton = async (
			addressName: string
		) => {
			const accountEntriesTableRow =
				await this.accountEntryAddressesTable.row(0, addressName, true);

			if (accountEntriesTableRow && accountEntriesTableRow.row) {
				const accountEntryAddressRadioButton =
					accountEntriesTableRow.row.getByRole('radio');

				if (accountEntryAddressRadioButton) {
					return accountEntryAddressRadioButton;
				}
				else {
					throw new Error(
						`Cannot locate radio button in row of ${addressName}`
					);
				}
			}
			throw new Error(
				`Cannot locate address row with name ${addressName}`
			);
		};
		this.accountIdInput = page.getByLabel('Account ID');
		this.accountNameInput = page.getByLabel('Account Name');
		this.addDomainLink = page.locator(
			'[id="_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_addDomains"]'
		);
		this.addDomainFrame = page.frameLocator('iframe[title="Add Domain"]');
		this.addressesTab = page.getByRole('link', {
			name: 'Addresses',
		});
		this.assignUserMessage = page.getByText('Assign a user to this person');
		this.backButton = page
			.getByRole('link', {exact: true, name: 'Back'})
			.or(page.getByRole('link', {name: 'Return to Full Page'}));
		this.categoryClearAllButton = page.getByLabel('Clear All');
		this.categoryLabel = (name) =>
			page.getByLabel('Public Categories').getByText(name);
		this.changeImageButton = page.getByLabel('Change Image');
		this.channelDefaultsLink = page.getByRole('link', {
			exact: true,
			name: 'Channel Defaults',
		});
		this.contactLink = page.getByRole('link', {name: 'Contact'});
		this.customFieldInput = (name) =>
			page.locator(`.field[name*="${name}"]`);
		this.descriptionInput = page.getByLabel('Description');
		this.detailsTab = page.getByRole('link', {
			name: 'Details',
		});
		this.defaultBillingAddress = (name) => {
			return page.locator('address').first().getByText(name);
		};
		this.defaultShippingAddress = (name) => {
			return page.locator('address').last().getByText(name);
		};
		this.domainCell = (value) => {
			return page.getByRole('cell', {name: value});
		};
		this.domainRemoveButton = (value) => {
			return page
				.getByRole('row', {name: value})
				.getByRole('link', {name: 'Remove'});
		};
		this.externalReferenceCodeInput = page.getByLabel(
			'External Reference Code'
		);
		this.frameDomainInput = this.addDomainFrame.getByLabel('Domain');
		this.frameSaveButton = this.addDomainFrame.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.imageInput = page.getByLabel('Image', {exact: true});
		this.organizationsLink = page.getByRole('link', {
			exact: true,
			name: 'Organizations',
		});
		this.page = page;
		this.personAccountUserContainer = page.locator(
			'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_personAccountUserContainer'
		);
		this.personAccountUserName = (name) =>
			this.personAccountUserContainer.getByText(name);
		this.personAccountUserRemoveButton =
			this.personAccountUserContainer.getByRole('link', {name: 'Remove'});
		this.personAccountUserSelectButton =
			this.personAccountUserContainer.getByRole('link', {name: 'Select'});
		this.removeBillingDefaultAddressButton = page
			.locator('address')
			.first()
			.locator('..')
			.getByRole('link', {name: 'Remove'});
		this.removeShippingDefaultAddressButton = page
			.locator('address')
			.last()
			.locator('..')
			.getByRole('link', {name: 'Remove'});
		this.rolesLink = page.getByRole('link', {exact: true, name: 'Roles'});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.selectCategoriesButton = (vocabularyName: string) =>
			page
				.getByLabel(`Select ${vocabularyName}`)
				.and(page.getByRole('button'));
		this.selectTagsButton = page
			.getByLabel('Select Tags')
			.and(page.getByRole('button'));
		this.setBillingDefaultAddressButton = page
			.getByRole('link', {name: 'Set Default Address'})
			.or(page.getByRole('link', {name: 'Change'}))
			.locator('[data-type="billing"]:scope');
		this.setShippingDefaultAddressButton = page
			.getByRole('link', {name: 'Set Default Address'})
			.or(page.getByRole('link', {name: 'Change'}))
			.locator('[data-type="shipping"]:scope');
		this.setDefaultAddressFrameSaveButton = page
			.locator('[class$=btn-group-spaced]')
			.getByText('Save');
		this.tagInput = (name) => page.getByRole('row', {name});
		this.taxIDInput = page.getByLabel('Tax ID');
		this.typeInput = page.getByLabel('Type');
		this.uploadImageSelectImageButton = page
			.frameLocator('iframe[title="Upload Image"]')
			.getByLabel('Select Image');
		this.uploadImageDoneButton = page
			.frameLocator('iframe[title="Upload Image"]')
			.getByRole('button', {name: 'Done'});
		this.usersLink = page.getByRole('link', {exact: true, name: 'Users'});
		this.validDomainsHeading = page.getByRole('heading', {
			name: 'Valid Domains',
		});
		this.vocabularyLabel = (name) =>
			page
				.getByLabel('Public Categories')
				.getByText(name)
				.filter({has: page.locator('label:scope')});
	}

	async createAccount(
		apiHelpers: DataApiHelpers,
		{
			avatar = '',
			description = '',
			domains = [],
			externalReferenceCode = '',
			name = getRandomString(),
			taxID = getRandomString(),
			type = 'business',
		}: {
			avatar?: string;
			description?: string;
			domains?: string[];
			externalReferenceCode?: string;
			name?: string;
			taxID?: string;
			type?: string;
		}
	) {
		await this.accountNameInput.fill(name);
		await this.descriptionInput.fill(description);
		await this.taxIDInput.fill(taxID);
		await this.typeInput.selectOption(type);

		if (avatar) {
			const fileChooserPromise = this.page.waitForEvent('filechooser');

			await this.changeImageButton.click();
			await this.uploadImageSelectImageButton.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(avatar);
			await this.uploadImageDoneButton.click();
		}

		if (domains && domains.length) {
			await this.addDomainLink.click();

			await expect(this.frameDomainInput).toBeEnabled();

			await this.frameDomainInput.fill(domains.join());
			await this.frameSaveButton.click();
		}

		if (externalReferenceCode) {
			await this.externalReferenceCodeInput.fill(externalReferenceCode);
		}

		await this.saveButton.click();

		await waitForAlert(this.page);

		apiHelpers.data.push({
			id: await this.accountIdInput.inputValue(),
			type: 'account',
		});
	}
}
