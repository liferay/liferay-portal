import MetricBar from '../MetricBar';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('MetricBar', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<MetricBar />);
		expect(container).toMatchSnapshot();
	});

	it('should render full MetricBar', () => {
		const {container} = render(<MetricBar percent={1} />);
		expect(container).toMatchSnapshot();
	});

	it('should render small MetricBar', () => {
		const {container} = render(<MetricBar size='sm' />);
		expect(container).toMatchSnapshot();
	});

	it('should render w/ children', () => {
		const {queryByText} = render(
			<MetricBar>
				<div data-testid='child'>{'Test Test'}</div>
			</MetricBar>
		);
		expect(queryByText('Test Test')).toBeTruthy();
	});
});
