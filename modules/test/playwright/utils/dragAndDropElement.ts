/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export default async function dragAndDropElement({
	dragTarget,
	dropTarget,
	force = false,
	onDragging,
	page,
}: {
	dragTarget: Locator;
	dropTarget: Locator;
	force?: boolean;
	onDragging?: () => Promise<void>;
	page: Page;
}) {
	await dragTarget.hover({force});

	await page.mouse.down();

	await dropTarget.hover({force});

	const boundingClientRect = await dropTarget.evaluate((element) =>
		element.getBoundingClientRect()
	);

	await dropTarget.hover({
		force,
		position: {
			x: boundingClientRect.width / 2,
			y: boundingClientRect.height / 2,
		},
	});

	await onDragging?.();

	await page.mouse.up();
}
