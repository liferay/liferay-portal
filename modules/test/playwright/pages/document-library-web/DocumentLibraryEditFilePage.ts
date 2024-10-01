/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {expandSection} from '../../utils/expandSection';
import {waitForAlert} from '../../utils/waitForAlert';
import {DocumentLibraryPage} from './DocumentLibraryPage';

export class DocumentLibraryEditFilePage {
	readonly page: Page;

	readonly descriptionInput: Locator;
	readonly documentLibraryPage: DocumentLibraryPage;
	readonly permissionViewSelector: Locator;
	readonly publishButton: Locator;
	readonly publishDateSelector: Locator;
	readonly saveButton: Locator;
	readonly scheduleButton: Locator;
	readonly selectForUpdateButton: Locator;
	readonly titleSelector: Locator;

	constructor(page: Page) {
		this.page = page;

		this.descriptionInput = page.locator(
			'#_com_liferay_document_library_web_portlet_DLAdminPortlet_description'
		);
		this.documentLibraryPage = new DocumentLibraryPage(page);
		this.permissionViewSelector = page.getByLabel('Viewable by');
		this.publishButton = page.getByRole('button', {
			exact: true,
			name: 'Publish',
		});
		this.publishDateSelector = page.getByLabel('Publish Date');
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.scheduleButton = page.getByRole('button', {name: 'Schedule'});
		this.selectForUpdateButton = page.getByLabel('Upload', {exact: true});
		this.titleSelector = page.getByLabel('Title');
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.documentLibraryPage.goto(siteUrl);
		await this.documentLibraryPage.goToCreateNewFile();
	}

	async assertPrivateFileIconInSelectPopUp(assetType: string) {
		await this.documentLibraryPage.assertPrivateFileIcon(
			this.page.frameLocator(`iframe[title="Select ${assetType}"]`)
		);
	}

	async changeViewInItemSelector(assetType: string, viewType: string) {
		const modalIframe = this.page.frameLocator(
			`iframe[title="Select ${assetType}"]`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: modalIframe.getByRole('menuitem', {name: viewType}),
			trigger: modalIframe.getByLabel(
				'Select View, Currently Selected: '
			),
		});
	}

	async goToNewFileDifferentType(
		type: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.documentLibraryPage.goto(siteUrl);

		await this.documentLibraryPage.goToCreateNewFileWithDifferentType(type);
	}

	async openFieldset(name: 'Categorization') {
		const fieldset = await this.page.getByRole('group', {
			name,
		});

		if (await fieldset.locator('.panel-body').isHidden()) {
			await fieldset.getByRole('button', {name}).click();
		}
	}

	async publishFileEntry() {
		if (await this.saveButton.isVisible()) {
			await this.saveButton.click();
		}
		else {
			await this.publishButton.click();
		}

		await waitForAlert(this.page);
	}

	async publishNewBasicFileEntry(
		title: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await this.titleSelector.fill(title);

		if (await this.saveButton.isVisible()) {
			await this.saveButton.click();
		}
		else {
			await this.publishButton.click();
		}
		await waitForAlert(
			this.page,
			'Success:Your request completed successfully.'
		);
	}
	async publishNewBasicFileEntryWithoutGoTo(title: string) {
		await this.titleSelector.fill(title);

		if (await this.saveButton.isVisible()) {
			await this.saveButton.click();
		}
		else {
			await this.publishButton.click();
		}
	}

	async publishMultipleFiles(dTypeTitle: string, filePaths: string[]) {
		await this.page.getByRole('button', {name: 'Select Files'}).waitFor();
		await this.page.locator('input[type="file"]').setInputFiles(filePaths);

		await this.page.getByLabel('Select All').check();

		await this.page.getByRole('button', {name: 'Document Type'}).click();
		await this.page.getByRole('button', {name: 'Basic Document'}).click();

		await this.page.getByRole('menuitem', {name: dTypeTitle}).click();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: this.page.locator(
				'#_com_liferay_document_library_web_portlet_DLAdminPortlet_documentLibraryContainer'
			),
			trigger: this.page.getByRole('button', {name: 'Publish'}),
		});
	}

	async publishNewFileWithoutGuestViewPermission(
		title: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await this.titleSelector.fill(title);
		if (await this.permissionViewSelector.isVisible()) {
			await this.permissionViewSelector.selectOption('Site Member');
		}
		else {
			await this.page.getByRole('button', {name: 'Permissions'}).click();
			await this.permissionViewSelector.selectOption('Site Member');
		}

		await this.publishButton.click();
	}

	async publishNewFileWithScheduleDate(
		scheduleDate: string,
		title: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await this.titleSelector.fill(title);

		const isClosed =
			!(await this.scheduleButton.getAttribute('aria-expanded')) ||
			(await this.scheduleButton.getAttribute('aria-expanded')) ===
				'false';

		if (isClosed) {
			await this.scheduleButton.click();
		}

		await this.publishDateSelector.click();
		await this.publishDateSelector.fill(scheduleDate);
		await this.publishDateSelector.click();
		await this.publishDateSelector.press('Escape');
		await this.page
			.locator(
				'[id="_com_liferay_document_library_web_portlet_DLAdminPortlet_displayDateTime"]'
			)
			.fill('00:00');

		if (await this.saveButton.isVisible()) {
			await this.saveButton.click();
		}
		else {
			await this.publishButton.click();
		}
	}

	async selectSpecificDisplayPage(displayPageName: string) {
		const displayPageFieldSet = this.page.locator('fieldset', {
			hasText: 'Display Page',
		});

		await expandSection(displayPageFieldSet);
		await displayPageFieldSet
			.getByTitle('Display Page Template Type')
			.selectOption('Specific');
		displayPageFieldSet.getByRole('button', {name: 'Select'}).click();
		const selectDisplayPageModal = await this.page.frameLocator(
			'iframe[title*="Select Page"]'
		);

		await this.page
			.locator('.modal-title', {
				hasText: 'Select Page',
			})
			.waitFor({
				state: 'visible',
			});

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-title', {
				hasText: 'Select Page',
			}),
			trigger: selectDisplayPageModal.getByLabel(
				'Select ' + displayPageName
			),
		});
	}
}
