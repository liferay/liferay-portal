/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import {
	getCollectionFilterValue,
	setCollectionFilterValue,
} from '@liferay/fragment-renderer-collection-filter-impl';
import {dateUtils} from 'frontend-js-web';
import React from 'react';

export function FragmentCollectionFilterDate({
	date,
	fragmentEntryLinkId,
	isDisabled,
	targetCollections,
}) {
	const value = getCollectionFilterValue(date, fragmentEntryLinkId);

	return (
		<ClayDatePicker
			ariaLabels={{
				buttonChooseDate: `${Liferay.Language.get('select-date')}`,
				buttonDot: `${Liferay.Language.get('select-current-date')}`,
				buttonNextMonth: `${Liferay.Language.get('select-next-month')}`,
				buttonPreviousMonth: `${Liferay.Language.get(
					'select-previous-month'
				)}`,
				dialog: `${Liferay.Language.get('select-date')}`,
				selectMonth: `${Liferay.Language.get('select-a-month')}`,
				selectYear: `${Liferay.Language.get('select-a-year')}`,
			}}
			disabled={isDisabled}
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
			onValueChange={(value) =>
				setCollectionFilterValue(
					date,
					fragmentEntryLinkId,
					value,
					targetCollections
				)
			}
			placeholder="YYYY-MM-DD"
			value={value}
			weekdaysShort={dateUtils.getWeekdaysShort()}
		/>
	);
}
