/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React from 'react';

import '../../../../../css/utilities.scss';
import {DATE_FORMAT} from '../../../../components/date_filter/types';
import FieldDatePicker from '../../../../components/forms/FieldDatePicker';
import {FieldRadio} from '../../../../components/forms/FieldRadio';
import FieldSelectWithOption from '../../../../components/forms/FieldSelectWithOption';
import FieldText from '../../../../components/forms/FieldText';
import {getScheduleSummary} from './summary';
import {
	IntervalUnit,
	MONTH_DAYS,
	RepeatType,
	ScheduleValues,
	TimeZoneOption,
	WEEKDAYS,
	YEAR_INTERVALS,
} from './types';
import {
	MONTHS,
	REPEAT_OPTIONS,
	REPEAT_TYPE_OPTIONS,
	WEEKDAY_ORDINAL_OPTIONS,
	getIntervalText,
	getWeekdayName,
} from './utils';

const MONTH_MAX_DAYS = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

const MONTH_VALUES = MONTHS.map((month) => month.value);

const DATE_TIME_PLACEHOLDER = `${DATE_FORMAT} HH:MM`.toUpperCase();

export default function PublishScheduler({
	cronExpressionErrorMessage,
	endDateTimeErrorMessage,
	onChange,
	startDateTimeErrorMessage,
	timeZones,
	value,
}: {
	cronExpressionErrorMessage?: string;
	endDateTimeErrorMessage?: string;
	onChange: (scheduleValues: ScheduleValues) => void;
	startDateTimeErrorMessage?: string;
	timeZones: TimeZoneOption[];
	value: ScheduleValues;
}) {
	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const currentYear = new Date().getFullYear();

	const set = (partialScheduleValues: Partial<ScheduleValues>) =>
		onChange({...value, ...partialScheduleValues});

	const toggleIn = (list: number[], item: number) => {
		if (!list.includes(item)) {
			return [...list, item];
		}

		if (list.length === 1) {
			return list;
		}

		return list.filter((value) => value !== item);
	};

	const repeatsOnDayOfWeek = value.repeatType === RepeatType.DayOfWeek;

	const scheduleSummary = getScheduleSummary(value);

	const selectedMonthDays = value.monthDays.length
		? value.monthDays
		: MONTH_DAYS;
	const selectedMonths = value.months.length ? value.months : MONTH_VALUES;

	const yearMonth = selectedMonths[0];

	const yearIntervalOptions = YEAR_INTERVALS.map((yearInterval) => ({
		label: getIntervalText(yearInterval, IntervalUnit.Year, locale),
		value: yearInterval,
	}));

	const monthDayOptions = MONTH_DAYS.slice(
		0,
		MONTH_MAX_DAYS[yearMonth - 1]
	).map((monthDay) => ({
		label: sub(Liferay.Language.get('day-x'), String(monthDay)),
		value: monthDay,
	}));

	const weekdayOptions = WEEKDAYS.map((weekday) => ({
		label: getWeekdayName(weekday, locale),
		value: weekday,
	}));

	const repeatEverySelect = (
		<FieldSelectWithOption
			label={Liferay.Language.get('repeat-every')}
			name="publishScheduleRepeatEvery"
			onChange={(event) =>
				set({yearInterval: Number(event.target.value)})
			}
			options={yearIntervalOptions}
			value={String(value.yearInterval)}
		/>
	);

	const toggleButtonGrid = (
		className: string,
		items: number[],
		selected: number[],
		getLabel: (item: number) => string,
		onToggle: (items: number[]) => void
	) => (
		<div className={classnames('date-part-grid', className)}>
			{items.map((item) => (
				<ClayButton
					aria-pressed={selected.includes(item)}
					displayType={
						selected.includes(item) ? 'primary' : 'secondary'
					}
					key={item}
					onClick={() => onToggle(toggleIn(selected, item))}
				>
					{getLabel(item)}
				</ClayButton>
			))}
		</div>
	);

	return (
		<ClayLayout.Sheet className="mt-4 option-group">
			<div className="mb-3 sheet-title">
				{Liferay.Language.get('when-to-publish')}
			</div>

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

			{value.enabled && (
				<div className="mt-4">
					<ClayLayout.Row>
						<ClayLayout.Col md={6} size={12}>
							<FieldDatePicker
								dateFormat={DATE_FORMAT}
								errorMessage={startDateTimeErrorMessage}
								id="publishScheduleStartDateTime"
								label={Liferay.Language.get('start-date')}
								name="publishScheduleStartDateTime"
								onChange={(startDateTime) =>
									set({
										startDateTime: startDateTime as string,
									})
								}
								placeholder={DATE_TIME_PLACEHOLDER}
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
														selectedMonthDays[0],
													],
													months: [yearMonth],
												}
											: {months: []}),
										unit,
										yearInterval: 1,
									});
								}}
								options={REPEAT_OPTIONS}
								value={value.unit}
							/>
						</ClayLayout.Col>

						{(value.unit === IntervalUnit.Month ||
							value.unit === IntervalUnit.Year) && (
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
						<ClayLayout.Row>
							<ClayLayout.Col size={12}>
								<FieldText
									errorMessage={cronExpressionErrorMessage}
									helpMessage={sub(
										Liferay.Language.get('for-example-x'),
										'0 30 15 ? * MON-FRI *'
									)}
									label={Liferay.Language.get(
										'cron-expression'
									)}
									name="publishScheduleCronExpression"
									onChange={(event) =>
										set({
											cronExpression: event.target.value,
										})
									}
									value={value.cronExpression}
								/>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}

					{value.unit === IntervalUnit.Week && (
						<ClayLayout.Row>
							<ClayLayout.Col size={12}>
								<ClayForm.Group>
									<label>
										{Liferay.Language.get('repeat-on')}
									</label>

									{toggleButtonGrid(
										'',
										WEEKDAYS,
										value.weekdays,
										(weekday) =>
											getWeekdayName(weekday, locale),
										(weekdays) => set({weekdays})
									)}
								</ClayForm.Group>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}

					{value.unit === IntervalUnit.Month && (
						<ClayLayout.Row>
							<ClayLayout.Col size={12}>
								<ClayForm.Group>
									<label>
										{Liferay.Language.get(
											'repeat-on-month'
										)}
									</label>

									{toggleButtonGrid(
										'month-grid',
										MONTH_VALUES,
										selectedMonths,
										(month) => MONTHS[month - 1].label,
										(months) => set({months})
									)}
								</ClayForm.Group>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}

					{value.unit === IntervalUnit.Month &&
						!repeatsOnDayOfWeek && (
							<ClayLayout.Row>
								<ClayLayout.Col size={12}>
									<ClayForm.Group>
										<label>
											{Liferay.Language.get('repeat-on')}
										</label>

										{toggleButtonGrid(
											'',
											MONTH_DAYS,
											selectedMonthDays,
											String,
											(monthDays) => set({monthDays})
										)}
									</ClayForm.Group>
								</ClayLayout.Col>
							</ClayLayout.Row>
						)}

					{value.unit === IntervalUnit.Year &&
						!repeatsOnDayOfWeek && (
							<ClayLayout.Row>
								<ClayLayout.Col md={6} size={12}>
									<FieldSelectWithOption
										label={Liferay.Language.get(
											'repeat-on-day'
										)}
										name="publishScheduleRepeatOnDay"
										onChange={(event) =>
											set({
												monthDays: [
													Number(event.target.value),
												],
											})
										}
										options={monthDayOptions}
										value={String(selectedMonthDays[0])}
									/>
								</ClayLayout.Col>

								<ClayLayout.Col md={6} size={12}>
									<FieldSelectWithOption
										label={Liferay.Language.get(
											'repeat-on-month'
										)}
										name="publishScheduleRepeatOnMonth"
										onChange={(event) => {
											const month = Number(
												event.target.value
											);

											set({
												monthDays: [
													Math.min(
														selectedMonthDays[0],
														MONTH_MAX_DAYS[
															month - 1
														]
													),
												],
												months: [month],
											});
										}}
										options={MONTHS}
										value={String(yearMonth)}
									/>
								</ClayLayout.Col>
							</ClayLayout.Row>
						)}

					{(value.unit === IntervalUnit.Month ||
						value.unit === IntervalUnit.Year) &&
						repeatsOnDayOfWeek && (
							<ClayLayout.Row>
								<ClayLayout.Col md={6} size={12}>
									<FieldSelectWithOption
										label={Liferay.Language.get(
											'repeat-on'
										)}
										name="publishScheduleWeekdayOrdinal"
										onChange={(event) =>
											set({
												weekdayOrdinal:
													event.target.value,
											})
										}
										options={WEEKDAY_ORDINAL_OPTIONS}
										value={value.weekdayOrdinal}
									/>
								</ClayLayout.Col>

								<ClayLayout.Col md={6} size={12}>
									<FieldSelectWithOption
										label={Liferay.Language.get('weekday')}
										name="publishScheduleWeekday"
										onChange={(event) =>
											set({
												weekday: Number(
													event.target.value
												),
											})
										}
										options={weekdayOptions}
										value={String(value.weekday)}
									/>
								</ClayLayout.Col>

								{value.unit === IntervalUnit.Year && (
									<ClayLayout.Col md={6} size={12}>
										<FieldSelectWithOption
											label={Liferay.Language.get(
												'repeat-on-month'
											)}
											name="publishScheduleRepeatOnMonth"
											onChange={(event) =>
												set({
													months: [
														Number(
															event.target.value
														),
													],
												})
											}
											options={MONTHS}
											value={String(yearMonth)}
										/>
									</ClayLayout.Col>
								)}
							</ClayLayout.Row>
						)}

					{value.unit === IntervalUnit.Year && (
						<ClayLayout.Row>
							<ClayLayout.Col md={6} size={12}>
								{repeatEverySelect}
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}

					<ClayLayout.Row>
						<ClayLayout.Col md={6} size={12}>
							<FieldDatePicker
								dateFormat={DATE_FORMAT}
								disabled={value.neverEnd}
								errorMessage={endDateTimeErrorMessage}
								id="publishScheduleEndDateTime"
								label={Liferay.Language.get('end-date')}
								name="publishScheduleEndDateTime"
								onChange={(endDateTime) =>
									set({
										endDateTime: endDateTime as string,
									})
								}
								placeholder={DATE_TIME_PLACEHOLDER}
								time
								value={value.endDateTime}
								years={{
									end: currentYear + 10,
									start: currentYear,
								}}
							/>
						</ClayLayout.Col>
					</ClayLayout.Row>

					<ClayCheckbox
						checked={value.neverEnd}
						label={Liferay.Language.get('never-end')}
						onChange={() => set({neverEnd: !value.neverEnd})}
					/>

					{scheduleSummary && (
						<ClayAlert
							className="mb-0 mt-3"
							displayType="info"
							title={`${Liferay.Language.get('summary')}:`}
						>
							{scheduleSummary}
						</ClayAlert>
					)}
				</div>
			)}
		</ClayLayout.Sheet>
	);
}
