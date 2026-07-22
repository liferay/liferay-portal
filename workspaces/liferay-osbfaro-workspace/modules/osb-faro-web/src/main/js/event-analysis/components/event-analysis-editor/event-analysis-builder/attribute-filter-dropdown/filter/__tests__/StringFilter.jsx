import mockStore from 'test/mock-store';
import React from 'react';
import StringFilter from '../StringFilter';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/123/456/event-analysis/789']}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider
							cache={
								new InMemoryCache({
									addTypename: false,
									freezeResults: false
								})
							}
						>
							<StringFilter onSubmit={jest.fn()} {...props} />
						</MockedProvider>
					}
					path='/workspace/:groupId/:channelId/event-analysis/:id/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('StringFilter', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
