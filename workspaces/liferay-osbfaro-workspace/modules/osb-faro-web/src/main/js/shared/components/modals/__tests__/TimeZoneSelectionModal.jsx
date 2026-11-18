import * as pedantic from 'test/pedantic';
import mockStore from 'test/mock-store';
import React from 'react';
import TimeZoneSelectionModal from '../TimeZoneSelectionModal';
import {cleanup, fireEvent, screen, waitFor} from '@testing-library/react';
import {fromJS} from 'immutable';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

jest.mock('shared/util/date', () => ({
	...jest.requireActual('shared/util/date'),
	getDateNow: jest.fn(() => require('moment').utc('2019-01-01T12:10:00Z'))
}));

const mockGroupId = '23';

const DefaultComponent = ({
	observer,
	timeZone = {timeZoneId: 'UTC'},
	...props
}) => (
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
					observer={observer}
					onClose={jest.fn()}
					{...props}
				/>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

const renderTimeZoneModal = props =>
	renderModal(observer => (
		<DefaultComponent observer={observer} {...props} />
	));

describe('TimeZoneSelectionModal', () => {
	beforeEach(() => {
		pedantic.disable();
	});

	afterEach(() => {
		pedantic.enable();
		cleanup();
	});

	it('should render', async () => {
		await renderTimeZoneModal();

		const {getByText} = screen;

		expect(getByText(/^workspace timezone$/i)).toBeInTheDocument();
		expect(
			getByText(/Your workspace now supports custom timezones/)
		).toBeInTheDocument();

		expect(getByText(/^current time:$/i)).toBeInTheDocument();
		expect(getByText('12:10 PM')).toBeInTheDocument();

		expect(getByText(/^do this later$/i)).toBeInTheDocument();
		expect(getByText(/^set timezone$/i)).toBeInTheDocument();
	});

	it('renders the current time in the workspace time zone', async () => {
		await renderTimeZoneModal({
			timeZone: {country: 'Brazil', timeZoneId: 'America/Recife'}
		});

		expect(screen.getByText('9:10 AM')).toBeInTheDocument();
	});

	it('renders the current UTC time after the country changes', async () => {
		await renderTimeZoneModal({
			timeZone: {country: 'Brazil', timeZoneId: 'America/Recife'}
		});

		const {getByText} = screen;

		await waitFor(() =>
			expect(getByText(/^chile$/i)).toBeInTheDocument()
		);

		fireEvent.change(document.querySelector('select'), {
			target: {value: 'Chile'}
		});

		await waitFor(() =>
			expect(getByText('12:10 PM')).toBeInTheDocument()
		);
	});
});
