import ConnectDXPModal from '../ConnectDXPModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/123/settings/data-source/add/liferay']}
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
							<ConnectDXPModal groupId='123' onClose={noop} {...props} />
						</MockedProvider>
					}
					path='/workspace/:groupId/settings/data-source/add/liferay/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('ConnectDXPModal', () => {
	it('renders', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
