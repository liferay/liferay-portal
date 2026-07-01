/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PieDatum} from '../types/PieDatum';

export function computePrecedingTotals(data: PieDatum[]): number[] {
	const precedingTotals: number[] = [];
	let runningTotal = 0;

	for (const datum of data) {
		precedingTotals.push(runningTotal);
		runningTotal += Math.max(0, datum.value);
	}

	return precedingTotals;
}
