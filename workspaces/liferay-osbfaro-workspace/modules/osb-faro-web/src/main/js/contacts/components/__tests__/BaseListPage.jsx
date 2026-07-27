import * as data from 'test/data';
import * as useDataSources from 'shared/context/dataSources';
import BaseListPage from '../BaseListPage';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, waitFor} from '@testing-library/react';
import {createOrderIOMap} from 'shared/util/pagination';
import {MemoryRouter, Route} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {mockEmptyState, mockSuccessState} from 'test/__mocks__/mock-objects';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {times} from 'lodash';
import {User} from 'shared/util/records';
import {waitForLoadingToBeRemoved} from 'test/helpers';

const TOTAL = 5;

const ACCOUNTS = times(TOTAL, i => data.mockAccount(i));

const USER = new User(data.mockUser());

const defaultProps = (empty = false) => ({
	channelId: '123123',
	columns: [
		{
			accessor: 'name',
			label: 'Name'
		},
		{
			accessor: 'id',
			label: 'Id'
		}
	],
	currentUser: USER,
	dataSourceFn: jest.fn(() =>
		Promise.resolve(
			empty ? {items: [], total: 0} : {items: ACCOUNTS, total: TOTAL}
		)
	),
	entityLabel: 'Accounts',
	groupId: '23',
	noResultsConfig: {
		description: 'There is no account data from existing data sources.',
		title: 'No account data available.'
	},
	orderIOMap: createOrderIOMap('name')
});

const WrappedComponent = ({alerts, empty, query}) => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/123123/contacts/segments']}
		>
			<Route path={Routes.CONTACTS_LIST_SEGMENT}>
				<ChannelContext.Provider value={mockChannelContext()}>
					<BaseListPage
						alerts={alerts}
						{...defaultProps(empty)}
						query={query}
					/>
				</ChannelContext.Provider>
			</Route>
		</MemoryRouter>
	</Provider>
);

jest.unmock('react-dom');

const mockUseDataSource = useDataSources;

describe('BaseListPage', () => {
	afterEach(cleanup);
	mockUseDataSource.useDataSources = jest.fn(() => mockSuccessState);

	it('should render', async () => {
		const {getByText} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(document.body);

		expect(getByText('Accounts')).toBeInTheDocument();
	});

	it('should load accounts', async () => {
		const {container, getByText} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelectorAll('.table-head-title')).toHaveLength(2);
		expect(container.querySelector('tbody').children).toHaveLength(5);
		expect(getByText('account0')).toBeInTheDocument();
		expect(getByText('Showing 1 to 2 of 5 entries.')).toBeInTheDocument();
	});

	it('should render "No Account" empty state with no query', async () => {
		const {container, getByText} = render(<WrappedComponent empty />);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText('There is no account data from existing data sources.')
		).toBeInTheDocument();
		expect(getByText('No account data available.')).toBeInTheDocument();
	});

	it('should render different message in the empty state with query', async () => {
		const {container, getByText} = render(
			<WrappedComponent empty query='test' />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.tbar-nav').children).toHaveLength(2);

		expect(
			container.querySelectorAll('.tbar-section.text-truncate')[0]
		).toHaveTextContent('0 Results for "test"');
		expect(
			container.querySelectorAll('.tbar-section.text-truncate')[1]
		).toHaveTextContent('Clear');

		expect(getByText('No results were found.')).toBeInTheDocument();
	});

	it('should render with alerts', async () => {
		const {getByRole, getByText} = render(
			<WrappedComponent alerts={[{message: 'foo alert'}]} />
		);

		await waitFor(() => getByRole('alert'));

		expect(getByText('foo alert')).toBeInTheDocument();
	});
});

describe('BaseListPage with no Data Source', () => {
	it('should render EmptyState', () => {
		mockUseDataSource.useDataSources = jest.fn(() => mockEmptyState);

		const {getByText} = render(<WrappedComponent />);

		expect(getByText('No Data Sources Connected')).toBeInTheDocument();
		expect(
			getByText('Connect a data source to get started.')
		).toBeInTheDocument();
		expect(
			getByText('Access our documentation to learn more.')
		).toBeInTheDocument();
	});
});
