/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {datetimeUtils} from '@liferay/object-js-components-web';
import {dateUtils} from 'frontend-js-web';
import React, {useCallback, useState} from 'react';

import type {FirstDayOfWeekLocale} from 'frontend-js-web';

interface IDateField {
	id: string;
	initialValue?: string;
	onChange: (value: string) => Promise<void> | void;
	required?: boolean;
}

export const dateConfig = datetimeUtils.generateDateConfigurations({
	defaultLanguageId: Liferay.ThemeDisplay.getDefaultLanguageId(),
	locale: Liferay.ThemeDisplay.getLanguageId(),
	type: 'Date',
});

export default function DateField({
	id,
	initialValue = '',
	onChange,
	required = true,
}: IDateField) {
	const [error, setError] = useState<string>('');
	const [value, setValue] = useState<string>(initialValue);

	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const handleError = useCallback(
		(value: string) => {
			if (required && !value) {
				setError(Liferay.Language.get('this-field-is-required'));
			}
			else {
				setError('');
			}
		},
		[required]
	);

	const handleChange = async (value: string) => {
		setError('');
		setValue(value);

		await onChange(value);
	};

	return (
		<ClayDatePicker
			aria-describedby={error}
			dateFormat={dateConfig.clayFormat}
			firstDayOfWeek={dateUtils.getFirstDayOfWeek(
				locale as FirstDayOfWeekLocale
			)}
			id={id}
			months={dateUtils.getMonthsLong(locale)}
			onBlur={({target: {value}}) => handleError(value)}
			onChange={handleChange}
			placeholder={dateConfig.momentFormat}
			value={value}
			weekdaysShort={dateUtils.getWeekdaysShort(locale)}
			years={{
				end: new Date().getFullYear() + 25,
				start: new Date().getFullYear() - 100,
			}}
		/>
	);
}
