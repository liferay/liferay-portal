/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(loginTest());

const TOAST_SELECTOR =
	'#ToastAlertContainer .alert-notifications-fixed > .alert';

async function expectBoxesToStay({
	expandedIndexes,
	expectedBoxes,
	page,
}: {
	expandedIndexes: number[];
	expectedBoxes: DOMRect[];
	page: Page;
}) {
	const boxes = await getBoxes(page);

	expect(boxes).toHaveLength(expectedBoxes.length);

	boxes.forEach((box, index) => {
		expect(
			Math.abs(box.bottom - expectedBoxes[index].bottom)
		).toBeLessThanOrEqual(1);

		if (!expandedIndexes.includes(index)) {
			expect(
				Math.abs(box.top - expectedBoxes[index].top)
			).toBeLessThanOrEqual(1);
		}
	});
}

async function getBoxes(page: Page): Promise<DOMRect[]> {
	return page
		.locator(TOAST_SELECTOR)
		.evaluateAll((elements) =>
			elements.map((element) => element.getBoundingClientRect().toJSON())
		);
}

async function isInsideToast(locator: Locator) {
	return locator.evaluate((element) => {
		const box = element.getBoundingClientRect();
		const toastBox = element.closest('.alert')!.getBoundingClientRect();

		return (
			box.bottom <= toastBox.bottom + 1 &&
			box.left >= toastBox.left - 1 &&
			box.right <= toastBox.right + 1 &&
			box.top >= toastBox.top - 1
		);
	});
}

async function isOnTop(locator: Locator) {
	return locator.evaluate((element) => {
		const {height, left, top, width} = element.getBoundingClientRect();

		return [
			[left + width / 2, top + 4],
			[left + width / 2, top + height / 2],
			[left + width / 2, top + height - 4],
		].every(([x, y]) => element.contains(document.elementFromPoint(x, y)));
	});
}

async function openToasts({
	message,
	page,
	total,
}: {
	message?: string;
	page: Page;
	total: number;
}) {
	const count = await page.locator(TOAST_SELECTOR).count();

	await page.evaluate(
		({count, message, total}) => {
			const types = ['success', 'info', 'warning', 'danger'] as const;

			for (let i = count + 1; i <= total; i++) {
				Liferay.Util.openToast({
					autoClose: false,
					message: message ?? `Toast ${i}.`,
					type: types[(i - 1) % types.length],
				});
			}
		},
		{count, message, total}
	);

	await expect(page.locator(TOAST_SELECTOR)).toHaveCount(total);
}

test.beforeEach(async ({page}) => {
	await page.goto('/');
});

test('Stacks toasts as they pile up', {tag: '@LPD-109681'}, async ({page}) => {
	const toasts = page.locator(TOAST_SELECTOR);
	const tall = toasts.first();

	await test.step('6 toasts are not stacked', async () => {
		await openToasts({
			message: 'Line 1.<br>Line 2.<br><a href="#">Line 3</a>',
			page,
			total: 1,
		});
		await openToasts({page, total: 6});

		const boxes = await getBoxes(page);

		expect(boxes[0].height).toBeGreaterThan(boxes[1].height);

		for (let i = 1; i < boxes.length; i++) {
			expect(boxes[i].top).toBeGreaterThanOrEqual(boxes[i - 1].bottom);
		}

		for (const toast of await toasts.all()) {
			await expect(
				isInsideToast(toast.getByRole('button', {name: 'Close'}))
			).resolves.toBe(true);
		}
	});

	await test.step('7 toasts narrow the older ones', async () => {
		await openToasts({page, total: 7});

		const boxes = await getBoxes(page);

		expect(boxes[0].width).toBeLessThan(boxes[6].width);

		await expect(toasts.last()).toBeInViewport({ratio: 1});
	});

	await test.step('13 toasts overlap', async () => {
		await openToasts({page, total: 13});

		const boxes = await getBoxes(page);

		expect(boxes[1].top).toBeLessThan(boxes[0].bottom);
		expect(boxes[0].height).toBe(boxes[1].height);

		await expect(tall).toHaveCSS('overflow', 'hidden');
		await expect(
			isInsideToast(tall.locator('.alert-autofit-row'))
		).resolves.toBe(false);

		await expect(toasts.last()).toBeInViewport({ratio: 1});
		await expect(isOnTop(toasts.last())).resolves.toBe(true);
	});

	await test.step('21 toasts hide the older text visually only', async () => {
		await openToasts({page, total: 21});

		await expect(toasts.first().locator('.alert-autofit-row')).toHaveCSS(
			'opacity',
			'0'
		);
		await expect(toasts.last().locator('.alert-autofit-row')).toHaveCSS(
			'opacity',
			'1'
		);

		await expect(
			page.locator('#ToastAlertContainer').getByRole('alert')
		).toHaveCount(21);
		await expect(tall).toContainText('Line 1.');

		await expect(toasts.last()).toBeInViewport({ratio: 1});
		await expect(isOnTop(toasts.last())).resolves.toBe(true);
	});

	await test.step('hovering an older toast raises it', async () => {
		const before = await getBoxes(page);

		await tall.hover({position: {x: before[0].width / 2, y: 4}});

		await expect(tall).toHaveCSS('position', 'absolute');
		await expect(tall.locator('.alert-autofit-row')).toHaveCSS(
			'opacity',
			'1'
		);
		await expectBoxesToStay({
			expandedIndexes: [0],
			expectedBoxes: before,
			page,
		});
		await expect(
			isInsideToast(tall.locator('.alert-autofit-row'))
		).resolves.toBe(true);
		await expect(tall).toBeInViewport({ratio: 1});
		await expect(isOnTop(tall)).resolves.toBe(true);
	});

	await test.step('focusing an older toast raises it and Enter closes it', async () => {
		await page.mouse.move(0, 0);

		const before = await getBoxes(page);
		const link = tall.getByRole('link', {name: 'Line 3'});

		await link.focus();

		await expect(tall.locator('.alert-autofit-row')).toHaveCSS(
			'opacity',
			'1'
		);
		await expectBoxesToStay({
			expandedIndexes: [0],
			expectedBoxes: before,
			page,
		});
		await expect(
			isInsideToast(tall.locator('.alert-autofit-row'))
		).resolves.toBe(true);
		await expect(link).toBeInViewport({ratio: 1});
		await expect(isOnTop(tall)).resolves.toBe(true);

		await link.blur();
		await toasts.nth(1).hover({position: {x: before[1].width / 2, y: 4}});
		await link.focus();

		await expectBoxesToStay({
			expandedIndexes: [0, 1],
			expectedBoxes: before,
			page,
		});

		await tall.getByRole('button', {name: 'Close'}).focus();
		await page.keyboard.press('Enter');

		await expect(toasts).toHaveCount(20);
	});
});

test(
	'Fits the toasts in a 320px wide viewport',
	{tag: '@LPD-109681'},
	async ({page}) => {
		await page.setViewportSize({height: 640, width: 320});

		const getOverflow = () =>
			page.evaluate(
				() =>
					document.documentElement.scrollWidth -
					document.documentElement.clientWidth
			);

		const overflowBefore = await getOverflow();

		await openToasts({page, total: 21});

		await expect(getOverflow()).resolves.toBeLessThanOrEqual(
			overflowBefore
		);

		for (const box of await getBoxes(page)) {
			expect(box.left).toBeGreaterThanOrEqual(0);
			expect(box.right).toBeLessThanOrEqual(320);
		}
	}
);
