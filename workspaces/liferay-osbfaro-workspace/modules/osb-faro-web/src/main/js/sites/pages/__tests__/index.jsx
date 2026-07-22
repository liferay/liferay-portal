import * as data from 'test/data';
import * as useDataSources from 'shared/context/dataSources';
import BasePage from 'shared/components/base-page';
import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render} from '@testing-library/react';
import {Dashboard} from '../index';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {mockEmptyState, mockSuccessState} from 'test/__mocks__/mock-objects';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {User} from 'shared/util/records';
import {UserRoleNames} from 'shared/util/constants';

jest.unmock('react-dom');

const MEMBER_USER = new User(
	data.mockUser(23, {roleName: UserRoleNames.Member})
);

const MOCK_CONTEXT = {
	rangeKey: {defaultValue: '30'},
	router: {
		params: {
			channelId: '123',
			groupId: '2000'
		},
		query: {
			rangeKey: '30'
		}
	}
};

const mockUseDataSource = useDataSources;

const WrappedComponent = props => (
	<ApolloProvider client={client}>
		<Provider store={mockStore()}>
			<MemoryRouter initialEntries={['/workspace/2000/123/sites']}>
				<RouterRoutes>
					<Route
						element={
							<ChannelContext.Provider value={mockChannelContext()}>
								<BasePage.Context.Provider value={MOCK_CONTEXT}>
									<Dashboard
										currentUser={MEMBER_USER}
										router={MOCK_CONTEXT.router}
										{...props}
									/>
								</BasePage.Context.Provider>
							</ChannelContext.Provider>
						}
						path={`${Routes.SITES}/*`}
					/>
				</RouterRoutes>
			</MemoryRouter>
		</Provider>
	</ApolloProvider>
);

describe('Sites Dashboard Index', () => {
	afterEach(cleanup);
	mockUseDataSource.useDataSources = jest.fn(() => mockSuccessState);

	beforeAll(() => {
		delete window.location;
	});

	it('renders w/ "No Sites Connected" as title', () => {
		window.location = {
			pathname: '/workspace/2000/123/sites'
		};

		const CHANNEL_CONTEXT_MOCK = {
			channelDispatch: () => {},
			channels: [],
			selectedChannel: null
		};

		const WrappedComponentWithContext = props => (
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<MemoryRouter
						initialEntries={['/workspace/2000/123/sites']}
					>
						<RouterRoutes>
							<Route
								element={
									<ChannelContext.Provider
										value={CHANNEL_CONTEXT_MOCK}
									>
										<BasePage.Context.Provider value={MOCK_CONTEXT}>
											<Dashboard
												currentUser={MEMBER_USER}
												router={MOCK_CONTEXT.router}
												{...props}
											/>
										</BasePage.Context.Provider>
									</ChannelContext.Provider>
								}
								path={`${Routes.SITES}/*`}
							/>
						</RouterRoutes>
					</MemoryRouter>
				</Provider>
			</ApolloProvider>
		);

		const {container} = render(<WrappedComponentWithContext />);

		expect(container.querySelector('.title-section')).toHaveTextContent(
			'No Sites Connected'
		);
	});
});

describe('sites with no Data Source', () => {
	it('should render EmptyState', () => {
		window.location = {
			pathname: '/workspace/2000/123/sites'
		};
		mockUseDataSource.useDataSources = jest.fn(() => mockEmptyState);

		const {getByText} = render(<WrappedComponent />);

		expect(
			getByText('No Sites Synced from Data Sources')
		).toBeInTheDocument();
		expect(
			getByText('Connect a data source with sites data.')
		).toBeInTheDocument();
		expect(
			getByText('Access our documentation to learn more.')
		).toBeInTheDocument();
	});
});
