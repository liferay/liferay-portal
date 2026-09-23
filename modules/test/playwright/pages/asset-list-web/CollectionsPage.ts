/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

type FilterCondition = {
	field: string;
	fieldGroup?: string;
	operator?: string;
	quantifier?: string;
	value?: string;
};

export class CollectionsPage {
	readonly conditionBuilder: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.conditionBuilder = page.locator('.condition-builder');
		this.page = page;
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.collections}`
		);

		await this.page.waitForLoadState('networkidle');
	}

	/**
	 * Creates a dynamic collection with the given name.
	 */
	async createWebContentDynamicCollection(name: string, siteUrl: string) {
		await this.addNewDynamicCollection(name);

		await this.configureSourceItemType({
			itemSubtype: 'Basic Web Content',
			itemType: 'Web Content Article',
		});

		await this.save();

		return {
			classPK: await this.getCollectionClassPK(name, siteUrl),
		};
	}

	/**
	 * Adds a dynamic or manual collection with a given name.
	 */
	async addNewCollection(name: string, isDynamic: boolean) {
		await this.page
			.locator('.creation-menu')
			.getByRole('button', {name: 'New'})
			.first()
			.click();

		const collectionType = isDynamic
			? 'Dynamic Collection'
			: 'Manual Collection';

		await this.page.getByRole('menuitem', {name: collectionType}).click();

		await this.page.getByPlaceholder('Title').fill(name);

		await this.save();
	}

	/**
	 * Add a dynamic collection with the given name.
	 */
	async addNewDynamicCollection(name: string) {
		await this.addNewCollection(name, true);
	}

	/**
	 * Add a manual collection with the given name.
	 */
	async addNewManualCollection(name: string) {
		await this.addNewCollection(name, false);
	}

	/**
	 * Opens a collection from the Collections list for editing.
	 */
	async openCollection(name: string) {
		await this.page.getByRole('link', {name}).click();

		await this.page.waitForLoadState('networkidle');
	}

	/**
	 * On a collection's edit page, adds a personalized variation for the given
	 * segment. The segment picker renders inside a modal iframe.
	 */
	async addPersonalizedVariation(segmentName: string) {
		const newPersonalizedVariationDialog = this.page
			.getByRole('dialog')
			.filter({hasText: 'New Personalized Variation'});

		await clickAndExpectToBeVisible({
			target: newPersonalizedVariationDialog,
			trigger: this.page.getByRole('button', {
				name: 'Add Personalized Variation',
			}),
		});

		await newPersonalizedVariationDialog
			.frameLocator('iframe')
			.getByText(segmentName)
			.click();

		await waitForAlert(this.page);
	}

	/**
	 * On a collection's edit page, deprioritizes the given variation through its
	 * actions menu in the personalized variations panel.
	 */
	async deprioritizeVariation(variationTitle: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('button', {name: 'Deprioritize'}),
			trigger: this.page.getByRole('button', {
				name: `Actions for ${variationTitle}`,
			}),
		});
	}

	async deleteCollection(name: string) {
		const menuButton = this.page
			.getByRole('row', {name})
			.getByLabel('Show Actions')
			.first();

		await menuButton.scrollIntoViewIfNeeded();

		await menuButton.click();

		await this.page.getByRole('menuitem', {name: 'Delete'}).click();

		await this.page
			.getByRole('button', {
				exact: true,
				name: 'Delete',
			})
			.click();

		await waitForAlert(this.page);

		await this.page.waitForLoadState('networkidle');
	}

	async renameCollection(oldName: string, newName: string) {
		const menuButton = this.page
			.getByRole('row', {name: oldName})
			.getByLabel('Show Actions')
			.first();

		await menuButton.scrollIntoViewIfNeeded();

		await menuButton.click();

		await this.page.getByRole('menuitem', {name: 'Rename'}).click();

		await this.page.getByPlaceholder('Title').fill(newName);

		await this.save();
	}

	/**
	 * Restricts the collection to multiple item types by choosing "Select
	 * Types" and moving the given types out of the "In Use" list. Call `save`
	 * to persist it.
	 */
	async restrictSourceItemTypes(excludedTypes: string[]) {
		await this.page
			.getByRole('combobox', {name: 'Item Type'})
			.selectOption({label: 'Select Types'});

		const itemTypesBox = this.page.locator('[id$="classNamesBoxes"]');

		const inUseList = itemTypesBox.getByLabel('In Use', {exact: true});

		for (const excludedType of excludedTypes) {
			await inUseList.selectOption({label: excludedType});

			await itemTypesBox
				.getByRole('button', {
					name: /move selected items from in use to available/i,
				})
				.click();
		}
	}

	/**
	 * Configures the collection to a single item type (and optional subtype).
	 * Call `save` to persist it.
	 */
	async configureSourceItemType({
		itemSubtype,
		itemType,
	}: {
		itemSubtype?: string;
		itemType: string;
	}) {
		await this.page
			.getByRole('combobox', {name: 'Item Type'})
			.selectOption({label: itemType});

		if (itemSubtype) {
			const subtypeSelect = this.page
				.locator('.asset-subtype:not(.hide)')
				.getByLabel('Item Subtype');

			await subtypeSelect.waitFor();

			await subtypeSelect.selectOption({label: itemSubtype});
		}
	}

	/**
	 * Saves the collection, from its edit page or from the dialog that creates
	 * or renames it, and waits for the confirmation alert.
	 */
	async save() {
		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	/**
	 * On a collection's edit page, picks one of the two ordering columns. Call
	 * `save` to persist it. Only available with the LPD-74731 feature flag
	 * enabled, which replaces the ordering selects with pickers fed by the item
	 * type's properties.
	 */
	async setOrderByColumn({
		column,
		field,
		fieldGroup,
	}: {
		column: 'And Then By' | 'Order By';
		field: string;
		fieldGroup: string;
	}) {
		const picker = this.page.getByLabel(column);

		await clickAndExpectToBeVisible({
			target: picker,
			trigger: this.page.getByRole('button', {
				exact: true,
				name: 'Ordering',
			}),
		});

		await picker.click();

		// As in the filter, a field label only identifies a property within its
		// group.

		await this.page
			.getByRole('group', {name: fieldGroup})
			.getByRole('option', {exact: true, name: field})
			.click();
	}

	/**
	 * On a collection's edit page, adds one condition per entry to the Filter
	 * section. Call `save` to persist them. Only available with the LPD-74731
	 * feature flag enabled, which replaces the tags and categories rules with
	 * the condition builder.
	 */
	async addFilterConditions(conditions: Array<FilterCondition>) {
		await this.openFilterSection();

		for (const [index, condition] of conditions.entries()) {
			if (index) {
				await this.addFilterRow();
			}

			await this.fillFilterCondition(index, condition);
		}
	}

	/**
	 * On a collection's edit page, appends an empty row to the Filter section's
	 * condition builder.
	 */
	async addFilterRow() {
		await this.page.getByRole('button', {name: 'Add Filter'}).click();
	}

	/**
	 * On a collection's edit page, the nth row of the Filter section's condition
	 * builder, which scopes the row's own pickers.
	 */
	getFilterConditionRow(index: number) {
		return this.conditionBuilder
			.locator('.condition-builder__row')
			.nth(index);
	}

	/**
	 * On a collection's edit page, fills the nth row of the Filter section. Every
	 * part after the field is optional.
	 */
	async fillFilterCondition(
		index: number,
		{field, fieldGroup, operator, quantifier, value}: FilterCondition
	) {
		const row = this.getFilterConditionRow(index);

		await row.getByLabel('Field').click();

		const fieldOption = fieldGroup
			? this.page
					.getByRole('group', {name: fieldGroup})
					.getByRole('option', {exact: true, name: field})
			: this.page.getByRole('option', {exact: true, name: field});

		await fieldOption.click();

		// Select Operator + Quantifier

		for (const [label, option] of [
			['Operator', operator],
			['Quantifier', quantifier],
		]) {
			if (!option) {
				continue;
			}

			await row.getByLabel(label as string).click();

			await this.page
				.getByRole('option', {exact: true, name: option})
				.click();
		}

		// Provide Value (if present)

		if (value !== undefined) {
			await row.getByLabel('Value').fill(value);
		}
	}

	/**
	 * On a collection's edit page, opens the Filter section and waits for its
	 * condition builder to be on screen.
	 */
	async openFilterSection() {
		await clickAndExpectToBeVisible({
			target: this.conditionBuilder,
			trigger: this.page.getByRole('button', {
				exact: true,
				name: 'Filter',
			}),
		});
	}

	/**
	 * On a collection's edit page, restricts the collection's scope to a Space
	 * through the Scope section. Call `save` to persist it.
	 */
	async scopeToSpace(spaceName: string) {
		const selectSiteButton = this.page.getByRole('button', {
			name: 'Select Site',
		});

		await clickAndExpectToBeVisible({
			target: selectSiteButton,
			trigger: this.page.getByRole('button', {
				exact: true,
				name: 'Scope',
			}),
		});

		await selectSiteButton.click();

		await this.page
			.getByRole('menuitem', {name: 'Other Site, Asset Library, or'})
			.click();

		const scopeFrame = this.page
			.locator('iframe[title="Scope"]')
			.contentFrame();

		await scopeFrame.getByRole('link', {name: 'Spaces'}).click();

		await scopeFrame
			.getByRole('link', {exact: true, name: spaceName})
			.click();
	}

	/**
	 * On a collection's edit page, opens the View Items modal for the default
	 * variation and returns the frame that lists the resolved items.
	 */
	async openViewItems() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'View Items'}),
			trigger: this.page.getByRole('button', {name: 'Show Actions'}),
		});

		const viewItemsIframe = this.page.locator('iframe[title="View Items"]');

		await viewItemsIframe.waitFor();

		return viewItemsIframe.contentFrame();
	}

	/**
	 * On a manual collection's edit page, clicks "Select" to open the asset
	 * entries item selector modal and returns the modal dialog locator.
	 */
	async openSelectItemsModal() {
		await this.page.getByRole('button', {name: 'Select Items'}).click();

		const modal = this.page.getByRole('dialog');

		await modal.waitFor();

		return modal;
	}

	/**
	 * On a manual collection's edit page, opens the asset entries item selector
	 * modal, checks the given assets, and confirms the selection.
	 */
	async selectAssets(assetTitles: string[]) {
		const selectItemsModal = await this.openSelectItemsModal();

		for (const assetTitle of assetTitles) {
			await selectItemsModal
				.getByRole('checkbox', {name: `Select ${assetTitle}`})
				.check();
		}
		await selectItemsModal
			.locator('.modal-footer')
			.getByRole('button', {exact: true, name: 'Select'})
			.click();
	}

	/**
	 * Gets the collection classPK.
	 */
	async getCollectionClassPK(name: string, siteUrl: string) {
		await this.goto(siteUrl);

		const classPK = await this.page
			.locator(`button[data-assetlistentrytitle="${name}"]`)
			.first()
			.evaluate((event) => event.dataset.assetlistentryid);

		return Number(classPK);
	}
}
