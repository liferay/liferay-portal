import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import TopPagesCardContent, {
	ITopPagesCardItem,
	TOP_PAGES_TABS,
} from '../TopPagesCardContent';
import {cleanup, render, screen} from '@testing-library/react';
import {EXIT_RATE_METRIC} from 'shared/util/pagination';
import {MemoryRouter} from 'react-router-dom';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';

jest.unmock('react-dom');

const MOCK_CONTEXT = {
	filters: {},
	router: {
		params: {
			channelId: '123',
			groupId: '456',
		},
		query: {},
	},
};

const RANGE_SELECTORS = {
	rangeEnd: null as unknown as string,
	rangeKey: RangeKeyTimeRanges.Last30Days,
	rangeStart: null as unknown as string,
};

const item = (overrides: Partial<ITopPagesCardItem>): ITopPagesCardItem => ({
	assetId: 'https://liferay.com/page',
	assetTitle: 'A page',
	entrancesMetric: {value: 1},
	exitRateMetric: {value: 2},
	visitorsMetric: {value: 3},
	...overrides,
});

const renderContent = (
	items: ITopPagesCardItem[],
	activeTabId = TOP_PAGES_TABS[0].tabId,
	{
		footer,
		rangeSelectors = RANGE_SELECTORS,
	}: {
		footer?: {label: string; href: string};
		rangeSelectors?: RangeSelectors;
	} = {}
) =>
	render(
		<Provider store={mockStore()}>
			<BasePage.Context.Provider value={MOCK_CONTEXT}>
				<MemoryRouter>
					<TopPagesCardContent
						activeTabId={activeTabId}
						footer={footer}
						items={items}
						onActiveTabIdChange={noop}
						rangeSelectors={rangeSelectors}
					/>
				</MemoryRouter>
			</BasePage.Context.Provider>
		</Provider>
	);

describe('TopPagesCardContent', () => {
	afterEach(cleanup);

	it('should link a page that has a title to its dashboard', () => {
		renderContent([item({})]);

		expect(screen.getByText('A page').closest('a')).toHaveAttribute(
			'href',
			'/workspace/456/123/sites/pages/overview/https%3A%2F%2Fliferay.com%2Fpage/A%20page'
		);
	});

	it('should link a page with no title, leaving the title out of the route', () => {
		const {container} = renderContent([item({assetTitle: ''})]);

		expect(
			container.querySelector('.table-title a')?.getAttribute('href')
		).toBe(
			'/workspace/456/123/sites/pages/overview/https%3A%2F%2Fliferay.com%2Fpage'
		);
	});

	it('should render the exit rate with a single decimal place', () => {
		renderContent(
			[item({exitRateMetric: {value: 0.4567}})],
			EXIT_RATE_METRIC
		);

		expect(screen.getByText('45.7%')).toBeInTheDocument();
	});

	it('should render the exit rate with no trailing zero', () => {
		renderContent(
			[
				item({exitRateMetric: {value: 1.00001}}),
				item({
					assetId: 'https://liferay.com/b',
					exitRateMetric: {value: 0.24},
				}),
			],
			EXIT_RATE_METRIC
		);

		expect(screen.getByText('100%')).toBeInTheDocument();
		expect(screen.getByText('24%')).toBeInTheDocument();
	});

	it('should render a dash when the exit rate is not a finite number', () => {
		renderContent(
			[item({exitRateMetric: {value: undefined as unknown as number}})],
			EXIT_RATE_METRIC
		);

		expect(screen.getByText('-')).toBeInTheDocument();
	});

	it('should carry the selected range and the active tab into the footer action', () => {
		renderContent([item({})], EXIT_RATE_METRIC, {
			footer: {href: '/pages', label: 'View All'},
			rangeSelectors: {
				rangeEnd: '2026-07-20',
				rangeKey: RangeKeyTimeRanges.CustomRange,
				rangeStart: '2026-07-01',
			},
		});

		const href = screen
			.getByText('View All')
			.closest('a')
			?.getAttribute('href');

		expect(href).toContain('rangeKey=CUSTOM');
		expect(href).toContain('rangeStart=2026-07-01');
		expect(href).toContain('rangeEnd=2026-07-20');
		expect(href).toContain(`field=${EXIT_RATE_METRIC}`);
	});

	it('should not link a page with no canonical url', () => {
		const {container} = renderContent([item({assetId: ''})]);

		expect(container.querySelector('.table-title a')).toBeNull();
		expect(screen.getByText('A page')).toBeInTheDocument();
	});
});
