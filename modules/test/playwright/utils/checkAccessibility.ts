/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AxeBuilder from '@axe-core/playwright';

// eslint-disable-next-line
import {formatAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {Page, expect} from '@playwright/test';

interface Params {
	bestPractices?: boolean;
	page: Page;
	selectors?: string[];
	selectorsToExclude?: string[];
	soft?: boolean;
}

/**
 * Check accessibility on a page.
 *
 * It uses the axe API to analyze a page and return a JSON object that lists
 * any accessibility issues found.
 *
 * @param bestPractices Enables best practices
 * @param selectors An array of selectors to analyze
 * @param selectorsToExclude An array of selectors toexclude from the analysis
 * @param page Current page
 * @param soft A flag to enable soft assertions
 */

const TAGS = ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa', 'wcag22aa'];

export async function checkAccessibility({
	bestPractices = false,
	page,
	selectors,
	selectorsToExclude,
	soft = true,
}: Params) {
	const tags = bestPractices ? [...TAGS, 'best-practice'] : TAGS;

	const axeBuilder = new AxeBuilder({page});

	if (selectors) {
		for (const selector of selectors) {
			await page.locator(selector).waitFor();

			axeBuilder.include(selector);
		}
	}

	if (selectorsToExclude) {
		for (const selector of selectorsToExclude) {
			axeBuilder.exclude(selector);
		}
	}

	const {violations} = await axeBuilder.withTags(tags).analyze();

	(soft ? expect.soft : expect)(
		violations.length,
		formatAccessibility(violations)
	).toBe(0);
}
