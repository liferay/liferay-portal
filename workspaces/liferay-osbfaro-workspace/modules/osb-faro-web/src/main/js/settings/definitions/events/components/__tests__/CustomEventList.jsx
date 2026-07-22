import * as API from 'shared/api';
import * as data from 'test/data';
import * as NotificationAlertList from 'shared/components/NotificationAlertList';
import CustomEventList from '../CustomEventList';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventDefinitionsReq} from 'test/graphql-data';
import {NotificationSubtypes} from 'shared/util/records/Notification';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {Routes} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

const mockNotificationAlertList = NotificationAlertList;

const WrappedComponent = ({
	children,
	initialEntries = [
		'/workspace/23/settings/definitions/events/custom?delta=1'
	],
	mocks = [
		mockEventDefinitionsReq(
			[
				data.mockEventDefinition(0, {
					__typename: 'EventDefinition',
					type: 'CUSTOM'
				})
			],
			{
				blocked: false,
				eventType: 'CUSTOM'
			}
		)
	]
}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={initialEntries}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider
							cache={new InMemoryCache({freezeResults: false})}
							mocks={mocks}
						>
							{children}
						</MockedProvider>
					}
					path={`${Routes.SETTINGS_DEFINITIONS_EVENTS_CUSTOM}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('CustomEventList', () => {
	afterEach(cleanup);

	it('should render', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {container, queryByTestId} = render(
			<WrappedComponent>
				<CustomEventList groupId='23' />
			</WrappedComponent>
		);

		await waitForLoadingToBeRemoved(container);

		expect(queryByTestId('select-all-checkbox')).toBeTruthy();
		expect(container).toMatchSnapshot();
	});

	it('should render w/o select all checkbox if user is not an admin', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => false}));

		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockMemberUser('23'))
		);

		const {container, queryByTestId} = render(
			<WrappedComponent>
				<CustomEventList groupId='23' />
			</WrappedComponent>
		);

		await waitForLoadingToBeRemoved(container);

		expect(queryByTestId('select-all-checkbox')).toBeNull();
	});

	it('should render alert notification', async () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		mockNotificationAlertList.useNotificationsAPI = jest.fn(() => ({
			data: range(1).map(i =>
				data.mockNotification(i, {
					subtype: NotificationSubtypes.BlockedEventsLimit
				})
			),
			loading: false,
			refetch: () => {}
		}));

		const {container} = render(
			<WrappedComponent>
				<CustomEventList groupId='23' />
			</WrappedComponent>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
