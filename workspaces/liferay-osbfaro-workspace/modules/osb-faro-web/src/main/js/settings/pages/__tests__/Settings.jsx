import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore, {mockStoreData, mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import {fromJS} from 'immutable';
import {MemoryRouter, Route} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';
import {Routes, toRoute} from 'shared/util/router';
import {Settings} from '../Settings';
import {SubscriptionNames} from 'shared/util/subscriptions';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';

jest.unmock('react-dom');

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

const usagePath = toRoute(Routes.SETTINGS_USAGE, {groupId: '23'});

const mockStoreDataSaaS = mockStoreData.setIn(
	['projects', '23', 'data', 'faroSubscription'],
	fromJS({name: SubscriptionNames.LiferaySaasEnterprisePlan})
);

const renderSettingsAt = store =>
	render(
		<Provider store={store}>
			<MemoryRouter initialEntries={[usagePath]}>
				<Route path={Routes.SETTINGS}>
					<Settings />
				</Route>
			</MemoryRouter>
		</Provider>
	);

describe('Settings usage page routing', () => {
	beforeEach(() => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));
	});

	it('should render the classic Usage Overview page for a non-SaaS, non-LDP project', async () => {
		renderSettingsAt(mockStore());

		expect(
			await screen.findByTestId('subscription-details')
		).toBeInTheDocument();
	});

	it('should render the SaaS Usage Overview page for an LDP project', async () => {
		API.projects.fetch.mockReturnValueOnce(
			Promise.resolve(
				data.mockProject('23', {
					faroSubscription: fromJS({
						name: SubscriptionNames.LiferayDataPlatformPrivateBeta
					})
				})
			)
		);

		renderSettingsAt(mockStore(mockStoreDataLDP));

		expect(
			await screen.findByText('View Your Workspace Metrics')
		).toBeInTheDocument();
	});

	it('should render the SaaS Usage Overview page for a SaaS project', async () => {
		API.projects.fetch.mockReturnValueOnce(
			Promise.resolve(
				data.mockProject('23', {
					faroSubscription: fromJS({
						name: SubscriptionNames.LiferaySaasEnterprisePlan
					})
				})
			)
		);

		renderSettingsAt(mockStore(mockStoreDataSaaS));

		expect(
			await screen.findByText('View Your SaaS Project Metrics')
		).toBeInTheDocument();
	});
});
