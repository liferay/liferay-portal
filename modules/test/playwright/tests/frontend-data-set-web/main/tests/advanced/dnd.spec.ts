/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EFDSVisualizationMode} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

let dataTransfer: any;

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Advanced');

	await expect(
		page.getByText('This is a description for sample 1.')
	).toBeVisible();

	dataTransfer = await page.evaluateHandle(
		(data) => {
			const dt = new DataTransfer();

			const file = new File([data.toString('hex')], 'image1.jpeg', {
				type: 'image/jpg',
			});
			dt.items.add(file);

			return dt;
		},
		readFileSync(path.join(__dirname, '../../dependencies/image1.jpeg'))
	);
});

test(
	'Check drop files behavior',
	{tag: '@LPD-44645'},
	async ({fdsSamplePage, page}) => {
		const containsClass = async (
			locator: Locator,
			classes: string[]
		): Promise<boolean> => {
			const classList: string = await locator.getAttribute('class');

			for (const className of classes) {
				if (!classList.includes(className)) {
					return false;
				}
			}

			return true;
		};

		const dragFileOverLocator = async (
			locator: Locator,
			expectedClass?: string[]
		) => {
			await locator.dispatchEvent('dragstart', {dataTransfer});
			await locator.dispatchEvent('dragenter', {dataTransfer});
			await locator.dispatchEvent('dragover', {dataTransfer});

			expectedClass &&
				expect(
					await containsClass(locator, expectedClass)
				).toBeTruthy();
		};

		const dropFileOverLocator = async (
			locator: Locator,
			expectedText: string
		) => {
			await locator.dispatchEvent('dragstart', {dataTransfer});
			await locator.dispatchEvent('dragenter', {dataTransfer});
			await locator.dispatchEvent('dragover', {dataTransfer});
			await locator.dispatchEvent('drop', {dataTransfer});
			await locator.dispatchEvent('dragend', {dataTransfer});

			await expect(fdsSamplePage.fileDropModal).toBeInViewport();
			await expect(fdsSamplePage.fileDropModal).toContainText(
				expectedText
			);

			await fdsSamplePage.fileDropModal.focus();

			await page.keyboard.press('Escape');
		};

		const stopDragFileOverLocator = async (locator: Locator) => {
			await locator.dispatchEvent('dragleave', {dataTransfer});
			await locator.dispatchEvent('dragend', {dataTransfer});

			await expect(locator).not.toHaveClass('drop-target');
		};

		await test.step('drag files over FDS main area highlights it', async () => {
			await dragFileOverLocator(fdsSamplePage.fdsWrapper, [
				'data-set-wrapper',
				'visualization-mode-table',
				'drop-target',
			]);
			await stopDragFileOverLocator(fdsSamplePage.fdsWrapper);
		});

		await test.step('drop files over FDS main area', async () => {
			await dropFileOverLocator(
				fdsSamplePage.fdsWrapper,
				'no specific drop target'
			);
		});

		const visualizations = [
			{
				initialClass: ['card'],
				itemLocatorContainer: fdsSamplePage.cards.items,
				visualizationMode: EFDSVisualizationMode.CARDS,
				wrapperClass: 'visualization-mode-cards',
			},
			{
				initialClass: ['list-group-item', 'list-group-item-flex'],
				itemLocatorContainer: fdsSamplePage.list.items,
				visualizationMode: EFDSVisualizationMode.LIST,
				wrapperClass: 'visualization-mode-list',
			},
			{
				initialClass: [],
				itemLocatorContainer: fdsSamplePage.table.bodyRows,
				visualizationMode: EFDSVisualizationMode.TABLE,
				wrapperClass: 'visualization-mode-table',
			},
		];

		for (const visualization of visualizations) {
			const {
				initialClass,
				itemLocatorContainer,
				visualizationMode,
				wrapperClass,
			} = visualization as any;

			await fdsSamplePage.changeVisualizationMode({
				page,
				visualizationMode,
			});

			const blueItem = itemLocatorContainer
				.filter({
					hasText: 'sample 100.',
				})
				.first();

			await test.step(`drag files over a droppable ${visualizationMode} item highlights it, FDS main area is not highlighted`, async () => {
				await dragFileOverLocator(blueItem, [
					'drop-target',
					...initialClass,
				]);

				await expect(fdsSamplePage.fdsWrapper).toHaveClass(
					`data-set-wrapper ${wrapperClass}`
				);

				await stopDragFileOverLocator(blueItem);
			});

			await test.step(`drop files over a droppable ${visualizationMode} item, includes item info`, async () => {
				await dropFileOverLocator(blueItem, 'Dropped on item');
			});

			const greenItem = itemLocatorContainer
				.filter({
					hasText: 'sample 1.',
				})
				.first();

			await test.step(`drag files over a non-droppable ${visualizationMode} highlights the main area, no item area is highlighted`, async () => {
				await dragFileOverLocator(greenItem);

				await expect(fdsSamplePage.fdsWrapper).toHaveClass(
					`data-set-wrapper ${wrapperClass} drop-target`
				);

				await expect(greenItem).not.toHaveClass('drop-target');

				await stopDragFileOverLocator(greenItem);
			});

			await test.step(`drop files over a non-droppable ${visualizationMode} item, does not include item info`, async () => {
				await dropFileOverLocator(greenItem, 'no specific drop target');
			});
		}
	}
);
