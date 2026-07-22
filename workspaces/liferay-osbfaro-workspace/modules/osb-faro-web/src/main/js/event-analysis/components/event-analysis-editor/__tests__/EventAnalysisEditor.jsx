import EventAnalysisEditor from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {CalculationTypes} from 'event-analysis/utils/types';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('Event Analysis Editor', () => {
	it('render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
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
									<EventAnalysisEditor
										type={CalculationTypes.Total}
									/>
								</MockedProvider>
							}
							path='/*'
						/>
					</RouterRoutes>
				</MemoryRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
