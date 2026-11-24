import AcquisitionsCard from '../AcquisitionsCard';
import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import {CompositionTypes, RangeKeyTimeRanges} from 'shared/util/constants';
import {MemoryRouter} from 'react-router-dom';
import {
	mockAcquisitionsReq,
	mockPreferenceReq,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
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
						mockAcquisitionsReq(),
					]}
				>
					<AcquisitionsCard
						compositionBagName={CompositionTypes.Acquisitions}
						label="card label"
					/>
				</MockedProvider>
			</MemoryRouter>
		</BasePage.Context.Provider>
	</Provider>
);

describe('AcquisitionsCard', () => {
	it('renders', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
