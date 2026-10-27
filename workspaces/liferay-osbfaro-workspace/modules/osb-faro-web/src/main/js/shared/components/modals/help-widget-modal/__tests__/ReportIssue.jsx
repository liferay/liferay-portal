jest.unmock('react-dom');

import ClayModal from '@clayui/modal';
import mockStore from 'test/mock-store';
import React from 'react';
import ReportIssue from '../ReportIssue';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {cleanup} from '@testing-library/react';
import {renderModal} from 'test/render-modal';
import {waitForLoadingToBeRemoved} from 'test/helpers';

describe('ReportIssue', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {queryByText} = await renderModal(observer => (
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
									<ClayModal observer={observer}>
										<ReportIssue
											onClose={jest.fn()}
											onNext={jest.fn()}
										/>
									</ClayModal>
								</MockedProvider>
							}
							path='/workspace/:groupId'
						/>
					</Routes>
				</MemoryRouter>
			</Provider>
		));

		await waitForLoadingToBeRemoved(document.body);

		expect(queryByText('Issue Title')).toBeTruthy();
		expect(queryByText('Description')).toBeTruthy();
		expect(document.body).toMatchSnapshot();
	});
});
