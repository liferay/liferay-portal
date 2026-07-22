import mockStore from 'test/mock-store';
import React from 'react';
import RequestList from '../RequestList';
import RequestListQuery from '../../queries/RequestListQuery';
import {cleanup, render} from '@testing-library/react';
import {GDPRRequestStatuses, GDPRRequestTypes} from 'shared/util/constants';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {User} from 'shared/util/records';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const mockItems = [
	{
		batchId: '1',
		completeDate: null,
		createDate: '2019-10-09T00:00',
		emailAddresses: ['t.smith@nosaints.com'],
		id: '10',
		status: GDPRRequestStatuses.Running,
		type: GDPRRequestTypes.Delete
	},
	{
		batchId: '2',
		completeDate: null,
		createDate: '2019-10-09T00:00',
		emailAddresses: ['alice.bryant@example.com'],
		id: '20',
		status: GDPRRequestStatuses.Running,
		type: GDPRRequestTypes.Unsuppress
	},
	{
		batchId: '3',
		completeDate: '2019-10-05T00:00',
		createDate: '2019-09-09T00:00',
		emailAddresses: ['scott.gilbert@example.com'],
		id: '30',
		status: GDPRRequestStatuses.Expired,
		type: GDPRRequestTypes.Suppress
	},
	{
		batchId: '4',
		completeDate: '2019-11-10T00:00',
		createDate: '2019-11-09T00:00',
		emailAddresses: ['foo@bar.com'],
		id: '4',
		status: GDPRRequestStatuses.Completed,
		type: GDPRRequestTypes.Suppress
	},
	{
		batchId: '5',
		completeDate: null,
		createDate: '2019-09-09T00:00',
		emailAddresses: ['lillie.foster@example.com'],
		id: '50',
		status: GDPRRequestStatuses.Error,
		type: GDPRRequestTypes.Access
	},
	{
		batchId: '6',
		completeDate: null,
		createDate: '2019-09-09T00:00',
		emailAddresses: ['bazbuz@example.com'],
		id: '60',
		status: GDPRRequestStatuses.Pending,
		type: GDPRRequestTypes.Delete
	},
	{
		batchId: '7',
		completeDate: '2019-12-05T00:00',
		createDate: '2019-12-05T00:00',
		emailAddresses: ['scott.gilbert@example.com'],
		id: '70',
		status: GDPRRequestStatuses.Completed,
		type: GDPRRequestTypes.Access
	}
];

const mocks = [
	{
		request: {
			query: RequestListQuery,
			variables: {
				keywords: '',
				size: 10,
				sort: {column: 'createDate', type: 'DESC'},
				start: 0
			}
		},
		result: {
			data: {
				dataControlTasks: {
					__typename: 'DataControlTaskBag',
					dataControlTasks: mockItems,
					total: 7
				}
			}
		}
	}
];

const Wrapper = ({children, store = mockStore()}) => (
	<Provider store={store}>
		<MemoryRouter
			initialEntries={[
				'/workspace/23/settings/data-privacy/request-log/?delta=10'
			]}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider addTypename={false} mocks={mocks}>
							{children}
						</MockedProvider>
					}
					path='/workspace/:groupId/settings/data-privacy/request-log/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('RequestList', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(
			<Wrapper>
				<RequestList
					currentUser={new User({roleName: 'Site Owner'})}
					timeZoneId='UTC'
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a request row as checkable with a download button if the status is "DONE"', async () => {
		const {container} = render(
			<Wrapper>
				<RequestList
					currentUser={new User({roleName: 'Site Owner'})}
					timeZoneId='UTC'
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		const row4 = container.querySelector('tbody tr:nth-child(4)');

		expect(row4.querySelector('input[type="checkbox"]')).not.toBeDisabled();
		expect(row4.querySelector('.btn-secondary')).toHaveTextContent(
			'Download'
		);
	});

	it('should render a request row as disabled with no download button if the status is not "DONE"', async () => {
		const {container} = render(
			<Wrapper>
				<RequestList
					currentUser={new User({roleName: 'Site Owner'})}
					timeZoneId='UTC'
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		// Row 1 status is Running
		const row1 = container.querySelector('tbody tr:nth-child(1)');

		expect(row1.querySelector('input[type="checkbox"]')).toBeDisabled();
		expect(row1.querySelector('.btn-secondary')).toBeNull();
	});

	it('should render a request row as disabled with a "download expired" message if the request status is EXPIRED', async () => {
		const {container} = render(
			<Wrapper>
				<RequestList
					currentUser={new User({roleName: 'Site Owner'})}
					timeZoneId='UTC'
				/>
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		// Row 3 status is Expired
		const row3 = container.querySelector('tbody tr:nth-child(3)');

		expect(row3.querySelector('input[type="checkbox"]')).toBeDisabled();
		expect(row3).toHaveTextContent('Download Expired');
	});
});
