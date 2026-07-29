import BasePage from 'shared/components/base-page';
import PagePathCard from '../PagePathCard';
import React from 'react';
import {CHART_COLORS, MAIN_NODE_COLOR, SECONDARY_NODE_COLOR} from '../utils';
import {cleanup, fireEvent, getByTestId, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPagePathReq} from 'test/graphql-data';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const PREVIOUS_PATH_NODES = [
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'https://www.site1.com',
		external: false,
		title: 'Site 1',
		views: 10000,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'https://www.site2.com',
		external: false,
		title: 'Site 2',
		views: 10000,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'https://www.site3.com',
		external: false,
		title: 'Site 3',
		views: 5000,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'others',
		external: false,
		title: 'others',
		views: 500,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'drop-offs',
		external: false,
		title: 'drop-offs',
		views: 8000,
	},
];

const FOLLOWING_PATH_NODES = [
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'https://www.google.com',
		external: true,
		title: 'Google',
		views: 10000,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'https://www.facebook.com',
		external: true,
		title: 'Facebook',
		views: 10000,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'https://www.instagram.com',
		external: true,
		title: 'Instagram',
		views: 8000,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'direct',
		external: false,
		title: 'direct',
		views: 5000,
	},
	{
		__typename: 'PagePathNode',
		canonicalUrl: 'others',
		external: false,
		title: 'others',
		views: 1000,
	},
];

const DATA = {
	pagePath: {
		__typename: 'PagePath',
		canonicalUrl: 'https://www.liferay.com',
		followingPagePathNodes: PREVIOUS_PATH_NODES,
		previousPagePathNodes: FOLLOWING_PATH_NODES,
		title: 'Liferay Home Page',
		views: 100000,
	},
};

const EMPTY_STATE_DATA = {
	pagePath: {
		__typename: 'PagePath',
		canonicalUrl: 'https://www.liferay.com',
		followingPagePathNodes: [],
		previousPagePathNodes: [],
		title: 'Liferay Home Page',
		views: 100000,
	},
};

const WrapperComponent = ({
	accountId,
	data,
	rangeSelectors = {
		rangeEnd: '',
		rangeKey: RangeKeyTimeRanges.Last30Days,
		rangeStart: '',
	},
	reqOptions = {},
	segmentId,
}: {
	accountId?: string;
	data: any;
	rangeSelectors?: RangeSelectors;
	reqOptions?: Record<string, unknown>;
	segmentId?: string;
}) => (
	<MemoryRouter
		initialEntries={[
			'/workspace/4567/123/sites/pages/overview/https%3A%2F%2Fliferay.com%2Fhome/Liferay%20DXP%20-%20Home?rangeKey=30',
		]}
	>
		<Route path="/workspace/:groupId/:channelId/sites/pages/overview/:touchpoint/:title">
			<BasePage.Context.Provider
				value={{accountId, filters: {}, router: {}, segmentId}}
			>
				<MockedProvider
					cache={new InMemoryCache({freezeResults: false} as any)}
					mocks={[
						mockPagePathReq(data, {
							accountId,
							segmentId,
							...reqOptions,
						} as any),
					]}
				>
					<PagePathCard rangeSelectors={rangeSelectors} />
				</MockedProvider>
			</BasePage.Context.Provider>
		</Route>
	</MemoryRouter>
);

describe('PagePathCard', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(<WrapperComponent data={DATA} />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should filter by the selected account', async () => {
		const {container} = render(
			<WrapperComponent accountId="100" data={DATA} />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should filter by the selected segment', async () => {
		const {container} = render(
			<WrapperComponent data={DATA} segmentId="segment-100" />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render empty state', async () => {
		const {container, getByText} = render(
			<WrapperComponent data={EMPTY_STATE_DATA} />
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Path Analysis')).toBeInTheDocument();
		expect(getByText('No data was found.')).toBeInTheDocument();
		expect(
			getByText(
				'Check back later to verify if data has been received from your data sources.'
			)
		).toBeInTheDocument();
		expect(getByText('Learn more about path.')).toHaveAttribute(
			'href',
			'https://learn.liferay.com/w/dxp/personalization/analytics-cloud/touchpoints/sites-analytics/pages-analytics/paths-analytics'
		);
	});

	it('should render node and path with correct colors', async () => {
		const {container} = render(<WrapperComponent data={DATA} />);

		await waitForLoadingToBeRemoved(container);

		const nodes = container.querySelectorAll(
			'.recharts-sankey-nodes > .recharts-layer'
		);

		/**
		 * 5 previous node +
		 * 5 following node +
		 * 1 main node
		 */

		expect(nodes).toHaveLength(11);

		nodes.forEach((node, index) => {

			// Main node is always the first one

			if (index === 0) {
				expect(node.querySelector('path')).toHaveAttribute(
					'fill',
					MAIN_NODE_COLOR
				);

				return;
			}

			const title = getByTestId(
				node as HTMLElement,
				'sankey-node-title'
			).textContent;

			if (
				title === 'Drop Offs' ||
				title === 'Other Referrals' ||
				title === 'Other Pages'
			) {
				expect(node.querySelector('path')).toHaveAttribute(
					'fill',
					SECONDARY_NODE_COLOR
				);

				return;
			}

			expect(node.querySelector('path')).toHaveAttribute(
				'fill',
				CHART_COLORS[index - 1]
			);
		});
	});

	it('should render popover when a URL is hovered', async () => {
		const {container, getByText} = render(<WrapperComponent data={DATA} />);

		await waitForLoadingToBeRemoved(container);

		const nodes = container.querySelectorAll(
			'.recharts-sankey-nodes > .recharts-layer'
		);

		/**
		 * 5 previous node +
		 * 5 following node +
		 * 1 main node
		 */

		expect(nodes).toHaveLength(11);

		const url = getByText('https://www.site3....');

		fireEvent.mouseOver(url);

		expect(container.querySelector('.popover-body')).toBeInTheDocument();

		expect(getByText('Page Views | Exit Pages')).toBeInTheDocument();

		expect(getByText('5,000')).toBeInTheDocument();
	});

	it('should check if tooltip are rendered', async () => {
		const {container} = render(<WrapperComponent data={DATA} />);

		await waitForLoadingToBeRemoved(container);

		const nodes = container.querySelectorAll(
			'.recharts-sankey-nodes > .recharts-layer'
		);

		const tooltip = container.querySelector('[data-tooltip-align="right"]');

		expect(tooltip).toBeInTheDocument();

		expect(tooltip).toHaveAttribute('title', 'Go to Dashboard Page');

		/**
		 * 5 previous node +
		 * 5 following node +
		 * 1 main node
		 */

		expect(nodes).toHaveLength(11);
	});

	it('should create the link with the rangeKey from dropdown', async () => {
		const {container} = render(
			<WrapperComponent
				data={DATA}
				rangeSelectors={{
					rangeEnd: '',
					rangeKey: RangeKeyTimeRanges.Last24Hours,
					rangeStart: '',
				}}
				reqOptions={{rangeKey: Number(RangeKeyTimeRanges.Last24Hours)}}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const link = container.querySelector('[data-tooltip-align="right"]');

		expect(link).toHaveAttribute(
			'href',
			'/workspace/4567/123/sites/pages/overview/https%3A%2F%2Fwww.liferay.com/Liferay%20Home%20Page?rangeKey=0'
		);
	});

	it('should create the link with the rangeKey from dropdown even in a empty state', async () => {
		const {container} = render(
			<WrapperComponent
				data={EMPTY_STATE_DATA}
				rangeSelectors={{
					rangeEnd: '',
					rangeKey: RangeKeyTimeRanges.Last24Hours,
					rangeStart: '',
				}}
				reqOptions={{rangeKey: Number(RangeKeyTimeRanges.Last24Hours)}}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const link = container.querySelector('[data-tooltip-align="right"]');

		expect(link).toHaveAttribute(
			'href',
			'/workspace/4567/123/sites/pages/overview/https%3A%2F%2Fwww.liferay.com/Liferay%20Home%20Page?rangeKey=0'
		);
	});
});
