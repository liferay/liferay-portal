import {formatDataValue, isMissingDataValue} from '../data-value';
import {getCustomDateFormat} from '../date';

describe('data-value', () => {
	describe('formatDataValue', () => {
		it.each`
			type          | value     | expected
			${'count'}    | ${1520}   | ${'1.5K'}
			${'count'}    | ${0}      | ${'0'}
			${'currency'} | ${0}      | ${'0'}
			${'duration'} | ${0}      | ${'00:00:00'}
			${'number'}   | ${1234}   | ${'1,234'}
			${'number'}   | ${0}      | ${'0'}
			${'percent'}  | ${7.3}    | ${'7.3%'}
			${'percent'}  | ${0}      | ${'0%'}
			${'ratio'}    | ${0.073}  | ${'7.3%'}
			${'text'}     | ${'Acme'} | ${'Acme'}
			${'text'}     | ${0}      | ${'0'}
		`(
			'should format the $type $value as $expected',
			({expected, type, value}) => {
				expect(formatDataValue(value, type)).toBe(expected);
			}
		);

		it.each`
			type         | value
			${'count'}   | ${null}
			${'date'}    | ${undefined}
			${'number'}  | ${NaN}
			${'percent'} | ${Infinity}
			${'text'}    | ${''}
		`(
			'should return the no data value for the $type $value',
			({type, value}) => {
				expect(formatDataValue(value, type)).toBe('–');
			}
		);

		it('should format a date with the given format, in UTC', () => {
			expect(
				formatDataValue('2026-06-10T01:30:00Z', 'date', {
					dateFormat: getCustomDateFormat(),
				})
			).toBe('Jun 10, 2026');
		});

		it('should round a number to the given precision', () => {
			expect(formatDataValue(1.567, 'number', {precision: 2})).toBe(
				'1.57'
			);
		});

		it('should format a number in the given locale', () => {
			expect(
				formatDataValue(1.5, 'number', {locale: 'de-DE', precision: 1})
			).toBe('1,5');
		});
	});

	describe('isMissingDataValue', () => {
		it('should treat a numeric string as present for numeric types', () => {
			expect(isMissingDataValue('15', 'count')).toBe(false);
		});

		it('should treat a non-numeric string as missing for numeric types', () => {
			expect(isMissingDataValue('abc', 'count')).toBe(true);
		});

		it('should treat the no data value as missing', () => {
			expect(isMissingDataValue('–')).toBe(true);
		});
	});
});
