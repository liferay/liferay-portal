/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../../../apps/site/site-cms-site-initializer/src/main/resources/META-INF/resources/js/common/utils/constants';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
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
		| 'Move'
		| 'Move To'
		| 'Share'
		| 'Show Details'
		| 'View'
		| 'View History';
	filter: string;
	parentAction?: 'Copy';
}

interface CopyOrMoveDestinationArgs {
	destinationFolder: string;
	destinationSpace: string;
}

interface ItemCopyOrMoveArgs extends CopyOrMoveDestinationArgs {
	itemTitle: string;
}

export class AssetsPage {
	readonly page: Page;

	readonly apiHelpers: ApiHelpers;

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

		this.apiHelpers = new ApiHelpers(page);

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

	async gotoContents(spaceName?: string) {
		if (spaceName) {
			const rootFolder =
				await this.apiHelpers.objectFolder.getObjectEntryFolderByExternalReferenceCode(
					{externalReferenceCode: 'L_CONTENTS', scopeKey: spaceName}
				);

			await this.gotoFolder(rootFolder.id, rootFolder.title);
		}
		else {
			await this.page.goto(PORTLET_URLS.cmsContents);
			await this.page.getByRole('heading', {name: 'Contents'}).waitFor();
		}
	}

	async gotoFiles() {
		await this.page.goto(PORTLET_URLS.cmsFiles);
		await this.page.getByRole('heading', {name: 'Files'}).waitFor();
	}

	async gotoFolder(folderId: string, folderTitle: string) {
		const className =
			await this.apiHelpers.jsonWebServicesClassName.fetchClassName(
				OBJECT_ENTRY_FOLDER_CLASS_NAME
			);

		await this.page.goto(
			`${PORTLET_URLS.cmsViewFolder}/${className.classNameId}/${folderId}`
		);

		await this.page.getByTestId(`testId${folderTitle}`).waitFor();
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

	async expectBulkItemActionHidden(action: string) {
		await this.dataSetFragmentPage.expectBulkItemActionHidden({action});
	}

	async expectItemActionHidden(action: string, filter: string) {
		await this.dataSetFragmentPage.expectItemActionHidden({action, filter});
	}

	async bulkCopyTo(args: CopyOrMoveDestinationArgs) {
		await this.page
			.getByRole('button', {exact: true, name: 'Copy To'})
			.click();

		await this.selectCopyOrMoveDestination(args);
	}

	async bulkMoveTo(args: CopyOrMoveDestinationArgs) {
		await this.page
			.getByRole('button', {exact: true, name: 'Move To'})
			.click();

		await this.selectCopyOrMoveDestination(args);
	}

	async copyTo({itemTitle, ...destination}: ItemCopyOrMoveArgs) {
		await this.execItemAction({
			action: 'Copy To',
			filter: itemTitle,
			parentAction: 'Copy',
		});

		await this.selectCopyOrMoveDestination(destination);
	}

	async moveTo({itemTitle, ...destination}: ItemCopyOrMoveArgs) {
		await this.execItemAction({
			action: 'Move',
			filter: itemTitle,
		});

		await this.selectCopyOrMoveDestination(destination);
	}

	getCopyOrMoveDestinationDialog() {
		return this.page.getByRole('dialog', {name: /^(Copy|Move) .+ To$/});
	}

	async selectCopyOrMoveDestination({
		destinationFolder,
		destinationSpace,
	}: CopyOrMoveDestinationArgs) {
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
		await expect(async () => {
			await this.gotoAll();

			await this.page
				.getByRole('menuitem', {exact: true, name: spaceName})
				.click({timeout: 2000});

			await this.page
				.getByRole('menuitem', {exact: true, name: 'Contents'})
				.click({timeout: 2000});

			await this.page
				.getByRole('heading', {name: 'Contents'})
				.waitFor({timeout: 2000});
		}).toPass({timeout: 30000});
	}

	async gotoSpaceFiles(spaceName: string) {
		await expect(async () => {
			await this.gotoAll();

			await this.page
				.getByRole('menuitem', {exact: true, name: spaceName})
				.click({timeout: 2000});

			await this.page
				.getByRole('menuitem', {exact: true, name: 'Files'})
				.click({timeout: 2000});

			await this.page
				.getByRole('heading', {name: 'Files'})
				.waitFor({timeout: 2000});
		}).toPass({timeout: 30000});

		await this.changeVisualizationMode('Table');
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

	async selectAllItems(spanMultiplePages: boolean) {
		await this.page.getByTitle('Select Items').click();

		if (spanMultiplePages) {
			const selectAllLink = this.page.getByRole('button', {
				exact: true,
				name: 'Select All',
			});

			await expect(selectAllLink).toBeVisible();

			await selectAllLink.click();
		}

		await expect(this.page.getByText('All Selected')).toBeVisible();
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
