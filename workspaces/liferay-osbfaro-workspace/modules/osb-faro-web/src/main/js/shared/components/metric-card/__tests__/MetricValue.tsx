import MetricValue from '../MetricValue';
import React from 'react';
import {MetricType} from '../metrics';
import {render} from '@testing-library/react';

describe('MetricValue', () => {
	it('should render the component', () => {
		const {container} = render(<MetricValue value="100K" />);

		expect(container).toMatchSnapshot();
	});

	it('should render the component with number type', () => {
		const {container} = render(
			<MetricValue type={MetricType.Number} value="100K" />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the component with percentage ', () => {
		const {container} = render(
			<MetricValue type={MetricType.Percentage} value="100%" />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the component with time ', () => {
		const {container} = render(
			<MetricValue type={MetricType.Time} value="12m 40s" />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the component with ratings ', () => {
		const {container} = render(
			<MetricValue type={MetricType.Ratings} value="10/10" />
		);

		expect(container).toMatchSnapshot();
	});
});
