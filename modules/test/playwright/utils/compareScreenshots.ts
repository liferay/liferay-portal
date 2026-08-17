/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';
import * as fs from 'fs';
import {getComparator} from 'playwright-core/lib/utils';

export function compareScreenshots(
	screenshotA: Buffer,
	screenshotB: Buffer,
	{
		errorMessage = 'The screenshots are not the same.',
		writeDiff = false,
	}: {
		errorMessage?: string;
		writeDiff?: boolean;
	} = {}
) {
	const comparator = getComparator('image/png');

	const buffer = comparator(screenshotA, screenshotB);

	if (buffer === null) {
		return;
	}

	if (writeDiff && buffer.diff) {
		const filePath = test.info().outputPath('diff.png');

		fs.writeFileSync(filePath, buffer.diff);

		throw new Error(`${errorMessage} Check the diff at "${filePath}".`);
	}

	throw new Error(errorMessage);
}
