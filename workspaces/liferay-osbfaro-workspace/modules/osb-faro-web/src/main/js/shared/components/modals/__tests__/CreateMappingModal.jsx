jest.unmock('react-dom');

import CreateMappingModal from '../CreateMappingModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

describe('CreateMappingModal', () => {
	it('should render', async () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter initialEntries={['/workspace/23']}>
					<Routes>
						<Route
							element={
								<MockedProvider
									cache={
										new InMemoryCache({
											addTypename: false
										})
									}
									mocks={[]}
								>
									<CreateMappingModal
										groupId='23'
										onClose={noop}
									/>
								</MockedProvider>
							}
							path='/workspace/:groupId'
						/>
					</Routes>
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
