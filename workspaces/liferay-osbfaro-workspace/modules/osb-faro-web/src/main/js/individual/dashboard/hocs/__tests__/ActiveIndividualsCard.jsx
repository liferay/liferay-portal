import ActiveIndividualsCard from '../ActiveIndividualsCard';
import client from 'shared/apollo/client';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {cleanup, render} from '@testing-library/react';

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useLocation: () => ({
		search: '?rangeKey=30'
	}),
	useParams: () => ({
		channelId: '456'
	})
}));

describe('ActiveIndividualsCard', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<ApolloProvider client={client}>
				<ActiveIndividualsCard />
			</ApolloProvider>
		);
		expect(container).toMatchSnapshot();
	});
});
