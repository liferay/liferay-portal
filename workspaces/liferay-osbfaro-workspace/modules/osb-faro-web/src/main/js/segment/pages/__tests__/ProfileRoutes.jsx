import * as API from 'shared/api';
import DataSourcesProvider from 'shared/context/dataSources';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen} from '@testing-library/react';
import {mockChannelContext} from 'test/mock-channel-context';
import {mockSegment} from 'test/data';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {SegmentProfileRoutes} from '../ProfileRoutes';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
		id: 'test'
	})
}));

describe('SegmentProfileRoutes', () => {
	afterEach(cleanup);

	beforeAll(() => {
		delete window.location;
	});

	it('should render', async () => {
		window.location = {pathname: '/'};

		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelContext.Provider value={mockChannelContext()}>
						<DataSourcesProvider groupId='23'>
							<SegmentProfileRoutes />
						</DataSourcesProvider>
					</ChannelContext.Provider>
				</BrowserRouter>
			</Provider>
		);

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

		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelContext.Provider value={mockChannelContext()}>
						<DataSourcesProvider groupId='23'>
							<SegmentProfileRoutes />
						</DataSourcesProvider>
					</ChannelContext.Provider>
				</BrowserRouter>
			</Provider>
		);

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

		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelContext.Provider value={mockChannelContext()}>
						<DataSourcesProvider groupId='23'>
							<SegmentProfileRoutes />
						</DataSourcesProvider>
					</ChannelContext.Provider>
				</BrowserRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Edit Segment')).toBeTruthy();
		expect(screen.queryByText('Download Reports')).toBeNull();
		expect(screen.queryByText('Refresh Data')).toBeNull();
	});

	it('should render the external reference code with its label', async () => {
		window.location = {pathname: '/'};

		API.individualSegment.fetch.mockReturnValueOnce(
			Promise.resolve(mockSegment(0, {externalReferenceCode: 'my-erc'}))
		);

		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelContext.Provider value={mockChannelContext()}>
						<DataSourcesProvider groupId='23'>
							<SegmentProfileRoutes />
						</DataSourcesProvider>
					</ChannelContext.Provider>
				</BrowserRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('ERC: my-erc')).toBeTruthy();
	});
});
