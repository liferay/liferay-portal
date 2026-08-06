import {accountsListColumns, metricsListColumns} from '../table-columns';

describe('accountsListColumns', () => {
	it('activitiesCount should format a number with locale-aware grouping', () => {
		expect(accountsListColumns.activitiesCount.dataFormatter(1234567)).toBe(
			'1,234,567'
		);
	});

	it('activitiesCount should format a numeric string with locale-aware grouping', () => {
		expect(
			accountsListColumns.activitiesCount.dataFormatter('1234567')
		).toBe('1,234,567');
	});
});

describe('metricsListColumns', () => {
	it('ratingsMetric should format the rating out of 10, stripping trailing zeros', () => {
		expect(metricsListColumns.ratingsMetric.dataFormatter(0.8)).toBe(
			'8/10'
		);
		expect(metricsListColumns.ratingsMetric.dataFormatter(0.753)).toBe(
			'7.53/10'
		);
	});
});
