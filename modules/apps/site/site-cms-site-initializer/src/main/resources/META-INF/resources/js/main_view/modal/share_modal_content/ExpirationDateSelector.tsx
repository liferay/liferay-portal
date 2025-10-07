/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import ClayDropDown from '@clayui/drop-down';
import {dateUtils} from 'frontend-js-web';
import React from 'react';

const CUTOFF_TIME = '23:59';

const INVALID_DATE = {
	EXPIRED: 'EXPIRED',
	NAN: 'NaN',
};

export function formatDateForView(date: string): string {
	const formattedDate = new Date(date.replace('--:--', CUTOFF_TIME));

	if (isNaN(formattedDate.getTime())) {
		return INVALID_DATE.NAN;
	}

	if (formattedDate < new Date()) {
		return INVALID_DATE.EXPIRED;
	}

	return formattedDate.toLocaleString(
		Liferay.ThemeDisplay.getBCP47LanguageId(),
		{
			day: 'numeric',
			hour: 'numeric',
			minute: 'numeric',
			month: 'short',
			year: 'numeric',
		}
	);
}

export function formatDateToISO(date: string): string {
	const formattedDate = new Date(date.replace('--:--', CUTOFF_TIME));

	return formattedDate.toISOString();
}

export default function ExpirationDateSelector({
	dateExpired,
	onChange,
}: {
	dateExpired?: string;
	onChange: (value: object) => void;
}) {
	const handleChange = (value: string) => {
		if (value === '') {
			onChange({dateExpired: '', error: ''});
		}
		else {
			const formattedDate = formatDateForView(value);

			onChange({
				dateExpired: value,
				error:
					formattedDate === INVALID_DATE.NAN
						? Liferay.Language.get(
								'please-select-a-valid-expiration-date'
							)
						: formattedDate === INVALID_DATE.EXPIRED
							? Liferay.Language.get(
									'please-enter-an-expiration-date-that-comes-after-today'
								)
							: '',
			});
		}
	};

	return (
		<ClayDropDown
			trigger={
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('set-expiration-date')}
					borderless
					className="inline-item inline-item-before lfr-portal-tooltip"
					displayType="secondary"
					monospaced
					size="xs"
					symbol="date-time"
					title={Liferay.Language.get('set-expiration-date')}
				/>
			}
		>
			<ClayDropDown.ItemList>
				<ClayDropDown.Section>
					<ClayDatePicker
						firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
						months={[
							`${Liferay.Language.get('january')}`,
							`${Liferay.Language.get('february')}`,
							`${Liferay.Language.get('march')}`,
							`${Liferay.Language.get('april')}`,
							`${Liferay.Language.get('may')}`,
							`${Liferay.Language.get('june')}`,
							`${Liferay.Language.get('july')}`,
							`${Liferay.Language.get('august')}`,
							`${Liferay.Language.get('september')}`,
							`${Liferay.Language.get('october')}`,
							`${Liferay.Language.get('november')}`,
							`${Liferay.Language.get('december')}`,
						]}
						onChange={handleChange}
						placeholder={Liferay.Language.get('yyyy-mm-dd hh:mm')}
						time={true}
						value={dateExpired}
						years={{
							end: new Date().getFullYear(),
							start: 1998,
						}}
					/>
				</ClayDropDown.Section>
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
