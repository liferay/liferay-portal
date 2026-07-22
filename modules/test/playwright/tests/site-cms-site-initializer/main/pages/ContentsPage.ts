/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

type SidePanelName =
	| 'Categorization'
	| 'General'
	| 'Comments'
	| 'Projects'
	| 'Schedule';

type Field =
	| {
			label: string;
			nth?: number;
			value: string;
	  }
	| {
			label: string;
			nth?: number;
			type: 'Rich Text';
			value: string;
	  }
	| {
			label: string;
			nth?: number;
			type: 'Checkbox';
			value: boolean;
	  }
	| {
			label: string;
			nth?: number;
			type: 'Picklist';
			value: string;
	  };

export class ContentsPage {
	readonly page: Page;

	readonly newButton: Locator;
	readonly previewButton: Locator;
	readonly publishButton: Locator;
	readonly apiHelpers: ApiHelpers;

	constructor(page: Page) {
		this.page = page;

		this.apiHelpers = new ApiHelpers(page);
		this.newButton = page.locator(
			'[data-testid="fdsCreationActionButton"]'
		);
		this.previewButton = page
			.locator('.content-editor__toolbar')
			.getByRole('button', {
				name: 'Preview',
			});
		this.publishButton = page
			.getByText('Publish', {exact: true})
			.or(page.getByText('Submit for Workflow', {exact: true}));
	}

	async goto() {
		await expect(async () => {
			await this.page.goto(PORTLET_URLS.cmsContents);

			await this.newButton.waitFor({state: 'visible', timeout: 3000});
		}).toPass();
	}

	async closeSidePanel() {
		const trigger = this.page.locator(
			'.content-editor__side-panel button[aria-selected="true"]'
		);

		if (trigger) {
			await clickAndExpectToBeHidden({
				target: trigger,
				trigger,
			});
		}
	}

	async createContent(type: string, space: string = 'Default') {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: type}),
			trigger: this.newButton,
		});

		// Wait for first of Content Editor Sidebar and Space Selector

		const first = await Promise.race([
			this.page
				.getByRole('tab', {name: 'General'})
				.waitFor({state: 'visible'})
				.then(() => 'content-editor-sidebar'),
			this.page
				.getByRole('dialog')
				.waitFor({state: 'visible'})
				.then(() => 'space-selector'),
		]);

		// If Space Selector is shown, select space

		if (first === 'space-selector') {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('option', {name: space}),
				trigger: this.page.getByRole('dialog').getByLabel('Space'),
			});

			await this.page.getByRole('button', {name: 'Save'}).click();
		}

		await this.page
			.locator('.cms-control-menu')
			.getByText('Edit')
			.or(this.page.locator('.cms-control-menu').getByText('New'))
			.waitFor();

		await this.page
			.locator('.loading-animation')
			.nth(0)
			.waitFor({state: 'hidden'});
	}

	async createFolder(folderName: string, spaceName?: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.newButton,
		});

		await this.page.getByRole('heading', {name: 'New Folder'}).waitFor();

		await this.page.getByLabel('NameRequired').fill(folderName);

		if (spaceName) {
			const spaceSelector = this.page.getByLabel('SpaceMandatory');

			if (await spaceSelector.isVisible()) {
				await spaceSelector.click();
				await this.page.getByRole('option', {name: spaceName}).click();
			}
		}

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page, `Success:${folderName} was created`);
	}

	async deleteContent(title: string, recycleBinEnabled: boolean = true) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}))
			.first();

		this.page.once('dialog', async (dialog) => {
			await dialog.accept();
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: card.locator('button'),
		});

		if (recycleBinEnabled) {
			await waitForAlert(this.page, `Success:${title} was moved`, {
				autoClose: false,
			});
		}
		else {
			await waitForAlert(
				this.page,
				`Success:${title} has been permanently deleted.`
			);
		}
	}

	async deleteFolder(folderName: string, recycleBinEnabled: boolean = true) {
		await this.page
			.locator('tr', {hasText: folderName})
			.locator('td.cell-item-actions')
			.getByRole('button')
			.click();

		await this.page.getByRole('menuitem', {name: 'Delete'}).click();

		if (recycleBinEnabled) {
			await waitForAlert(this.page, `Success:${folderName} was moved`, {
				autoClose: false,
			});
		}
		else {
			await this.page
				.getByRole('button', {name: 'Delete Folder'})
				.click();

			await waitForAlert(
				this.page,
				`Success:${folderName} has been permanently deleted.`
			);
		}
	}

	async editContent(title: string) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: card.locator('button'),
		});

		await this.openSidePanel('General');

		await this.closeSidePanel();
	}

	async fillData(fields: Field[]) {
		for (const field of fields) {
			const element = this.page
				.getByLabel(field.label)
				.nth(field.nth || 0);

			if (!('type' in field)) {
				await element.fill(field.value);
			}
			else if (field.type === 'Rich Text') {
				await element.getByRole('textbox').click();

				await this.page.keyboard.type(field.value);
			}
			else if (field.type === 'Checkbox') {
				await element.setChecked(field.value);
			}
			else if (field.type === 'Picklist') {
				await element.clear();
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: this.page.getByRole('option', {name: field.value}),
					trigger: element,
				});
			}
		}
	}

	async navigateTo(folderName: string) {
		await this.page
			.getByRole('row', {name: folderName})
			.getByRole('link')
			.click();

		await this.page.getByPlaceholder('Search').waitFor({state: 'visible'});
	}

	async openSchedulePublication() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Schedule Publication',
			}),
			trigger: this.page.getByTitle('Publish Options'),
		});
	}

	async openSidePanel(panelName: SidePanelName = 'General') {
		await clickAndExpectToBeVisible({
			target: this.page.locator('.sidebar-header', {hasText: panelName}),
			trigger: this.page.getByLabel(panelName),
		});
	}

	async saveContent() {
		await clickAndExpectToBeVisible({
			target: this.newButton,
			timeout: 5000,
			trigger: this.publishButton,
		});
	}

	async saveContentAsDraft() {
		await this.page
			.getByRole('button', {
				exact: true,
				name: 'Save as Draft',
			})
			.click();

		await waitForAlert(this.page, 'The draft was saved successfully');
	}

	async shareContent(title: string) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Share',
			}),
			trigger: card.locator('button'),
		});

		await expect(
			this.page.getByRole('dialog', {name: title})
		).toBeVisible();
	}

	async translateContent(title: string) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Translate'}),
			trigger: card.locator('button'),
		});

		await expect(
			this.page.locator('button[type="submit"]', {hasText: 'Publish'})
		).toBeVisible();
	}

	async viewContent(title: string) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'View',
			}),
			trigger: card.locator('button'),
		});

		await expect(
			this.page.getByRole('dialog', {name: title})
		).toBeVisible();
	}

	async viewShowDetails(title: string) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Show Details',
			}),
			trigger: card.locator('button'),
		});
	}
}
