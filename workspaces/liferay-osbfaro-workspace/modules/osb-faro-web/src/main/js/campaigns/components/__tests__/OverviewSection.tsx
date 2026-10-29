import OverviewSection from '../OverviewSection';
import React from 'react';
import {CampaignMetricType, ICampaignMetric} from 'shared/api/campaigns';
import {cleanup, render, screen} from '@testing-library/react';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {TrendClassification} from 'segment/types';

jest.unmock('react-dom');

const buildRangeSelectors = (
	rangeKey: RangeKeyTimeRanges,
	rangeStart = '',
	rangeEnd = ''
): RangeSelectors => ({rangeEnd, rangeKey, rangeStart});

const LAST_30_DAYS = buildRangeSelectors(RangeKeyTimeRanges.Last30Days);

const getTrendLabels = (container: HTMLElement) =>
	Array.from(
		container.querySelectorAll('[data-testid="metric-card-trend"]'),
		(trend) => trend.textContent
	);

const buildMetric = (
	metricType: CampaignMetricType,
	value: number,
	percentage = 0
): ICampaignMetric => ({
	metricType,
	trend: {
		percentage,
		trendClassification: percentage
			? TrendClassification.Positive
			: TrendClassification.Neutral,
	},
	value,
});

const METRICS: ICampaignMetric[] = [
	buildMetric(CampaignMetricType.CampaignCount, 202, 36.8),
	buildMetric(CampaignMetricType.AccountsTouched, 1800, 14.1),
	buildMetric(CampaignMetricType.OpenPipelineAmount, 504000000, 1.3),
	buildMetric(CampaignMetricType.ClosedWonAmount, 124000000, 0.6),
];

const cardOf = (title: string) => screen.getByText(title).closest('.card')!;

describe('OverviewSection', () => {
	afterEach(cleanup);

	it('should render the overview header and all four card titles', () => {
		const {getByText} = render(
			<OverviewSection metrics={[]} rangeSelectors={LAST_30_DAYS} />
		);

		expect(getByText('OVERVIEW')).toBeInTheDocument();
		expect(getByText('Campaigns')).toBeInTheDocument();
		expect(getByText('Accounts Touched')).toBeInTheDocument();
		expect(getByText('Pipeline Value')).toBeInTheDocument();
		expect(getByText('Closed Won')).toBeInTheDocument();
	});

	it('should leave the range to the page selector rather than the header', () => {
		const {container} = render(
			<OverviewSection metrics={[]} rangeSelectors={LAST_30_DAYS} />
		);

		// The range used to be pinned beside the heading as a fixed 90 day
		// window. The page's range selector owns it now.

		expect(container.textContent).not.toMatch(/\d{4} – /);
	});

	it('should replace all four cards with a spinner while fetching', () => {
		const {container, getByText} = render(
			<OverviewSection
				loading
				metrics={METRICS}
				rangeSelectors={LAST_30_DAYS}
			/>
		);

		expect(container.querySelectorAll('.loading-root')).toHaveLength(4);

		// `MetricCard` returns early when loading, so a loading card carries
		// neither its title nor its value — only the section header survives.

		expect(getByText('OVERVIEW')).toBeInTheDocument();
		expect(container.textContent).not.toContain('Pipeline Value');
		expect(container.textContent).not.toContain('504M');
	});

	it('should fall back to 0 when the request has not resolved', () => {
		const {getAllByText} = render(
			<OverviewSection metrics={null} rangeSelectors={LAST_30_DAYS} />
		);

		expect(getAllByText('0')).toHaveLength(4);
	});

	it('should map each metric to its card by metricType', () => {
		const metrics = [
			buildMetric(CampaignMetricType.CampaignCount, 11),
			buildMetric(CampaignMetricType.AccountsTouched, 22),
			buildMetric(CampaignMetricType.OpenPipelineAmount, 33),
			buildMetric(CampaignMetricType.ClosedWonAmount, 44),
		];

		render(
			<OverviewSection metrics={metrics} rangeSelectors={LAST_30_DAYS} />
		);

		expect(cardOf('Campaigns').textContent).toContain('11');
		expect(cardOf('Accounts Touched').textContent).toContain('22');
		expect(cardOf('Pipeline Value').textContent).toContain('33');
		expect(cardOf('Closed Won').textContent).toContain('44');
	});

	it('should fall back to 0 for a card without a matching metric', () => {
		const metrics = [buildMetric(CampaignMetricType.CampaignCount, 11)];

		const {getAllByText, getByText} = render(
			<OverviewSection metrics={metrics} rangeSelectors={LAST_30_DAYS} />
		);

		expect(getByText('11')).toBeInTheDocument();
		expect(getAllByText('0')).toHaveLength(3);
	});

	it('should abbreviate every value, with no currency symbol', () => {
		render(
			<OverviewSection metrics={METRICS} rangeSelectors={LAST_30_DAYS} />
		);

		expect(cardOf('Campaigns').textContent).toContain('202');
		expect(cardOf('Accounts Touched').textContent).toContain('1.8K');
		expect(cardOf('Pipeline Value').textContent).toContain('504M');
		expect(cardOf('Closed Won').textContent).toContain('124M');

		expect(cardOf('Pipeline Value').textContent).not.toContain('$');
	});

	it('should keep the abbreviation suffix uppercase', () => {
		const {getByText} = render(
			<OverviewSection metrics={METRICS} rangeSelectors={LAST_30_DAYS} />
		);

		// `MetricCard` lowercases its value through CSS alone, which leaves
		// textContent reading '504M' while the card renders '504m'. Assert on
		// the class, since no assertion over the text can catch this.

		expect(getByText('504M').closest('.text-uppercase')).toBeTruthy();
		expect(getByText('1.8K').closest('.text-uppercase')).toBeTruthy();
	});

	it('should render each trend against the window of the selected range', () => {
		const {container} = render(
			<OverviewSection metrics={METRICS} rangeSelectors={LAST_30_DAYS} />
		);

		const trendLabels = getTrendLabels(container);

		expect(trendLabels).toHaveLength(4);

		trendLabels.forEach((trendLabel) => {
			expect(trendLabel).toContain('vs. Last 30 Days');
		});

		expect(container.textContent).toContain('36.8%');
		expect(container.textContent).toContain('14.1%');
		expect(container.textContent).toContain('1.3%');
		expect(container.textContent).toContain('0.6%');
	});

	it.each([
		[RangeKeyTimeRanges.Last7Days, 'vs. Last 7 Days'],
		[RangeKeyTimeRanges.Last90Days, 'vs. Last 90 Days'],
		[RangeKeyTimeRanges.LastYear, 'vs. Last 365 Days'],
		[RangeKeyTimeRanges.Last24Hours, 'vs. Last 24 Hours'],
		[RangeKeyTimeRanges.Yesterday, 'vs. Last 1 Days'],
	])('should name the window of range key %s', (rangeKey, trendLabel) => {
		const {container} = render(
			<OverviewSection
				metrics={METRICS}
				rangeSelectors={buildRangeSelectors(rangeKey)}
			/>
		);

		expect(getTrendLabels(container)[0]).toContain(trendLabel);
	});

	it('should count the days of a custom range, both ends included', () => {
		const {container} = render(
			<OverviewSection
				metrics={METRICS}
				rangeSelectors={buildRangeSelectors(
					RangeKeyTimeRanges.CustomRange,
					'2026-10-01',
					'2026-10-14'
				)}
			/>
		);

		expect(getTrendLabels(container)[0]).toContain('vs. Last 14 Days');
	});

	it('should render the trend alone for all time', () => {
		const {container} = render(
			<OverviewSection
				metrics={METRICS}
				rangeSelectors={buildRangeSelectors(RangeKeyTimeRanges.AllTime)}
			/>
		);

		expect(getTrendLabels(container)[0]).not.toContain('vs.');
		expect(container.textContent).toContain('36.8%');
	});
});
