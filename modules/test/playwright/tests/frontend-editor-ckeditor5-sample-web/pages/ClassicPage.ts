/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class ClassicPage {
	readonly editable: Locator;
	readonly itemSelectorFrame: FrameLocator;
	readonly sourceEditable: Locator;
	readonly sourceEditingEnhancedDialog: {
		cancelButton: Locator;
		editable: Locator;
		saveButton: Locator;
	};
	readonly toolbar: {
		buttonLabels: Locator;
		container: Locator;
	};
	readonly wordCountContainer: Locator;

	constructor(page: Page) {
		this.editable = page.locator('.ck-editor__editable');

		this.itemSelectorFrame = page.frameLocator(
			'iframe[title="Select Item"]'
		);

		this.sourceEditable = page.locator(
			'.ck-source-editing-area > textarea'
		);

		const sourceEditingEnhancedDialog = page.getByRole('dialog', {
			name: 'Edit source',
		});

		this.sourceEditingEnhancedDialog = {
			cancelButton: sourceEditingEnhancedDialog.getByRole('button', {
				exact: true,
				name: 'Cancel',
			}),
			editable: sourceEditingEnhancedDialog.locator('.cm-content'),
			saveButton: sourceEditingEnhancedDialog.getByRole('button', {
				exact: true,
				name: 'Save',
			}),
		};

		const toolbarContainer = page.getByLabel('Editor toolbar');

		this.toolbar = {
			buttonLabels: toolbarContainer
				.getByRole('button')
				.locator('.ck-button__label'),
			container: toolbarContainer,
		};

		this.wordCountContainer = page.locator(
			'[data-testid="word-count-container"]'
		);
	}
}
