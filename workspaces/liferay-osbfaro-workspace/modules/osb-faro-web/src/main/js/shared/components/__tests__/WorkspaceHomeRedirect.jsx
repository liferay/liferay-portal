import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import WorkspaceHomeRedirect from '../WorkspaceHomeRedirect';
import {ChannelContext} from 'shared/context/channel';
import {fromJS} from 'immutable';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';

const CHANNELS = [
	data.mockChannel(1, 0, {id: '1'}),
	data.mockChannel(2, 0, {id: '2'}),
];

const renderAtWorkspaceHome = () =>
	render(
		<Provider store={mockStore(fromJS({}))}>
			<ChannelContext.Provider
				value={{channels: CHANNELS, selectedChannel: CHANNELS[0]}}
			>
				<MemoryRouter initialEntries={['/workspace/23']}>
					<RouterRoutes>
						<Route
							element={<WorkspaceHomeRedirect />}
							path="workspace/:groupId"
						/>

						<Route
							element={<div>{'sites of channel 1'}</div>}
							path="workspace/:groupId/1/sites"
						/>

						<Route
							element={<div>{'sites of channel 2'}</div>}
							path="workspace/:groupId/2/sites"
						/>
					</RouterRoutes>
				</MemoryRouter>
			</ChannelContext.Provider>
		</Provider>
	);

describe('WorkspaceHomeRedirect', () => {
	it('redirects to the sites of the default channel of the user', async () => {
		API.preferences.fetchDefaultChannelId.mockReturnValueOnce(
			Promise.resolve({defaultChannelId: '2'})
		);

		renderAtWorkspaceHome();

		expect(
			await screen.findByText('sites of channel 2')
		).toBeInTheDocument();
	});

	it('redirects to the first channel when the default channel is gone', async () => {
		API.preferences.fetchDefaultChannelId.mockReturnValueOnce(
			Promise.resolve({defaultChannelId: '999'})
		);

		renderAtWorkspaceHome();

		expect(
			await screen.findByText('sites of channel 1')
		).toBeInTheDocument();
	});
});
