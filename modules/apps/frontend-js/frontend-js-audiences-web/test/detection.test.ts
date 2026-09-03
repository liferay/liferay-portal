/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as audiences from '../src/main/resources/META-INF/resources/main/implementation';

import type {
	Attribute,
	Audience,
	AudiencesDefinition,
	Conjunction,
	Operator,
	Rule,
} from '../src/main/resources/META-INF/resources/main/index';

const URL = 'https://example.com/audiences.json';

const TIMEOUT = 1000;

async function flushMicrotasks() {
	for (let i = 0; i < 10; i++) {
		await Promise.resolve();
	}
}

function mockAudiencesDefinition(conjunction: Conjunction, rules: Rule[]) {
	return mockAudiencesDefinitionWithAudiences([
		{
			conjunction,
			id: 'the_audience',
			rules,
		},
	]);
}

function mockAudiencesDefinitionWithAttribute(
	attribute: Attribute,
	operator: Operator,
	value: any
) {
	return mockAudiencesDefinition('AND', [
		{
			attribute,
			operator,
			value,
		},
	]);
}

function mockAudiencesDefinitionWithAudiences(audiences: Audience[]) {
	const audiencesDefinition: AudiencesDefinition = {audiences};

	(global as any).fetch = jest.fn(() =>
		Promise.resolve({
			json: () => Promise.resolve(audiencesDefinition),
			ok: true,
			status: 200,
			statusText: 'OK',
		})
	);
}

describe('detection', () => {
	afterEach(() => {
		jest.useRealTimers();

		jest.dontMock('https://example.com/custom.js');

		jest.dontMock('https://example.com/custom-error.js');

		jest.dontMock('https://example.com/custom-slow.js');

		jest.restoreAllMocks();

		for (const item of document.cookie.split(';')) {
			document.cookie = `${item.split('=')[0].trim()}=; path=/; max-age=0`;
		}

		delete (document as any).referrer;

		delete (global as any).Analytics;

		delete (navigator as any).userAgent;

		window.history.replaceState({}, '', '/');

		audiences.clear();
		audiences.setLogEnabled(false);
	});

	beforeEach(() => {
		jest.useFakeTimers();
		jest.setSystemTime(new Date('2009-04-23T10:30:00'));

		jest.doMock(
			'https://example.com/custom.js',
			() => ({
				__esModule: true,
				getCountry: () => 'US',
			}),
			{virtual: true}
		);

		jest.doMock(
			'https://example.com/custom-error.js',
			() => ({
				__esModule: true,
				getCountry: () => {
					throw new Error('The custom attribute is broken');
				},
			}),
			{virtual: true}
		);

		jest.doMock(
			'https://example.com/custom-slow.js',
			() => ({
				__esModule: true,
				getCountry: () => new Promise(() => {}),
			}),
			{virtual: true}
		);

		jest.spyOn(
			Intl.DateTimeFormat.prototype,
			'resolvedOptions'
		).mockReturnValue({timeZone: 'America/New_York'} as any);

		document.cookie = 'REMEMBER_ME=true';
		document.cookie = 'JSESSIONID=ba8e4d1c';

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
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'browser_name',
				'eq',
				'Firefox'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'browser_name',
				'eq',
				'WebKit'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute browser_version', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'browser_version',
				'eq',
				'151.0'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'browser_version',
				'eq',
				'123.0'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute cookies', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'cookies',
				'includes',
				'REMEMBER_ME=true'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'cookies',
				'includes',
				'REMEMBER_YOU=false'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute custom', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'custom:https://example.com/custom.js#getCountry',
				'eq',
				'US'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'custom:https://example.com/custom.js#getCountry',
				'eq',
				'ES'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute hostname', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute('hostname', 'eq', 'localhost');

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'hostname',
				'eq',
				'google.com'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute language', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute('language', 'eq', 'en-US');

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute('language', 'eq', 'es-ES');

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute local_date', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'local_date',
				'eq',
				'2009-04-23'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'local_date',
				'eq',
				'1980-01-01'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute local_hour', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute('local_hour', 'eq', 10);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute('local_hour', 'eq', 0);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute local_hour', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute('pathname', 'eq', '/home');

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute('pathname', 'eq', '/work');

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute referrer', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'referrer',
				'eq',
				'https://www.wikipedia.org/'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'referrer',
				'eq',
				'https://www.wikimedia.org/'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute request_parameters', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'request_parameters',
				'includes',
				'utm_source=newsletter'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'request_parameters',
				'includes',
				'language=en'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute segments', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'segments',
				'includes',
				'SEGMENT_REAL_TIME'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'segments',
				'includes',
				'NON_EXISTENT_SEGMENT'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute timezone', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'timezone',
				'eq',
				'America/New_York'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'timezone',
				'eq',
				'Pacific/Midway'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute url', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'url',
				'eq',
				'http://localhost/home?utm_source=newsletter&language=es'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'url',
				'eq',
				'http://otherhost/web'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	describe('attribute user_agent', () => {
		it('positive test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'user_agent',
				'eq',
				'Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('negative test', async () => {
			mockAudiencesDefinitionWithAttribute(
				'user_agent',
				'eq',
				'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko)'
			);

			await audiences.runDetection(URL);

			expect(audiences.get()).toEqual(new Set());
		});
	});

	it('matches an AND audience only when every rule matches', async () => {
		mockAudiencesDefinition('AND', [
			{attribute: 'hostname', operator: 'eq', value: 'localhost'},
			{attribute: 'language', operator: 'eq', value: 'en-US'},
		]);

		await audiences.runDetection(URL);

		expect(audiences.get()).toEqual(new Set(['the_audience']));

		audiences.clear();

		mockAudiencesDefinition('AND', [
			{attribute: 'hostname', operator: 'eq', value: 'localhost'},
			{attribute: 'browser_name', operator: 'eq', value: 'Chrome'},
		]);

		await audiences.runDetection(URL);

		expect(audiences.get()).toEqual(new Set());
	});

	it('matches an OR audience when any rule matches', async () => {
		mockAudiencesDefinition('OR', [
			{attribute: 'hostname', operator: 'eq', value: 'example.com'},
			{attribute: 'language', operator: 'eq', value: 'en-US'},
		]);

		await audiences.runDetection(URL);

		expect(audiences.get()).toEqual(new Set(['the_audience']));

		audiences.clear();

		mockAudiencesDefinition('OR', [
			{attribute: 'hostname', operator: 'eq', value: 'example.com'},
			{attribute: 'browser_name', operator: 'eq', value: 'Chrome'},
		]);

		await audiences.runDetection(URL);

		expect(audiences.get()).toEqual(new Set());
	});

	it('matches the remaining audiences when one audience throws', async () => {
		audiences.setLogEnabled(true);

		const consoleLog = jest
			.spyOn(console, 'log')
			.mockImplementation(() => {});

		mockAudiencesDefinitionWithAudiences([
			{
				conjunction: 'AND',
				id: 'the_broken_audience',
				rules: [
					{
						attribute:
							'custom:https://example.com/custom-error.js#getCountry',
						operator: 'eq',
						value: 'US',
					},
				],
			},
			{
				conjunction: 'AND',
				id: 'the_audience',
				rules: [
					{attribute: 'hostname', operator: 'eq', value: 'localhost'},
				],
			},
		]);

		await audiences.runDetection(URL);

		expect(audiences.get()).toEqual(new Set(['the_audience']));

		expect(
			consoleLog.mock.calls.find((call) =>
				call[2].includes(
					`Audience 'the_broken_audience' is not matched because its evaluation failed with error`
				)
			)
		).toBeTruthy();
	});

	describe('timeout', () => {
		it('discards the audiences that are still being detected', async () => {
			mockAudiencesDefinitionWithAudiences([
				{
					conjunction: 'AND',
					id: 'the_slow_audience',
					rules: [
						{
							attribute:
								'custom:https://example.com/custom-slow.js#getCountry',
							operator: 'eq',
							value: 'US',
						},
					],
				},
				{
					conjunction: 'AND',
					id: 'the_audience',
					rules: [
						{
							attribute: 'hostname',
							operator: 'eq',
							value: 'localhost',
						},
					],
				},
			]);

			const promise = audiences.runDetection(URL, {
				timeout: TIMEOUT,
			});

			await flushMicrotasks();

			jest.advanceTimersByTime(TIMEOUT);

			await promise;

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});

		it('discards every audience when the definition never arrives', async () => {
			(global as any).fetch = jest.fn(() => new Promise(() => {}));

			const promise = audiences.runDetection(URL, {
				timeout: TIMEOUT,
			});

			jest.advanceTimersByTime(TIMEOUT);

			await promise;

			expect(audiences.get()).toEqual(new Set());
		});

		it('detects the audiences that finish before the timeout', async () => {
			mockAudiencesDefinitionWithAttribute('hostname', 'eq', 'localhost');

			await audiences.runDetection(URL, {timeout: TIMEOUT});

			expect(audiences.get()).toEqual(new Set(['the_audience']));
		});
	});
});
