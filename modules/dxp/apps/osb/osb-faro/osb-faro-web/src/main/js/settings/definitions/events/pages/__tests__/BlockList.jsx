import BlockList from '../BlockList';
import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/react-components';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '456',
		groupId: '2000'
	})
}));

describe('BlockList', () => {
	it('should render', () => {
		const {container} = render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<StaticRouter>
						<BlockList groupId='23' />
					</StaticRouter>
				</Provider>
			</ApolloProvider>
		);

		expect(container).toMatchSnapshot();
	});
});
