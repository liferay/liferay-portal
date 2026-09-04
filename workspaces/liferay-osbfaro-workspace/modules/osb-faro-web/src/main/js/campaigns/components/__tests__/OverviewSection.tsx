import OverviewSection from '../OverviewSection';
import React from 'react';
import {
	CampaignMetricType,
	ICampaignMetric,
	mockCampaignMetrics,
} from '../../utils/mock-campaigns';
import {cleanup, render} from '@testing-library/react';
import {TrendClassification} from 'segment/types';

jest.unmock('react-dom');

// The header renders a range ending today, so pin "now" and assert the literal
// the reader sees rather than recomputing it with the same helpers.

jest.mock('shared/util/date', () => ({
	...jest.requireActual('shared/util/date'),
	getDateNow: () => jest.requireActual('moment').utc('2026-09-04'),
}));

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

describe('OverviewSection', () => {
	afterEach(cleanup);

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

		const window = getByText('Jun 4, 2026 – Sep 4, 2026');

		expect(window).toBeInTheDocument();
		expect(window).toHaveClass('text-secondary');
	});

	it('should end the window today and open it three months earlier', () => {
		const {getByText} = render(<OverviewSection metrics={[]} />);

		// An en dash, as the design has it, not a hyphen.

		expect(getByText(/\u2013/)).toBeInTheDocument();
		expect(getByText(/^Jun 4, 2026 /)).toBeInTheDocument();
		expect(getByText(/ Sep 4, 2026$/)).toBeInTheDocument();
	});

	it('should map each metric to its card by metricType', () => {
		const metrics = [
			buildMetric(CampaignMetricType.CampaignCount, 11),
			buildMetric(CampaignMetricType.AccountsTouched, 22),
			buildMetric(CampaignMetricType.OpenPipelineAmount, 33),
			buildMetric(CampaignMetricType.ClosedWonAmount, 44),
		];

		const {getByText} = render(<OverviewSection metrics={metrics} />);

		const cardOf = (title: string) => getByText(title).closest('.card')!;

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
		const {getByText} = render(
			<OverviewSection metrics={mockCampaignMetrics} />
		);

		const cardOf = (title: string) => getByText(title).closest('.card')!;

		expect(cardOf('Campaigns').textContent).toContain('202');
		expect(cardOf('Accounts Touched').textContent).toContain('1.8K');
		expect(cardOf('Pipeline Value').textContent).toContain('504M');
		expect(cardOf('Closed Won').textContent).toContain('124M');

		expect(
			getByText('Pipeline Value').closest('.card')!.textContent
		).not.toContain('$');
	});

	it('should keep the abbreviation suffix uppercase', () => {
		const {getByText} = render(
			<OverviewSection metrics={mockCampaignMetrics} />
		);

		// `MetricCard` lowercases its value through CSS alone, which leaves
		// textContent reading '504M' while the card renders '504m'. Assert on
		// the class, since no assertion over the text can catch this.

		expect(getByText('504M')).toHaveClass('text-uppercase');
		expect(getByText('1.8K')).toHaveClass('text-uppercase');
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
