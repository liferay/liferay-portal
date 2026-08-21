import * as API from 'shared/api';
import DataSourcesProvider from 'shared/context/dataSources';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {mockSegment} from 'test/data';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {SegmentProfileRoutes} from '../ProfileRoutes';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('../Overview', () => () => <div>{'SegmentOverview'}</div>);
jest.mock('../OverviewRealTime', () => () => (
	<div>{'SegmentOverviewRealTime'}</div>
));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
		id: 'test'
	})
}));

const ENTITY_URL = '/workspace/23/123/contacts/segments/test';

const ENTITY_ROUTE = `${Routes.CONTACTS_SEGMENT}/*`;

const renderProfileRoutes = () =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter initialEntries={[ENTITY_URL]}>
				<ChannelContext.Provider value={mockChannelContext()}>
					<DataSourcesProvider groupId='23'>
						<RouterRoutes>
							<Route
								element={<SegmentProfileRoutes />}
								path={ENTITY_ROUTE}
							/>
						</RouterRoutes>
					</DataSourcesProvider>
				</ChannelContext.Provider>
			</MemoryRouter>
		</Provider>
	);

describe('SegmentProfileRoutes', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = renderProfileRoutes();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getAllByText('Seattle0').length).toBeGreaterThan(0);
	});

	it('should render the account dashboard for an account segment', async () => {
		window.location = {pathname: '/'};

		API.individualSegment.fetch.mockReturnValueOnce(
			Promise.resolve(
				mockSegment(0, {segmentCategory: SegmentCategories.Account})
			)
		);

		const {container} = renderProfileRoutes();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Account Batch Segment')).toBeTruthy();
		expect(screen.getByText('SEGMENT MEMBERSHIP')).toBeTruthy();
	});

	it('should offer the edit action and no report actions for a real time segment', async () => {
		window.location = {pathname: '/'};

		API.individualSegment.fetch.mockReturnValueOnce(
			Promise.resolve(
				mockSegment(0, {segmentType: SegmentTypes.RealTime})
			)
		);

		const {container} = renderProfileRoutes();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Edit Segment')).toBeTruthy();
		expect(screen.queryByText('Download Reports')).toBeNull();
		expect(screen.queryByText('Refresh Data')).toBeNull();
	});

	it('should render the external reference code with its label', async () => {
		API.individualSegment.fetch.mockReturnValueOnce(
			Promise.resolve(mockSegment(0, {externalReferenceCode: 'my-erc'}))
		);

		const {container} = renderProfileRoutes();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('ERC: my-erc')).toBeTruthy();
	});
});
