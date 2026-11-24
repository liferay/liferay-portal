import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopPagesCard from '../TopPagesCard';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockPreferenceReq,
	mockSitesTopPagesReq,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

const MOCK_CONTEXT = {
	filters: {},
	router: {
		params: {
			channelId: '123',
			groupId: '456',
		},
		query: {
			rangeKey: RangeKeyTimeRanges.Last30Days,
		},
	},
};

const DefaultComponent = () => (
	<Provider store={mockStore()}>
		<BasePage.Context.Provider value={MOCK_CONTEXT}>
			<MemoryRouter>
				<MockedProvider
					addTypename={false}
					mocks={[
						mockTimeRangeReq(),
						mockPreferenceReq(),
						mockSitesTopPagesReq(),
					]}
				>
					<TopPagesCard
						footer={{
							href: 'link-to-the-next-page',
							label: 'view pages',
						}}
						label="card label"
					/>
				</MockedProvider>
			</MemoryRouter>
		</BasePage.Context.Provider>
	</Provider>
);

describe('TopPagesCard', () => {
	it('renders', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
