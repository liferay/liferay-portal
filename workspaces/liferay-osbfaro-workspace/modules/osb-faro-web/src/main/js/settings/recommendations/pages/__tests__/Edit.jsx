import * as data from 'test/data';
import client from 'shared/apollo/client';
import Edit from '../Edit';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {MockedProvider} from '@apollo/client/testing';
import {mockRecommendationReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: () => ({state: 'unblocked'}),
	useParams: () => ({
		groupId: '23'
	})
}));

const defaultProps = {
	router: {params: {groupId: '23'}, query: {delta: '10', page: '1'}}
};

const DefaultComponent = props => (
	<ApolloProvider client={client}>
		<Provider store={mockStore()}>
			<MemoryRouter>
				<MockedProvider
					mocks={[
						mockRecommendationReq(data.mockRecommendationJob('321'))
					]}
				>
					<Edit
						{...defaultProps}
						{...props}
						router={{params: {groupId: '123', jobId: '321'}}}
					/>
				</MockedProvider>
			</MemoryRouter>
		</Provider>
	</ApolloProvider>
);

describe('Edit', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoading(container);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
