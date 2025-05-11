/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function round(value: number, precision = 0) {
	const factor = Math.pow(10, precision);

	return Math.round(value * factor) / factor;
}

export function toThousands(number: number) {
	if (number < 1e3) {
		return String(round(number, 2));
	}

	let factor = 1e-3;
	let suffix = 'K';

	if (number >= 1e6 && number < 1e9) {
		factor = 1e-6;
		suffix = 'M';
	}
	else if (number >= 1e9 && number < 1e12) {
		factor = 1e-9;
		suffix = 'B';
	}
	else if (number >= 1e12) {
		factor = 1e-12;
		suffix = 'T';
	}

	return `${round(number * factor, 2)}${suffix}`.toUpperCase();
}
