jest.unmock('react-dom');

import mockStore from 'test/mock-store';
import React from 'react';
import ReportIssue from '../ReportIssue';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

describe('ReportIssue', () => {
	it('should render', async () => {
		const {container, queryByText} = render(
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
									<ReportIssue
										onClose={jest.fn()}
										onNext={jest.fn()}
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

		expect(queryByText('Issue Title')).toBeTruthy();
		expect(queryByText('Description')).toBeTruthy();
		expect(container).toMatchSnapshot();
	});
});
