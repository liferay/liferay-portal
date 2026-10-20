/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function intervalToDuration(start, end) {
	const intervalMiliseconds = end - start;
	const duration = {};

	const seconds = Math.floor(intervalMiliseconds / 1000);
	duration.seconds = seconds % 60;

	const minutes = Math.floor(seconds / 60);
	duration.minutes = minutes % 60;

	const hours = Math.floor(minutes / 60);
	duration.hours = hours % 24;

	const days = Math.floor(hours / 24);
	duration.days = days % 30;

	const months = Math.floor(days / 30);
	duration.months = months % 12;

	duration.years = Math.floor(months / 12);

	return duration;
}

function hoursToMilliseconds(hours) {
	return hours * 60 * 60 * 1000;
}

function minutesToMilliseconds(minutes) {
	return minutes * 60 * 1000;
}

export function durationAsMilliseconds(days = 0, fullHours) {
	const [hours = 0, minutes = 0] = fullHours.split(':');

	return (
		hoursToMilliseconds(Number(days) * 24 + Number(hours)) +
		minutesToMilliseconds(Number(minutes))
	);
}

export function formatDuration(millisecondsDuration) {
	const duration = getDurationValues(millisecondsDuration);

	const durationParts = [
		{
			label: Liferay.Language.get('days-abbreviation'),
			value: duration.days,
		},
		{
			label: Liferay.Language.get('hours-abbreviation'),
			value: duration.hours,
		},
		{
			label: Liferay.Language.get('minutes-abbreviation'),
			value: duration.minutes,
		},
	].filter((part) => part.value > 0);

	if (!durationParts.length) {
		return `${duration.seconds ? 1 : 0}${Liferay.Language.get(
			'minutes-abbreviation'
		)}`;
	}

	return durationParts.map((part) => `${part.value}${part.label}`).join(' ');
}

export function formatHours(hours, minutes) {
	const padHours = (value) =>
		(value && value.toString().padStart(2, '0')) || '00';

	if (hours || minutes) {
		return [hours, minutes].map(padHours).join(':');
	}

	return '';
}

export function getDurationValues(durationValue) {
	const fullDuration = intervalToDuration(
		new Date(0),
		new Date(durationValue)
	);

	return {
		days: fullDuration.days || null,
		hours: fullDuration.hours || null,
		minutes: fullDuration.minutes || null,
		seconds: fullDuration.seconds || null,
	};
}

export function remainingTimeFormat(
	onTime,
	remainingTime = 0,
	ignoreZeros = false
) {
	const remainingTimePositive = onTime ? remainingTime : remainingTime * -1;

	const {days, hours, minutes, seconds} = intervalToDuration(
		new Date(0, 0, 0, 0, 0, 0, 0),
		new Date(0, 0, 0, 0, 0, 0, remainingTimePositive)
	);

	let durationText = '';

	if (ignoreZeros) {
		if (Number(days) > 0) {
			durationText += `${days}d `;
		}

		if (Number(hours) > 0) {
			durationText += `${hours}h `;
		}

		if (Number(minutes) > 0) {
			durationText += `${minutes}min`;
		}

		if (!durationText) {
			durationText += `${seconds}sec`;
		}

		durationText = String(durationText).trimEnd();
	}
	else {
		durationText = `${days}d ${hours}h ${minutes}min`;
	}

	const onTimeText = onTime
		? Liferay.Language.get('left[remaining]')
		: Liferay.Language.get('overdue');

	return [durationText, onTimeText];
}
