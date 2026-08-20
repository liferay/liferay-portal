/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useField} from 'formik';
import React from 'react';

import PublishScheduler from './PublishScheduler';
import {ScheduleValues, TimeZoneOption} from './types';
import {getScheduleValuesErrors} from './utils';

export function FormikFieldPublishScheduler({
	name,
	timeZones,
}: {
	name: string;
	timeZones: TimeZoneOption[];
}) {
	const [field, , helpers] = useField<ScheduleValues>(name);

	const scheduleValuesErrors = getScheduleValuesErrors(field.value);

	return (
		<PublishScheduler
			cronExpressionErrorMessage={scheduleValuesErrors.cronExpression}
			endDateTimeErrorMessage={scheduleValuesErrors.endDateTime}
			onChange={(scheduleValues) => helpers.setValue(scheduleValues)}
			startDateTimeErrorMessage={scheduleValuesErrors.startDateTime}
			timeZones={timeZones}
			value={field.value}
		/>
	);
}
