/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export enum EEditorType {
	ALLOYEDITOR = 'alloyeditor',
	CKEDITOR4 = 'ckeditor4',
	CKEDITOR5 = 'ckeditor5',
}

export async function waitForEditor({
	container: containerProp,
	editorType = EEditorType.CKEDITOR5,
	page,
}: {
	container?: Locator;
	editorType?: EEditorType;
	page: Page;
}) {
	if (editorType === EEditorType.CKEDITOR5) {
		const container = containerProp ?? page.locator('.lfr-ck').first();

		await container.locator('.ck-content').waitFor({state: 'visible'});
	}
	else if (editorType === EEditorType.CKEDITOR4) {
		const container = containerProp ?? page.locator('.cke');

		await container
			.frameLocator('iframe')
			.locator('.cke_editable')
			.waitFor({state: 'visible'});
	}
	else {
		const container =
			containerProp ?? page.locator('.alloy-editor-container');

		await container.locator('.ae-editable').waitFor({state: 'visible'});
	}
}

export enum EFDSVisualizationMode {
	CARDS = 'cards',
	LIST = 'list',
	TABLE = 'table',
}

export async function waitForFDS({
	empty = false,
	page,
	visualizationMode = EFDSVisualizationMode.TABLE,
}: {
	empty?: boolean;
	page: Page;
	visualizationMode?: EFDSVisualizationMode;
}) {
	if (empty) {
		await page.locator('.fds .c-empty-state').waitFor({state: 'visible'});

		return;
	}

	if (visualizationMode === EFDSVisualizationMode.CARDS) {
		await page.locator('.fds .cards-container').waitFor({state: 'visible'});
	}
	else if (visualizationMode === EFDSVisualizationMode.LIST) {
		await page.locator('.fds .list-sheet').waitFor({state: 'visible'});
	}
	else if (visualizationMode === EFDSVisualizationMode.TABLE) {
		await page.locator('.fds .table').waitFor({state: 'visible'});
	}
}

export async function waitForInputLocalized(page: Page, id: string) {
	await page.evaluate(async (id) => await Liferay.componentReady(id), id);
}

/**
 * This utility is just for modals that don't have content in iframe. If the
 * content is in iframe, then we must wait until iframe content is loaded,
 * and check something within that content.
 */
export async function waitForModal({
	container: containerProp,
	innerElement: innerElementProp,
	page,
}: {
	container?: Locator;
	innerElement?: Locator;
	page: Page;
}) {
	const container = containerProp ?? page.locator('.modal');

	const innerElement =
		innerElementProp ?? container.getByRole('button').first();

	await innerElement.waitFor({state: 'visible'});

	// Wait until opening animation is complete.

	await innerElement.scrollIntoViewIfNeeded();
}
