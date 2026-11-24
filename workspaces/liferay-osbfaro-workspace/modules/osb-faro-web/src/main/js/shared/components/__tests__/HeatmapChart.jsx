import HeatMapChart, {
	getNicedExtent,
	getThresholdsFromData
} from '../HeatmapChart';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {range} from 'lodash';

describe('HeatMapChart', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<HeatMapChart data={[]} />);

		expect(container).toMatchSnapshot();
	});
});

describe('getNicedExtent', () => {
	it.each`
		extent                    | nicedExtent
		${[47, 539]}              | ${[0, 600]}
		${[500, 1450]}            | ${[600, 1400]}
		${[undefined, undefined]} | ${[undefined, undefined]}
		${[0, 10]}                | ${[0, 10]}
	`(
		'should modify the $extent to be the nice, round values in $nicedExtent',
		({extent, nicedExtent}) => {
			expect(getNicedExtent(extent)).toEqual(
				expect.arrayContaining(nicedExtent)
			);
		}
	);
});

describe('getThresholdsFromData', () => {
	it('should take the min and max of the data and create 4 equally-spaced threshold bins', () => {
		const mockData = range(168).map(i => ({value: i}));
		expect(getThresholdsFromData(mockData)).toEqual(
			expect.arrayContaining([1, 50, 100, 150, 200])
		);
	});

	it('should take the min and max of the data and create 4 equally-spaced threshold bins when the min of the data is greater than 0', () => {
		const mockData = range(168).map(i => ({value: 150 + i}));
		expect(getThresholdsFromData(mockData)).toEqual(
			expect.arrayContaining([150, 200, 250, 300, 350])
		);
	});
});
