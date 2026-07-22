import configureStore from 'shared/store/store-dev';
import Interests from '../Interests';
import React from 'react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = ({
	mocks = [mockTimeRangeReq(), mockPreferenceReq()],
	...props
}) => (
	<Provider store={configureStore()}>
		<MemoryRouter initialEntries={['/workspace/23/sites/interests']}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider mocks={mocks}>
							<Interests
								router={{params: {groupId: '23'}, query: {}}}
								{...props}
							/>
						</MockedProvider>
					}
					path={`${Routes.SITES_INTERESTS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('Sites Dashboard Interests', () => {
	it('render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
