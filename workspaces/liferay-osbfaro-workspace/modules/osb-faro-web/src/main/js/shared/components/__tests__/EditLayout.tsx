jest.mock('shared/components/MaintenanceAlert', () => () => null);

jest.mock('shared/components/NotificationAlertList', () => ({
	__esModule: true,
	default: () => null,
	useNotificationsAPI: () => ({data: [], loading: false}),
}));

jest.mock('shared/pages/NoPropertiesAvailable', () => () => 'NoProperties');

import * as API from 'shared/api';
import * as data from 'test/data';
import EditLayout from '../EditLayout';
import mockStore, {mockStoreData} from 'test/mock-store';
import React from 'react';
import WorkspaceGate from '../WorkspaceGate';
import {ChannelProvider} from 'shared/context/channel';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {mockDataSourcesReq} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const CHANNELS = [
	data.mockChannel(1, 0, {id: '1'}),
	data.mockChannel(2, 0, {id: '2'}),
];

const renderLayout = (path: string) =>
	render(
		<Provider store={mockStore(mockStoreData)}>
			<ChannelProvider>
				<MockedProvider
					cache={new InMemoryCache({addTypename: false})}
					mocks={[mockDataSourcesReq()]}
				>
					<MemoryRouter initialEntries={[path]}>
						<RouterRoutes>
							<Route path="workspace/:groupId/*">
								<Route element={<WorkspaceGate />}>
									<Route element={<EditLayout />}>
										<Route
											element={
												<div>
													{'event analysis editor'}
												</div>
											}
											path=":channelId?/event-analysis/create"
										/>
									</Route>
								</Route>
							</Route>
						</RouterRoutes>
					</MemoryRouter>
				</MockedProvider>
			</ChannelProvider>
		</Provider>
	);

describe('EditLayout', () => {
	beforeEach(() => {
		(API.channels.fetchAll as jest.Mock).mockReturnValue(
			Promise.resolve({items: CHANNELS})
		);
	});

	it('renders the editor of the selected channel', async () => {
		renderLayout('/workspace/23/2/event-analysis/create');

		expect(
			await screen.findByText('event analysis editor')
		).toBeInTheDocument();
	});

	it('does not render the product menu', async () => {
		renderLayout('/workspace/23/1/event-analysis/create');

		await screen.findByText('event analysis editor');

		expect(
			screen.queryByRole('combobox', {name: /^property$/i})
		).not.toBeInTheDocument();
		expect(screen.queryByTitle(/language/i)).not.toBeInTheDocument();
	});

	it('renders NoPropertiesAvailable when the workspace has no channels', async () => {
		(API.channels.fetchAll as jest.Mock).mockReturnValue(
			Promise.resolve({items: []})
		);

		renderLayout('/workspace/23/event-analysis/create');

		expect(await screen.findByText('NoProperties')).toBeInTheDocument();
		expect(
			screen.queryByText('event analysis editor')
		).not.toBeInTheDocument();
	});
});
