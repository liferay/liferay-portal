/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

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
	readonly ellipsisProductSpecification: Locator;
	readonly ellipsisFrameProductSpecification: FrameLocator;
	readonly frameChooseSpecification: (
		specificationName: string
	) => Promise<Locator>;
	readonly frameChooseSpecificationValue: (
		specificationValue: string
	) => Promise<string[]>;
	readonly frameDropdownSpecification: Locator;
	readonly frameSubmitSpecification: Locator;
	readonly menuItemSpecification: (
		chooseAddOrCreate: string
	) => Promise<Locator>;
	readonly page: Page;
	readonly productConfigurationLink: Locator;
	readonly productDetailsInput: (inputName: string) => Promise<Locator>;
	readonly productDiagramLink: Locator;
	readonly productMediaLink: Locator;
	readonly productOptionsLink: Locator;
	readonly productRelationsLink: Locator;
	readonly productSkusLink: Locator;
	readonly productVisibilityLink: Locator;
	readonly publishLink: Locator;
	readonly visibleToggle: Locator;

	constructor(page: Page) {
		this.addSpecification = page
			.getByTestId('management-toolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.addSpecificationFrame = page.frameLocator('iframe >> nth=2');
		this.addExistingSpecificationValueTextbox =
			this.addSpecificationFrame.getByRole('textbox');
		this.backLink = page.getByRole('link', {exact: true, name: 'Back'});
		this.closeEditFrame = page
			.frameLocator('iframe >> nth=1')
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
		this.ellipsisProductSpecification = page.getByRole('button', {
			name: 'Actions',
		});
		this.ellipsisFrameProductSpecification =
			page.frameLocator('iframe >> nth=1');
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
		this.frameChooseSpecification = async (specificationName: string) => {
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
		this.menuItemSpecification = async (chooseAddOrCreate: string) => {
			return page.getByRole('menuitem', {name: chooseAddOrCreate});
		};
		this.page = page;
		this.productConfigurationLink = page.getByRole('link', {
			name: 'Configuration',
		});
		this.productDetailsInput = async (inputName: string) =>
			page.getByLabel(inputName);
		this.productDiagramLink = page.getByRole('link', {
			name: 'Diagram',
		});
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
		this.visibleToggle = this.ellipsisFrameProductSpecification.getByLabel(
			'Visible',
			{exact: true}
		);
	}

	async addOrEditProductSpecification(
		chooseAddOrEdit: string,
		specificationName: string,
		specificationValue?: string
	) {
		await this.addSpecification.click();
		await (await this.menuItemSpecification(chooseAddOrEdit)).click();
		await this.frameDropdownSpecification.click();
		await (await this.frameChooseSpecification(specificationName)).click();
		if (specificationValue) {
			await this.frameChooseSpecificationValue(specificationValue);
		}
		await this.frameSubmitSpecification.click();
	}

	async addExistingProductSpecification(
		chooseAddOrEdit: string,
		specificationName: string,
		specificationValue: string
	) {
		await this.addSpecification.click();
		await (await this.menuItemSpecification(chooseAddOrEdit)).click();
		await this.frameDropdownSpecification.click();
		await (await this.frameChooseSpecification(specificationName)).click();
		await this.addExistingSpecificationValueTextbox.fill(
			specificationValue
		);
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
		specificationValue: string
	) {
		await this.ellipsisProductSpecification.click();
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
