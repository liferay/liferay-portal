import {
	formatStringToLowercase,
	getAlignPosition,
	getInitials,
	getPercentage,
	getRangeSelectorsFromQuery,
	getSafeDecodedURIComponent,
	getSafeDisplayValue,
	getSafeRangeSelectors,
	getSafeTouchpoint,
	isBlank,
	isEllipisActive,
	normalizeRangeSelectors,
	truncateText,
} from '../util';

describe('util', () => {
	describe('getSafeDecodedURIComponent', () => {
		it('should decode a URI component', () => {
			expect(getSafeDecodedURIComponent('test%20test')).toEqual(
				'test test'
			);
		});

		it('should return the original string if decoding fails', () => {
			expect(getSafeDecodedURIComponent('%E0%A4%A')).toEqual('%E0%A4%A');
		});

		it('should return an empty string if value is undefined', () => {
			expect(getSafeDecodedURIComponent(undefined)).toEqual('');
		});

		it('should return an empty string if value is null', () => {
			expect(getSafeDecodedURIComponent(null)).toEqual('');
		});

		it('should return an empty string if value is not a string', () => {
			expect(getSafeDecodedURIComponent(123)).toEqual('');
		});
	});

	describe('formatStringToLowercase', () => {
		it('should format a string to lowercase', () => {
			const text = '   THIS IS A NOT LOWERCASE TEXT   ';
			const lowercaseText = formatStringToLowercase(text);

			expect(lowercaseText).toEqual('this is a not lowercase text');
		});
	});

	describe('getAlignPosition', () => {
		it('should return an align position top when it dont have a suggested position', () => {
			const source = document.createElement('div');
			const target = document.createElement('div');

			expect(getAlignPosition(source, target)).toEqual('top');
		});

		it('should return an align position bottom when it have a suggested position', () => {
			const source = document.createElement('div');
			const target = document.createElement('div');

			expect(getAlignPosition(source, target, 'bottom')).toEqual(
				'bottom'
			);
		});
	});

	describe('getInitials', () => {
		it('should return uppercased initials for a full name', () => {
			expect(getInitials('Adriano Interaminense')).toEqual('AI');
		});

		it('should cap the result at three initials', () => {
			expect(getInitials('Foo Bar Baz Qux')).toEqual('FBB');
		});

		it('should return an empty string when name is undefined', () => {
			expect(getInitials(undefined)).toEqual('');
		});

		it('should return an empty string when name is null', () => {
			expect(getInitials(null)).toEqual('');
		});

		it('should return an empty string when no argument is provided', () => {
			expect(getInitials()).toEqual('');
		});
	});

	describe('getPercentage', () => {
		it('should convert number to percent passing current number and total number', () => {
			const number1 = 50;
			const number2 = 1000;
			const percent = getPercentage(number1, number2);

			expect(percent).toEqual(5);
		});

		it('should return number 0 if number is invalid, passing current number and total number', () => {
			const number1 = 0;
			const number2 = 0;
			const percent = getPercentage(number1, number2);

			expect(percent).toEqual(0);
		});
	});

	describe('getSafeDisplayValue', () => {
		it.each`
			value        | expected
			${0}         | ${0}
			${123}       | ${123}
			${undefined} | ${'-'}
			${null}      | ${'-'}
			${''}        | ${'-'}
			${'test'}    | ${'test'}
		`(
			'should return $expected if the value is $value',
			({expected, value}) => {
				expect(getSafeDisplayValue(value, '-')).toBe(expected);
			}
		);

		it('should return the no data value by default if the value is blank', () => {
			expect(getSafeDisplayValue(null)).toBe('–');
		});
	});

	describe('getSafeTouchpoint', () => {
		it.each`
			value                                                        | expected
			${'http://localhost:7400/日本語ページ'}                      | ${'http://localhost:7400/%E6%97%A5%E6%9C%AC%E8%AA%9E%E3%83%9A%E3%83%BC%E3%82%B8'}
			${'http://liferay.com/موقعي العربي الرائع'}                  | ${'http://liferay.com/%D9%85%D9%88%D9%82%D8%B9%D9%8A%20%D8%A7%D9%84%D8%B9%D8%B1%D8%A8%D9%8A%20%D8%A7%D9%84%D8%B1%D8%A7%D8%A6%D8%B9'}
			${'http://liferay.com/home'}                                 | ${'http://liferay.com/home'}
			${'http://liferay.com/?js_fast_load=0'}                      | ${'http://liferay.com/?js_fast_load=0'}
			${'http://liferay.com/home?js_fast_load=0'}                  | ${'http://liferay.com/home?js_fast_load=0'}
			${'http://liferay.com/my home page?js_fast_load=0'}          | ${'http://liferay.com/my%20home%20page?js_fast_load=0'}
			${'http://liferay.com/home?js_fast_load=0#section'}          | ${'http://liferay.com/home?js_fast_load=0#section'}
			${'http://liferay.com/home?日本語ページ=0#section'}          | ${'http://liferay.com/home?%E6%97%A5%E6%9C%AC%E8%AA%9E%E3%83%9A%E3%83%BC%E3%82%B8=0#section'}
			${'http://liferay.com/home?js_fast_load=0&test=123#section'} | ${'http://liferay.com/home?js_fast_load=0&test=123#section'}
			${'http://liferay.com'}                                      | ${'http://liferay.com'}
			${'it-is-a-custom-touchpoint'}                               | ${'it-is-a-custom-touchpoint'}
			${'Any'}                                                     | ${null}
			${''}                                                        | ${null}
			${undefined}                                                 | ${null}
		`(
			'should return $expected if the value is $value',
			({expected, value}) => {
				expect(getSafeTouchpoint(value)).toBe(expected);
			}
		);
	});

	describe('isBlank', () => {
		it.each`
			value        | expected
			${0}         | ${false}
			${123}       | ${false}
			${undefined} | ${true}
			${null}      | ${true}
			${''}        | ${true}
			${'test'}    | ${false}
		`(
			'should return $expected if the value is $value',
			({expected, value}) => {
				expect(isBlank(value)).toBe(expected);
			}
		);
	});

	describe('isEllipisActive', () => {
		it('should return true if is an ellipsis', () => {
			const event = {
				target: {
					offsetWidth: 100,
					scrollWidth: 200,
				},
			};

			expect(isEllipisActive(event)).toBeTruthy();
		});
	});

	describe('truncateText', () => {
		it('should truncate the text', () => {
			const text = 'this is a text that should be truncate';
			const truncatedText = truncateText(text, 25);

			expect(truncatedText).toEqual('this is a text that sh...');
		});

		it('should truncate the text by adding a dot at the end of the text', () => {
			const text = 'this is a text that should be truncate';
			const truncatedText = truncateText(text, 25, '.');

			expect(truncatedText).toEqual('this is a text that shou.');
		});

		it('should truncate the text when it reaches 100 letters', () => {
			const text =
				'this is a text that should be truncate, this is a text that should be truncate, this is a text that should be truncate';
			const truncatedText = truncateText(text);

			expect(truncatedText).toEqual(
				'this is a text that should be truncate, this is a text that should be truncate, this is a text th...'
			);
		});

		it('should not truncate text', () => {
			const text = 'this is a not truncate text';
			const truncatedText = truncateText(text, 30);

			expect(truncatedText).toEqual('this is a not truncate text');
		});
	});

	describe('getRangeSelectorsFromQuery', () => {
		it.each`
			rangeEnd        | rangeKey    | rangeStart      | results
			${''}           | ${'30'}     | ${''}           | ${{rangeEnd: '', rangeKey: '30', rangeStart: ''}}
			${'null'}       | ${'90'}     | ${'null'}       | ${{rangeEnd: null, rangeKey: '90', rangeStart: null}}
			${'2020-04-04'} | ${'CUSTOM'} | ${'2020-04-01'} | ${{rangeEnd: '2020-04-04', rangeKey: 'CUSTOM', rangeStart: '2020-04-01'}}
		`(
			'should convert $rangeEnd, $rangeKey, & $rangeStart to $results',
			({rangeEnd, rangeKey, rangeStart, results}) => {
				expect(
					getRangeSelectorsFromQuery({rangeEnd, rangeKey, rangeStart})
				).toMatchObject(results);
			}
		);
	});

	describe('getSafeRangeSelectors', () => {
		it.each`
			rangeEnd        | rangeKey    | rangeStart      | results
			${''}           | ${'30'}     | ${''}           | ${{rangeEnd: null, rangeKey: 30, rangeStart: null}}
			${null}         | ${'90'}     | ${null}         | ${{rangeEnd: null, rangeKey: 90, rangeStart: null}}
			${'2020-04-04'} | ${'CUSTOM'} | ${'2020-04-01'} | ${{rangeEnd: '2020-04-04', rangeKey: null, rangeStart: '2020-04-01'}}
		`(
			'should convert $rangeEnd, $rangeKey, & $rangeStart to $results',
			({rangeEnd, rangeKey, rangeStart, results}) => {
				expect(
					getSafeRangeSelectors({rangeEnd, rangeKey, rangeStart})
				).toMatchObject(results);
			}
		);
	});

	describe('normalizeRangeSelectors', () => {
		it.each`
			rangeEnd        | rangeKey | rangeStart      | results
			${null}         | ${30}    | ${null}         | ${{rangeEnd: '', rangeKey: '30', rangeStart: ''}}
			${'2020-04-04'} | ${null}  | ${'2020-04-01'} | ${{rangeEnd: '2020-04-04', rangeKey: 'CUSTOM', rangeStart: '2020-04-01'}}
		`(
			'should convert $rangeEnd, $rangeKey, & $rangeStart to $results',
			({rangeEnd, rangeKey, rangeStart, results}) => {
				expect(
					normalizeRangeSelectors({rangeEnd, rangeKey, rangeStart})
				).toMatchObject(results);
			}
		);
	});
});
