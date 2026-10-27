import LifecycleRedirect from '../LifecycleRedirect';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter, useLocation} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23'}),
}));

const mockedUseCurrentUser = useCurrentUser as jest.Mock;
const mockedUseDataSources = useDataSources as jest.Mock;
const mockedUseRequest = useRequest as jest.Mock;

const mockLifecycles = (
	lifecycles: {
		defaultAccountLifecycle?: boolean;
		externalReferenceCode: string;
		id: string;
	}[],
	loading = false
) =>
	mockedUseRequest.mockReturnValue({
		data: lifecycles,
		error: false,
		loading,
	});

const LocationProbe = () => (
	<div data-testid="location">{useLocation().pathname}</div>
);

const renderPage = () =>
	render(
		<Provider store={mockStore()}>
			<ChannelContext.Provider value={mockChannelContext() as any}>
				<MemoryRouter initialEntries={['/workspace/23/123/lifecycle']}>
					<LifecycleRedirect />

					<LocationProbe />
				</MemoryRouter>
			</ChannelContext.Provider>
		</Provider>
	);

const dashboardURL = (lifecycleId: string) =>
	toRoute(Routes.LIFECYCLE_DASHBOARD, {
		channelId: '123',
		groupId: '23',
		lifecycleId,
	});

describe('LifecycleRedirect', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});
		mockedUseDataSources.mockReturnValue({empty: false, loading: false});
	});

	afterEach(cleanup);

	it('redirects to the default lifecycle of the channel by its external reference code', () => {
		mockLifecycles([
			{
				defaultAccountLifecycle: true,
				externalReferenceCode: 'erc-8',
				id: '8',
			},
		]);

		renderPage();

		expect(screen.getByTestId('location')).toHaveTextContent(
			dashboardURL('erc-8')
		);
	});

	it('asks for the default lifecycle of the current channel', () => {
		mockLifecycles([
			{
				defaultAccountLifecycle: true,
				externalReferenceCode: 'erc-8',
				id: '8',
			},
		]);

		renderPage();

		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				variables: {
					channelId: '123',
					defaultAccountLifecycle: true,
					groupId: '23',
				},
			})
		);
	});

	it('waits for the lifecycles before redirecting', () => {
		mockLifecycles([], true);

		renderPage();

		expect(screen.getByTestId('location')).toHaveTextContent(
			'/workspace/23/123/lifecycle'
		);
		expect(screen.queryByText(/configure a new lifecycle/i)).toBeNull();
	});

	describe('when the lifecycles fail to load', () => {
		const refetch = jest.fn();

		beforeEach(() =>
			mockedUseRequest.mockReturnValue({
				data: null,
				error: true,
				loading: false,
				refetch,
			})
		);

		it('shows an error instead of the empty state', () => {
			renderPage();

			expect(
				screen.getByText(/an.unexpected.error.occurred/i)
			).toBeInTheDocument();
			expect(screen.queryByText(/configure a new lifecycle/i)).toBeNull();
			expect(screen.getByTestId('location')).toHaveTextContent(
				'/workspace/23/123/lifecycle'
			);
		});

		it('reloads the lifecycles', () => {
			renderPage();

			fireEvent.click(screen.getByRole('button', {name: /reload/i}));

			expect(refetch).toHaveBeenCalled();
		});
	});

	describe('when no lifecycles are configured', () => {
		beforeEach(() => mockLifecycles([]));

		it('titles the page generically', () => {
			renderPage();

			expect(
				screen.getByRole('heading', {name: /^lifecycles$/i})
			).toBeInTheDocument();
		});

		it('shows the Lifecycles breadcrumb as the current page, not a link', () => {
			const {container} = renderPage();

			const crumb = container.querySelector('.breadcrumb-item.active');

			expect(crumb).toHaveTextContent(/^lifecycles$/i);
			expect(crumb!.querySelector('a')).toBeNull();
		});

		it('renders the "Configure a New Lifecycle" empty state', () => {
			renderPage();

			expect(
				screen.getByText(/configure a new lifecycle/i)
			).toBeInTheDocument();
		});

		it('links the New Lifecycle action to the create route', () => {
			renderPage();

			expect(
				screen.getByRole('link', {name: /new lifecycle/i})
			).toHaveAttribute(
				'href',
				toRoute(Routes.LIFECYCLE_CREATE, {
					channelId: '123',
					groupId: '23',
				})
			);
		});

		it('asks for a data source when none is connected', () => {
			mockedUseDataSources.mockReturnValue({empty: true, loading: false});

			renderPage();

			expect(
				screen.getByText(/no data sources connected/i)
			).toBeInTheDocument();
			expect(screen.queryByText(/configure a new lifecycle/i)).toBeNull();
		});
	});
});
