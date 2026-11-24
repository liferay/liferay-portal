import React from 'react';
import TypeTrend, {TrendItem} from '../TypeTrend';
import {cleanup, render} from '@testing-library/react';

describe('TypeTrend', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<TypeTrend items={[]} />);
		expect(container).toMatchSnapshot();
	});
});

describe('TrendItem', () => {
	const mockTrendItem = {
		change: -0.02,
		data: [8, 5, 3, 2, 1],
		id: 'totalIndividuals',
		title: Liferay.Language.get('total-individuals'),
		total: 1341321
	};

	afterEach(cleanup);

	xit('should render', () => {
		const {container} = render(<TrendItem {...mockTrendItem} />);
		expect(container).toMatchSnapshot();
	});

	it('should render a negative change', () => {
		const {container} = render(
			<TrendItem {...mockTrendItem} change={-0.2} />
		);
		expect(container.querySelector('.change .decrease')).not.toBeNull();
		expect(
			container.querySelector('.change .lexicon-icon-caret-bottom')
		).not.toBeNull();
		expect(container.querySelector('.change')).toHaveTextContent(
			'vs. Last 30 Days'
		);
	});

	it('should render a positive change', () => {
		const {container} = render(
			<TrendItem {...mockTrendItem} change={0.2} />
		);
		expect(container.querySelector('.change .increase')).not.toBeNull();
		expect(
			container.querySelector('.change .lexicon-icon-caret-top')
		).not.toBeNull();
	});

	it('should render a 0 change', () => {
		const {container} = render(<TrendItem {...mockTrendItem} change={0} />);
		expect(container.querySelector('.change .increase')).toBeNull();
		expect(container.querySelector('.change .decrease')).toBeNull();
		expect(container.querySelector('.change .icon-root')).toBeNull();
	});

	it('should render a fallback display if change is infinite', () => {
		const {container} = render(
			<TrendItem {...mockTrendItem} change={Infinity} />
		);
		expect(container.querySelector('.change b')).toHaveTextContent('--');
	});
});
