/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ObjectField} from '../../../../../apps/object/object-admin-rest-client-js/src/main/resources/META-INF/resources/node';
import {PORTLET_URLS} from '../../../utils/portletUrls';

export class ViewObjectEntriesPage {
	readonly addObjectEntryButton: Locator;
	readonly backButton: Locator;
	readonly deletionConfirmationModal: Locator;
	readonly deleteFileButton: Locator;
	readonly duplicateEntryErrorMessage: Locator;
	readonly editObjectEntryForm: Locator;
	readonly frameSelect: FrameLocator;
	readonly frontendDatasetActions: Locator;
	readonly frontendDatasetDeleteAction: Locator;
	readonly page: Page;
	readonly richTextIFrame: FrameLocator;
	readonly richTextInput: Locator;
	readonly saveObjectEntryButton: Locator;
	readonly saveObjectEntryButtonArabic: Locator;
	readonly searchBar: Locator;
	readonly searchButton: Locator;
	readonly searchContainer: Locator;
	readonly selectFileButton: Locator;
	readonly selectFileButtonArabic: Locator;
	readonly selectFileIframe: FrameLocator;
	readonly selectFileIframeArabic: FrameLocator;
	readonly successMessage: Locator;
	readonly successMessageArabic: Locator;

	constructor(page: Page) {
		this.addObjectEntryButton = page
			.getByTestId('fdsCreationActionButton')
			.first();
		this.backButton = page.getByTitle('Back');
		this.deleteFileButton = page.getByRole('button', {name: 'Delete'});
		this.deletionConfirmationModal = page
			.getByRole('dialog')
			.and(page.getByLabel('Delete Entry'));
		this.duplicateEntryErrorMessage = page.getByText(
			'Error:The field values are already in use. Please choose unique values.'
		);
		this.editObjectEntryForm = page.locator('[id="editObjectEntry"]');
		this.frameSelect = page
			.locator('iframe[title="Select"]')
			.contentFrame();
		this.frontendDatasetActions = page.getByRole('button', {
			name: 'Actions',
		});
		this.frontendDatasetDeleteAction = page.getByRole('menuitem', {
			name: 'Delete',
		});
		this.page = page;
		this.richTextIFrame = page
			.getByRole('application', {
				name: /Rich Text Editor, _com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_.*_ddm\$\$.*\$.*\$en_US/,
			})
			.frameLocator('iframe');
		this.richTextInput = this.richTextIFrame.getByRole('textbox');
		this.searchBar = this.frameSelect.getByPlaceholder('Search for');
		this.searchButton = this.frameSelect.getByRole('button', {
			name: 'Search for',
		});
		this.searchContainer = this.frameSelect.locator(
			'[id="_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer"]'
		);
		this.saveObjectEntryButton = page.getByRole('button', {name: 'Save'});
		this.saveObjectEntryButtonArabic = page.getByRole('button', {
			name: 'إحفظ',
		});
		this.selectFileButton = page.getByRole('button', {name: 'Select File'});
		this.selectFileButtonArabic = page.getByRole('button', {
			name: 'إختر مجلّد',
		});
		this.selectFileIframe = page.frameLocator(
			'iframe[title="Select File"]'
		);
		this.selectFileIframeArabic = page.frameLocator(
			'iframe[title="إختر مجلّد"]'
		);
		this.successMessage = page.getByText(
			'Your request completed successfully.'
		);
		this.successMessageArabic = page.getByText('نجاح:تم تنفيذ طلبك بنجاح.');
	}

	async assertErrorWithDuplicateEntryValue() {
		await this.duplicateEntryErrorMessage.waitFor();
		await expect(this.duplicateEntryErrorMessage).toBeVisible();
	}

	async clickAddObjectEntry(objectName?: string) {
		objectName
			? await this.page.getByLabel('Add ' + objectName).click()
			: await this.addObjectEntryButton.click();

		await this.editObjectEntryForm.waitFor({state: 'visible'});
	}

	async fillObjectEntry({
		objectFieldBusinessType,
		objectFieldLabel,
		objectFieldValue,
	}: {
		objectFieldBusinessType?: ObjectField['businessType'];
		objectFieldLabel?: string;
		objectFieldValue: string;
	}) {
		if (objectFieldBusinessType === 'RichText') {
			await this.page.waitForSelector('iframe');

			await this.richTextInput.fill(objectFieldValue);

			await this.richTextInput.click({button: 'left'});

			await this.richTextInput.press('Backspace');

			return;
		}

		await this.page
			.getByLabel(objectFieldLabel, {exact: true})
			.fill(objectFieldValue);
	}

	async selectDropdownItem(fieldName: string, optionName: string) {
		await this.page.getByLabel(fieldName).click();
		await this.page.getByRole('option', {name: optionName}).click();
	}

	async selectFileFromDocumentsAndMedia(fileName: string) {
		await this.selectFileButton.click();

		await this.selectFileIframe
			.getByRole('link', {name: 'Sites and Libraries'})
			.click();

		await this.selectFileIframe
			.getByRole('link', {name: 'Liferay DXP'})
			.click();

		await this.selectFileIframe
			.getByRole('link', {name: 'Provided by Liferay'})
			.click();

		await this.selectFileIframe.getByText(fileName).dblclick();
	}

	async selectFileFromDocumentsAndMediaArabic() {
		await this.selectFileButtonArabic.click();

		await this.selectFileIframeArabic
			.getByRole('link', {name: 'المواقع والمكتبات'})
			.click();

		await this.selectFileIframeArabic
			.getByRole('link', {name: 'Liferay DXP'})
			.click();

		await this.selectFileIframeArabic
			.getByRole('link', {name: 'Provided by Liferay'})
			.click();

		await this.selectFileIframeArabic
			.locator(
				'[id="_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_repositoryEntriesSearchContainer"] img'
			)
			.first()
			.click();
	}

	async goto(
		objectDefinitionClassName: string,
		regionalCode?: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		if (!regionalCode) {
			regionalCode = 'en';
		}

		const [_, objectDefinitionClassNameSuffix] =
			objectDefinitionClassName.split('#');

		await this.page.goto(
			`/${regionalCode}/group${siteUrl ?? '/guest'}${
				PORTLET_URLS.objects
			}_${objectDefinitionClassNameSuffix}`,
			{waitUntil: 'load'}
		);
	}
}
