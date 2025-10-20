import MatchingPagesModal from '../MatchingPagesModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {MockedProvider} from '@apollo/react-testing';
import {mockRecommendationPageAssetsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router-dom';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

describe('MatchingPagesModal', () => {
	it('should render', async () => {
		const {container} = render(
			<StaticRouter>
				<MockedProvider mocks={[mockRecommendationPageAssetsReq([])]}>
					<Provider store={mockStore()}>
						<MatchingPagesModal
							itemFilters={[
								{
									name: 'includeFilter',
									value: '.*custom-assets'
								}
							]}
						/>
					</Provider>
				</MockedProvider>
			</StaticRouter>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
