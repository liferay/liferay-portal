/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ETimeZoneBehaviors} from '../constants';

interface IDateTimeRendererOptions {
	format?: {
		day?: 'numeric' | '2-digit';
		hour?: 'numeric' | '2-digit';
		minute?: 'numeric' | '2-digit';
		month?: 'numeric' | '2-digit' | 'short' | 'long' | 'narrow';
		second?: 'numeric' | '2-digit';
		timeZone?: string;
		year?: 'numeric' | '2-digit';
	};
	timeZoneBehavior?: ETimeZoneBehaviors;
}

function DateTimeRenderer({
	options,
	value,
}: {
	options?: IDateTimeRendererOptions;
	value: number | string;
}) {
	if (!value) {
		return null;
	}

	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const dateOptions = {
		day: options?.format?.day || 'numeric',
		hour: options?.format?.hour || 'numeric',
		minute: options?.format?.minute || 'numeric',
		month: options?.format?.month || 'short',
		second: options?.format?.second || 'numeric',
		timeZone: options?.format?.timeZone || 'UTC',
		year: options?.format?.year || 'numeric',
	};

	if (
		options?.timeZoneBehavior ===
		ETimeZoneBehaviors.APPLY_THEME_DISPLAY_TIME_ZONE
	) {
		dateOptions.timeZone = Liferay.ThemeDisplay.getTimeZone();
	}

	const formattedDate = new Intl.DateTimeFormat(locale, dateOptions).format(
		new Date(value)
	);

	return formattedDate;
}

export default DateTimeRenderer;
