/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import PublishScheduler from '../../../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/components/scheduler/PublishScheduler';
import {toWallClockDateTime} from '../../../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/components/scheduler/cron';
import {
	IntervalUnit,
	ScheduleValues,
} from '../../../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/components/scheduler/types';
import {getInitialScheduleValues} from '../../../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/components/scheduler/utils';

const user = userEvent.setup({delay: null});

const DAY = 24 * 60 * 60 * 1000;

const START_DATE_TIME = toWallClockDateTime(
	new Date(Date.now() + DAY).toISOString(),
	'UTC'
);

function renderPublishScheduler(
	partialScheduleValues: Partial<ScheduleValues>,
	onChange: (scheduleValues: ScheduleValues) => void = jest.fn()
) {
	return render(
		<PublishScheduler
			onChange={onChange}
			timeZones={[
				{label: '(UTC) Coordinated Universal Time', value: 'UTC'},
			]}
			value={{
				...getInitialScheduleValues('UTC'),
				...partialScheduleValues,
			}}
		/>
	);
}

describe('PublishScheduler', () => {
	it('shows the summary once the start date is set', () => {
		renderPublishScheduler({
			enabled: true,
			startDateTime: START_DATE_TIME,
		});

		expect(
			screen.getByText(
				'the-process-runs-once-on-x-at-x-and-does-not-repeat'
			)
		).toBeInTheDocument();
	});

	it('hides the summary while there is no start date', () => {
		renderPublishScheduler({enabled: true});

		expect(
			screen.queryByText(
				'the-process-runs-once-on-x-at-x-and-does-not-repeat'
			)
		).not.toBeInTheDocument();
	});

	it('shows the weekday buttons for a weekly repetition', async () => {
		const onChange = jest.fn();

		renderPublishScheduler(
			{enabled: true, unit: IntervalUnit.Week},
			onChange
		);

		expect(screen.getByRole('button', {name: 'Monday'})).toHaveAttribute(
			'aria-pressed',
			'true'
		);
		expect(screen.getByRole('button', {name: 'Thursday'})).toHaveAttribute(
			'aria-pressed',
			'false'
		);

		await user.click(screen.getByRole('button', {name: 'Thursday'}));

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({weekdays: [2, 5]})
		);
	});

	it('selects every month when the repetition unit becomes monthly', async () => {
		const onChange = jest.fn();

		renderPublishScheduler(
			{enabled: true, unit: IntervalUnit.Day},
			onChange
		);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			IntervalUnit.Month
		);

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({months: [], unit: IntervalUnit.Month})
		);
	});

	it('toggles the months of a monthly repetition', async () => {
		const onChange = jest.fn();

		renderPublishScheduler(
			{enabled: true, months: [1, 4], unit: IntervalUnit.Month},
			onChange
		);

		await user.click(screen.getByRole('button', {name: 'july'}));

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({months: [1, 4, 7]})
		);
	});

	it('keeps a single day and month when the repetition becomes yearly', async () => {
		const onChange = jest.fn();

		renderPublishScheduler(
			{
				enabled: true,
				monthDays: [5, 15],
				months: [],
				unit: IntervalUnit.Month,
			},
			onChange
		);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			IntervalUnit.Year
		);

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				monthDays: [5],
				months: [1],
				unit: IntervalUnit.Year,
			})
		);
	});

	it('offers only the days the yearly repetition month has', () => {
		renderPublishScheduler({
			enabled: true,
			months: [2],
			unit: IntervalUnit.Year,
		});

		expect(
			within(
				screen.getByRole('combobox', {name: 'repeat-on-day'})
			).getAllByRole('option')
		).toHaveLength(29);
	});

	it('clamps the day when the yearly repetition month gets shorter', async () => {
		const onChange = jest.fn();

		renderPublishScheduler(
			{
				enabled: true,
				monthDays: [31],
				months: [1],
				unit: IntervalUnit.Year,
			},
			onChange
		);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat-on-month'}),
			'2'
		);

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({monthDays: [29], months: [2]})
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderPublishScheduler({
			enabled: true,
			startDateTime: START_DATE_TIME,
		});

		await checkAccessibility({context: container});
	});
});
