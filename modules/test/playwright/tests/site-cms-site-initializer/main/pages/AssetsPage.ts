/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {getTempDir} from '../../../../utils/temp';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {DataSetPage} from './DataSetPage';

// Page for All, Content and Files page

interface ExecItemActionArgs {
	action:
		| 'Copy To'
		| 'Delete'
		| 'Download'
		| 'Duplicate'
		| 'Edit'
		| 'Expire'
		| 'Export for Translation'
		| 'Move To'
		| 'Share'
		| 'Show Details'
		| 'View'
		| 'View History';
	filter: string;
	parentAction?: 'Copy';
}

interface BulkCopyOrMoveArgs {
	destinationFolder: string;
	destinationSpace: string;
}

export class AssetsPage {
	readonly page: Page;

	readonly dataSetFragmentPage: DataSetPage;
	readonly galleryNavigation: Locator;
	readonly galleryPreview: Locator;
	readonly galleryThumbnails: Locator;
	readonly modalDeleteButton: Locator;
	readonly newButton: Locator;
	readonly processingTasksButton: Locator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;

		headRow: Locator;
	};
	readonly taskStatusButton: (buttonName: string) => Locator;
	readonly taskStatusDropdownItemButton: (taskName: string) => Locator;
	readonly taskStatusDropdownList: Locator;
	readonly taskStatusFormsButton: Locator;
	readonly viewAllTasksLink: Locator;

	readonly modalContainer: Locator;
	readonly modal: {
		body: Locator;
		container: Locator;
		footer: Locator;
		title: Locator;
	};

	constructor(page: Page) {
		this.page = page;

		this.dataSetFragmentPage = new DataSetPage(page);
		this.galleryNavigation = page.locator('.fds-gallery-view__navigation');
		this.galleryPreview = page.locator('.fds-gallery-view__preview');
		this.galleryThumbnails = page.locator('.fds-gallery-view__thumbnails');
		this.newButton = page.getByTestId('fdsCreationActionButton').first();
		this.processingTasksButton = page.getByRole('button', {
			name: /Processing Tasks?/,
		}) as Locator;
		this.taskStatusButton = (buttonName: string) => {
			return page.getByRole('button', {exact: true, name: buttonName});
		};
		this.taskStatusDropdownItemButton = (taskName: string) => {
			return page.getByText(taskName);
		};
		this.taskStatusDropdownList = page.locator('ul.task-status');
		this.taskStatusFormsButton = page
			.locator('li.tbar-item')
			.locator('svg.lexicon-icon-forms');
		this.viewAllTasksLink = page.getByRole('link', {
			exact: true,
			name: 'View All Tasks',
		});

		this.table = this.dataSetFragmentPage.table;

		const modalContainer = page.locator('.modal-dialog');

		this.modal = {
			body: modalContainer.locator('.modal-body'),
			container: modalContainer,
			footer: modalContainer.locator('.modal-footer'),
			title: modalContainer.locator('.modal-title'),
		};

		this.modalDeleteButton = this.modal.footer.getByRole('button', {
			exact: true,
			name: 'Delete',
		});
	}

	async gotoAll() {
		await this.page.goto(PORTLET_URLS.cmsAll);
		await this.page.getByRole('heading', {name: 'All'}).waitFor();
	}

	async gotoContents() {
		await this.page.goto(PORTLET_URLS.cmsContents);
		await this.page.getByRole('heading', {name: 'Contents'}).waitFor();
	}

	async gotoFiles() {
		await this.page.goto(PORTLET_URLS.cmsFiles);
		await this.page.getByRole('heading', {name: 'Files'}).waitFor();
	}

	async createContent(type: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: type}),
			trigger: this.newButton,
		});
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execBulkItemAction(action: string) {
		await this.dataSetFragmentPage.execBulkItemAction({action});
	}

	async bulkCopyTo(args: BulkCopyOrMoveArgs) {
		await this.page
			.getByRole('button', {exact: true, name: 'Copy To'})
			.click();

		await this.selectCopyOrMoveDestination(args);
	}

	async bulkMoveTo(args: BulkCopyOrMoveArgs) {
		await this.page
			.getByRole('button', {exact: true, name: 'Move To'})
			.click();

		await this.selectCopyOrMoveDestination(args);
	}

	getCopyOrMoveDestinationDialog() {
		return this.page.getByRole('dialog', {name: /^(Copy|Move) .+ To$/});
	}

	async selectCopyOrMoveDestination({
		destinationFolder,
		destinationSpace,
	}: BulkCopyOrMoveArgs) {
		const dialog = this.getCopyOrMoveDestinationDialog();

		await dialog.waitFor();

		await dialog.getByLabel(destinationSpace).click();

		await dialog
			.getByRole('radio', {
				exact: true,
				name: `Select ${destinationFolder}`,
			})
			.click();

		await dialog.getByRole('button', {exact: true, name: 'Select'}).click();
	}

	async gotoSpaceContents(spaceName: string) {
		await this.gotoAll();

		await this.page
			.getByRole('menuitem', {exact: true, name: spaceName})
			.click();

		await this.page
			.getByRole('menuitem', {exact: true, name: 'Contents'})
			.click();

		await this.page.getByRole('heading', {name: 'Contents'}).waitFor();
	}

	async execItemAction({action, filter, parentAction}: ExecItemActionArgs) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
			parentAction,
		});
	}

	async changeVisualizationMode(
		...args: Parameters<DataSetPage['changeVisualizationMode']>
	) {
		await this.dataSetFragmentPage.changeVisualizationMode(...args);
	}

	async selectItems(titles: string[]) {
		for (const title of titles) {
			const card = this.page
				.locator('tr', {hasText: title})
				.or(this.getCardItem(title));

			await card.getByRole('checkbox').check();
		}
	}

	async navigateByGalleryArrows(direction: 'Previous' | 'Next') {
		await this.galleryNavigation
			.getByRole('button', {name: direction})
			.click();
	}

	getCardItem(name: string) {
		return this.page.locator('.card', {hasText: name});
	}

	async execCardItemAction({action, filter}: ExecItemActionArgs) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.getCardItem(filter).getByLabel(`${filter} Actions`),
		});
	}

	async exportForTranslation(
		isBulk: boolean,
		targetLanguages: string[]
	): Promise<string> {
		for (const targetLanguage of targetLanguages) {
			const targetLanguageCheckbox = this.page
				.locator('.modal')
				.getByLabel(targetLanguage);

			await targetLanguageCheckbox.check();
		}

		const downloadPromise = this.page.waitForEvent('download');

		await this.page
			.locator('.modal-footer')
			.getByRole('button', {exact: true, name: 'Export'})
			.click();

		if (isBulk) {
			await waitForAlert(
				this.page,
				'Warning:The export of all selected contents is being prepared. Please do not close this window or navigate to another section.',
				{
					type: 'warning',
				}
			);
		}

		await waitForAlert(
			this.page,
			'Success:The download will begin shortly'
		);

		const download = await downloadPromise;

		const filePath = getTempDir() + download.suggestedFilename();

		await download.saveAs(filePath);

		return filePath;
	}
}
