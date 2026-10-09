/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	pageEditorPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'CKEditor 5 displays correctly in modal with cadmin',
	{tag: '@LPD-89211'},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		const widgetId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: widgetId,
					widgetName:
						'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToWidgetConfiguration(widgetId);

		const configurationIframe = page.frameLocator(
			'iframe[title*="Configuration"]'
		);

		await configurationIframe
			.getByText('Subscriptions', {exact: true})
			.first()
			.click();

		const toolbarButton = configurationIframe
			.locator('.cadmin .ck.ck-toolbar .ck-button')
			.first();

		await expect(toolbarButton).toBeVisible();

		const padding = await toolbarButton.evaluate(
			(element) => getComputedStyle(element).padding
		);

		expect(padding).not.toBe('0px');

		const minWidth = await toolbarButton.evaluate(
			(element) => getComputedStyle(element).minWidth
		);

		expect(minWidth).not.toBe('0px');
		expect(parseFloat(minWidth)).toBeGreaterThan(0);

		const borderWidth = await toolbarButton.evaluate(
			(element) => getComputedStyle(element).borderWidth
		);

		expect(borderWidth).not.toBe('0px');
	}
);

test(
	'Dark high contrast palette does not apply on pages set to the light color scheme',
	{tag: '@LPD-108598'},
	async ({page}) => {
		await page.emulateMedia({colorScheme: 'dark', contrast: 'more'});

		await page.goto('/group/control_panel/manage');

		await expect(page.locator('html')).toHaveAttribute(
			'data-color-scheme',
			'light'
		);

		await expect(page.locator('body')).toHaveCSS(
			'background-color',
			'rgb(255, 255, 255)'
		);

		const cadminWhite = await page
			.locator('.cadmin')
			.first()
			.evaluate((element) => {
				const child = document.createElement('div');

				child.style.backgroundColor = 'var(--white)';

				element.appendChild(child);

				const backgroundColor = getComputedStyle(child).backgroundColor;

				child.remove();

				return backgroundColor;
			});

		expect(cadminWhite).toBe('rgb(255, 255, 255)');
	}
);

async function getResolvedColor(locator: Locator, value: string) {
	return locator.evaluate((element, value) => {
		const child = document.createElement('div');

		child.style.color = value;

		element.appendChild(child);

		const color = getComputedStyle(child).color;

		child.remove();

		return color;
	}, value);
}

test(
	'High contrast class applies the high contrast palette in both color schemes',
	{tag: '@LPD-108865'},
	async ({page}) => {
		await page.goto('/group/control_panel/manage');

		const body = page.locator('body');
		const cadmin = page.locator('.cadmin').first();

		await body.evaluate((element) =>
			element.classList.add('c-prefers-high-contrast')
		);

		await test.step('Light color scheme', async () => {
			await expect(page.locator('html')).toHaveAttribute(
				'data-color-scheme',
				'light'
			);

			await expect(body).toHaveCSS(
				'background-color',
				'rgb(255, 255, 255)'
			);

			expect(await getResolvedColor(body, 'var(--link-color)')).toBe(
				'rgb(0, 40, 117)'
			);

			expect(
				await getResolvedColor(cadmin, 'var(--cadmin-link-color)')
			).toBe('rgb(0, 40, 117)');
		});

		await test.step('Dark color scheme', async () => {
			await page
				.locator('html')
				.evaluate((element) =>
					element.setAttribute('data-color-scheme', 'dark')
				);

			await expect(body).toHaveCSS('background-color', 'rgb(17, 17, 22)');

			expect(await getResolvedColor(body, 'var(--link-color)')).toBe(
				'rgb(179, 205, 255)'
			);

			expect(
				await getResolvedColor(cadmin, 'var(--cadmin-link-color)')
			).toBe('rgb(179, 205, 255)');
		});
	}
);

test(
	'Light high contrast palette applies on light pages when the OS prefers more contrast',
	{tag: '@LPD-108865'},
	async ({page}) => {
		await page.emulateMedia({colorScheme: 'light', contrast: 'more'});

		await page.goto('/group/control_panel/manage');

		await expect(page.locator('html')).toHaveAttribute(
			'data-color-scheme',
			'light'
		);

		expect(
			await getResolvedColor(page.locator('body'), 'var(--link-color)')
		).toBe('rgb(0, 40, 117)');

		expect(
			await getResolvedColor(
				page.locator('.cadmin').first(),
				'var(--cadmin-link-color)'
			)
		).toBe('rgb(0, 40, 117)');
	}
);
