/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '@liferay/object-admin-rest-client-js';
import {FrameLocator, Locator, Page, expect} from '@playwright/test';
import path from 'path';

import {PORTLET_URLS} from '../../../utils/portletUrls';

export class ViewObjectEntriesPage {
	readonly addObjectEntryButton: Locator;
	readonly backButton: Locator;
	readonly dateTimeInput: Locator;
	readonly deletionConfirmationModal: Locator;
	readonly deleteFileButton: Locator;
	readonly duplicateEntryErrorMessage: Locator;
	readonly editObjectEntryForm: Locator;
	readonly frameSelect: FrameLocator;
	readonly frontendDatasetActions: Locator;
	readonly frontendDatasetDeleteAction: Locator;
	readonly neverReview: Locator;
	readonly objectEntryButton: Locator;
	readonly page: Page;
	readonly publishDateInput: Locator;
	readonly publishObjectEntryDropdown: Locator;
	readonly publishOption: Locator;
	readonly reviewDateInput: Locator;
	readonly saveObjectEntryButton: Locator;
	readonly saveObjectEntryButtonArabic: Locator;
	readonly schedulePublicationOption: Locator;
	readonly schedulePublicationButton: Locator;
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
		this.dateTimeInput = page.getByPlaceholder('__/__/____ __:__ _');
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
		this.neverReview = page.getByLabel('Never Review', {exact: true});
		this.objectEntryButton = page.getByRole('link', {name: 'View'});
		this.page = page;
		this.publishDateInput = page.getByLabel('Publish Date' + 'Mandatory', {
			exact: true,
		});
		this.publishObjectEntryDropdown = page.getByRole('button', {
			name: 'Publish',
		});
		this.publishOption = page.getByRole('menuitem', {name: 'Publish'});
		this.reviewDateInput = page.getByLabel('Review Date' + 'Mandatory', {
			exact: true,
		});
		this.schedulePublicationButton = page
			.getByLabel('Schedule Publication')
			.getByRole('button', {name: 'Schedule'});
		this.schedulePublicationOption = page.getByRole('menuitem', {
			name: 'Schedule Publication',
		});
		this.searchBar = this.frameSelect.getByPlaceholder('Search for');
		this.searchButton = this.frameSelect.getByRole('button', {
			name: 'Search for',
		});
		this.searchContainer = this.frameSelect.locator(
			'[id="_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer"]'
		);
		this.saveObjectEntryButton = page.getByRole('button', {name: 'Save'});
		this.saveObjectEntryButtonArabic = page.getByRole('button', {
			name: 'حفظ',
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

	async choosePublicationOption(option: 'schedule' | 'publish') {
		await this.publishObjectEntryDropdown.click();

		if (option === 'schedule') {
			await this.schedulePublicationOption.click();
		}
		else {
			await this.publishOption.click();
		}
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

			const richTextInput = this.page
				.getByRole('application', {
					name: objectFieldLabel,
				})
				.frameLocator('iframe')
				.getByRole('textbox');

			await richTextInput.fill(objectFieldValue);

			await richTextInput.click({button: 'left'});

			await richTextInput.press('Backspace');

			return;
		}

		await this.page
			.getByLabel(objectFieldLabel, {exact: true})
			.fill(objectFieldValue);
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
			{waitUntil: 'networkidle'}
		);
	}

	async goToObjectDefinitionEntry(objectDefinition: string) {
		await this.goto(objectDefinition);
		await this.objectEntryButton.click();
	}

	async scheduleForCurrentDate(
		scheduleField: 'Display' | 'Expiration' | 'Review'
	) {
		const fieldLabel = `${scheduleField} DateMandatory`;

		await this.page
			.locator('div')
			.filter({hasText: new RegExp(`^${fieldLabel}$`)})
			.getByLabel('Choose date')
			.click();

		await this.page
			.locator('div:not([aria-hidden="true"])')
			.getByRole('button', {name: 'Select current date'})
			.click();
	}

	async selectDropdownItem(fieldName: string, optionName: string) {
		await this.page.getByLabel(fieldName).click();
		await this.page.getByRole('option', {name: optionName}).click();
	}

	async selectDropdownItemWithSearch(optionName: string) {
		await this.page.getByPlaceholder('Search').click();
		await this.page.getByRole('menuitem', {name: optionName}).click();
	}

	async selectFileFromDocumentsAndMedia(fileName: string) {
		await this.selectFileIframe
			.getByRole('link', {name: 'Sites and Libraries'})
			.click();

		await this.selectFileIframe
			.getByRole('link', {name: 'Liferay DXP'})
			.click();

		await this.selectFileIframe
			.getByRole('link', {name: 'Provided by Liferay'})
			.click();

		await expect(
			this.selectFileIframe.getByLabel('Search for', {exact: true})
		).toBeEnabled();

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

	async selectFileFromUserComputer(dirName: string, fileName: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.selectFileButton.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(dirName, 'dependencies', fileName)
		);

		await this.page.getByText(fileName).waitFor({state: 'visible'});
	}
}
