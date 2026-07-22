import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockSuppressedUsersListReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {SuppressedUsers} from '../SuppressedUsers';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({
		id: '123',
		isAdmin: () => true,
		name: 'Marcos',
		userId: '456'
	})
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'})
}));

const mockItems = [
	{
		createDate: '2024-01-01T00:00:00Z',
		dataControlTaskBatchId: '123',
		dataControlTaskCreateDate: '2024-01-01T00:00:00Z',
		dataControlTaskStatus: 'PENDING',
		emailAddress: 'Test@liferay.com',
		id: '321'
	}
];

describe('SuppressedUsers', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const mocks = [
			mockSuppressedUsersListReq(mockItems, {
				size: 5,
				sort: {column: 'createDate', type: 'DESC'}
			})
		];

		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter
					initialEntries={[
						'/workspace/23/settings/data-privacy/suppressed-users?delta=5'
					]}
				>
					<RouterRoutes>
						<Route
							element={
								<MockedProvider addTypename={false} mocks={mocks}>
									<SuppressedUsers
										router={{
											params: {groupId: '23'},
											query: {delta: '5', page: '1'}
										}}
									/>
								</MockedProvider>
							}
							path={`${Routes.SETTINGS_DATA_PRIVACY_SUPPRESSED_USERS}/*`}
						/>
					</RouterRoutes>
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
