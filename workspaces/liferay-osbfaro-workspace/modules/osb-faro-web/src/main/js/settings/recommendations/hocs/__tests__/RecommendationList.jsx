import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import RecommendationList from '../RecommendationList';
import RecommendationListQuery from '../../queries/RecommendationListQuery';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockJobBag} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

export function mockRecommendationListReq() {
	return {
		request: {
			query: RecommendationListQuery,
			variables: {
				keywords: '',
				size: 10,
				sort: {column: 'name', type: 'DESC'},
				start: 0
			}
		},
		result: {
			data: mockJobBag(range(10).map(i => data.mockRecommendationJob(i)))
		}
	};
}

const defaultProps = {
	router: {params: {groupId: '23'}, query: {delta: '10', page: '1'}}
};

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/23/settings/recommendations?delta=10&field=name&sortOrder=DESC'
			]}
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
							mocks={[mockRecommendationListReq()]}
						>
							<RecommendationList
								groupId='23'
								{...defaultProps}
								{...props}
							/>
						</MockedProvider>
					}
					path={`${Routes.SETTINGS_RECOMMENDATIONS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('RecommendationList', () => {
	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
