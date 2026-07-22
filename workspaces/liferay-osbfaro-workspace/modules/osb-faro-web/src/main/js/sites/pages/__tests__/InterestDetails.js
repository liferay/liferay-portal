import client from 'shared/apollo/client';
import InterestDetails from '../InterestDetails';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {ChannelContext} from 'shared/context/channel';
import {mockChannelContext} from 'test/mock-channel-context';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useLocation: () => ({
		search: '?rangeKey=30',
	}),
	useParams: () => ({
		channelId: '456',
		groupId: '123',
		query: {
			rangeKey: '30',
		},
	}),
}));

describe('Sites Dashboard InterestDetails', () => {
	it('render', () => {
		const {container} = render(
			<ApolloProvider client={client}>
				<MemoryRouter>
					<ChannelContext.Provider value={mockChannelContext()}>
						<InterestDetails
							channelName="Test Channel"
							router={{
								params: {channelId: '456', groupId: '123'},
								query: {rangeKey: '30'},
							}}
						/>
					</ChannelContext.Provider>
				</MemoryRouter>
			</ApolloProvider>
		);

		expect(container).toMatchSnapshot();
	});
});
