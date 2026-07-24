import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import MarketoCampaignOverview from '../MarketoCampaignOverview';
import {cleanup, render} from '@testing-library/react';
import {DataSource} from 'shared/util/records';
import {MemoryRouter, Route} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {useRequest} from 'shared/hooks/useRequest';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn()
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({isAdmin: () => true})
}));

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/settings/data-source/test']}
		>
			<Route path='/workspace/:groupId/settings/data-source/:id'>
				<MockedProvider addTypename={false}>
					<MarketoCampaignOverview {...props} />
				</MockedProvider>
			</Route>
		</MemoryRouter>
	</Provider>
);

describe('MarketoCampaignOverview', () => {
	afterEach(cleanup);

	it('should render the connected data source with the synced leads and companies count', async () => {
		useRequest.mockReturnValue({
			data: 10,
			loading: false
		});

		const {container} = render(
			<WrappedComponent
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockMarketoCampaignDataSource
				)}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render the reconnect view when the data source is disconnected', async () => {
		useRequest.mockReturnValue({
			data: 10,
			loading: false
		});

		const {container} = render(
			<WrappedComponent
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockMarketoCampaignDataSource,
					1,
					{
						state: 'DISCONNECTED',
						status: 'INACTIVE'
					}
				)}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render an error when the count requests fail', async () => {
		useRequest.mockReturnValue({
			error: true,
			loading: false
		});

		const {container} = render(
			<WrappedComponent
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockMarketoCampaignDataSource
				)}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
