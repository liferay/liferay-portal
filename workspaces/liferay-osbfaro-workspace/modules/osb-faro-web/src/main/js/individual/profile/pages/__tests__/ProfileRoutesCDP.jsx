import * as data from 'test/data';
import DataSourcesProvider from 'shared/context/dataSources';
import IndividualProfileRoutesCDP from '../ProfileRoutesCDP';
import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render} from '@testing-library/react';
import {Individual} from 'shared/util/records';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

const ENTITY_URL =
	'/workspace/23/123/contacts/individuals/known-individuals/test/segments';

const ENTITY_ROUTE = `${Routes.CONTACTS_INDIVIDUAL}/*`;

const defaultProps = {
	channelId: '123',
	groupId: '23',
	id: 'test',
	individual: data.getImmutableMock(Individual, data.mockIndividual)
};

jest.unmock('react-dom');

describe('IndividualProfileRoutes', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<ChannelContext.Provider value={mockChannelContext()}>
					<DataSourcesProvider groupId={defaultProps.groupId}>
						<MemoryRouter initialEntries={[ENTITY_URL]}>
							<RouterRoutes>
								<Route
									element={
										<IndividualProfileRoutesCDP
											{...defaultProps}
										/>
									}
									path={ENTITY_ROUTE}
								/>
							</RouterRoutes>
						</MemoryRouter>
					</DataSourcesProvider>
				</ChannelContext.Provider>
			</Provider>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
