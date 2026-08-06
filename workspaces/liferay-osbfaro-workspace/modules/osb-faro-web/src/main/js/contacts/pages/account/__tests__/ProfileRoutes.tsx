import mockStore from 'test/mock-store';
import ProfileRoutes from '../ProfileRoutes';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen, waitFor, within} from '@testing-library/react';
import {createMemoryHistory} from 'history';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Router} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/util/breadcrumbs', () => ({
	getAccounts: jest.fn(() => ({active: false, label: 'Accounts'})),
	getEntityName: jest.fn(({label}: {label?: string} = {}) => ({
		active: true,
		label,
	})),
	getHome: jest.fn(({label}: {label?: string} = {}) => ({
		active: false,
		label: label || 'Home',
	})),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
		id: 'acc-1',
	}),
}));

jest.mock('../Activities', () => ({
	__esModule: true,
	default: () => <div data-testid="account-activities" />,
}));

jest.mock('../Overview', () => ({
	__esModule: true,
	default: ({account}: {account?: {accountName?: string}}) => (
		<div data-testid="account-overview">{account?.accountName}</div>
	),
}));

jest.mock('../Profile', () => ({
	__esModule: true,
	default: () => <div data-testid="account-profile" />,
}));

const mockedUseRequest = useRequest as jest.Mock;

const ROUTE_PARAMS = {channelId: '123', groupId: '23', id: 'acc-1'};

const store = mockStore();

const renderProfileRoutes = (
	history = createMemoryHistory({
		initialEntries: ['/workspace/23/123/accounts/acc-1'],
	})
) =>
	render(
		<Provider store={store}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<Router history={history}>
					<ProfileRoutes />
				</Router>
			</ChannelContext.Provider>
		</Provider>
	);

describe('AccountProfileRoutes', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(cleanup);

	it('renders a loading indicator while the account request is in flight', () => {
		mockedUseRequest.mockReturnValue({
			data: null,
			error: false,
			loading: true,
		});

		const {container} = renderProfileRoutes();

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
		expect(screen.queryByText('Account Not Found')).not.toBeInTheDocument();
	});

	it('renders the not-found error page when the account request errors', () => {
		mockedUseRequest.mockReturnValue({
			data: null,
			error: true,
			loading: false,
		});

		renderProfileRoutes();

		expect(screen.getByText('Account Not Found')).toBeInTheDocument();
		expect(
			screen.getByText('The account you are looking for does not exist.')
		).toBeInTheDocument();
		expect(screen.getByText('Go to Accounts')).toBeInTheDocument();
	});

	it('renders the not-found error page when the account does not exist', () => {
		mockedUseRequest.mockReturnValue({
			data: null,
			error: false,
			loading: false,
		});

		renderProfileRoutes();

		expect(screen.getByText('Account Not Found')).toBeInTheDocument();
	});

	it('points the error page link back to the accounts list', () => {
		mockedUseRequest.mockReturnValue({
			data: null,
			error: true,
			loading: false,
		});

		renderProfileRoutes();

		expect(screen.getByText('Go to Accounts').closest('a')).toHaveAttribute(
			'href',
			expect.stringContaining('accounts')
		);
	});

	it('renders the account page when the account exists', () => {
		mockedUseRequest.mockReturnValue({
			data: {accountName: 'Acme Corp'},
			error: false,
			loading: false,
		});

		renderProfileRoutes();

		expect(screen.getAllByText('Acme Corp').length).toBeGreaterThan(0);
		expect(screen.queryByText('Account Not Found')).not.toBeInTheDocument();
	});

	it('lists overview as the first tab in the account nav bar', () => {
		mockedUseRequest.mockReturnValue({
			data: {accountName: 'Acme Corp'},
			error: false,
			loading: false,
		});

		renderProfileRoutes();

		const navTabs = within(screen.getByRole('navigation')).getAllByRole(
			'link'
		);

		expect(navTabs.map((navTab) => navTab.textContent)).toEqual([
			'Overview',
			'Activities',
			'Profile',
		]);
		expect(navTabs[0]).toHaveAttribute(
			'href',
			toRoute(Routes.CONTACTS_ACCOUNT_OVERVIEW, ROUTE_PARAMS)
		);
	});

	it('renders the overview page on the overview route', async () => {
		mockedUseRequest.mockReturnValue({
			data: {accountName: 'Acme Corp'},
			error: false,
			loading: false,
		});

		renderProfileRoutes(
			createMemoryHistory({
				initialEntries: [
					toRoute(Routes.CONTACTS_ACCOUNT_OVERVIEW, ROUTE_PARAMS),
				],
			})
		);

		expect(
			await screen.findByTestId('account-overview')
		).toBeInTheDocument();
	});

	it('passes the account to the overview page', async () => {
		mockedUseRequest.mockReturnValue({
			data: {accountName: 'Acme Corp'},
			error: false,
			loading: false,
		});

		renderProfileRoutes(
			createMemoryHistory({
				initialEntries: [
					toRoute(Routes.CONTACTS_ACCOUNT_OVERVIEW, ROUTE_PARAMS),
				],
			})
		);

		expect(await screen.findByTestId('account-overview')).toHaveTextContent(
			'Acme Corp'
		);
	});

	it('renders the activities page on the activities route', async () => {
		mockedUseRequest.mockReturnValue({
			data: {accountName: 'Acme Corp'},
			error: false,
			loading: false,
		});

		renderProfileRoutes(
			createMemoryHistory({
				initialEntries: [
					toRoute(Routes.CONTACTS_ACCOUNT_ACTIVITIES, ROUTE_PARAMS),
				],
			})
		);

		expect(
			await screen.findByTestId('account-activities')
		).toBeInTheDocument();
	});

	it('lands on overview when opening an account', async () => {
		mockedUseRequest.mockReturnValue({
			data: {accountName: 'Acme Corp'},
			error: false,
			loading: false,
		});

		const history = createMemoryHistory({
			initialEntries: [toRoute(Routes.CONTACTS_ACCOUNT, ROUTE_PARAMS)],
		});

		renderProfileRoutes(history);

		await waitFor(() =>
			expect(history.location.pathname).toBe(
				toRoute(Routes.CONTACTS_ACCOUNT_OVERVIEW, ROUTE_PARAMS)
			)
		);

		expect(
			await screen.findByTestId('account-overview')
		).toBeInTheDocument();
	});
});
