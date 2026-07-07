/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const MIN_STEPS = 2;
const MAX_STEPS = 6;
const DEFAULT_STEPS = 5;

export function getClampedSteps(steps: number): number {
	const safeSteps = Number.isFinite(steps) ? steps : DEFAULT_STEPS;

	return Math.min(MAX_STEPS, Math.max(MIN_STEPS, safeSteps));
}
