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
	colors: ['#000000', '#ffffff'],
	data: DATA,
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

		expect(container.querySelectorAll('path')).toHaveLength(DATA.length);
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
});
