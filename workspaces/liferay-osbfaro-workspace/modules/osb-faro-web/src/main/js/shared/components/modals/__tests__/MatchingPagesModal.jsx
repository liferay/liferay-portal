import MatchingPagesModal from '../MatchingPagesModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {MockedProvider} from '@apollo/client/testing';
import {mockRecommendationPageAssetsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

describe('MatchingPagesModal', () => {
	it('should render', async () => {
		const {container} = render(
			<MemoryRouter>
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
			</MemoryRouter>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
