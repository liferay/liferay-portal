import OverviewSection from '../OverviewSection';
import React from 'react';
import {
	CampaignMetricType,
	ICampaignMetric,
	mockCampaignMetrics,
} from '../../utils/mock-campaigns';
import {cleanup, render, screen} from '@testing-library/react';
import {TrendClassification} from 'segment/types';

jest.unmock('react-dom');

const buildMetric = (
	metricType: CampaignMetricType,
	value: number
): ICampaignMetric => ({
	metricType,
	trend: {
		percentage: 0,
		trendClassification: TrendClassification.Neutral,
	},
	value,
});

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
		render(<OverviewSection metrics={mockCampaignMetrics} />);

		expect(cardOf('Campaigns').textContent).toContain('202');
		expect(cardOf('Accounts Touched').textContent).toContain('1.8K');
		expect(cardOf('Pipeline Value').textContent).toContain('504M');
		expect(cardOf('Closed Won').textContent).toContain('124M');

		expect(cardOf('Pipeline Value').textContent).not.toContain('$');
	});

	it('should keep the abbreviation suffix uppercase', () => {
		const {getByText} = render(
			<OverviewSection metrics={mockCampaignMetrics} />
		);

		// `MetricCard` lowercases its value through CSS alone, which leaves
		// textContent reading '504M' while the card renders '504m'. Assert on
		// the class, since no assertion over the text can catch this.

		expect(getByText('504M').closest('.text-uppercase')).toBeTruthy();
		expect(getByText('1.8K').closest('.text-uppercase')).toBeTruthy();
	});

	it('should render each trend against the previous 90 day window', () => {
		const {container} = render(
			<OverviewSection metrics={mockCampaignMetrics} />
		);

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
