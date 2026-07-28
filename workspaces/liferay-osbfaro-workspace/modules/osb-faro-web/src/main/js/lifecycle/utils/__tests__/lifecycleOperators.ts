import {
	OperatorType,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';

describe('resolveOperatorType', () => {
	it('resolves the data category sent by the catalog', () => {
		expect(resolveOperatorType('Text', 'STRING')).toBe(OperatorType.Text);
		expect(resolveOperatorType('Number', 'NUMERIC')).toBe(
			OperatorType.Number
		);
	});

	it('resolves a data category whose casing differs from the catalog', () => {
		expect(resolveOperatorType('TEXT', 'STRING')).toBe(OperatorType.Text);
		expect(resolveOperatorType('number', 'NUMERIC')).toBe(
			OperatorType.Number
		);
	});

	it('resolves a duration regardless of its casing', () => {
		expect(resolveOperatorType('Number', 'duration')).toBe(
			OperatorType.Duration
		);
	});

	it('resolves nothing when the data category is missing', () => {
		expect(resolveOperatorType(null, 'STRING')).toBeNull();
	});

	it('resolves nothing for a data category it does not know', () => {
		expect(resolveOperatorType('Geolocation', 'STRING')).toBeNull();
	});
});
