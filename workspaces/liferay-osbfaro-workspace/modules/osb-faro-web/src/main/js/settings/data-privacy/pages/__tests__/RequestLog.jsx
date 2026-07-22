import mockStore from 'test/mock-store';
import React from 'react';
import RequestLog from '../RequestLog';
import {cleanup, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC'
	})
}));

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/settings/data-privacy/request-log']}
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
							<RequestLog
								router={{params: {groupId: '23'}, query: {}}}
								{...props}
							/>
						</MockedProvider>
					}
					path='/workspace/:groupId/settings/data-privacy/request-log/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('RequestLog', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
