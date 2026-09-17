/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import POM from '../../../../utils/POM';

export class AUISamplePage extends POM {
	constructor(page: Page, url: string) {
		super(page, url);
	}

	getChecker(): Locator {
		return this.page.getByTestId('auiDeprecationChecker');
	}

	getModule(moduleKey: string): Locator {
		return this.page.locator(`[data-module-key="${moduleKey}"]`);
	}

	getSummary(): Locator {
		return this.page.getByTestId('auiDeprecationCheckerSummary');
	}

	override async waitFor() {
		await expect(this.getChecker()).toHaveAttribute(
			'data-status',
			/pass|fail/
		);
	}
}
