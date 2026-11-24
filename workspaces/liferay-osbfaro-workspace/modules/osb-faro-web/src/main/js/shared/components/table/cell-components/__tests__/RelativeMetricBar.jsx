import React from 'react';
import RelativeMetricBar from '../RelativeMetricBar';
import {cleanup, render} from '@testing-library/react';

describe('RelativeMetricBar', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<RelativeMetricBar
				data={{
					count: 6,
					name: 'Test Test'
				}}
				maxCount={10}
				showTitle={false}
				totalCount={12}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ name', () => {
		const {getByText} = render(
			<RelativeMetricBar
				data={{
					count: 6,
					name: 'Test Test'
				}}
				maxCount={10}
				showName
				totalCount={12}
			/>
		);

		expect(getByText('Test Test')).toBeTruthy();
	});

	it('should render w/ data-tooltip attr', () => {
		const {container} = render(
			<RelativeMetricBar
				data={{
					count: 6,
					name: 'Test Test'
				}}
				maxCount={10}
				showName
				totalCount={12}
			/>
		);

		expect(container.querySelector('[data-tooltip]')).toBeTruthy();
	});
});
