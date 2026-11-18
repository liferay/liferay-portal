import React from 'react';
import {
	getAxisTickText,
	getTextWidth,
	getYAxisLabel,
	getYAxisWidth,
	RechartsTooltip
} from '../recharts';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('Recharts Util', () => {
	describe('getTextWidth', () => {
		it('should return text width', () => {
			// jest-canvas-mock v2: measureText returns {width: text.length}, TEXT_PADDING=4
			// 'test'.length === 4, so Math.ceil(4) + 4 = 8
			expect(getTextWidth('test')).toEqual(8);
		});
	});

	describe('getAxisTickText', () => {
		it('should return a function', () => {
			expect(getAxisTickText('x')).toBeFunction();
		});

		it('should render when returned function is called', () => {
			const {container} = render(
				getAxisTickText('x')({
					payload: {offset: 2, value: 4},
					textAnchor: 'middle',
					x: 12,
					y: 12
				})
			);

			const textEl = container.querySelector('text');

			expect(textEl).toBeInTheDocument();
			expect(textEl).toHaveAttribute('text-anchor', 'middle');
		});
	});

	describe('RechartsTooltip', () => {
		it('should render', () => {
			const {container, getByText} = render(
				<RechartsTooltip
					dateTitle='12-12-12'
					rows={[{label: 'test', value: 123}]}
					title='Test Title'
				/>
			);

			expect(
				container.querySelector('.bb-tooltip-container')
			).toBeInTheDocument();
			expect(getByText('Test Title')).toBeInTheDocument();
			expect(getByText('test')).toBeInTheDocument();
		});
	});

	describe('getYAxisLabel', () => {
		it('should return a function', () => {
			expect(getYAxisLabel('Test')).toBeFunction();
		});

		it('should render when returned function is called', () => {
			const {container} = render(
				getYAxisLabel('Test')({
					viewBox: {
						height: 14,
						width: 26,
						x: 12,
						y: 24
					}
				})
			);

			const textEl = container.querySelector('text');

			expect(textEl).toBeInTheDocument();
			expect(textEl).toHaveTextContent(/test/i);
		});
	});

	describe('getYAxisWidth', () => {
		it('should max y-axis width', () => {
			// jest-canvas-mock: measureText returns {width: text.length}
			// 'test test'.length = 9, Math.ceil(9) + 4 = 13
			// 'meow'.length = 4, Math.ceil(4) + 4 = 8
			// minWidth default = 30, max(13, 8, 30) = 30
			expect(
				getYAxisWidth([{title: 'test test'}, {title: 'meow'}], 'title')
			).toBe(30);
		});

		it('should minWidth for y-axis width', () => {
			const minWidth = 60;

			expect(getYAxisWidth([{title: 'test'}], 'title', minWidth)).toBe(
				minWidth
			);
		});
	});
});
