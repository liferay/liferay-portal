import * as pedantic from 'test/pedantic';
import mockStore from 'test/mock-store';
import React from 'react';
import TimeZoneSelectionModal from '../TimeZoneSelectionModal';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {fromJS} from 'immutable';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

jest.mock('shared/util/date', () => ({
	...jest.requireActual('shared/util/date'),
	getDateNow: jest.fn(() => require('moment').utc('2019-01-01T12:10:00Z'))
}));

const mockGroupId = '23';

const DefaultComponent = ({timeZone = {timeZoneId: 'UTC'}, ...props}) => (
	<Provider
		store={mockStore(
			fromJS({
				projects: {
					[mockGroupId]: {
						data: {
							timeZone
						}
					}
				}
			})
		)}
	>
		<MemoryRouter>
			<MockedProvider
				cache={
					new InMemoryCache({
						addTypename: false
					})
				}
				mocks={[]}
			>
				<TimeZoneSelectionModal
					groupId={mockGroupId}
					notificationId='123'
					onClose={jest.fn()}
					{...props}
				/>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('TimeZoneSelectionModal', () => {
	beforeEach(() => {
		pedantic.disable();
	});

	afterEach(() => {
		pedantic.enable();
		cleanup();
	});

	it('should render', () => {
		const {getByText} = render(<DefaultComponent />);

		expect(getByText('Workspace Timezone')).toBeInTheDocument();
		expect(
			getByText(/Your workspace now supports custom timezones/)
		).toBeInTheDocument();

		expect(getByText('Current Time:')).toBeInTheDocument();
		expect(getByText('12:10 PM')).toBeInTheDocument();

		expect(getByText('Do This Later')).toBeInTheDocument();
		expect(getByText('Set Timezone')).toBeInTheDocument();
	});

	it('renders the current time in the workspace time zone', () => {
		const {getByText} = render(
			<DefaultComponent
				timeZone={{country: 'Brazil', timeZoneId: 'America/Recife'}}
			/>
		);

		expect(getByText('9:10 AM')).toBeInTheDocument();
	});

	it('renders the current UTC time after the country changes', async () => {
		const {container, getByText} = render(
			<DefaultComponent
				timeZone={{country: 'Brazil', timeZoneId: 'America/Recife'}}
			/>
		);

		await waitFor(() =>
			expect(getByText('Chile')).toBeInTheDocument()
		);

		fireEvent.change(container.querySelector('select'), {
			target: {value: 'Chile'}
		});

		await waitFor(() =>
			expect(getByText('12:10 PM')).toBeInTheDocument()
		);
	});
});
