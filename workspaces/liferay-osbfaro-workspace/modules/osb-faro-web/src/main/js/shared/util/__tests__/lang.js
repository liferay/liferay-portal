import {DataSourceTypes, EntityTypes} from '../constants';
import {DEFAULT_LOCALE, setLocale} from '../locale';
import {
	getDataSourceLangKey,
	getPluralMessage,
	getTypeLangKey,
	sub,
} from '../lang';

describe('sub', () => {
	it('should return an array', () => {
		const res = sub('hello world', [''], false);

		expect(res).toEqual(['hello world']);
	});

	it('should return a string', () => {
		const res = sub('hello world', ['']);

		expect(res).toEqual('hello world');
	});

	it('should return with a subbed value for {0}', () => {
		const res = sub('hello {0}', ['world']);

		expect(res).toEqual('hello world');
	});

	it('should return with multiple subbed values', () => {
		const res = sub('My name is {0} {1}', ['hello', 'world']);

		expect(res).toEqual('My name is hello world');
	});

	it('should return an array with multiple subbed values', () => {
		const res = sub('My name is {0} {1}', ['hello', 'world'], false);

		expect(res).toEqual(['My name is ', 'hello', ' ', 'world']);
	});
});

describe('getTypeLangKey', () => {
	it('should lang key for account', () => {
		expect(getTypeLangKey(EntityTypes.Account)).toBe('Accounts');
	});
});

describe('getDataSourceLangKey', () => {
	it('should return a lang key for a data-source type', () => {
		expect(getDataSourceLangKey(DataSourceTypes.Liferay)).toBe(
			'Liferay DXP'
		);
	});
});

describe('getPluralMessage', () => {
	it.each`
		count        | expected
		${1}         | ${'1 person'}
		${2}         | ${'2 people'}
		${0}         | ${'0 people'}
		${23}        | ${'23 people'}
		${-1}        | ${'-1 people'}
		${undefined} | ${'0 people'}
	`(
		'should return message "$expected" for count $count',
		({count, expected}) => {
			const plural = '{0} people';
			const singular = '{0} person';

			expect(getPluralMessage(singular, plural, count)).toEqual(expected);
		}
	);

	it('should return a message with a subArray', () => {
		const plural = '{0} results found for {1}';
		const singular = '{0} result found for {1}';

		expect(
			getPluralMessage(singular, plural, 12, true, [12, 'test'])
		).toEqual('12 results found for test');

		expect(
			getPluralMessage(singular, plural, 1, true, [1, 'test'])
		).toEqual('1 result found for test');
	});

	it('should format the count in the current locale', () => {
		setLocale('de-DE');

		try {
			expect(getPluralMessage('{0} person', '{0} people', 1234)).toEqual(
				'1.234 people'
			);
		}
		finally {
			setLocale(DEFAULT_LOCALE);
		}
	});
});
