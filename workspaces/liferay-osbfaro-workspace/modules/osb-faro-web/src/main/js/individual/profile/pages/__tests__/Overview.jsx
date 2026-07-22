import * as data from 'test/data';
import mockStore from 'test/mock-store';
import Overview from '../Overview';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Individual} from 'shared/util/records';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockEventMetrics,
	mockPreferenceReq,
	mockSessions,
	mockTimeRangeReq
} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const variables = {channelId: undefined};

describe('IndividualOverview', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container, getByText} = render(
			<MockedProvider
				mocks={[
					mockEventMetrics(variables),
					mockTimeRangeReq(),
					mockPreferenceReq(),
					mockSessions(variables)
				]}
			>
				<Provider store={mockStore()}>
					<MemoryRouter>
						<Overview
							groupId='23'
							id='test'
							individual={data.getImmutableMock(
								Individual,
								data.mockIndividual
							)}
						/>
					</MemoryRouter>
				</Provider>
			</MockedProvider>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('View All Details')).toBeInTheDocument();
	});
});
