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
// content to test what CalendarView feeds it.

jest.mock('@fullcalendar/react', () => {
	const React = require('react');

	return {
		__esModule: true,
		default: React.forwardRef((props: any, _ref: unknown) => (
			<div>
				{props.dayCellContent?.({
					date: new Date(2026, 6, 15),
					dayNumberText: '15',
				})}
			</div>
		)),
	};
});

jest.mock('../../js/utils/api', () => ({
	getProjectById: jest.fn(() =>
		Promise.resolve({
			data: {dateCreated: '2026-07-01', dueDate: '2026-07-31'},
		})
	),
	patchTaskById: jest.fn(() => Promise.resolve({})),
}));

const renderCalendarView = (hasAddTaskPermission: boolean) =>
	render(
		<CalendarView
			hasAddTaskPermission={hasAddTaskPermission}
			items={[]}
			itemsActions={[]}
			projectId="123"
			projectObjectDefinitionId={456}
		/>
	);

describe('CalendarView', () => {
	beforeEach(() => {
		(globalThis as any).Liferay.fire = jest.fn();
	});

	it('hides the add task button without add task permission', () => {
		renderCalendarView(false);

		expect(screen.queryByLabelText('add-task')).not.toBeInTheDocument();
	});

	it('shows the add task button with add task permission', () => {
		renderCalendarView(true);

		expect(screen.getByLabelText('add-task')).toBeInTheDocument();
	});
});
