import client from 'shared/apollo/client';
import CreateItemSimilarity from '../CreateItemSimilarity';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/react-components';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
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
			<StaticRouter>
				<CreateItemSimilarity {...defaultProps} {...props} />
			</StaticRouter>
		</Provider>
	</ApolloProvider>
);

describe('Recommendations', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
