import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import View from '../View';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockRecommendationJobRunsReq,
	mockRecommendationReq
} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC'
	})
}));

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/123/settings/recommendations/321']}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider
							mocks={[
								mockRecommendationJobRunsReq([
									data.mockRecommendationJobRun(0)
								]),
								mockRecommendationReq(
									data.mockRecommendationJob('321', {
										nextRunDate: new Date().getTime()
									})
								)
							]}
						>
							<View
								router={{params: {groupId: '123', jobId: '321'}}}
								{...props}
							/>
						</MockedProvider>
					}
					path={`${Routes.SETTINGS_RECOMMENDATION_MODEL_VIEW}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('View', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
