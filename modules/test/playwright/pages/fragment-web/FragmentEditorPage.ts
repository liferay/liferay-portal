/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export class FragmentEditorPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async addConfiguration(configuration: string) {

		// Go to Configuration Tab

		await this.page.getByRole('tab', {name: 'Configuration'}).waitFor();

		await this.page.getByRole('tab', {name: 'Configuration'}).click();
		await this.page.getByText('Add the JSON configuration').waitFor();

		// Delete current configuration

		const codeMirror = this.page.locator('.CodeMirror-scroll').last();
		await codeMirror.click();

		await this.page.keyboard.press('Control+KeyA');
		await this.page.keyboard.press('Backspace');

		await this.page.getByText('Changes Saved').waitFor();

		// Fill with new configuration

		await this.page.keyboard.type(configuration);
		await this.page.getByText('Changes Saved').waitFor();
	}

	async addHTML(html: string) {

		// Go to Code Tab

		await this.page.getByRole('tab', {name: 'Code'}).click();

		// Fill with new value

		await this.page
			.locator('.html.source-editor .CodeMirror')
			.evaluate(
				(element: any, html) => element.CodeMirror.setValue(html),
				html
			);

		await this.page.getByText('Changes Saved').waitFor();
	}

	async publish() {
		await this.page.getByRole('button', {name: 'Publish'}).click();

		await waitForAlert(this.page);
	}
}
