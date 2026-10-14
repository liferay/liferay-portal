import LifecycleRedirect from '../LifecycleRedirect';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen} from '@testing-library/react';
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
	lifecycles: {default?: boolean; id: string}[],
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

	it('redirects to the first lifecycle while none is flagged', () => {
		mockLifecycles([{id: '7'}, {id: '8'}]);

		renderPage();

		expect(screen.getByTestId('location')).toHaveTextContent(
			dashboardURL('7')
		);
	});

	it('redirects to the lifecycle flagged as the default', () => {
		mockLifecycles([{id: '7'}, {default: true, id: '8'}]);

		renderPage();

		expect(screen.getByTestId('location')).toHaveTextContent(
			dashboardURL('8')
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

	describe('when no lifecycles are configured', () => {
		beforeEach(() => mockLifecycles([]));

		it('titles the page generically', () => {
			renderPage();

			expect(
				screen.getByRole('heading', {name: /^lifecycles$/i})
			).toBeInTheDocument();
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
