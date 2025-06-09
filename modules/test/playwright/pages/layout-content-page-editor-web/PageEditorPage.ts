/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {collapseSection} from '../../utils/collapseSection';
import dragAndDropElement from '../../utils/dragAndDropElement';
import {expandSection} from '../../utils/expandSection';
import fillAndClickOutside from '../../utils/fillAndClickOutside';
import {hoverAndExpectToBeVisible} from '../../utils/hoverAndExpectToBeVisible';
import {selectElement} from '../../utils/selectElement';
import {waitForAlert} from '../../utils/waitForAlert';
import {SegmentEditorPage} from '../segments-web/SegmentEditorPage';

const VIEWPORTS_CLASSNAMES = {
	'Desktop': 'desktop',
	'Landscape Phone': 'landscapeMobile',
	'Portrait Phone': 'portraitMobile',
	'Tablet': 'tablet',
};

type MappingItemConfiguration = {
	entity: string;
	entry: string;
	entryLocator?: Locator;
	field: string;
	folder?: string;
};

type MappingConfiguration =
	| {
			mapping: MappingItemConfiguration;
			source?: 'content';
	  }
	| {
			mapping: {field: string};
			relationship: string;
			source: 'relationship';
	  }
	| {
			mapping: {field: string};
			source: 'structure';
	  };

export class PageEditorPage {
	readonly page: Page;

	readonly editModeButton: Locator;
	readonly experienceSelector: Locator;
	readonly languageSelector: Locator;
	readonly publishButton: Locator;
	readonly publishMasterButton: Locator;
	readonly publishToLiveButton: Locator;
	readonly redoButton: Locator;
	readonly segmentEditorPage: SegmentEditorPage;
	readonly selectItemMappingButton: Locator;
	readonly undoButton: Locator;
	readonly undoHistory: Locator;

	constructor(page: Page) {
		this.page = page;

		this.editModeButton = page.getByLabel('Select edit mode').first();
		this.experienceSelector = page.locator(
			'.page-editor__experience-selector'
		);
		this.languageSelector = page
			.locator('.page-editor__toolbar')
			.getByLabel('Select a language');
		this.publishButton = page.getByLabel('Publish', {exact: true}).or(
			page.getByLabel('Submit for Workflow', {
				exact: true,
			})
		);
		this.publishMasterButton = page.getByLabel('Publish Master', {
			exact: true,
		});
		this.publishToLiveButton = page.getByRole('button', {
			name: 'Publish to Live',
		});
		this.redoButton = page.getByTitle('Redo');
		this.segmentEditorPage = new SegmentEditorPage(page);
		this.selectItemMappingButton = page.getByLabel('Select Item');
		this.undoButton = page.getByTitle('Undo');
		this.undoHistory = page.locator('.page-editor__undo-history');
	}

	async goto(
		layout: Layout,
		siteUrl?: Site['friendlyUrlPath'],
		doAsUserId?: string
	) {
		await this.page.goto('/');

		await this.page.goto(
			`/web${siteUrl || '/guest'}${layout.friendlyUrlPath || layout.friendlyURL}?p_l_mode=edit${doAsUserId ? '&doAsUserId=' + doAsUserId : ''}`
		);
	}

	async addFragment(setName: string, name: string, dropTarget?: Locator) {
		await this.goToSidebarTab('Components');

		await this.page
			.getByRole('tab', {exact: true, name: 'Fragments'})
			.click();

		const header = this.page.getByRole('menuitem', {
			exact: true,
			name: setName,
		});

		await expandSection(header);

		if (dropTarget) {
			await dragAndDropElement({
				dragTarget: this.page.getByRole('menuitem', {name}).first(),
				dropTarget,
				page: this.page,
			});
		}
		else {
			await this.page.getByLabel(`Add ${name}`).focus();

			await this.page.keyboard.press('Enter');
			await this.page.keyboard.press('Enter');
		}

		if (name !== 'Stepper') {
			await this.waitForChangesSaved();
		}
	}

	async addFragmentComment(fragmentId: string, comment: string) {
		await this.selectFragment(fragmentId);

		await this.goToSidebarTab('Comments');

		const commentButton = this.page.getByRole('button', {
			exact: true,
			name: 'Comment',
		});

		await this.page.getByLabel('Add Comment').click();

		await this.page.keyboard.type(comment);

		await expect(commentButton).toBeEnabled();

		await commentButton.click();
		await commentButton.waitFor({state: 'hidden'});
	}

	async addRuleAction() {
		await this.page.getByLabel('Select Action').press('Enter');
		await this.page.keyboard.press('Tab');
		await this.page.keyboard.press('Enter');
		await this.page.keyboard.press('Tab');
		await this.page.keyboard.press('Enter');
		await this.page.keyboard.press('Tab');
		await this.page
			.getByRole('button', {name: 'Add Action'})
			.press('Enter');
	}

	async addRuleCondition() {
		await this.page
			.getByLabel('Select Item for the Condition')
			.press('Enter');
		await this.page.keyboard.press('Tab');
		await this.page.keyboard.press('Enter');
		await this.page.keyboard.press('Tab');
		await this.page.keyboard.press('Enter');
		await this.page.keyboard.press('Tab');
		await this.page
			.getByRole('button', {name: 'Add Condition'})
			.press('Enter');
	}

	async addWidget(category: string, name: string, dropTarget?: Locator) {
		await this.goToSidebarTab('Components');

		await this.page
			.getByRole('tab', {exact: true, name: 'Widgets'})
			.click();

		const header = this.page.getByRole('menuitem', {
			exact: true,
			name: category,
		});

		await expandSection(header);

		if (dropTarget) {
			await dragAndDropElement({
				dragTarget: this.page.getByRole('menuitem', {name}).first(),
				dropTarget,
				page: this.page,
			});
		}
		else {
			await this.page.getByLabel(`Add ${name}`).first().focus();

			await this.page.keyboard.press('Enter');
			await this.page.keyboard.press('Enter');
		}

		await this.waitForChangesSaved();
	}

	async changeEditMode(mode: 'Page Design' | 'Content Editing') {
		const currentMode = await this.editModeButton.evaluate(
			(element) => element.textContent
		);

		if (currentMode === mode) {
			return;
		}

		await this.editModeButton.click();

		await this.page.getByRole('option', {name: mode}).click();
	}

	async changeEditableConfiguration({
		editableId,
		fieldLabel,
		fragmentId,
		tab,
		value,
	}: {
		editableId: string;
		fieldLabel: string;
		fragmentId: string;
		tab: EditableConfigurationTab;
		value?: string | boolean;
	}) {

		// Select editable and go to the configuration tab

		await this.selectEditable(fragmentId, editableId);

		await this.changeConfiguration({
			fieldLabel,
			tab,
			value,
		});
	}

	async changeFragmentConfiguration({
		fieldLabel,
		fragmentId,
		isDesktop = true,
		panel,
		tab,
		value,
		valueFromStylebook,
	}: {
		fieldLabel: string;
		fragmentId: string;
		isDesktop?: boolean;
		panel?: string;
		tab: FragmentConfigurationTab;
		value?: string | boolean;
		valueFromStylebook?: boolean;
	}) {
		await this.selectFragment(fragmentId, isDesktop);

		await this.changeConfiguration({
			fieldLabel,
			panel,
			tab,
			value,
			valueFromStylebook,
		});
	}

	async changeConfiguration({
		fieldLabel,
		panel,
		tab,
		value,
		valueFromStylebook,
	}: {
		fieldLabel: string;
		panel?: string;
		tab: ConfigurationTab;
		value: string | boolean;
		valueFromStylebook?: boolean;
	}) {
		await this.goToConfigurationTab(tab);

		const field = panel
			? this.page
					.getByRole('tabpanel', {name: tab})
					.locator('.panel', {hasText: panel})
					.getByLabel(fieldLabel, {
						exact: true,
					})
			: this.page
					.getByRole('tabpanel', {name: tab})
					.getByLabel(fieldLabel, {
						exact: true,
					});

		await field.waitFor();

		if (valueFromStylebook) {
			await field
				.getByLabel('Value from Stylebook', {exact: true})
				.click();

			const valueButton = this.page.getByTitle(value as string, {
				exact: true,
			});

			await valueButton.click();
		}
		else {

			// Change value in different way depending on field type

			const type = await field.evaluate((element) => element.tagName);

			if (type === 'INPUT' || type === 'TEXTAREA') {
				const inputType = await field.evaluate(
					(element: HTMLInputElement) => element.type
				);

				if (inputType === 'checkbox') {
					const checked = await field.evaluate(
						(element: HTMLInputElement) => element.checked
					);

					if (value !== checked) {
						await field.click();
					}

					return;
				}

				await field.fill(value as string);
			}
			else if (type === 'SELECT') {
				await field.selectOption(value as string);
			}
			else if (type === 'BUTTON') {
				await field.click();
			}
		}

		// The change is applied on blur

		await field.blur();

		await this.waitForChangesSaved();
	}

	async changeFragmentSpacing(
		fragmentId: string,
		spacingType: SpacingType,
		value: string,
		unit?: StyleUnit
	) {
		await this.openSpacingSelector(fragmentId, spacingType);

		if (unit) {
			await this.page
				.locator('.page-editor__spacing-selector__dropdown')
				.getByRole('button', {name: 'Select a unit'})
				.click();

			await this.page.getByRole('menuitem', {name: unit}).click();

			const input = this.page.getByRole(
				unit === 'custom' ? 'textbox' : 'spinbutton',
				{
					name: spacingType,
				}
			);

			await fillAndClickOutside(this.page, input, value);

			await input.waitFor({state: 'hidden'});
		}
		else {
			const selector = this.page.getByLabel(
				`Set ${spacingType} to ${value}`
			);

			await selector.click();
			await selector.waitFor({state: 'hidden'});
		}

		await this.waitForChangesSaved();
	}

	async chooseCollectionDisplayCollection(type: string, title: string) {
		await this.page.getByLabel('Select Collection', {exact: true}).click();

		await this.page
			.frameLocator('iframe[title="Select"]')
			.getByRole('link', {name: type})
			.click();

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-dialog'),
			trigger: this.page
				.frameLocator('iframe[title="Select"]')
				.getByRole('button', {name: 'Select ' + title}),
		});
	}

	async clickFragmentOption(
		fragmentId: string,
		name: string,
		isDesktop = true
	) {
		await this.selectFragment(fragmentId, isDesktop);

		await this.page
			.locator('.page-editor__topper__item')
			.getByRole('button', {name: 'Options'})
			.click();

		await this.page
			.locator('.dropdown-menu.show')
			.getByText(name, {exact: true})
			.click();
	}

	async clickPageAction(action: string) {
		await expect(async () => {
			await clickAndExpectToBeVisible({
				target: this.page.getByRole('menuitem', {
					name: action,
				}),
				trigger: this.page
					.locator('.control-menu-nav-item')
					.getByLabel('Options', {exact: true}),
			});

			await this.page
				.getByRole('menuitem', {
					name: action,
				})
				.click({timeout: 1000});
		}).toPass();
	}

	async clickPageContentAction(
		action: string,
		name: string,
		subMenuAction?: string
	) {
		await this.goToSidebarTab('Page Content');

		const content = this.page.getByLabel(name);

		if (subMenuAction) {
			await clickAndExpectToBeVisible({
				autoClick: false,
				target: this.page.getByRole('menuitem', {name: action}),
				trigger: content.getByTitle('Open Actions Menu'),
			});

			await hoverAndExpectToBeVisible({
				autoClick: true,
				target: this.page.locator(`[data-label="${subMenuAction}"]`),
				trigger: this.page.getByRole('menuitem', {name: action}),
			});
		}
		else {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {name: action}),
				trigger: content.getByTitle('Open Actions Menu'),
			});
		}
	}

	async closeExperienceSelector() {
		await collapseSection(this.experienceSelector);

		await this.page
			.getByText('Select Experience')
			.waitFor({state: 'hidden'});
	}

	async copyFragment(fragmentId: string) {
		await this.selectFragment(fragmentId);

		await this.page.keyboard.press('Control+C');

		await this.waitForChangesSaved();
	}

	async createExperience(name: string) {
		await this.openExperienceSelector();

		await this.page.getByLabel('New Experience').click();

		const nameInput = this.page.getByPlaceholder('Experience Name');

		await nameInput.waitFor();

		await expect(nameInput).toHaveAttribute('required');

		await fillAndClickOutside(this.page, nameInput, name);

		await this.page.locator('.modal-footer').getByText('Save').click();

		await this.page.getByText('Select Experience').waitFor();

		await this.closeExperienceSelector();

		await waitForAlert(
			this.page,
			'Success:The experience was created successfully.',
			{autoClose: false}
		);
	}

	async cutFragment(fragmentId: string) {
		await this.selectFragment(fragmentId);

		await this.page.keyboard.press('Control+X');

		await this.waitForChangesSaved();
	}

	async deleteExperience(name: string) {
		await this.openExperienceSelector();

		this.page.on('dialog', async (dialog) => await dialog.accept());

		await this.page
			.locator('.dropdown-menu__experience', {
				hasText: name,
			})
			.getByLabel('Delete Experience')
			.click();

		await this.closeExperienceSelector();

		await waitForAlert(
			this.page,
			'Success:The experience was deleted successfully.',
			{autoClose: false}
		);
	}

	async deleteFragment(fragmentId: string) {
		await this.selectFragment(fragmentId);
		await this.page.keyboard.press('Backspace');

		await this.waitForChangesSaved();
	}

	async dragToFragment({
		drop = true,
		position,
		source,
		targetId,
	}: {
		drop?: boolean;
		position: 'bottom' | 'middle' | 'top';
		source: Locator;
		targetId: string;
	}) {

		// Try dragging source until movement preview appears

		await expect(async () => {
			const sourceBox = await source.boundingBox();

			await source.hover({timeout: 1000});

			await this.page.mouse.down();

			await this.page.mouse.move(sourceBox.x + 5, sourceBox.y + 5);

			await this.page
				.getByLabel('Movement Preview')
				.waitFor({timeout: 1000});
		}).toPass();

		// Move it to target until drag feedback appears

		await expect(async () => {
			const target = this.page.locator(
				`.lfr-layout-structure-item-topper-${targetId}`
			);
			const targetBox = await target.boundingBox();

			const randomX = Math.random() * targetBox.width;

			const y =
				position === 'top'
					? targetBox.y + 10
					: position === 'bottom'
						? targetBox.y + targetBox.height - 10
						: targetBox.y + targetBox.height / 2;

			await this.page.mouse.move(targetBox.x + randomX, y, {steps: 10});

			const dragCssClass =
				position === 'top'
					? 'drag-over-top'
					: position === 'bottom'
						? 'drag-over-bottom'
						: 'drag-over-middle';

			await expect(target).toHaveClass(new RegExp(dragCssClass), {
				timeout: 1000,
			});
		}).toPass({timeout: 10000});

		// Drop if specified

		if (drop) {
			await this.page.mouse.up();

			await this.waitForChangesSaved();
		}
	}

	async dragTreeNode({
		position = 'middle',
		source,
		target,
	}: {
		position?: 'bottom' | 'middle' | 'top';
		source: {
			label: string;
			nth?: number;
		};
		target: {
			label: string;
			nth?: number;
		};
	}) {

		// Go to Browser

		await this.goToSidebarTab('Browser');

		const sourceNode = this.page
			.locator('.page-editor__page-structure__tree-node', {
				hasText: source.label,
			})
			.nth(source.nth || 0);

		const targetNode = this.page
			.locator('.page-editor__page-structure__tree-node', {
				hasText: target.label,
			})
			.nth(target.nth || 0);

		// Select and drag source node

		await sourceNode.hover();

		await this.page.mouse.down();

		// Calculate drop data

		const targetBox = await targetNode.boundingBox();

		const y =
			position === 'middle'
				? targetBox.height / 2
				: position === 'bottom'
					? targetBox.height - 2
					: 2;

		const cssClass =
			position === 'middle'
				? /drag-over-middle/
				: position === 'bottom'
					? /drag-over-bottom/
					: /drag-over-top/;

		// Check hover is correct

		await expect(async () => {
			await targetNode.hover({
				position: {
					x: targetBox.width / 2,
					y,
				},
			});

			await expect(targetNode).toHaveClass(cssClass, {
				timeout: 1000,
			});
		}).toPass();

		// Execute drop

		await this.page.mouse.up();
	}

	async duplicateExperience(experience: string) {
		await this.openExperienceSelector();

		await this.page
			.locator('.dropdown-menu__experience', {
				hasText: experience,
			})
			.getByLabel('Duplicate Experience')
			.click();

		await waitForAlert(
			this.page,
			'Success:The experience was duplicated successfully.',
			{autoClose: false}
		);
	}

	async duplicateFragment(fragmentId: string) {
		await this.selectFragment(fragmentId);

		await this.page.keyboard.press('Alt+Control+D');

		await this.waitForChangesSaved();
	}

	async editHTMLEditable({
		editableId,
		fragmentId,
		value,
	}: {
		editableId: string;
		fragmentId: string;
		useBackwardCompatibility?: boolean;
		value: string;
	}) {

		// Select fragment and editable

		await this.selectEditable(fragmentId, editableId);

		const editable = this.getEditable({
			editableId,
			fragmentId,
		});

		// Enable editor

		await editable.click();

		// Set the content using codemirror API and save

		await this.page
			.locator('.CodeMirror')
			.evaluate(
				(element: any, value) => element.CodeMirror.setValue(value),
				value
			);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await this.waitForChangesSaved();
	}

	async editTextEditable(
		fragmentId: string,
		editableId: string,
		value: string
	) {

		// Select fragment and editable

		await this.selectFragment(fragmentId);

		await this.selectEditable(fragmentId, editableId);

		// Click editable again to enable edition

		const editable = this.getEditable({
			editableId,
			fragmentId,
		});

		await editable.click();

		// Click CKEditor

		const editor = editable.locator('[contenteditable="true"]');

		await editor.waitFor();
		await editor.click();

		// Clear current content and fill with new one

		await this.page.keyboard.press('ControlOrMeta+KeyA');
		await this.page.keyboard.press('Backspace');

		await this.page.keyboard.type(value);

		// Make sure the editable gets the new value

		await expect(async () => {
			await this.page.keyboard.press('Escape');

			await this.waitForChangesSaved();

			await expect(editor).not.toBeVisible({
				timeout: 1000,
			});

			await expect(editable).toHaveText(value, {
				timeout: 1000,
			});
		}).toPass();
	}

	async editExperienceName(name: string, newName: string) {
		await this.openExperienceSelector();

		await this.page
			.locator('.dropdown-menu__experience', {
				hasText: name,
			})
			.getByLabel('Edit Experience')
			.click();

		const nameInput = this.page.getByPlaceholder('Experience Name');

		await nameInput.waitFor();

		await fillAndClickOutside(this.page, nameInput, newName);

		await this.page.locator('.modal-footer').getByText('Save').click();

		await this.page.getByText('Select Experience').waitFor();

		await this.closeExperienceSelector();

		await waitForAlert(
			this.page,
			'Success:The experience was updated successfully.',
			{autoClose: false}
		);
	}

	async editExperienceSegment(name: string, segment: string) {
		await this.openExperienceSelector();

		await this.page
			.locator('.dropdown-menu__experience', {hasText: name})
			.getByLabel('Edit Experience')
			.click();

		// Check segment already exists, otherwise create it

		const audienceSelector = this.page.getByLabel('Audience');

		const options = await audienceSelector.evaluate(
			(element: HTMLSelectElement) =>
				Array.from(element.options).map((option) => option.label)
		);

		if (options.includes(segment)) {
			await audienceSelector.selectOption({label: segment});
		}
		else {
			await this.page.getByText('New Segment').click();

			await this.page.getByText('No Conditions yet').waitFor();

			await this.segmentEditorPage.createSegment(segment, {
				user: ['First Name'],
			});

			await this.page.getByText('Edit Experience').waitFor();
		}

		// Save changes

		await this.page.locator('.modal-footer').getByText('Save').click();

		await this.page.getByText('Select Experience').waitFor();

		await this.closeExperienceSelector();

		await waitForAlert(
			this.page,
			'Success:The experience was updated successfully.',
			{autoClose: false}
		);
	}

	/**
	 * Get id of a fragment that was manually added to the page.
	 *
	 * This is for cases in which we are not able to use a page definition to
	 * create the page, for example when editing display page templates.
	 *
	 * It's recommended to change fragment's name before using this method
	 * so we make sure we get the id for the desired fragment
	 *
	 * @param fragmentName Name of the fragment
	 * @param index Position of the fragment in the page (if there are more than one)
	 */

	async getFragmentId(fragmentName: string, index: number = 0) {
		const topper = this.page
			.locator(`.page-editor__topper[data-name="${fragmentName}"]`)
			.nth(index);

		const fragmentId = await topper.evaluate((element) =>
			Array.from(element.classList)
				.find((cssClass) =>
					cssClass.includes('lfr-layout-structure-item')
				)
				.replace('lfr-layout-structure-item-topper-', '')
		);

		return fragmentId;
	}

	async getFragmentStyle({
		fragmentId,
		isDesktop = true,
		isTopperStyle = false,
		style,
	}: {
		fragmentId: string;
		isDesktop?: boolean;
		isTopperStyle?: boolean;
		style: string;
	}) {
		const element = isTopperStyle
			? this.getTopper(fragmentId, isDesktop)
			: this.getFragment(fragmentId, isDesktop);

		const styles = await element.evaluate((element) =>
			window.getComputedStyle(element)
		);

		return styles[style];
	}

	async goToConfigurationTab(tab: ConfigurationTab) {
		await this.page.getByRole('tab', {exact: true, name: tab}).click();
	}

	async goToSidebarTab(tab: SidebarTab) {
		const tabElement = this.page.getByRole('tab', {exact: true, name: tab});

		await selectElement(tabElement);
	}

	async goToWidgetConfiguration(widgetId: string) {
		await this.clickFragmentOption(widgetId, 'Configuration');
	}

	async hideFragment(fragmentId: string, isDesktop = true) {
		await this.clickFragmentOption(fragmentId, 'Hide Fragment', isDesktop);

		await this.waitForChangesSaved();
	}

	async isActive(fragmentId: string, isDesktop = true) {
		const editMode = await this.editModeButton.evaluate(
			(element) => element.textContent
		);

		if (editMode === 'Content Editing') {
			return false;
		}

		const topper = this.getTopper(fragmentId, isDesktop);

		return await topper.evaluate((element) =>
			element.classList.contains('active')
		);
	}

	async isMaster() {
		const toolbar = this.page.locator('.page-editor__toolbar');

		return await toolbar.evaluate((element) =>
			element.classList.contains('page-editor__toolbar--master-layout')
		);
	}

	async mapObjectAction({
		entity,
		entry,
		fragmentId,
	}: {
		entity: string;
		entry: string;
		fragmentId: string;
	}) {
		await this.selectFragment(fragmentId);

		await this.changeConfiguration({
			fieldLabel: 'Type',
			tab: 'General',
			value: 'Action',
		});

		await this.selectEditable(fragmentId, 'action');

		await this.page.getByRole('tab', {exact: true, name: 'Action'}).click();

		await this.setMappedItem({
			entity,
			entry,
			entryLocator: this.page
				.frameLocator('iframe[title="Select"]')
				.getByText(entry)
				.first(),
		});

		await this.changeConfiguration({
			fieldLabel: 'Action',
			tab: 'Action',
			value: 'addObjectEntryName',
		});

		await this.changeConfiguration({
			fieldLabel: 'Success Interaction',
			tab: 'Action',
			value: 'displayPage',
		});

		await this.changeConfiguration({
			fieldLabel: 'Display Page',
			tab: 'Action',
			value: 'ObjectEntry_displayPageURL',
		});

		await this.changeConfiguration({
			fieldLabel: 'Error Interaction',
			tab: 'Action',
			value: 'notification',
		});
	}

	async mapFormFragment(
		fragmentId: string,
		type: string,
		fields?: string[] | 'all',
		options?: {
			addLocalizationSelect?: boolean;
		}
	) {
		const fragment = this.getFragment(fragmentId);

		await fragment.getByLabel('Content Type').selectOption(type);

		const fieldsModal = this.page.frameLocator(
			'iframe[title="Manage Form Fields"]'
		);

		await fieldsModal
			.getByLabel('Select All Items on the Page')
			.check({trial: true});

		if (!fields || fields === 'all') {
			await fieldsModal
				.getByLabel('Select All Items on the Page')
				.check();
		}
		else {
			for (const field of fields) {
				await fieldsModal
					.getByRole('row', {name: field})
					.getByRole('checkbox')
					.check();
			}
		}

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-title', {
				hasText: 'Manage Form Fields',
			}),
			trigger: this.page.locator('.modal-footer').getByText('Save'),
		});

		const addLocalizationSelectDialog = this.page.getByRole('dialog', {
			name: 'Add Localization Select',
		});

		if (await addLocalizationSelectDialog.isVisible()) {
			if (options?.addLocalizationSelect) {
				await addLocalizationSelectDialog
					.getByRole('button', {name: 'Add Localization Select'})
					.click();
			}
			else {
				await addLocalizationSelectDialog
					.getByRole('button', {name: 'Cancel'})
					.click();
			}
		}

		await waitForAlert(
			this.page,
			'Success:Your form has been successfully loaded.',
			{autoClose: true}
		);
	}

	async mapEditableLink({
		editableId,
		fragmentName,
		linkConfiguration,
	}: {
		editableId: string;
		fragmentName: string;
		linkConfiguration:
			| {
					type: 'URL';
					url: string;
			  }
			| {layoutTitle: string; type: 'Page'}
			| {mappingConfiguration: MappingConfiguration; type: 'Mapped URL'};
	}) {
		const fragmentId = await this.getFragmentId(fragmentName);

		await this.selectEditable(fragmentId, editableId);

		await this.page.getByRole('tab', {exact: true, name: 'Link'}).click();

		await this.setLinkConfiguration(linkConfiguration);
	}

	async openExperienceSelector() {
		await expandSection(this.experienceSelector);

		await this.page.getByText('Select Experience').waitFor();
	}

	async openMappingSelector() {
		await this.selectItemMappingButton.click();

		const hasRecentItems = await this.page
			.getByRole('menuitem', {name: 'Select Item...'})
			.isVisible();

		if (hasRecentItems) {
			await this.page
				.getByRole('menuitem', {name: 'Select Item...'})
				.click();
		}
	}

	async openSpacingSelector(fragmentId: string, spacingType: SpacingType) {
		await this.selectFragment(fragmentId);
		await this.goToConfigurationTab('Styles');

		await this.page.getByLabel(spacingType, {exact: true}).click();
	}

	async pasteFragment(fragmentId: string) {
		await this.selectFragment(fragmentId);

		await this.page.keyboard.press('Control+V');

		await this.waitForChangesSaved();
	}

	async publishPage() {
		const isMaster = await this.isMaster();

		const button = isMaster ? this.publishMasterButton : this.publishButton;

		await button.waitFor();
		await button.click();

		await waitForAlert(this.page, 'successfully');
	}

	async redoAction() {
		await this.redoButton.click();

		await this.waitForChangesSaved();
	}

	async regenerateDisplayPage() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Autogenerate Default Experience',
			}),
			trigger: this.page
				.locator('.page-editor__toolbar')
				.getByRole('button', {name: 'Actions'}),
		});
	}

	async removeFragment(fragmentId: string) {
		await this.selectFragment(fragmentId);

		const fragment = this.getFragment(fragmentId);

		await this.page.keyboard.press('Backspace');

		await this.waitForChangesSaved();

		await fragment.waitFor({state: 'hidden'});
	}

	async resetSpacing(fragmentId: string, spacingType: SpacingType) {
		await this.openSpacingSelector(fragmentId, spacingType);

		const resetButton = this.page.getByLabel('Reset to Initial Value');

		if (await resetButton.isVisible()) {
			await resetButton.click();
			await resetButton.waitFor({state: 'hidden'});
		}

		await this.waitForChangesSaved();
	}

	async selectFragment(fragmentId: string, isDesktop = true) {
		if (await this.isActive(fragmentId, isDesktop)) {
			return;
		}

		const fragment = this.getFragment(fragmentId, isDesktop);

		await fragment.click();

		// Click the tree node again if it wasn't activated

		if (!(await this.isActive(fragmentId, isDesktop))) {
			await this.goToSidebarTab('Browser');

			const treeNode = this.page.locator(
				`.treeview-link[data-id$="${fragmentId}"]`
			);

			await treeNode.click();

			await expect(treeNode).toHaveClass(/focus/);
		}
	}

	async selectDirectImage(fileName: string, imageId: string) {
		await this.selectEditable(imageId, 'image-square');

		await this.page.getByTitle('Select Image').click();

		const articleCard = this.page
			.frameLocator('iframe[title="Select"]')
			.getByText(fileName, {exact: false});

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-dialog'),
			trigger: articleCard,
		});

		await this.waitForChangesSaved();
	}

	async selectEditable(
		fragmentId: string,
		editableId: string,
		isDesktop = true
	) {
		const editable = this.getEditable({
			editableId,
			fragmentId,
			isDesktop,
		});

		const editableIsActive = await editable.evaluate((element) =>
			element.classList.contains('page-editor__editable--active')
		);

		if (!editableIsActive) {
			await this.selectFragment(fragmentId, isDesktop);

			await editable.click();

			await expect(editable).toHaveClass(/page-editor__editable--active/);
		}
	}

	async selectStyleBook(name: string) {
		await this.goToSidebarTab('Page Design Options');

		await this.goToConfigurationTab('Style Book');

		await this.page.getByRole('button', {name}).click();
	}

	async selectVideo({
		fragmentId,
		isDesktop = true,
		title,
		videoURL,
	}: {
		fragmentId: string;
		isDesktop?: boolean;
		title?: string;
		videoURL?: string;
	}) {
		await this.selectFragment(fragmentId, isDesktop);

		await this.page.getByTitle('Select Video', {exact: true}).click();

		const selectIframe = this.page.frameLocator('iframe[title="Select"]');

		if (title) {
			await selectIframe
				.getByRole('link', {exact: true, name: 'Documents and Media'})
				.click();

			await selectIframe.getByTitle(title, {exact: true}).click();
		}
		else if (videoURL) {
			await selectIframe.getByLabel('Video URL').fill(videoURL);

			const addButton = selectIframe.getByRole('button', {
				exact: true,
				name: 'Add',
			});

			await addButton.isEnabled();

			await addButton.click();
		}
	}

	async setLinkConfiguration(
		linkConfiguration:
			| {
					type: 'URL';
					url: string;
			  }
			| {layoutTitle: string; type: 'Page'}
			| {mappingConfiguration: MappingConfiguration; type: 'Mapped URL'}
	) {
		await this.page
			.getByRole('combobox', {exact: true, name: 'Link'})
			.selectOption({label: linkConfiguration.type});

		if (linkConfiguration.type === 'URL') {
			await fillAndClickOutside(
				this.page,
				this.page.getByLabel('URL', {exact: true}),
				linkConfiguration.url
			);

			await this.waitForChangesSaved();
		}
		else if (linkConfiguration.type === 'Page') {
			const layoutTreeItem = this.page
				.frameLocator('iframe[title="Select"]')
				.getByRole('treeitem')
				.filter({hasText: linkConfiguration.layoutTitle});

			await clickAndExpectToBeVisible({
				target: layoutTreeItem,
				timeout: 3000,
				trigger: this.page.getByLabel('Select Page', {exact: true}),
			});

			await clickAndExpectToBeHidden({
				target: this.page.locator('.modal-dialog'),
				trigger: layoutTreeItem,
			});

			await this.waitForChangesSaved();
		}
		else {
			await this.setMappingConfiguration(
				linkConfiguration.mappingConfiguration
			);
		}
	}

	async setMappedItem({
		customMappingButtonLocator,
		entity,
		entry,
		entryLocator,
		field,
		folder,
	}: Omit<MappingItemConfiguration, 'field'> & {
		customMappingButtonLocator?: Locator;
		field?: string;
	}) {
		if (customMappingButtonLocator) {
			await customMappingButtonLocator.click();
		}
		else {
			await this.selectItemMappingButton.click();
		}

		const recentItem = this.page.getByRole('menuitem', {name: entry});

		if (await recentItem.isVisible()) {
			recentItem.click();
		}
		else {
			const hasRecentItems = await this.page
				.getByRole('menuitem', {name: 'Select Item...'})
				.isVisible();

			if (hasRecentItems) {
				if (customMappingButtonLocator) {
					await customMappingButtonLocator.click();
				}
				else {
					await this.selectItemMappingButton.click();
				}

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: this.page.getByRole('menuitem', {
						name: 'Select Item...',
					}),
					trigger: customMappingButtonLocator
						? customMappingButtonLocator
						: this.selectItemMappingButton,
				});
			}

			const iframe = this.page.frameLocator('iframe[title="Select"]');

			await iframe.getByRole('main').waitFor();

			const hasMenuBar = await iframe.getByRole('menubar').isVisible();

			if (hasMenuBar) {
				await clickAndExpectToBeVisible({
					target: iframe
						.locator('.sheet-title')
						.getByText(entity, {exact: true}),
					trigger: iframe.getByRole('menuitem', {
						exact: true,
						name: entity,
					}),
				});
			}

			if (folder) {
				await clickAndExpectToBeVisible({
					target: iframe
						.getByRole('paragraph')
						.filter({hasText: entry}),
					trigger: iframe.getByRole('link').filter({hasText: folder}),
				});
			}

			if (hasMenuBar) {
				await clickAndExpectToBeHidden({
					target: iframe
						.locator('.sheet-title')
						.getByText(entity, {exact: true}),
					trigger: entryLocator
						? entryLocator
						: iframe
								.getByRole('paragraph')
								.filter({hasText: entry}),
				});
			}
			else {
				if (entryLocator) {
					await entryLocator.waitFor();
				}
				else {
					await iframe
						.getByRole('paragraph')
						.filter({hasText: entry})
						.waitFor();
				}

				await clickAndExpectToBeHidden({
					target: iframe.locator('.sheet-title .lfr-item-viewer'),
					trigger: entryLocator
						? entryLocator
						: iframe
								.getByRole('paragraph')
								.filter({hasText: entry}),
				});
			}
		}

		await expect(
			this.page.locator('.page-editor__item-selector__content-input')
		).toHaveValue(entry);

		if (field) {
			await this.page
				.getByLabel('Field', {exact: true})
				.selectOption(field);

			await this.waitForChangesSaved();
		}
	}

	async setMappingConfiguration(
		mappingConfiguration:
			| {
					mapping: MappingItemConfiguration;
					source?: 'content';
			  }
			| {
					mapping: {field: string};
					relationship: string;
					source: 'relationship';
			  }
			| {
					mapping: {field: string};
					source: 'structure';
			  }
	) {
		const {source} = mappingConfiguration;

		// Select source and relationship if needed

		if (source) {
			await this.page
				.getByLabel('Source', {exact: true})
				.selectOption(source);
		}

		if (source === 'relationship') {
			await this.page
				.getByLabel('Relationship')
				.selectOption(mappingConfiguration.relationship);
		}

		// If source is not content, just select the field

		if (source === 'relationship' || source === 'structure') {
			await this.page
				.getByRole('combobox', {exact: true, name: 'Field'})
				.selectOption(mappingConfiguration.mapping.field);

			return;
		}

		// If source is content, select the item and the field

		await this.setMappedItem(mappingConfiguration.mapping);
	}

	async switchExperience(experience: string) {
		await this.openExperienceSelector();

		await this.page.getByText('Select Experience').waitFor();

		await this.page
			.locator('.dropdown-menu__experience', {
				hasText: experience,
			})
			.click();

		await expect(this.experienceSelector).toContainText(experience);

		await this.closeExperienceSelector();
	}

	async switchLanguage(language: string) {
		await this.languageSelector.click();

		await this.page
			.getByRole('option', {
				name: `${language} Language`,
			})
			.click();
	}

	async switchViewport(viewport: Viewport) {
		await this.page.getByLabel(viewport, {exact: true}).click();
		await this.page
			.locator(
				`.page-editor__layout-viewport--size-${VIEWPORTS_CLASSNAMES[viewport]}`
			)
			.waitFor();
	}

	async undoAction() {
		await this.undoButton.click();

		await this.waitForChangesSaved();
	}

	async waitForChangesSaved() {
		await this.page.getByLabel('Saved', {exact: true}).waitFor();

		await this.page
			.getByText(
				'Changes have been saved. Page editor will autosave new changes.'
			)
			.waitFor();
	}

	getEditable({
		editableId,
		fragmentId,
		isDesktop = true,
	}: {
		editableId: string;
		fragmentId: string;
		isDesktop?: boolean;
	}) {
		const fragment = this.getFragment(fragmentId, isDesktop);
		const dataAttributeLocator = fragment.locator(
			`[data-lfr-editable-id="${editableId}"]`
		);
		const tagLocator = fragment.locator(`lfr-editable[id="${editableId}"]`);

		return dataAttributeLocator.or(tagLocator).first();
	}

	getFragment(fragmentId: string, isDesktop = true) {
		if (isDesktop) {
			return this.page
				.locator(`.lfr-layout-structure-item-${fragmentId}`)
				.first();
		}
		else {
			return this.page
				.frameLocator('.page-editor__global-context-iframe')
				.locator(`.lfr-layout-structure-item-${fragmentId}`)
				.first();
		}
	}

	getTopper(fragmentId: string, isDesktop = true) {
		return isDesktop
			? this.page
					.locator(`.lfr-layout-structure-item-topper-${fragmentId}`)
					.first()
			: this.page
					.frameLocator('.page-editor__global-context-iframe')
					.locator(`.lfr-layout-structure-item-topper-${fragmentId}`)
					.first();
	}
}
