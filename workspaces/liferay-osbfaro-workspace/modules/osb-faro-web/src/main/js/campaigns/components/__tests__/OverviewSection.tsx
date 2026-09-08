import OverviewSection from '../OverviewSection';
import React from 'react';
import {CampaignMetricType, ICampaignMetric} from 'shared/api/campaigns';
import {cleanup, render, screen} from '@testing-library/react';
import {TrendClassification} from 'segment/types';

jest.unmock('react-dom');

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

	// The header renders a range ending today, so pin the clock and assert the
	// literal the reader sees rather than recomputing it with the same helpers.

	afterEach(() => {
		cleanup();
		jest.useRealTimers();
	});

	beforeEach(() => {
		jest.useFakeTimers().setSystemTime(
			new Date('2026-09-04T12:00:00.000Z')
		);
	});

	it('should render the overview header and all four card titles', () => {
		const {getByText} = render(<OverviewSection metrics={[]} />);

		expect(getByText('OVERVIEW')).toBeInTheDocument();
		expect(getByText('Campaigns')).toBeInTheDocument();
		expect(getByText('Accounts Touched')).toBeInTheDocument();
		expect(getByText('Pipeline Value')).toBeInTheDocument();
		expect(getByText('Closed Won')).toBeInTheDocument();
	});

	it('should state the window the metrics cover, as secondary text', () => {
		const {getByText} = render(<OverviewSection metrics={[]} />);

		// `TrailingNinetyDayRange` owns the window and covers it in its own
		// suite. Asserted here only to pin that the header renders it.

		const window = getByText('Jun 6, 2026 – Sep 3, 2026');

		expect(window).toBeInTheDocument();
		expect(window).toHaveClass('text-secondary');
	});

	it('should replace all four cards with a spinner while fetching', () => {
		const {container, getByText} = render(
			<OverviewSection loading metrics={METRICS} />
		);

		expect(container.querySelectorAll('.loading-root')).toHaveLength(4);

		// `MetricCard` returns early when loading, so a loading card carries
		// neither its title nor its value — only the section header survives.

		expect(getByText('OVERVIEW')).toBeInTheDocument();
		expect(container.textContent).not.toContain('Pipeline Value');
		expect(container.textContent).not.toContain('504M');
	});

	it('should fall back to 0 when the request has not resolved', () => {
		const {getAllByText} = render(<OverviewSection metrics={null} />);

		expect(getAllByText('0')).toHaveLength(4);
	});

	it('should map each metric to its card by metricType', () => {
		const metrics = [
			buildMetric(CampaignMetricType.CampaignCount, 11),
			buildMetric(CampaignMetricType.AccountsTouched, 22),
			buildMetric(CampaignMetricType.OpenPipelineAmount, 33),
			buildMetric(CampaignMetricType.ClosedWonAmount, 44),
		];

		render(<OverviewSection metrics={metrics} />);

		expect(cardOf('Campaigns').textContent).toContain('11');
		expect(cardOf('Accounts Touched').textContent).toContain('22');
		expect(cardOf('Pipeline Value').textContent).toContain('33');
		expect(cardOf('Closed Won').textContent).toContain('44');
	});

	it('should fall back to 0 for a card without a matching metric', () => {
		const metrics = [buildMetric(CampaignMetricType.CampaignCount, 11)];

		const {getAllByText, getByText} = render(
			<OverviewSection metrics={metrics} />
		);

		expect(getByText('11')).toBeInTheDocument();
		expect(getAllByText('0')).toHaveLength(3);
	});

	it('should abbreviate every value, with no currency symbol', () => {
		render(<OverviewSection metrics={METRICS} />);

		expect(cardOf('Campaigns').textContent).toContain('202');
		expect(cardOf('Accounts Touched').textContent).toContain('1.8K');
		expect(cardOf('Pipeline Value').textContent).toContain('504M');
		expect(cardOf('Closed Won').textContent).toContain('124M');

		expect(cardOf('Pipeline Value').textContent).not.toContain('$');
	});

	it('should keep the abbreviation suffix uppercase', () => {
		const {getByText} = render(<OverviewSection metrics={METRICS} />);

		// `MetricCard` lowercases its value through CSS alone, which leaves
		// textContent reading '504M' while the card renders '504m'. Assert on
		// the class, since no assertion over the text can catch this.

		expect(getByText('504M').closest('.text-uppercase')).toBeTruthy();
		expect(getByText('1.8K').closest('.text-uppercase')).toBeTruthy();
	});

	it('should render each trend against the previous 90 day window', () => {
		const {container} = render(<OverviewSection metrics={METRICS} />);

		const trends = container.querySelectorAll(
			'[data-testid="metric-card-trend"]'
		);

		expect(trends).toHaveLength(4);

		trends.forEach((trend) => {
			expect(trend.textContent).toContain('vs. Last 90 Days');
		});

		expect(container.textContent).toContain('36.8%');
		expect(container.textContent).toContain('14.1%');
		expect(container.textContent).toContain('1.3%');
		expect(container.textContent).toContain('0.6%');
	});
});
