/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React, {useState} from 'react';

import {
	getCurrentWallClockDateTime,
	isCompleteDateTime,
} from '../../utils/dateTime';
import FieldDatePicker from '../forms/FieldDatePicker';
import {EditingState, YEARS_OFFSET} from './types';
import {getValidation} from './utils';

type Props = {
	editing: EditingState;
	errors: ReturnType<typeof getValidation>['errors'];
	handleUpdateFilter: (payload: Partial<EditingState>) => void;
	timeZoneId: string;
};

function getEndDefaultTime(date: string, timeZoneId: string): string {
	const [today, time] = getCurrentWallClockDateTime(timeZoneId).split(' ');

	return date === today ? time : '23:59';
}

const DateRangeFields = ({
	editing,
	errors,
	handleUpdateFilter,
	timeZoneId,
}: Props) => {
	const [endDateTouched, setEndDateTouched] = useState(false);
	const [startDateTouched, setStartDateTouched] = useState(false);

	const currentYear = new Date().getFullYear();

	return (
		<>
			<ClayLayout.Col md={3}>
				<FieldDatePicker
					defaultTime="00:00"
					errorMessage={
						startDateTouched ||
						isCompleteDateTime(editing.startDate)
							? errors.startDate
							: undefined
					}
					formGroupProps={{className: 'mb-0'}}
					id="startDate"
					label={Liferay.Language.get('from')}
					name="startDate"
					onBlur={() => setStartDateTouched(true)}
					onChange={(value) =>
						handleUpdateFilter({startDate: value as string})
					}
					time
					value={editing.startDate}
					years={{
						end: currentYear,
						start: currentYear - YEARS_OFFSET,
					}}
				/>
			</ClayLayout.Col>

			<ClayLayout.Col md={3}>
				<FieldDatePicker
					defaultTime={(date) => getEndDefaultTime(date, timeZoneId)}
					errorMessage={
						endDateTouched || isCompleteDateTime(editing.endDate)
							? errors.endDate
							: undefined
					}
					formGroupProps={{className: 'mb-0'}}
					id="endDate"
					label={Liferay.Language.get('to[date-time]')}
					name="endDate"
					onBlur={() => setEndDateTouched(true)}
					onChange={(value) =>
						handleUpdateFilter({endDate: value as string})
					}
					time
					value={editing.endDate}
					years={{
						end: currentYear,
						start: currentYear - YEARS_OFFSET,
					}}
				/>
			</ClayLayout.Col>
		</>
	);
};

export default DateRangeFields;
