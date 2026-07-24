/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import CalendarView from '../../js/components/props_transformer/views/calendar_view/CalendarView';

jest.mock('@fullcalendar/daygrid', () => ({}));

jest.mock('@fullcalendar/interaction', () => ({}));

// FullCalendar cannot run under jsdom, so render only the day cell
// content and expose the props CalendarView feeds it.

jest.mock('@fullcalendar/react', () => {
	const React = require('react');

	return {
		__esModule: true,
		default: React.forwardRef((props: any, _ref: unknown) => (
			<div
				data-first-day={props.firstDay}
				data-locale={props.locale}
				data-testid="fullCalendar"
			>
				{props.dayCellContent?.({
					date: new Date(2026, 6, 15),
					dayNumberText: '15',
				})}
			</div>
		)),
	};
});

const renderCalendarView = (hasAddTaskPermission: boolean) =>
	render(
		<CalendarView
			cmpProjectObjectDefinitionId={456}
			cmpProjectObjectEntryId="123"
			hasAddTaskPermission={hasAddTaskPermission}
			items={[]}
			itemsActions={[]}
		/>
	);

describe('CalendarView', () => {
	beforeEach(() => {
		(globalThis as any).Liferay.fire = jest.fn();
		(globalThis as any).Liferay.ThemeDisplay.getBCP47LanguageId = jest.fn(
			() => 'en-US'
		);
	});

	it('hides the add task button without add task permission', () => {
		renderCalendarView(false);

		expect(screen.queryByLabelText('add-task')).not.toBeInTheDocument();
	});

	it('sets the week grid first day and locale from a Monday-first locale', () => {
		(globalThis as any).Liferay.ThemeDisplay.getBCP47LanguageId = jest.fn(
			() => 'de-DE'
		);

		renderCalendarView(false);

		const calendar = screen.getByTestId('fullCalendar');

		expect(calendar).toHaveAttribute('data-first-day', '1');
		expect(calendar).toHaveAttribute('data-locale', 'de-DE');
	});

	it('sets the week grid first day from a Sunday-first locale', () => {
		renderCalendarView(false);

		const calendar = screen.getByTestId('fullCalendar');

		expect(calendar).toHaveAttribute('data-first-day', '0');
		expect(calendar).toHaveAttribute('data-locale', 'en-US');
	});

	it('shows the add task button with add task permission', () => {
		renderCalendarView(true);

		expect(screen.getByLabelText('add-task')).toBeInTheDocument();
	});
});
