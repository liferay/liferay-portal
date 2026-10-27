jest.mock(
	'shared/pages/NoPropertiesAvailable',
	() => () => 'NoPropertiesAvailable'
);

import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import SidebarLayout from '../SidebarLayout';
import {ChannelContext} from 'shared/context/channel';
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

const renderSidebarLayout = ({
	channels = CHANNELS,
	selectedChannel = CHANNELS[0],
} = {}) =>
	render(
		<Provider store={mockStore()}>
			<ChannelContext.Provider value={{channels, selectedChannel}}>
				<MockedProvider
					cache={new InMemoryCache({addTypename: false})}
					mocks={[mockDataSourcesReq()]}
				>
					<MemoryRouter
						initialEntries={['/workspace/23/1/contacts/segments']}
					>
						<RouterRoutes>
							<Route path="workspace/:groupId/*">
								<Route element={<SidebarLayout />}>
									<Route
										element={<div>{'segments list'}</div>}
										path=":channelId?/contacts/segments"
									/>
								</Route>
							</Route>
						</RouterRoutes>
					</MemoryRouter>
				</MockedProvider>
			</ChannelContext.Provider>
		</Provider>
	);

describe('SidebarLayout', () => {
	it('renders the page with the product menu and the top bar', async () => {
		renderSidebarLayout();

		expect(await screen.findByText('segments list')).toBeInTheDocument();

		expect(
			screen.getByRole('combobox', {name: 'Property'})
		).toBeInTheDocument();
		expect(screen.getByTitle(/language/i)).toBeInTheDocument();
	});

	it('keeps the product menu when the workspace has no properties', async () => {
		renderSidebarLayout({channels: [], selectedChannel: null});

		expect(
			await screen.findByText('NoPropertiesAvailable')
		).toBeInTheDocument();
		expect(screen.queryByText('segments list')).not.toBeInTheDocument();

		expect(
			screen.getByRole('combobox', {name: 'Property'})
		).toBeInTheDocument();
	});
});
