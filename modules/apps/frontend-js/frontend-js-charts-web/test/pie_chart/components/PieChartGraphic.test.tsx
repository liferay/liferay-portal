/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import PieChartGraphic from '../../../src/main/resources/META-INF/resources/js/pie_chart/components/PieChartGraphic';

const DATA = [
	{label: 'Alpha', value: 30},
	{label: 'Beta', value: 70},
];

function createSliceRefFactory(sliceRefs: (SVGPathElement | null)[]) {
	return (index: number) => (element: SVGPathElement | null) => {
		sliceRefs[index] = element;
	};
}

const DEFAULT_PROPS = {
	activeIndex: null,
	baseId: 'pie-chart',
	colors: ['#000000', '#ffffff'],
	data: DATA,
	focusIndex: null,
	innerRadius: 0,
	onFocus: () => {},
	onHover: () => {},
	onHoverEnd: () => {},
	onKeyDown: () => {},
	onSliceBlur: () => {},
	pathFactory: () => 'M 0 0',
	pixelSize: 200,
	sliceRefFactory: createSliceRefFactory([]),
	total: 100,
};

function renderGraphic(props = {}) {
	return render(<PieChartGraphic {...DEFAULT_PROPS} {...props} />);
}

describe('PieChartGraphic', () => {
	it('renders one slice path per datum', () => {
		const {container} = renderGraphic();

		expect(container.querySelectorAll('.chart-pie-slice')).toHaveLength(
			DATA.length
		);
	});

	it('renders no slices when the total is not positive', () => {
		const {container} = renderGraphic({total: 0});

		expect(container.querySelectorAll('.chart-pie-slice')).toHaveLength(0);
	});

	it('sizes the viewBox from the given pixel size', () => {
		const {container} = renderGraphic({pixelSize: 300});

		expect(container.querySelector('svg')).toHaveAttribute(
			'viewBox',
			'0 0 300 300'
		);
	});

	it('renders the center label when innerRadius is positive', () => {
		const {container} = renderGraphic({innerRadius: 40});

		expect(
			container.querySelector('.chart-pie-center-label')
		).toBeInTheDocument();
	});

	it('omits the center label when innerRadius is zero', () => {
		const {container} = renderGraphic({innerRadius: 0});

		expect(
			container.querySelector('.chart-pie-center-label')
		).not.toBeInTheDocument();
	});

	it('calls onFocus with the slice index when a slice is clicked', async () => {
		const onFocus = jest.fn();

		renderGraphic({onFocus});

		await userEvent.click(screen.getAllByRole('img')[1]);

		expect(onFocus).toHaveBeenCalledWith(1);
	});

	it('calls onHover and onHoverEnd when hovering a slice', async () => {
		const onHover = jest.fn();
		const onHoverEnd = jest.fn();

		renderGraphic({onHover, onHoverEnd});

		const slice = screen.getAllByRole('img')[0];

		await userEvent.hover(slice);
		expect(onHover).toHaveBeenCalledWith(0);

		await userEvent.unhover(slice);
		expect(onHoverEnd).toHaveBeenCalled();
	});

	it('calls onKeyDown with the slice index on keyboard interaction', () => {
		const onKeyDown = jest.fn();

		renderGraphic({onKeyDown});

		const slice = screen.getAllByRole('img')[1];

		slice.dispatchEvent(
			new KeyboardEvent('keydown', {bubbles: true, key: 'ArrowRight'})
		);

		expect(onKeyDown).toHaveBeenCalledWith(expect.anything(), 1);
	});

	it('populates the sliceRefs array with the rendered slice elements', () => {
		const sliceRefs: (SVGPathElement | null)[] = [];

		render(
			<PieChartGraphic
				{...DEFAULT_PROPS}
				sliceRefFactory={createSliceRefFactory(sliceRefs)}
			/>
		);

		expect(sliceRefs).toHaveLength(DATA.length);
		expect(sliceRefs[0]?.tagName).toBe('path');
	});

	describe('focus ring overlay', () => {
		const MULTI_SLICE_DATA = [
			{label: 'Alpha', value: 10},
			{label: 'Beta', value: 20},
			{label: 'Gamma', value: 30},
			{label: 'Delta', value: 40},
		];

		function pathFactoryByStartAngle({startAngle}: {startAngle: number}) {
			return `M ${startAngle}`;
		}

		it('renders no overlay when no slice is focused', () => {
			const {container} = renderGraphic({
				data: MULTI_SLICE_DATA,
				focusIndex: null,
				pathFactory: pathFactoryByStartAngle,
			});

			expect(
				container.querySelector('.chart-pie-focus-ring')
			).not.toBeInTheDocument();
			expect(
				container.querySelector('.chart-pie-focus-halo')
			).not.toBeInTheDocument();
		});

		it('renders the overlay only for a non-last focused slice, clipped to its own shape', () => {
			const {container} = renderGraphic({
				data: MULTI_SLICE_DATA,
				focusIndex: 1,
				pathFactory: pathFactoryByStartAngle,
			});

			const focusedSlicePath = screen.getAllByRole('img')[1];
			const focusedSliceD = focusedSlicePath.getAttribute('d');

			const ring = container.querySelector('.chart-pie-focus-ring');
			const halo = container.querySelector('.chart-pie-focus-halo');

			expect(ring).toBeInTheDocument();
			expect(halo).toBeInTheDocument();
			expect(ring).toHaveAttribute('d', focusedSliceD);
			expect(halo).toHaveAttribute('d', focusedSliceD);

			const overlayGroup = ring?.closest('g');

			expect(overlayGroup).toHaveAttribute(
				'clip-path',
				'url(#pie-chart-slice-clip-1)'
			);
		});

		it('keeps overlay halo/ring paths out of the direct-child slice path selector, so the reveal animation does not retrigger on focus change', () => {
			const {container} = renderGraphic({
				data: MULTI_SLICE_DATA,
				focusIndex: 1,
				pathFactory: pathFactoryByStartAngle,
			});

			const directChildPaths = container.querySelectorAll(
				'svg.chart-pie-svg > path'
			);
			const anyDepthPaths = container.querySelectorAll(
				'svg.chart-pie-svg path'
			);

			expect(directChildPaths).toHaveLength(MULTI_SLICE_DATA.length);
			expect(anyDepthPaths.length).toBeGreaterThan(
				directChildPaths.length
			);

			directChildPaths.forEach((path) => {
				expect(path).toHaveClass('chart-pie-slice');
			});

			const overlayHalo = container.querySelector(
				'.chart-pie-focus-halo'
			);
			const overlayRing = container.querySelector(
				'.chart-pie-focus-ring'
			);

			expect(Array.from(directChildPaths)).not.toContain(overlayHalo);
			expect(Array.from(directChildPaths)).not.toContain(overlayRing);
		});

		it('generates a distinct clipPath per slice matching that slice own d', () => {
			const {container} = renderGraphic({
				data: MULTI_SLICE_DATA,
				focusIndex: null,
				pathFactory: pathFactoryByStartAngle,
			});

			const clipPaths = container.querySelectorAll('clipPath');

			expect(clipPaths).toHaveLength(MULTI_SLICE_DATA.length);

			clipPaths.forEach((clipPath, index) => {
				const slicePath = screen.getAllByRole('img')[index];

				expect(clipPath).toHaveAttribute(
					'id',
					`pie-chart-slice-clip-${index}`
				);
				expect(clipPath.querySelector('path')).toHaveAttribute(
					'd',
					slicePath.getAttribute('d')
				);
			});
		});
	});
});
