/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import React from 'react';

import '../../../../../css/utilities.scss';
import SectionHeader from '../../../../components/SectionHeader';
import FieldDatePicker from '../../../../components/forms/FieldDatePicker';
import {FieldRadio} from '../../../../components/forms/FieldRadio';
import FieldSelectWithOption from '../../../../components/forms/FieldSelectWithOption';
import {
	getCurrentWallClockDateTime,
	toTimeParts,
} from '../../../../utils/dateTime';
import {toCustomCronExpression} from './cron';
import {
	CustomCronFields,
	EndDateFields,
	MonthlyFields,
	RepeatAtFields,
	WeeklyFields,
	YearlyFields,
} from './fields';
import {getScheduleSummary} from './summary';
import {
	IntervalUnit,
	RepeatType,
	ScheduleValues,
	TimeZoneOption,
} from './types';
import {
	REPEAT_OPTIONS,
	REPEAT_TYPE_OPTIONS,
	getSelectedMonthDays,
	getSelectedMonths,
	isRepeatingUnit,
} from './utils';

function getStartDefaultTime(date: string, timeZoneId: string): string {
	const [today, time] = getCurrentWallClockDateTime(timeZoneId).split(' ');

	if (date !== today) {
		return '00:00';
	}

	const nextHour = toTimeParts(time).hour + 1;

	return nextHour > 23 ? '23:59' : `${String(nextHour).padStart(2, '0')}:00`;
}

export default function PublishScheduler({
	cronExpressionErrorMessage,
	endDateTimeErrorMessage,
	onChange,
	onCronExpressionBlur,
	onEndDateTimeBlur,
	onRepeatOnTimeBlur,
	onStartDateTimeBlur,
	repeatOnTimeErrorMessage,
	startDateTimeErrorMessage,
	timeZones,
	value,
}: {
	cronExpressionErrorMessage?: string;
	endDateTimeErrorMessage?: string;
	onChange: (scheduleValues: ScheduleValues) => void;
	onCronExpressionBlur?: () => void;
	onEndDateTimeBlur?: () => void;
	onRepeatOnTimeBlur?: () => void;
	onStartDateTimeBlur?: () => void;
	repeatOnTimeErrorMessage?: string;
	startDateTimeErrorMessage?: string;
	timeZones: TimeZoneOption[];
	value: ScheduleValues;
}) {
	const currentYear = new Date().getFullYear();

	const set = (partialScheduleValues: Partial<ScheduleValues>) =>
		onChange({...value, ...partialScheduleValues});

	const repeatsMonthlyOrYearly =
		value.unit === IntervalUnit.Month || value.unit === IntervalUnit.Year;

	const scheduleSummary = getScheduleSummary(value);

	return (
		<>
			<SectionHeader title={Liferay.Language.get('when-to-publish')} />

			<ClayForm.Group>
				<FieldRadio
					checked={!value.enabled}
					description={Liferay.Language.get(
						'the-process-starts-as-soon-as-you-publish'
					)}
					label={Liferay.Language.get('publish-now')}
					name="whenToPublish"
					onChange={() => set({enabled: false})}
					value="now"
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<FieldRadio
					checked={value.enabled}
					description={Liferay.Language.get(
						'choose-a-start-date-time-and-an-optional-recurrence'
					)}
					label={Liferay.Language.get('schedule-for-later')}
					name="whenToPublish"
					onChange={() => set({enabled: true})}
					value="schedule"
				/>
			</ClayForm.Group>

			{value.enabled && (
				<div className="mt-4">
					<ClayLayout.Row>
						<ClayLayout.Col md={6} size={12}>
							<FieldDatePicker
								defaultTime={(date) =>
									getStartDefaultTime(date, value.timeZoneId)
								}
								errorMessage={startDateTimeErrorMessage}
								id="publishScheduleStartDateTime"
								label={Liferay.Language.get('start-date')}
								name="publishScheduleStartDateTime"
								onBlur={onStartDateTimeBlur}
								onChange={(startDateTime) =>
									set({
										startDateTime: startDateTime as string,
									})
								}
								required
								time
								value={value.startDateTime}
								years={{
									end: currentYear + 10,
									start: currentYear,
								}}
							/>
						</ClayLayout.Col>

						<ClayLayout.Col md={6} size={12}>
							<FieldSelectWithOption
								label={Liferay.Language.get('time-zone')}
								name="publishScheduleTimeZoneId"
								onChange={(event) =>
									set({timeZoneId: event.target.value})
								}
								options={timeZones}
								value={value.timeZoneId}
							/>
						</ClayLayout.Col>
					</ClayLayout.Row>

					<ClayLayout.Row>
						<ClayLayout.Col md={6} size={12}>
							<FieldSelectWithOption
								label={Liferay.Language.get('repeat')}
								name="publishScheduleRepeat"
								onChange={(event) => {
									const unit = event.target
										.value as IntervalUnit;

									set({
										...(unit === IntervalUnit.Year
											? {
													monthDays: [
														getSelectedMonthDays(
															value
														)[0],
													],
													months: [
														getSelectedMonths(
															value
														)[0],
													],
												}
											: {months: []}),
										...(unit === IntervalUnit.Custom
											? {
													cronExpression:
														toCustomCronExpression(
															value
														),
												}
											: {}),
										unit,
										yearInterval: 1,
									});
								}}
								options={REPEAT_OPTIONS}
								value={value.unit}
							/>
						</ClayLayout.Col>

						{repeatsMonthlyOrYearly && (
							<ClayLayout.Col md={6} size={12}>
								<FieldSelectWithOption
									label={Liferay.Language.get('repeat-type')}
									name="publishScheduleRepeatType"
									onChange={(event) =>
										set({
											repeatType: event.target
												.value as RepeatType,
										})
									}
									options={REPEAT_TYPE_OPTIONS}
									value={value.repeatType}
								/>
							</ClayLayout.Col>
						)}
					</ClayLayout.Row>

					{value.unit === IntervalUnit.Custom && (
						<CustomCronFields
							errorMessage={cronExpressionErrorMessage}
							onBlur={onCronExpressionBlur}
							onChange={set}
							value={value}
						/>
					)}

					{value.unit === IntervalUnit.Week && (
						<WeeklyFields onChange={set} value={value} />
					)}

					{value.unit === IntervalUnit.Month && (
						<MonthlyFields onChange={set} value={value} />
					)}

					{value.unit === IntervalUnit.Year && (
						<YearlyFields onChange={set} value={value} />
					)}

					{isRepeatingUnit(value.unit) && (
						<RepeatAtFields
							errorMessage={repeatOnTimeErrorMessage}
							onBlur={onRepeatOnTimeBlur}
							onChange={set}
							value={value}
						/>
					)}

					{value.unit !== IntervalUnit.Never && (
						<EndDateFields
							errorMessage={endDateTimeErrorMessage}
							onBlur={onEndDateTimeBlur}
							onChange={set}
							value={value}
						/>
					)}

					{scheduleSummary && (
						<ClayAlert
							className="mb-0 mt-3"
							displayType="info"
							title={`${Liferay.Language.get('summary')}:`}
						>
							{scheduleSummary.map((sentence) => (
								<div key={sentence}>{sentence}</div>
							))}
						</ClayAlert>
					)}
				</div>
			)}
		</>
	);
}
