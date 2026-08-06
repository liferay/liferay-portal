import {
	formatYAxis,
	getFormattedMedian,
	getFormattedMedianLabel,
	getFormattedProbabilityToWin,
	getMetricName,
	getMetricUnit,
	getStatusColor,
	getStatusName,
	getTicks,
	getVariantLabels,
	toThousandsABTesting,
} from '../experiments';
import {DEFAULT_LOCALE, setLocale} from 'shared/util/locale';

const mockBestVariant = {
	changes: 1,
	control: false,
	dxpVariantId: 'DEFAULT',
	dxpVariantName: 'Control',
	trafficSplit: 50,
	uniqueVisitors: 1000,
};

describe('getStatusColor', () => {
	it('should return display property based on status', () => {
		expect(getStatusColor('COMPLETED')).toEqual('success');
		expect(getStatusColor('DRAFT')).toEqual('secondary');
		expect(getStatusColor('FINISHED_NO_WINNER')).toEqual('secondary');
		expect(getStatusColor('FINISHED_WINNER')).toEqual('success');
		expect(getStatusColor('RUNNING')).toEqual('info');
		expect(getStatusColor('SCHEDULED')).toEqual('warning');
		expect(getStatusColor('TERMINATED')).toEqual('danger');
	});
});

describe('getMetricName', () => {
	it('should return status name', () => {
		expect(getStatusName('COMPLETED')).toEqual('COMPLETE');
		expect(getStatusName('DRAFT')).toEqual('DRAFT');
		expect(getStatusName('FINISHED_NO_WINNER')).toEqual('NO WINNER');
		expect(getStatusName('FINISHED_WINNER')).toEqual('WINNER DECLARED');
		expect(getStatusName('RUNNING')).toEqual('RUNNING');
		expect(getStatusName('SCHEDULED')).toEqual('SCHEDULED');
		expect(getStatusName('TERMINATED')).toEqual('TERMINATED');
	});
});

describe('getMetricName', () => {
	it('should return metric name', () => {
		expect(getMetricName('BOUNCE_RATE')).toEqual('Bounce Rate');
		expect(getMetricName('CLICK_RATE')).toEqual('Click-Through Rate');
		expect(getMetricName('MAX_SCROLL_DEPTH')).toEqual('Max Scroll Depth');
		expect(getMetricName('TIME_ON_PAGE')).toEqual('View Duration');
	});
});

describe('getMetricUnit', () => {
	it('should return metric name', () => {
		expect(getMetricUnit('BOUNCE_RATE')).toEqual('%');
		expect(getMetricUnit('CLICK_RATE')).toEqual('%');
		expect(getMetricUnit('MAX_SCROLL_DEPTH')).toEqual('%');
		expect(getMetricUnit('TIME_ON_PAGE')).toEqual('s');
	});
});

describe('formatYAxis', () => {
	it('should return formatted Y axis for BOUNCE_RATE metric', () => {
		expect(formatYAxis(getMetricUnit('BOUNCE_RATE'))(100)).toEqual('100%');
	});

	it('should return formatted Y axis for CLICK_RATE metric', () => {
		expect(formatYAxis(getMetricUnit('CLICK_RATE'))(100)).toEqual('100%');
	});

	it('should return formatted Y axis for MAX_SCROLL_DEPTH metric', () => {
		expect(formatYAxis(getMetricUnit('MAX_SCROLL_DEPTH'))(100)).toEqual(
			'100%'
		);
	});

	it('should return formatted Y axis for TIME_ON_PAGE metric', () => {
		expect(formatYAxis(getMetricUnit('TIME_ON_PAGE'))(100)).toEqual('100s');
	});
});

describe('getFormattedMedian', () => {
	it('should return formatted median using BOUNCE_RATE metric', () => {
		expect(getFormattedMedian(50.4321, 'BOUNCE_RATE')).toEqual('50.43');
	});

	it('should return formatted median using CLICK_RATE metric', () => {
		expect(getFormattedMedian(50.4321, 'CLICK_RATE')).toEqual('50.432');
	});

	it('should return formatted median using MAX_SCROLL_DEPTH metric', () => {
		expect(getFormattedMedian(50.4321, 'MAX_SCROLL_DEPTH')).toEqual(
			'50.43'
		);
	});

	it('should return formatted median using TIME_ON_PAGE metric', () => {
		expect(getFormattedMedian(50.4321, 'TIME_ON_PAGE')).toEqual('50.43');
	});
});

describe('getFormattedMedianLabel', () => {
	it('should return formatted median using BOUNCE_RATE metric', () => {
		expect(getFormattedMedianLabel('BOUNCE_RATE')).toEqual(
			'Bounce Rate Median'
		);
	});

	it('should return formatted median using CLICK_RATE metric', () => {
		expect(getFormattedMedianLabel('CLICK_RATE')).toEqual(
			'Median Click-Through Rate'
		);
	});

	it('should return formatted median using MAX_SCROLL_DEPTH metric', () => {
		expect(getFormattedMedianLabel('MAX_SCROLL_DEPTH')).toEqual(
			'Max Scroll Depth Median'
		);
	});

	it('should return formatted median using TIME_ON_PAGE metric', () => {
		expect(getFormattedMedianLabel('TIME_ON_PAGE')).toEqual(
			'View Duration Median'
		);
	});
});

describe('getFormattedProbabilityToWin', () => {
	it('should return formatted probability to win', () => {
		expect(getFormattedProbabilityToWin(50.4321)).toEqual('50.4%');
	});

	it('should return formatted probability to win when value is less than 0.1', () => {
		expect(getFormattedProbabilityToWin(0.05)).toEqual('< 0.1%');
	});

	it('should return formatted probability to win when value is greater than 99.9', () => {
		expect(getFormattedProbabilityToWin(100)).toEqual('> 99.9%');
	});
});

describe('getVariantLabels', () => {
	it('should return a label in especific cases', () => {
		expect(
			getVariantLabels({
				bestVariant: mockBestVariant,
				dxpVariantId: 'DEFAULT',
				status: 'RUNNING',
			})
		).toEqual([{status: 'success', value: 'Current Best'}]);

		expect(
			getVariantLabels({
				bestVariant: mockBestVariant,
				dxpVariantId: 'DEFAULT',
				status: 'FINISHED_WINNER',
				winnerDXPVariantId: 'DEFAULT',
			})
		).toEqual([{status: 'success', value: 'Winner'}]);

		expect(
			getVariantLabels({
				bestVariant: mockBestVariant,
				dxpVariantId: 'DEFAULT',
				publishedDXPVariantId: 'DEFAULT',
				status: 'TERMINATED',
			})
		).toEqual([{status: 'info', value: 'Published'}]);

		expect(
			getVariantLabels({
				bestVariant: mockBestVariant,
				dxpVariantId: 'DEFAULT',
				publishedDXPVariantId: 'DEFAULT',
				status: 'FINISHED_WINNER',
				winnerDXPVariantId: 'DEFAULT',
			})
		).toEqual([
			{status: 'success', value: 'Winner'},
			{status: 'info', value: 'Published'},
		]);
	});
	it('should return an empty array', () => {
		expect(
			getVariantLabels({
				dxpVariantId: 'DEFAULT',
				status: 'RUNNING',
			})
		).toEqual([]);

		expect(
			getVariantLabels({
				bestVariant: mockBestVariant,
				dxpVariantId: 'DEFAULT',
				publishedDXPVariantId: undefined,
				status: 'FINISHED_NO_WINNER',
				winnerDXPVariantId: undefined,
			})
		).toEqual([]);

		expect(
			getVariantLabels({
				bestVariant: mockBestVariant,
				dxpVariantId: 'DEFAULT',
				publishedDXPVariantId: undefined,
				status: 'TERMINATED',
				winnerDXPVariantId: undefined,
			})
		).toEqual([]);
	});
});

describe('getTicks', () => {
	it('should not return intervals with max value 40', () => {
		expect(getTicks(40)).toStrictEqual([1, 6, 11, 16, 21, 26, 31, 36]);
	});

	it('should not return intervals with max value 20', () => {
		expect(getTicks(20)).toStrictEqual([1, 4, 7, 10, 13, 16, 19]);
	});

	it('should not return intervals with max value 21', () => {
		expect(getTicks(21)).toStrictEqual([1, 4, 7, 10, 13, 16, 19]);
	});

	it('should not return intervals with max value 30', () => {
		expect(getTicks(30)).toStrictEqual([1, 5, 9, 13, 17, 21, 25, 29]);
	});

	it('should not return intervals with max value 34', () => {
		expect(getTicks(34)).toStrictEqual([1, 5, 9, 13, 17, 21, 25, 29, 33]);
	});

	it('should not return intervals with max value 12', () => {
		expect(getTicks(12)).toStrictEqual([1, 3, 5, 7, 9, 11]);
	});

	it('should not return intervals with max value 16', () => {
		expect(getTicks(16)).toStrictEqual([1, 3, 5, 7, 9, 11, 13, 15]);
	});

	it('should return intervals with max value 15', () => {
		expect(getTicks(15)).toStrictEqual([1, 3, 5, 7, 9, 11, 13, 15]);
	});

	it('should not return intervals with max value 12', () => {
		expect(getTicks(12)).toStrictEqual([1, 3, 5, 7, 9, 11]);
	});
});

describe('toThousandsABTesting', () => {
	it('should return the number truncate', () => {
		expect(toThousandsABTesting(0.1)).toEqual('0.1');
		expect(toThousandsABTesting(1.4)).toEqual('1.4');
		expect(toThousandsABTesting(1.5)).toEqual('1.5');
		expect(toThousandsABTesting(2.45)).toEqual('2.4');
		expect(toThousandsABTesting(2.453)).toEqual('2.4');
		expect(toThousandsABTesting(2.456)).toEqual('2.4');
		expect(toThousandsABTesting(10.456)).toEqual('10.4');
		expect(toThousandsABTesting(150.5)).toEqual('150.5');
		expect(toThousandsABTesting(150.566)).toEqual('150.5');
		expect(toThousandsABTesting(1100)).toEqual('1.1K');
		expect(toThousandsABTesting(1520)).toEqual('1.5K');
		expect(toThousandsABTesting(2432)).toEqual('2.4K');
		expect(toThousandsABTesting(51444)).toEqual('51.4K');
		expect(toThousandsABTesting(255000.0)).toEqual('255K');
		expect(toThousandsABTesting(4500000)).toEqual('4.5M');
		expect(toThousandsABTesting(4500000000)).toEqual('4.5B');
		expect(toThousandsABTesting(4560000000)).toEqual('4.5B');
		expect(toThousandsABTesting(4567000000)).toEqual('4.5B');
		expect(toThousandsABTesting(1500000000000)).toEqual('1.5T');
	});

	it('does not understate the Japanese 万/億 tiers, whose mantissa can be a single digit', () => {
		setLocale('ja-JP');

		expect(toThousandsABTesting(15000)).toEqual('1.5万');
		expect(toThousandsABTesting(199999999)).toEqual('1.9億');

		setLocale(DEFAULT_LOCALE);
	});
});
