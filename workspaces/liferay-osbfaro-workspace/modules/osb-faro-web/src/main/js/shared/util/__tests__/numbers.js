import {
	formatPercent,
	formatPercentFromRatio,
	getFinitePercent,
	toCurrency,
	toDuration,
	toFixedPoint,
	toInt,
	toLocale,
	toRounded,
	toThousands,
	toThousandsBase,
	undoThousands,
} from '../numbers';

describe('toLocale', () => {
	it('should be return the number to locale', () => {
		expect(toLocale(0.3)).toEqual('0.3');
		expect(toLocale(0.123456)).toEqual('0.123456');
		expect(toLocale(123456.123456789)).toEqual('123,456.123457');
	});
});

describe('toThousands', () => {
	it('should truncate rather than round', () => {
		expect(toThousands(0.1)).toEqual('0.1');
		expect(toThousands(1.4)).toEqual('1.4');
		expect(toThousands(1.5)).toEqual('1.5');
		expect(toThousands(2.45)).toEqual('2.4');
		expect(toThousands(2.453)).toEqual('2.4');
		expect(toThousands(2.456)).toEqual('2.4');
		expect(toThousands(10.456)).toEqual('10.4');
		expect(toThousands(150.5)).toEqual('150.5');
		expect(toThousands(150.566)).toEqual('150.5');
		expect(toThousands(1100)).toEqual('1.1K');
		expect(toThousands(1520)).toEqual('1.5K');
		expect(toThousands(2432)).toEqual('2.4K');
		expect(toThousands(51444)).toEqual('51.4K');
		expect(toThousands(255000.0)).toEqual('255K');
		expect(toThousands(4500000)).toEqual('4.5M');
		expect(toThousands(4500000000)).toEqual('4.5B');
		expect(toThousands(4560000000)).toEqual('4.5B');
		expect(toThousands(4567000000)).toEqual('4.5B');
		expect(toThousands(1500000000000)).toEqual('1.5T');
	});

	it('should return the number formatted', () => {
		expect(toThousands(0)).toEqual('0');
		expect(toThousands(1)).toEqual('1');
		expect(toThousands(10)).toEqual('10');
		expect(toThousands(100)).toEqual('100');
		expect(toThousands(1000)).toEqual('1K');
		expect(toThousands(10000)).toEqual('10K');
		expect(toThousands(100000)).toEqual('100K');
		expect(toThousands(1000000)).toEqual('1M');
		expect(toThousands(10000000)).toEqual('10M');
		expect(toThousands(100000000)).toEqual('100M');
		expect(toThousands(1000000000)).toEqual('1B');
		expect(toThousands(10000000000)).toEqual('10B');
		expect(toThousands(100000000000)).toEqual('100B');
		expect(toThousands(1000000000000)).toEqual('1T');
	});

	it('should return an empty string if parameter it is not a number', () => {
		expect(toThousands('test')).toEqual('');
		expect(toThousands([])).toEqual('');
		expect(toThousands({})).toEqual('');
	});
});

describe('toThousandsBase', () => {
	it('should truncate the mantissa rather than round it', () => {
		const setFactor = (factor) => 2453 * factor;

		expect(toThousandsBase(2453, setFactor, 'en-US')).toEqual('2.4K');
	});

	it('should let the caller customize the mantissa, e.g. for toThousandsABTesting', () => {
		const setFactor = (factor) => Math.trunc(2453 * factor);

		expect(toThousandsBase(2453, setFactor, 'en-US')).toEqual('2K');
	});
});

describe('toFixedPoint', () => {
	it('should return the same number if the number is less than 999', () => {
		expect(toFixedPoint(10)).toEqual('10');
		expect(toFixedPoint(100)).toEqual('100');
		expect(toFixedPoint(555)).toEqual('555');
		expect(toFixedPoint(999)).toEqual('999');
	});

	it('should return the number formatted', () => {
		expect(toFixedPoint(1000)).toEqual('1,000');
		expect(toFixedPoint(10000)).toEqual('10,000');
		expect(toFixedPoint(100000)).toEqual('100,000');
		expect(toFixedPoint(1000000)).toEqual('1,000,000');
		expect(toFixedPoint(10000000)).toEqual('10,000,000');
		expect(toFixedPoint(100000000)).toEqual('100,000,000');
		expect(toFixedPoint(1000000000)).toEqual('1,000,000,000');
		expect(toFixedPoint(10000000000)).toEqual('10,000,000,000');
		expect(toFixedPoint(100000000000)).toEqual('100,000,000,000');
	});
});

describe('toRounded', () => {
	it('should return the rounded without decimal', () => {
		expect(toRounded(20.567, 0)).toEqual('21');
		expect(toRounded(1.123, 0)).toEqual('1');
		expect(toRounded(1.543, 0)).toEqual('2');
	});

	it('should be return the rounded single precision', () => {
		expect(toRounded(20.5678)).toEqual('20.6');
		expect(toRounded(1.123)).toEqual('1.1');
		expect(toRounded(1.543)).toEqual('1.5');
	});

	it('should return the rounded with two decimals', () => {
		expect(toRounded(20.5678, 2)).toEqual('20.57');
		expect(toRounded(1.123, 2)).toEqual('1.12');
		expect(toRounded(1.543, 2)).toEqual('1.54');
		expect(toRounded(0.300001, 2)).toEqual('0.30');
		expect(toRounded(30.0, 2)).toEqual('30');
	});
});

describe('toInt', () => {
	it('should be return the int', () => {
		const number = toInt('1000');

		expect(number).toEqual(1000);
	});
});

describe('toDuration', () => {
	it('should return the time formatted', () => {
		const number = toDuration(1000, undefined, 'seconds');

		expect(number).toEqual('16m 40s');
	});

	it('should return the time formatted displaying milliseconds', () => {
		const number = toDuration(12101989, 'DD[days] hh[h] mm[m] ss[s] S[ms]');

		expect(number).toEqual('03h 21m 41s 989ms');
	});

	it('should return the time in the default format', () => {
		const number = toDuration(99999999);

		expect(number).toEqual('01d 03h 46m 40s');
	});
});

describe('getFinitePercent', () => {
	it.each`
		curVal | totalVal | expected
		${0}   | ${0}     | ${null}
		${0}   | ${null}  | ${null}
		${0}   | ${NaN}   | ${null}
		${10}  | ${100}   | ${10}
		${50}  | ${100}   | ${50}
		${100} | ${100}   | ${100}
	`(
		'should calculate the percent of $curVal to $totalVal return $expected',
		({curVal, expected, totalVal}) => {
			expect(getFinitePercent(curVal, totalVal)).toBe(expected);
		}
	);
});

describe('formatPercentFromRatio', () => {
	it('should format a 0-1 ratio as a percentage, stripping trailing zeros', () => {
		expect(formatPercentFromRatio(0.1)).toEqual('10%');
		expect(formatPercentFromRatio(0.073)).toEqual('7.3%');
		expect(formatPercentFromRatio(1)).toEqual('100%');
	});
});

describe('formatPercent', () => {
	it('should format a 0-100 value as a percentage, stripping trailing zeros', () => {
		expect(formatPercent(10)).toEqual('10%');
		expect(formatPercent(7.3)).toEqual('7.3%');
		expect(formatPercent(100)).toEqual('100%');
	});
});

describe('toCurrency', () => {
	it('should format a value with the given currency code', () => {
		expect(toCurrency(1234.5, 'USD', 'en-US')).toEqual('$1,234.50');
	});

	it('should format a plain locale-aware number when no currency code is given', () => {
		expect(toCurrency(1234.5, undefined, 'en-US')).toEqual('1,234.5');
	});
});

describe('undoThousands', () => {
	it('should return a number aproximation for a formatted number', () => {
		expect(undoThousands('145')).toEqual(145);
		expect(undoThousands('23.5B')).toEqual(23500000000);
		expect(undoThousands('23.5K')).toEqual(23500);
		expect(undoThousands('23.5M')).toEqual(23500000);
	});

	it('should return the sum two numbers using method toThousand', () => {
		const formattedA = '1.5K';
		const formattedB = '2.5K';
		const formattedC = '140';

		const numberA = undoThousands(formattedA);
		const numberB = undoThousands(formattedB);
		const numberC = undoThousands(formattedC);

		expect(toThousands(numberA + numberB)).toEqual('4K');
		expect(toThousands(numberA + numberC)).toEqual('1.6K');
	});

	it('should return zero when no formatted value is specified', () => {
		expect(undoThousands()).toEqual(0);
	});

	it('should return zero when formatted value no matches', () => {
		expect(undoThousands('E$')).toEqual(0);
	});

	it('should return the number without multiplier when formatted doesnt have multipliter declared', () => {
		expect(undoThousands('50X')).toEqual(50);
		expect(undoThousands('100Y')).toEqual(100);
		expect(undoThousands('150Z')).toEqual(150);
	});
});
