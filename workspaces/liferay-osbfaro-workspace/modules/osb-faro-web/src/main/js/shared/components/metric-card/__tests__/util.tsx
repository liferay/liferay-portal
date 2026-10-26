jest.unmock('react-dom');

import * as data from 'test/data';
import {CompositeMetric, MetricType} from '../metrics';
import {
	convertHistogramKeysToDate,
	formatValue,
	getMetricsChartData,
	getRegexType,
	getSiteMetricsChartData,
} from '../util';

describe('formatValue', () => {
	it('captures an ASCII compact suffix as the unit', () => {
		const [result] = formatValue('4M', getRegexType(MetricType.Number));

		expect(result.props.children[0]).toBe('4');
		expect(result.props.children[1].props.children).toBe('M');
	});

	it('captures a Japanese compact suffix (万/億) as the unit', () => {
		const [tenThousands] = formatValue(
			'404.2万',
			getRegexType(MetricType.Number)
		);

		expect(tenThousands.props.children[0]).toBe('404.2');
		expect(tenThousands.props.children[1].props.children).toBe('万');

		const [hundredMillions] = formatValue(
			'1億',
			getRegexType(MetricType.Number)
		);

		expect(hundredMillions.props.children[0]).toBe('1');
		expect(hundredMillions.props.children[1].props.children).toBe('億');
	});
});

describe('convertHistogramKeysToDate', () => {
	it('should convert the histogram date key strings to Date types', () => {
		expect(
			[
				{
					key: '2018-07-16T00:00',
					previousValueKey: '2018-07-15T00:00',
					value: 15,
					valueKey: '2018-07-16T00:00',
				},
			].map(convertHistogramKeysToDate)
		).toMatchSnapshot();
	});
});

describe('getMetricsChartData', () => {
	it('should return data formatted for use in a Metrics chart', () => {
		const mockParameters = {
			histogram: data
				.mockMetricFragment(10)
				.histogram.metrics.map(convertHistogramKeysToDate) as any,
			name: 'fooMetric',
			title: '',
			tooltipTitle: '',
			type: MetricType.Number,
		};

		expect(getMetricsChartData(mockParameters)).toMatchSnapshot();
	});
});

describe('getSiteMetricsChartData', () => {
	it('should return data formatted for use in a Site Metrics chart', () => {
		const {name, title, tooltipTitle, type} = CompositeMetric;

		const mockParameters = {
			compositeData: {
				anonymousVisitorsMetric: data
					.mockMetricFragment(25)
					.histogram.metrics.map(convertHistogramKeysToDate),
				knownVisitorsMetric: data
					.mockMetricFragment(55)
					.histogram.metrics.map(convertHistogramKeysToDate),
			},
			histogram: data
				.mockMetricFragment(85)
				.histogram.metrics.map(convertHistogramKeysToDate),
			name,
			title,
			tooltipTitle,
			type,
		};
		expect(
			getSiteMetricsChartData(mockParameters as any)
		).toMatchSnapshot();
	});
});
