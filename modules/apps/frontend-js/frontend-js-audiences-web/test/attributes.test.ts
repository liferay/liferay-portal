/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Cache} from '../src/main/resources/META-INF/resources/main/cache';
import {getBrowserName} from '../src/main/resources/META-INF/resources/main/detection/attributes/browser_name';
import {getBrowserVersion} from '../src/main/resources/META-INF/resources/main/detection/attributes/browser_version';
import {getCookies} from '../src/main/resources/META-INF/resources/main/detection/attributes/cookies';
import {getCustom} from '../src/main/resources/META-INF/resources/main/detection/attributes/custom';
import {getDeviceType} from '../src/main/resources/META-INF/resources/main/detection/attributes/device_type';
import {getHostname} from '../src/main/resources/META-INF/resources/main/detection/attributes/hostname';
import {getLanguage} from '../src/main/resources/META-INF/resources/main/detection/attributes/language';
import {getLocalDate} from '../src/main/resources/META-INF/resources/main/detection/attributes/local_date';
import {getLocalHour} from '../src/main/resources/META-INF/resources/main/detection/attributes/local_hour';
import {getPathname} from '../src/main/resources/META-INF/resources/main/detection/attributes/pathname';
import {getReferrer} from '../src/main/resources/META-INF/resources/main/detection/attributes/referrer';
import {getRequestParameters} from '../src/main/resources/META-INF/resources/main/detection/attributes/request_parameters';
import {getSegments} from '../src/main/resources/META-INF/resources/main/detection/attributes/segments';
import {getTimezone} from '../src/main/resources/META-INF/resources/main/detection/attributes/timezone';
import {getUrl} from '../src/main/resources/META-INF/resources/main/detection/attributes/url';
import {getUserAgent} from '../src/main/resources/META-INF/resources/main/detection/attributes/user_agent';

describe('attributes', () => {
	afterEach(() => {
		jest.useRealTimers();

		jest.dontMock('https://example.com/custom-attribute.js');

		jest.restoreAllMocks();

		delete (document as any).cookie;
		delete (document as any).referrer;

		delete (global as any).Analytics;

		delete (navigator as any).userAgent;

		window.history.replaceState({}, '', '/');
	});

	beforeEach(() => {
		jest.useFakeTimers();
		jest.setSystemTime(new Date('2009-04-23T10:30:00'));

		jest.doMock(
			'https://example.com/custom-attribute.js',
			() => ({
				__esModule: true,
				default: () => 'default-value',
				getCountry: () => 'US',
			}),
			{virtual: true}
		);

		jest.spyOn(
			Intl.DateTimeFormat.prototype,
			'resolvedOptions'
		).mockReturnValue({timeZone: 'America/New_York'} as any);

		Object.defineProperty(document, 'cookie', {
			configurable: true,
			value: 'REMEMBER_ME=true; JSESSIONID=ba8e4d1c',
		});
		Object.defineProperty(document, 'referrer', {
			configurable: true,
			value: 'https://www.wikipedia.org/',
		});

		Object.defineProperty(global, 'Analytics', {
			configurable: true,
			value: {
				segment: {
					getBatchSegmentExternalReferenceCodes: () =>
						Promise.resolve(['SEGMENT_BATCH']),
					getRealTimeSegmentExternalReferenceCodes: () =>
						Promise.resolve(['SEGMENT_REAL_TIME']),
				},
			},
		});

		Object.defineProperty(navigator, 'userAgent', {
			configurable: true,
			value: 'Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0',
		});

		window.history.replaceState(
			{},
			'',
			'/home?utm_source=newsletter&language=es'
		);
	});

	describe('attribute browser_name', () => {
		it('works and returns a string', async () => {
			const value = getBrowserName(
				new Cache(new AbortController().signal)
			);

			expect(typeof value).toBe('string');
			expect(value).toBe('Firefox');
		});
	});

	describe('attribute browser_version', () => {
		it('works and returns a string', async () => {
			const value = getBrowserVersion(
				new Cache(new AbortController().signal)
			);

			expect(typeof value).toBe('string');
			expect(value).toBe('151.0');
		});
	});

	describe('attribute cookies', () => {
		it('works and returns a Set<string>', async () => {
			const value = getCookies();

			expect(value).toBeInstanceOf(Set);
			expect(value).toEqual(
				new Set(['REMEMBER_ME=true', 'JSESSIONID=ba8e4d1c'])
			);
		});

		it('decodes percent-encoded cookies', async () => {
			Object.defineProperty(document, 'cookie', {
				configurable: true,
				value: 'greeting=hello%20world; city=S%C3%A3o%20Paulo',
			});

			expect(getCookies()).toEqual(
				new Set(['greeting=hello world', 'city=São Paulo'])
			);
		});

		it('passes cookies that are not encoded unchanged', async () => {
			Object.defineProperty(document, 'cookie', {
				configurable: true,
				value: 'REMEMBER_ME=true; JSESSIONID=ba8e4d1c',
			});

			expect(getCookies()).toEqual(
				new Set(['REMEMBER_ME=true', 'JSESSIONID=ba8e4d1c'])
			);
		});

		it('passes cookies that are badly encoded unchanged', async () => {
			Object.defineProperty(document, 'cookie', {
				configurable: true,
				value: 'discount=50%; valid=a%20b',
			});

			expect(getCookies()).toEqual(
				new Set(['discount=50%', 'valid=a b'])
			);
		});
	});

	describe('attribute custom', () => {
		it('works and returns the default export', async () => {
			const value = await getCustom(
				'https://example.com/custom-attribute.js'
			);

			expect(value).toBe('default-value');
		});

		it('works and returns the function named in the fragment', async () => {
			const value = await getCustom(
				'https://example.com/custom-attribute.js#getCountry'
			);

			expect(value).toBe('US');
		});

		it('throws when the named function is missing', async () => {
			await expect(
				getCustom('https://example.com/custom-attribute.js#missing')
			).rejects.toThrow(
				"Module 'https://example.com/custom-attribute.js' does not " +
					"export any function named 'missing'"
			);
		});
	});

	describe('attribute device_type', () => {
		it('works and returns a string', async () => {
			Object.defineProperty(navigator, 'userAgent', {
				configurable: true,
				value:
					'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) ' +
					'AppleWebKit/605.1.15 (KHTML, like Gecko) ' +
					'Version/17.0 Mobile/15E148 Safari/604.1',
			});

			const value = getDeviceType(
				new Cache(new AbortController().signal)
			);

			expect(typeof value).toBe('string');
			expect(value).toBe('mobile');
		});

		it('falls back to desktop when the user agent has no device type', async () => {
			const value = getDeviceType(
				new Cache(new AbortController().signal)
			);

			expect(value).toBe('desktop');
		});
	});

	describe('attribute hostname', () => {
		it('works and returns a string', async () => {
			const value = getHostname();

			expect(typeof value).toBe('string');
			expect(value).toBe('localhost');
		});
	});

	describe('attribute language', () => {
		it('works and returns a string', async () => {
			const value = getLanguage();

			expect(typeof value).toBe('string');
			expect(value).toBe('en-US');
		});
	});

	describe('attribute local_date', () => {
		it('works and returns a string', async () => {
			const value = getLocalDate();

			expect(typeof value).toBe('string');
			expect(value).toBe('2009-04-23');
		});
	});

	describe('attribute local_hour', () => {
		it('works and returns a number', async () => {
			const value = getLocalHour();

			expect(typeof value).toBe('number');
			expect(value).toBe(10);
		});
	});

	describe('attribute pathname', () => {
		it('works and returns a string', async () => {
			const value = getPathname();

			expect(typeof value).toBe('string');
			expect(value).toBe('/home');
		});
	});

	describe('attribute referrer', () => {
		it('works and returns a string', async () => {
			const value = getReferrer();

			expect(typeof value).toBe('string');
			expect(value).toBe('https://www.wikipedia.org/');
		});
	});

	describe('attribute request_parameters', () => {
		it('works and returns a Set<string>', async () => {
			const value = getRequestParameters();

			expect(value).toBeInstanceOf(Set);
			expect(value).toEqual(
				new Set(['utm_source=newsletter', 'language=es'])
			);
		});
	});

	describe('attribute segments', () => {
		it('works and returns a Set<string>', async () => {
			const value = await getSegments(
				new Cache(new AbortController().signal)
			);

			expect(value).toBeInstanceOf(Set);
			expect(value).toEqual(
				new Set(['SEGMENT_BATCH', 'SEGMENT_REAL_TIME'])
			);
		});
	});

	describe('attribute timezone', () => {
		it('works and returns a string', async () => {
			const value = getTimezone();

			expect(typeof value).toBe('string');
			expect(value).toBe('America/New_York');
		});
	});

	describe('attribute url', () => {
		it('works and returns a string', async () => {
			const value = getUrl();

			expect(typeof value).toBe('string');
			expect(value).toBe(
				'http://localhost/home?utm_source=newsletter&language=es'
			);
		});
	});

	describe('attribute user_agent', () => {
		it('works and returns a string', async () => {
			const value = getUserAgent();

			expect(typeof value).toBe('string');
			expect(value).toBe(
				'Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0'
			);
		});
	});
});
