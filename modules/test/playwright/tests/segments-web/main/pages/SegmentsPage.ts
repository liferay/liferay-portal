/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import fillAndClickOutside from '../../../../utils/fillAndClickOutside';

export class SegmentsPage {
	readonly page: Page;

	readonly closeButton: Locator;
	readonly criterionLabel: Locator;
	readonly deleteButton: Locator;
	readonly editButton: Locator;
	readonly newSegmentButton: Locator;
	readonly panelLocator: Locator;
	readonly plusButton: Locator;
	readonly saveButton: Locator;
	readonly selectButton: Locator;
	readonly toggleButton: Locator;
	readonly viewMembersButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.closeButton = page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true});
		this.criterionLabel = page.locator('span.criterion-string');
		this.deleteButton = page.getByRole('menuitem', {name: 'Delete'});
		this.editButton = page.getByRole('menuitem', {name: 'Edit'});
		this.newSegmentButton = page.getByRole('button', {
			name: 'Add New User Segment',
		});
		this.panelLocator = page.locator('.side-panel.edit-mode');
		this.plusButton = page
			.locator('[data-qa-id="creationMenuNewButton"]')
			.locator('.lexicon-icon-plus');
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.selectButton = page.getByRole('button', {
			exact: true,
			name: 'Select',
		});
		this.toggleButton = page.locator(
			'button[aria-label="Enter Edit Mode"][title="Enter Edit Mode"]'
		);
		this.viewMembersButton = page.getByRole('button', {
			name: 'View Members',
		});
	}

	segmentLocator(segmentName: string): Locator {
		return this.page.locator('tr', {has: this.page.getByText(segmentName)});
	}

	async addSegmentField(
		criterion: string,
		property: string,
		segmentName: string
	) {
		const dropzone = this.page.locator(`.drop-zone-root`);
		const target =
			(await dropzone.count()) === 0
				? this.page.locator('.empty-drop-zone')
				: dropzone.last();

		await this.page.getByRole('button', {name: property}).click();

		await this.page.getByLabel(`Drag ${criterion}`).press('Enter');

		await target.press('Enter');

		await fillAndClickOutside(
			this.page,
			this.page.getByPlaceholder('Untitled Segment'),
			segmentName
		);
	}

	async assertErrorMessageIsVisible(
		message: string,
		errorTitle: string = 'Error'
	) {
		const errorTitleLocator = this.page.getByText(errorTitle, {
			exact: true,
		});
		const errorMessageLocator = this.page.getByText(message);

		await expect(errorTitleLocator).toBeVisible();
		await expect(errorMessageLocator).toBeVisible();
	}

	async changeCriterionInput(value: string) {
		const criterionOperator = this.page.locator('.edit-container select');

		await criterionOperator.selectOption(value);
	}

	async chooseLogic(logicType: 'And' | 'Or') {
		if (logicType !== 'And' && logicType !== 'Or') {
			throw new Error("Invalid logic type. Please choose 'And' or 'Or'.");
		}

		const logicButton = this.page.getByRole('button', {name: 'And'});
		const logicMenuitem = this.page.getByRole('menuitem', {
			exact: true,
			name: logicType,
		});

		await logicButton.waitFor({state: 'visible'});
		await logicButton.click();

		await logicMenuitem.waitFor({state: 'visible'});
		await logicMenuitem.click();
	}

	async clickDuplicateButton() {
		const duplicateButton = this.page.getByRole('button', {
			name: 'Duplicate Segment Property',
		});
		await duplicateButton.click();
	}

	async clickLinkByText(linkName: string) {
		const linkLocator = this.page.locator(`a:has-text('${linkName}')`);
		await linkLocator.click();
	}

	async clickAddNewSegmentButton() {
		await this.page
			.getByRole('link', {name: 'Add New User Segment'})
			.click();
	}

	async clickToggleButton() {
		await expect(this.toggleButton).toBeVisible();
		await expect(this.toggleButton).toBeEnabled();
		await this.toggleButton.click();
	}

	async closePanel() {
		const isPanelVisible = await this.panelLocator.isVisible();
		if (isPanelVisible) {
			await this.clickToggleButton();
			await expect(this.panelLocator).toBeHidden({timeout: 10000});
		}
	}

	async deleteAllSegmentEntries() {
		const noSegmentsText = await this.page.locator(
			'text=There are no segments.'
		);
		const isNoSegmentsVisible = await noSegmentsText.isVisible();

		if (isNoSegmentsVisible) {
			return;
		}

		await this.page.getByLabel('Select All Items on the Page').click();
		await this.page.getByRole('button', {name: 'Delete'}).click();
	}

	async deleteProperty() {
		const deleteButton = this.page.getByTitle('Delete Segment Property');
		await deleteButton.click();
	}

	async deleteUnavailableProperty() {
		const deleteButton = this.page.getByText('Delete Segment Property');
		await deleteButton.click();
	}

	async deleteSegment(segmentName: string) {
		const showMoreOptionsButton = this.page.getByLabel(
			`Show More Options for ${segmentName}`
		);

		await showMoreOptionsButton.waitFor({state: 'visible'});
		await showMoreOptionsButton.click();

		await this.deleteButton.waitFor({state: 'visible'});
		await this.deleteButton.click();
	}

	async editSegmentsEntry(name: string) {
		const showMoreOptionsButton = this.page.getByLabel(
			`Show More Options for ${name}`
		);

		await showMoreOptionsButton.waitFor({state: 'visible'});
		await showMoreOptionsButton.click();

		await this.editButton.waitFor({state: 'visible'});
		await this.editButton.click();
	}

	async fillField(value: string) {
		const fieldLocator = this.page.locator(
			'input[data-testid="simple-string"]'
		);

		await expect(fieldLocator).toBeVisible();
		await expect(fieldLocator).toBeEnabled();

		await fieldLocator.fill(value);

		await expect(fieldLocator).toHaveValue(value);
	}

	async openPanel() {
		const isPanelVisible = await this.panelLocator.isVisible();
		if (!isPanelVisible) {
			await this.clickToggleButton();
			await expect(this.panelLocator).toBeVisible({timeout: 10000});
		}
	}

	async selectAndScrollToProperty(
		tabName: 'User' | 'Organization' | 'Session',
		propertyName: string
	) {
		const tabLocator = this.page.getByRole('button', {
			exact: true,
			name: tabName,
		});
		const propertyLocator = this.page.getByText(propertyName);
		const isPropertyVisible = await propertyLocator.isVisible();

		if (!isPropertyVisible) {
			await tabLocator.click();
		}

		await propertyLocator.scrollIntoViewIfNeeded();
	}

	async selectCheckboxItem(itemName: string) {
		const iframeSelector = 'iframe#selectEntity_iframe_';
		await this.page.waitForSelector(iframeSelector);

		const iframe = this.page.frameLocator(iframeSelector);

		let rowLocator = iframe.locator('tr').filter({
			has: iframe.locator('td.lfr-name-column').filter({
				hasText: new RegExp(`^\\s*${itemName}\\s*$`),
			}),
		});

		if ((await rowLocator.count()) === 0) {
			rowLocator = iframe.locator('tr').filter({
				has: iframe.locator('td.lfr-title-column').filter({
					hasText: new RegExp(`^\\s*${itemName}\\s*$`),
				}),
			});
		}

		const checkbox = rowLocator.locator('input[type="checkbox"]');
		await checkbox.waitFor({state: 'visible'});
		await checkbox.click();

		const modalSelectButton = this.page.locator('.btn-primary', {
			hasText: 'Select',
		});
		await modalSelectButton.click();
	}

	async selectOption(optionName: string) {
		const optionSelectLocator = this.page.locator(
			'select[data-testid="options-string"]'
		);

		await expect(optionSelectLocator).toBeVisible();
		await expect(optionSelectLocator).toBeEnabled();

		await optionSelectLocator.selectOption({label: optionName});

		await this.saveButton.click();
	}

	async selectEntry(entryName: string) {
		const iframe = this.page.frameLocator('iframe#selectEntity_iframe_');

		const titleColumnLocator = iframe.locator('td.lfr-title-column', {
			hasText: entryName,
		});

		const nameColumnLocator = iframe.locator('td.lfr-name-column', {
			hasText: entryName,
		});

		await this.page.waitForLoadState('networkidle');
		await this.page.waitForTimeout(5000);

		let targetElement: Locator;

		if (await titleColumnLocator.first().isVisible()) {
			targetElement = titleColumnLocator.first();
		}
		else if (await nameColumnLocator.first().isVisible()) {
			targetElement = nameColumnLocator.first();
		}

		if (targetElement) {
			await targetElement.click();
		}
	}

	async selectCardItem(itemName: string) {
		const iframeSelector = 'iframe#selectEntity_iframe_';
		await this.page.waitForSelector(iframeSelector);

		const iframe = this.page.frameLocator(iframeSelector);

		const candidateLocators = [
			iframe.locator('a.selector-button', {
				hasText: new RegExp(`^\\s*${itemName}\\s*$`),
			}),

			iframe.locator('p.card-title', {
				hasText: new RegExp(`^\\s*${itemName}\\s*$`),
			}),
		];

		for (const locator of candidateLocators) {
			if ((await locator.count()) > 0) {
				await locator.first().click();

				return;
			}
		}
	}

	async viewCriterionValue(value: string) {
		const criterionElement = this.page.locator(
			`span.criterion-string >> b:has-text('${value}')`
		);

		await criterionElement.waitFor({state: 'visible'});
		expect(criterionElement).toHaveText(value);
	}

	async viewFieldTypes(typeName: string) {
		const fieldTypeLocator = this.page.locator(
			`div.panel-unstyled#${typeName}`
		);

		await expect(fieldTypeLocator).toBeVisible();
	}

	async viewMemberCount(memberCount: string) {
		const memberCountLocator = this.page.locator('div.btn-group-item b');

		await expect(memberCountLocator).toHaveText(memberCount);
	}

	async viewSegmentsItemTable(segmentName: string) {
		const itemSegmentTable = this.page.locator(
			`a:has-text('${segmentName}')`
		);

		await expect(itemSegmentTable).toBeVisible();
	}

	async viewMembers({
		expectedEmail,
		expectedName,
	}: {
		expectedEmail?: string;
		expectedName?: string;
	}) {
		await this.viewMembersButton.click();

		const frame = this.page.frameLocator(
			'iframe#segment-members-dialog_iframe_'
		);

		let memberLocator;

		if (expectedEmail && expectedName) {
			memberLocator = frame
				.locator('tr', {
					has: frame.locator('td.lfr-email-address-column', {
						hasText: expectedEmail,
					}),
				})
				.filter({
					has: frame.locator('td.lfr-name-column', {
						hasText: expectedName,
					}),
				});
		}
		else if (expectedEmail) {
			memberLocator = frame.locator('tr', {
				has: frame.locator('td.lfr-email-address-column', {
					hasText: expectedEmail,
				}),
			});
		}
		else if (expectedName) {
			memberLocator = frame.locator('tr', {
				has: frame.locator('td.lfr-name-column', {
					hasText: expectedName,
				}),
			});
		}
		else {
			throw new Error(
				'Must provide either expectedEmail or expectedName'
			);
		}

		await expect(memberLocator).toBeVisible({timeout: 15000});

		if (expectedEmail) {
			await expect(
				memberLocator.locator('td.lfr-email-address-column')
			).toContainText(expectedEmail);
		}

		if (expectedName) {
			await expect(
				memberLocator.locator('td.lfr-name-column')
			).toContainText(expectedName);
		}

		await this.closeButton.click();
	}
}
