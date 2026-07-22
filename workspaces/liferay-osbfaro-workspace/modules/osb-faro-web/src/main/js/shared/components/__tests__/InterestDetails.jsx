import client from 'shared/apollo/client';
import InterestDetails from '../InterestDetails';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockPreferenceReq,
	mockTimeRangeReq,
	mockTouchpointsReq
} from 'test/graphql-data';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const mockItems = [
	{
		__typename: 'PageMetric',
		assetId: 'https://www.liferay.com',
		assetTitle: 'Dashboard - Retail',
		avgTimeOnPageMetric: {
			__typename: 'Metric',
			value: 23
		},
		bounceRateMetric: {
			__typename: 'Metric',
			value: 0.23
		},
		dataSourceId: '123123',
		entrancesMetric: {
			__typename: 'Metric',
			value: 56
		},
		exitRateMetric: {
			__typename: 'Metric',
			value: 0.53
		},
		viewsMetric: {__typename: 'Metric', value: 243.0},
		visitorsMetric: {
			__typename: 'Metric',
			value: 45.0
		}
	}
];

const defaultProps = {
	router: {
		params: {
			channelId: '321321',
			groupId: '23',
			id: '321',
			interestId: 'test'
		}
	}
};

const DefaultComponent = () => (
	<ApolloProvider client={client}>
		<MockedProvider
			mocks={[
				mockTimeRangeReq(),
				mockPreferenceReq(),
				mockTouchpointsReq(mockItems, {size: 2})
			]}
		>
			<MemoryRouter
				initialEntries={[
					'/workspace/23/321321/contacts/accounts/123123/interests/test'
				]}
			>
				<RouterRoutes>
					<Route
						element={<InterestDetails {...defaultProps} />}
						path={Routes.CONTACTS_ACCOUNT_INTEREST_DETAILS}
					/>
				</RouterRoutes>
			</MemoryRouter>
		</MockedProvider>
	</ApolloProvider>
);

describe('InterestDetails', () => {
	it('renders', async () => {
		const {container, getByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('"test"')).toBeInTheDocument();
	});
});
