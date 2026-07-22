import * as API from 'shared/api';
import * as data from 'test/data';
import InterestTopics from '../InterestTopics';
import mockStore, {mockStoreData, toRD} from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const defaultProps = {
	groupId: '23'
};

const Wrapper = ({children, queryString = '', store = mockStore()}) => (
	<Provider store={store}>
		<MemoryRouter
			initialEntries={[
				`/workspace/23/settings/definitions/interest-topics${queryString}`
			]}
		>
			<RouterRoutes>
				<Route
					element={children}
					path={`${Routes.SETTINGS_DEFINITIONS_INTEREST_TOPICS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('InterestTopics', () => {
	afterEach(cleanup);

	it('should render', async () => {
		API.blockedKeywords.search.mockReturnValue(
			Promise.resolve({items: [data.mockBlockedKeyword(1)], total: 1})
		);

		const {container} = render(
			<Wrapper>
				<InterestTopics {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render page not found if an invalid page is provided', async () => {
		API.blockedKeywords.search.mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		const {container} = render(
			<Wrapper queryString='?page=33'>
				<InterestTopics {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.no-results-root')).toMatchSnapshot();
	});

	it('should render without an "add keyword" button if the user role is member', async () => {
		API.blockedKeywords.search.mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		const memberStore = mockStore(
			mockStoreData.setIn(['currentUser'], toRD('24'))
		);

		const {container, queryByText} = render(
			<Wrapper store={memberStore}>
				<InterestTopics {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(queryByText('Add Keyword')).toBeNull();
	});

	it('should render with an empty state', async () => {
		API.blockedKeywords.search.mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		const {container} = render(
			<Wrapper queryString='?query=foo'>
				<InterestTopics {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.no-results-root')).toMatchSnapshot();
	});

	it('should render with a message to add keywords if there are none', async () => {
		API.blockedKeywords.search.mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		const {container} = render(
			<Wrapper>
				<InterestTopics {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.no-results-root')).toMatchSnapshot();
	});

	it('should render with a member-specific message to add keywords if there are none', async () => {
		API.blockedKeywords.search.mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		const memberStore = mockStore(
			mockStoreData.setIn(['currentUser'], toRD('24'))
		);

		const {container} = render(
			<Wrapper store={memberStore}>
				<InterestTopics {...defaultProps} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.no-results-root')).toMatchSnapshot();
	});
});
