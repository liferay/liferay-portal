import Dashboard from '../index';
import DataSourcesProvider from 'shared/context/dataSources';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render} from '@testing-library/react';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '456',
		groupId: '123'
	})
}));

describe('Individuals Dashboard', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelContext.Provider value={mockChannelContext()}>
						<DataSourcesProvider groupId='123'>
							<Dashboard />
						</DataSourcesProvider>
					</ChannelContext.Provider>
				</BrowserRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
