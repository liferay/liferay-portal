/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {liferayConfig} from '../../liferay.config';
import POM from '../../utils/POM';

export class ApiExplorerPage extends POM {
	readonly helpPopover: Locator;
	readonly loading: Locator;
	readonly page: Page;

	constructor(page: Page) {
		super(page, '/o/api');
		this.helpPopover = page.locator('.popover-body');
		this.loading = page.locator('.loading');
		this.page = page;
	}

	async expectEndpointWithParameters(
		endpointPath: string,
		parameters: string[]
	) {
		await this.getEndpointLocator(endpointPath).click();
		for (const parameter of parameters) {
			await expect(
				this.page
					.getByRole('row')
					.getByRole('cell')
					.getByText(parameter)
			).toBeVisible();
		}
	}

	async expectEndpointWithoutParameters(
		endpointPath: string,
		parameters: string[]
	) {
		await this.getEndpointLocator(endpointPath).click();
		for (const parameter of parameters) {
			await expect(
				this.page
					.getByRole('row')
					.getByRole('cell')
					.getByText(parameter)
			).toBeHidden();
		}
		await this.getEndpointLocator(endpointPath).click();
	}

	async goToApplication(applicationURL: string) {
		await this.page.goto(
			`/o/api?endpoint=${liferayConfig.environment.baseUrl}/o/${applicationURL}/openapi.json`
		);
		await this.waitFor();
	}

	getEndpointLocator(endpointPath: string, options?: object): Locator {
		return this.page.locator(
			`//span[@data-path="${endpointPath}"]/a/span`,
			options
		);
	}

	getOperationBlock(operationId: string): Locator {
		return this.page.locator(`[id$="${operationId}"]`);
	}

	async waitFor() {
		await this.loading.waitFor({state: 'hidden'});
	}
}
