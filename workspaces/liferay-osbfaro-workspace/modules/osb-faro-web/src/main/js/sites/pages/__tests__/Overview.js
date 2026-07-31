import BasePage from 'shared/components/base-page';
import client from 'shared/apollo/client';
import Overview from '../Overview';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {MemoryRouter, Route} from 'react-router-dom';
import {mockChannelContext} from 'test/mock-channel-context';
import {Routes} from 'shared/util/router';

jest.unmock('react-dom');

const MOCK_CONTEXT = {
	rangeKey: {defaultValue: '30'},
	router: {
		params: {
			channelId: '123123',
			groupId: '23',
		},
		query: {
			rangeKey: '30',
		},
	},
};

const renderOverview = () =>
	render(
		<ApolloProvider client={client}>
			<MemoryRouter initialEntries={['/workspace/23/123123/sites']}>
				<Route path={Routes.SITES}>
					<ChannelContext.Provider value={mockChannelContext()}>
						<BasePage.Context.Provider value={MOCK_CONTEXT}>
							<Overview
								channelName="Test Channel"
								router={{
									params: {
										channelId: '123123',
										groupId: '23',
									},
								}}
							/>
						</BasePage.Context.Provider>
					</ChannelContext.Provider>
				</Route>
			</MemoryRouter>
		</ApolloProvider>
	);

describe('Sites Dashboard Overview', () => {
	afterEach(cleanup);

	it('should give the Top Pages and Acquisitions cards the same minimum height', () => {
		const {container} = renderOverview();

		expect(container.querySelector('.top-pages-card-root')).toHaveStyle(
			'min-height: 575px'
		);
		expect(container.querySelector('.acquisitions-card-root')).toHaveStyle(
			'min-height: 575px'
		);
	});

	it('should give the Visitors by Time, Search Terms and Interests cards the same minimum height', () => {
		const {container} = renderOverview();

		expect(container.querySelector('.visitors-by-time-card')).toHaveStyle(
			'min-height: 545px'
		);
		expect(container.querySelector('.search-terms-card-root')).toHaveStyle(
			'min-height: 545px'
		);
		expect(container.querySelector('.interests-card-root')).toHaveStyle(
			'min-height: 545px'
		);
	});

	it('render', () => {
		const {container, getAllByText, getByLabelText, getByText} = render(
			<ApolloProvider client={client}>
				<MemoryRouter initialEntries={['/workspace/23/123123/sites']}>
					<Route path={Routes.SITES}>
						<ChannelContext.Provider value={mockChannelContext()}>
							<BasePage.Context.Provider value={MOCK_CONTEXT}>
								<Overview
									channelName="Test Channel"
									router={{
										params: {
											channelId: '123123',
											groupId: '23',
										},
									}}
								/>
							</BasePage.Context.Provider>
						</ChannelContext.Provider>
					</Route>
				</MemoryRouter>
			</ApolloProvider>
		);
		fireEvent.click(getByText('All Visitors'));

		expect(getAllByText('All Visitors')[1]).toBeTruthy();
		expect(getByText('Anonymous Visitors')).toBeTruthy();
		expect(getByText('Known Visitors')).toBeTruthy();
		expect(getByLabelText('View All Pages')).toBeInTheDocument();
		expect(getByLabelText('View All Search Terms')).toBeInTheDocument();
		expect(getByLabelText('View All Interests')).toBeInTheDocument();

		expect(container).toMatchSnapshot();
	});
});
