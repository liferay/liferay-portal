import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import React from 'react';
import RecommendationStepCard from '../index';
import {ApolloProvider} from '@apollo/client';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: () => ({state: 'unblocked'}),
}));

describe('RecommendationStepCard', () => {
	it('should render', () => {
		const {container} = render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<MemoryRouter>
						<RecommendationStepCard
							router={{params: {groupId: '123'}}}
						/>
					</MemoryRouter>
				</Provider>
			</ApolloProvider>
		);

		expect(container).toMatchSnapshot();
	});
});
