/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

export class CommerceAdminProductDetailsPage {
	readonly addExistingSpecificationValueTextbox: Locator;
	readonly addSpecification: Locator;
	readonly addSpecificationFrame: FrameLocator;
	readonly backLink: Locator;
	readonly closeEditFrame: Locator;
	readonly createNewSpecificationProduct: Locator;
	readonly createNewValueSpecificationProduct: Locator;
	readonly dropdownProductSpecification: (
		chooseEditOrDelete: string
	) => Promise<Locator>;
	readonly editFrameSaveButton: Locator;
	readonly editFrameSpecificationProduct: (
		specificationValue: string
	) => Promise<string[]>;
	readonly editFrameSpecificationProductValue: Locator;
	readonly editSuccessMessage: Locator;
	readonly ellipsisProductSpecification: (
		specificationName: string
	) => Locator;
	readonly ellipsisFrameProductSpecification: FrameLocator;
	readonly frameChooseSpecification: (specificationName: string) => Locator;
	readonly frameChooseSpecificationValue: (
		specificationValue: string
	) => Promise<string[]>;
	readonly frameDropdownSpecification: Locator;
	readonly frameSubmitSpecification: Locator;
	readonly menuItemSpecification: (chooseAddOrCreate: string) => Locator;
	readonly nameInputLocaleSelector: Locator;
	readonly page: Page;
	readonly productConfigurationLink: Locator;
	readonly productDetailsInput: (inputName: string) => Promise<Locator>;
	readonly productDiagramLink: Locator;
	readonly productGroupsLink: Locator;
	readonly productId: Locator;
	readonly productMediaLink: Locator;
	readonly productOptionsLink: Locator;
	readonly productRelationsLink: Locator;
	readonly productSkusLink: Locator;
	readonly productVisibilityLink: Locator;
	readonly publishLink: Locator;
	readonly textTableCell: (text: string) => Locator;
	readonly visibleToggle: Locator;

	constructor(page: Page) {
		this.addSpecification = page
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.addSpecificationFrame = page.frameLocator('iframe >> nth=1');
		this.addExistingSpecificationValueTextbox =
			this.addSpecificationFrame.getByRole('textbox');
		this.backLink = page.locator('span[title="Back"]');
		this.closeEditFrame = page
			.frameLocator('iframe')
			.getByRole('button')
			.first();
		this.createNewSpecificationProduct =
			this.addSpecificationFrame.getByPlaceholder('Specification');
		this.createNewValueSpecificationProduct = this.addSpecificationFrame
			.getByRole('textbox')
			.nth(1);
		this.dropdownProductSpecification = async (
			chooseEditOrDelete: string
		) => {
			return page.getByRole('menuitem', {name: chooseEditOrDelete});
		};
		this.editFrameSpecificationProduct = async (
			specificationValue: string
		) => {
			return this.ellipsisFrameProductSpecification
				.locator(
					'select[name="_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_listTypeEntriesSelect"]'
				)
				.selectOption(specificationValue);
		};
		this.ellipsisProductSpecification = (specificationName: string) => {
			return page
				.locator('[data-testid="visualization-mode-table"]')
				.getByRole('button', {
					exact: true,
					name: `${specificationName} Actions`,
				});
		};
		this.ellipsisFrameProductSpecification = page.frameLocator('iframe');
		this.editFrameSaveButton =
			this.ellipsisFrameProductSpecification.getByRole('button', {
				name: 'Save',
			});
		this.editFrameSpecificationProductValue =
			this.ellipsisFrameProductSpecification.locator(
				'[id="_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_value"]'
			);
		this.editSuccessMessage =
			this.ellipsisFrameProductSpecification.getByText(
				'Success:Your request completed successfully.'
			);
		this.frameChooseSpecification = (specificationName: string) => {
			return this.addSpecificationFrame.getByRole('option', {
				name: specificationName,
			});
		};
		this.frameChooseSpecificationValue = async (
			specificationValue: string
		) => {
			return this.addSpecificationFrame
				.locator('select[name="listTypeEntriesSelect"]')
				.selectOption(specificationValue);
		};
		this.frameDropdownSpecification = this.addSpecificationFrame.getByLabel(
			'SpecificationRequired'
		);
		this.frameSubmitSpecification = this.addSpecificationFrame.getByRole(
			'button',
			{name: 'Submit'}
		);
		this.menuItemSpecification = (chooseAddOrCreate: string) => {
			return page.getByRole('menuitem', {name: chooseAddOrCreate});
		};
		this.nameInputLocaleSelector = page.locator(
			'[id$="nameMapAsXMLBoundingBox"] .input-localized-trigger'
		);
		this.page = page;
		this.productConfigurationLink = page.getByRole('link', {
			name: 'Configuration',
		});
		this.productDetailsInput = async (inputName: string) =>
			page.getByLabel(inputName);
		this.productDiagramLink = page.getByRole('link', {
			name: 'Diagram',
		});
		this.productGroupsLink = page.getByRole('link', {
			exact: true,
			name: 'Product Groups',
		});
		this.productId = page.locator('span:has-text("ID")+strong');
		this.productMediaLink = page.getByRole('link', {
			name: 'Media',
		});
		this.productOptionsLink = page.getByRole('link', {
			name: 'Options',
		});
		this.productRelationsLink = page.getByRole('link', {
			name: 'Product Relations',
		});
		this.productSkusLink = page.getByRole('link', {
			name: 'Skus',
		});
		this.productVisibilityLink = page.getByRole('link', {
			name: 'Visibility',
		});
		this.publishLink = page.getByRole('link', {name: 'Publish'});
		this.textTableCell = (text: string) =>
			this.page.getByRole('cell', {
				exact: true,
				name: text,
			});
		this.visibleToggle = this.ellipsisFrameProductSpecification.getByLabel(
			'Visible',
			{exact: true}
		);
	}

	async addExistingProductSpecification(
		chooseAddOrEdit: string,
		specificationName: string,
		specificationValue: string
	) {
		await this.addSpecification.click();
		await this.menuItemSpecification(chooseAddOrEdit).click();

		await expect(this.frameDropdownSpecification).toBeVisible();

		await this.frameDropdownSpecification.click();
		await this.frameChooseSpecification(specificationName).click();
		await this.addExistingSpecificationValueTextbox.fill(
			specificationValue
		);
		await this.frameSubmitSpecification.click();
	}

	async addOrEditProductSpecification(
		chooseAddOrEdit: string,
		specificationName: string,
		specificationValue?: string
	) {
		await this.addSpecification.click();
		await this.menuItemSpecification(chooseAddOrEdit).click();

		await expect(this.frameDropdownSpecification).toBeVisible();

		await this.frameDropdownSpecification.click();
		await this.frameChooseSpecification(specificationName).click();
		if (specificationValue) {
			await this.frameChooseSpecificationValue(specificationValue);
		}
		await this.frameSubmitSpecification.click();
	}

	async createSpecificationProduct(
		chooseAddOrCreate: string,
		specificationName: string,
		specificationValue?: string
	) {
		await this.addSpecification.click();
		await (await this.menuItemSpecification(chooseAddOrCreate)).click();
		await this.createNewSpecificationProduct.fill(specificationName);
		if (specificationValue) {
			await this.createNewValueSpecificationProduct.fill(
				specificationValue
			);
		}
		await this.frameSubmitSpecification.click();
	}

	async editOrDeleteProductSpecification(
		chooseEditOrDelete: string,
		specificationTitle: string,
		specificationValue: string
	) {
		await this.ellipsisProductSpecification(specificationTitle).click();
		await (
			await this.dropdownProductSpecification(chooseEditOrDelete)
		).click();
		await this.editFrameSpecificationProduct(specificationValue);
		await this.editFrameSaveButton.click();
	}

	async goToProductConfiguration() {
		await this.productConfigurationLink.click();
	}

	async goToProductDiagram() {
		await this.productDiagramLink.click();
	}

	async goToProductGroups() {
		await this.productGroupsLink.click();
	}

	async goToProductOptions() {
		await this.productOptionsLink.click();
	}

	async goToProductRelations() {
		await this.productRelationsLink.click();
	}

	async goToProductSkus() {
		await this.productSkusLink.click();
	}

	async goToProductVisibility() {
		await this.productVisibilityLink.click();
	}
}
