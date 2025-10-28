import * as data from 'test/data';
import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import React from 'react';
import View from '../View';
import {ApolloProvider} from '@apollo/react-components';
import {MockedProvider} from '@apollo/react-testing';
import {mockEventDefinitionReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		eventId: '0',
		groupId: '23'
	})
}));

describe('Event View page', () => {
	it('should render', async () => {
		const {container} = render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<MockedProvider
						mocks={[
							mockEventDefinitionReq(
								data.mockEventDefinition(0, {
									eventAttributeDefinitions: [
										{
											__typename:
												'EventAttributeDefinition',
											...data.mockEventAttributeDefinition(
												1
											)
										}
									]
								}),
								{id: '0'}
							)
						]}
					>
						<StaticRouter>
							<View eventId='0' groupId='23' />
						</StaticRouter>
					</MockedProvider>
				</Provider>
			</ApolloProvider>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
