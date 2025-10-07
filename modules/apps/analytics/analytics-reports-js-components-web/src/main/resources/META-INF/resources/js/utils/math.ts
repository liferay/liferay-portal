/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getPercentage(value: number) {
	return round(Math.abs(value), 2);
}

export function round(number: number, precision: number = 0) {
	const factor = Math.pow(10, precision);

	return Math.round(number * factor) / factor;
}

export function toThousands(number: number) {
	const setFactor = (factor: number) => round(number * factor, 2);

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

	return `${setFactor(factor)}${suffix}`.toUpperCase();
}

export function toDuration(
	time: number,
	format: string = 'DD[d] hh[h] mm[m] ss[s]'
): string {
	if (time === 0) {
		format = 'DD[d] hh[h] mm[m] s[s]';
	}

	const duration = new Date(time);

	const days = duration.getUTCDate() - 1;
	const hours = duration.getUTCHours();
	const minutes = duration.getUTCMinutes();
	const seconds = duration.getUTCSeconds();
	const formattedDuration = format
		.replace('DD', days.toString())
		.replace('hh', hours.toString().padStart(2, '0'))
		.replace('mm', minutes.toString().padStart(2, '0'))
		.replace('ss', seconds.toString().padStart(2, '0'))
		.replace('s', seconds.toString());

	return formattedDuration;
}
