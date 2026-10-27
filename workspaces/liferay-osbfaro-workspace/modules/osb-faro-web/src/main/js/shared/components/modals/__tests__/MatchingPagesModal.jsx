import MatchingPagesModal from '../MatchingPagesModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {MockedProvider} from '@apollo/client/testing';
import {mockRecommendationPageAssetsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {useModal} from '@clayui/modal';
import {waitForLoadingToBeRemoved} from 'test/helpers';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

const TestModal = props => {
	const {observer} = useModal({defaultOpen: true});

	return <MatchingPagesModal observer={observer} {...props} />;
};

describe('MatchingPagesModal', () => {
	it('should render', async () => {
		render(
			<MemoryRouter>
				<MockedProvider mocks={[mockRecommendationPageAssetsReq([])]}>
					<Provider store={mockStore()}>
						<TestModal
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

		await waitForModal();

		await waitForLoadingToBeRemoved(document.body);

		jest.runAllTimers();

		expect(document.body).toMatchSnapshot();
	});
});
