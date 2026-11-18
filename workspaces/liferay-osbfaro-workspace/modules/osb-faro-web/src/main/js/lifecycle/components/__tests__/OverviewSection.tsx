import OverviewSection from '../OverviewSection';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {IOverviewMetric, OverviewMetricType} from '../../utils/types';
import {TrendClassification} from 'segment/types';

jest.unmock('react-dom');

const buildMetric = (
	metricType: OverviewMetricType,
	value: number
): IOverviewMetric => ({
	metricType,
	trend: {
		percentage: 0,
		trendClassification: TrendClassification.Neutral,
	},
	value,
});

describe('OverviewSection', () => {
	afterEach(cleanup);

	it('should render the overview header and all three card titles', () => {
		const {getByText} = render(<OverviewSection />);

		expect(getByText('OVERVIEW')).toBeInTheDocument();
		expect(getByText(/^net new pipeline generated$/i)).toBeInTheDocument();
		expect(getByText(/^stalled accounts$/i)).toBeInTheDocument();
		expect(getByText(/^at risk accounts$/i)).toBeInTheDocument();
	});

	it('should fall back to 0 for each card when no metrics are provided', () => {
		const {getAllByText} = render(<OverviewSection />);

		expect(getAllByText('0')).toHaveLength(3);
	});

	it('should map each metric to its card by metricType', () => {
		const metrics = [
			buildMetric(OverviewMetricType.NewPipeline, 11),
			buildMetric(OverviewMetricType.Stalled, 22),
			buildMetric(OverviewMetricType.AtRisk, 33),
		];

		const {getByText} = render(<OverviewSection metrics={metrics} />);

		const cardOf = (title: string) => getByText(title).closest('.card')!;

		expect(cardOf('Net New Pipeline Generated').textContent).toContain(
			'11'
		);
		expect(cardOf('Stalled Accounts').textContent).toContain('22');
		expect(cardOf('At Risk Accounts').textContent).toContain('33');
	});

	it('should fall back to 0 for a card without a matching metric', () => {
		const metrics = [buildMetric(OverviewMetricType.NewPipeline, 11)];

		const {getAllByText, getByText} = render(
			<OverviewSection metrics={metrics} />
		);

		expect(getByText('11')).toBeInTheDocument();
		expect(getAllByText('0')).toHaveLength(2);
	});

	it('should render the trend label comparing against the last 90 days', () => {
		const {container} = render(<OverviewSection />);

		expect(container.textContent).toContain('vs. Last 90 Days');
		expect(container.textContent).not.toContain('Months');
	});

	it('should render a loading spinner in each card when loading is true', () => {
		const {container, queryByText} = render(<OverviewSection loading />);

		expect(container.querySelectorAll('.loading-root')).toHaveLength(3);
		expect(queryByText(/^net new pipeline generated$/i)).toBeNull();
		expect(queryByText(/^stalled accounts$/i)).toBeNull();
		expect(queryByText(/^at risk accounts$/i)).toBeNull();
	});
});
