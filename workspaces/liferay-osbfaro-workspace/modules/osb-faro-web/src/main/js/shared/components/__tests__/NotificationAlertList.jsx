import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore from 'test/mock-store';
import NotificationAlertList from '../NotificationAlertList';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		displayTimeZone: 'UTC -03:00 Brasilia Time (America/Recife)'
	})
}));

const defaultProps = {
	data: range(1).map(i => data.mockNotification(i)),
	groupId: '23',
	loading: false,
	refetch: () => {}
};

describe('NotificationAlertList', () => {
	API.notifications.fetchNotifications.mockReturnValue(
		Promise.resolve(range(1).map(i => data.mockNotification(i)))
	);

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<NotificationAlertList {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});

	it('should hide notification when click on close button', () => {
		const {container, queryByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<NotificationAlertList {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		jest.runAllTimers();

		fireEvent.click(container.querySelector('.close'));

		expect(
			queryByText('Workspace timezone has changed to as of today')
		).toBeNull();
	});
});
