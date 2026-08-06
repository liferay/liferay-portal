jest.unmock('react-dom');

import * as data from 'test/data';
import {CHART_COLOR_NAMES} from 'shared/util/charts';
import {CompositeMetric, MetricType} from '../metrics';
import {
	convertHistogramKeysToDate,
	formatValue,
	getMetricsChartData,
	getMetricsData,
	getRegexType,
	getSiteMetricsChartData,
} from '../util';
import {getIntervals} from 'shared/util/charts';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {toUnix} from 'shared/util/date';

const {stark: CHART_BLUE, starkL2: CHART_BLUE_L2} = CHART_COLOR_NAMES;

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

describe('getMetricsData', () => {
	it('should return the chart items', () => {
		const rangeKey = 30;
		const keyDate = '2018-07-16T00:00';
		const valueKeyDate = '1531699200000';
		const previousValueKeyDate = '1531612800000';
		const metrics = [
			{
				name: 'comments',
				sortField: 'comments',
				title: 'comments',
				tooltipTitle: 'Avg. Comments',
				type: MetricType.Number,
			},
			{
				name: 'views',
				sortField: 'views',
				title: 'Views',
				tooltipTitle: 'Avg. Views',
				type: MetricType.Number,
			},
		] as any;

		const result = {
			comments: {
				histogram: {
					asymmetricalComparison: false,
					metrics: [
						{
							key: keyDate,
							previousValue: 0,
							previousValueKey: previousValueKeyDate,
							trend: {
								percentage: 10,
								trendClassification: 'NEUTRAL',
							},
							value: 15,
							valueKey: valueKeyDate,
						},
					],
				},
				trend: {
					percentage: 10,
					trendClassification: 'NEUTRAL',
				},
			},
			views: {
				histogram: {
					asymmetricalComparison: false,
					metrics: [
						{
							key: keyDate,
							previousValue: 0,
							previousValueKey: previousValueKeyDate,
							trend: {
								percentage: 10,
								trendClassification: 'NEUTRAL',
							},
							value: 5,
							valueKey: valueKeyDate,
						},
					],
				},
				trend: {
					percentage: -100,
					trendClassification: 'NEGATIVE',
				},
			},
		};
		const metricsData = getMetricsData(result as any, metrics, {
			rangeKey: rangeKey as unknown as RangeKeyTimeRanges,
		});

		const dateKeysIMap = new Map([
			[toUnix(keyDate), [toUnix(valueKeyDate)]],
		]);
		const prevDateKeysIMap = new Map([
			[toUnix(keyDate), [toUnix(previousValueKeyDate)]],
		]);

		metricsData.forEach((metricData) => {
			delete (metricData as any).format;
		});

		expect(metricsData).toEqual([
			{
				content: {
					details: {
						asymmetricComparison: undefined,
						color: '#6B6C7E',
						icon: 'caret-top-l',
						label: '10%',
					},
					name: 'comments',
					title: 'comments',
					type: 'number',
					value: '',
				},
				data: [
					{
						color: CHART_BLUE,
						data: [15],
						id: 'data_1',
						name: 'Avg. Comments',
						tooltipTitle: 'Avg. Comments',
					},
					{
						color: CHART_BLUE_L2,
						data: [0],
						id: 'data_previous',
						name: 'Previous Period',
					},
					{data: [toUnix(keyDate)], id: 'x'},
				],
				dateKeysIMap,
				intervals: getIntervals(
					RangeKeyTimeRanges.Last30Days,
					[toUnix(keyDate)] as number[],
					'D',
					dateKeysIMap
				),
				prevDateKeysIMap,
			},
			{
				content: {
					asymmetricComparison: undefined,
					details: {
						color: '#DA1414',
						icon: 'caret-bottom-l',
						label: '100%',
					},
					name: 'views',
					title: 'Views',
					type: 'number',
					value: '',
				},
				data: [
					{
						color: CHART_BLUE,
						data: [5],
						id: 'data_1',
						name: 'Avg. Views',
						tooltipTitle: 'Avg. Views',
					},
					{
						color: CHART_BLUE_L2,
						data: [0],
						id: 'data_previous',
						name: 'Previous Period',
					},
					{data: [toUnix(keyDate)], id: 'x'},
				],
				dateKeysIMap,
				intervals: getIntervals(
					RangeKeyTimeRanges.Last30Days,
					[toUnix(keyDate)] as number[],
					'D',
					dateKeysIMap
				),
				prevDateKeysIMap,
			},
		]);
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
