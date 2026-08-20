/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../src/analytics';
import {SegmentCachedData} from '../src/segment';
import {Analytics as AnalyticsType} from '../src/types';
import {
	ANALYTICS_BATCH_SEGMENT_EXTERNAL_REFERENCE_CODES,
	THREE_HOURS_IN_MILLISECONDS,
} from '../src/utils/constants';
import {getItem, setItem} from '../src/utils/storage';
import {DXP_APPLICATION_IDS} from '../src/utils/validators';
import {
	INITIAL_ANALYTICS_CONFIG,
	sendDummyEvents,
	trackDummyEvents,
	wait,
} from './helpers';

const ANALYTICS_IDENTITY = {email: 'foo@bar.com', name: 'Foo Bar'};

const FLUSH_INTERVAL = 100;

const INITIAL_CONFIG = {
	...INITIAL_ANALYTICS_CONFIG,
	endpointUrl: 'https://ac-server.io',
	faroBackendUrl: 'https://ac-backend-server.io',
	flushInterval: FLUSH_INTERVAL,
};

describe('Analytics', () => {
	let Analytics: AnalyticsClient;

	beforeEach(() => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		localStorage.removeItem(AnalyticsType.Queues.Events);
		localStorage.removeItem(AnalyticsType.Keys.UserId);
	});

	afterEach(() => {
		Analytics.reset();
		AnalyticsClient.dispose();

		fetchMock.restore();

		jest.restoreAllMocks();
	});

	it('returns channelId from config by default', () => {
		expect(Analytics._getContext().channelId).toBe('4321');
	});

	it('returns channelId from middleware', () => {
		const middleware = ((request: {context: AnalyticsType.Context}) => {
			request.context.channelId = '5678';

			return request;
		}) as unknown as AnalyticsType.Middleware;

		Analytics.registerMiddleware(middleware);

		expect(Analytics._getContext().channelId).toBe('5678');
	});

	it('is exposed in the global scope', () => {
		expect((global as any).Analytics).toBeInstanceOf(Object);
	});

	it('exposes a "create" instantiation method', () => {
		expect(typeof AnalyticsClient.create).toBe('function');
	});

	it('accepts a configuration object', () => {
		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		expect(Analytics.config).toEqual(INITIAL_CONFIG);
	});

	it('regenerates the stored identity if the identity changed', async () => {
		fetchMock.mock(/identity$/i, () => Promise.resolve(200));

		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		await Analytics.setIdentity(ANALYTICS_IDENTITY);

		const previousIdentityHash = getItem(AnalyticsType.Keys.Identity);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			name: 'John',
		});

		const currentIdentityHash = getItem(AnalyticsType.Keys.Identity);

		expect(currentIdentityHash).not.toEqual(previousIdentityHash);
	});

	it('reports identity changes to the Identity Service', async () => {
		fetchMock.mock('*', () => Promise.resolve(200));

		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		let identityCalled = 0;

		await Analytics.setIdentity(ANALYTICS_IDENTITY);

		await wait(FLUSH_INTERVAL);

		fetchMock.restore();
		fetchMock.mock(/identity$/, () => {
			identityCalled += 1;

			return '';
		});

		await Analytics.setIdentity({
			email: 'test@liferay.com',
			name: 'Test',
		});

		await wait(FLUSH_INTERVAL);

		expect(identityCalled).toBe(1);
	});

	it('sends the given fields to the Identity Service', async () => {
		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		let identityBody: {[key: string]: any} = {};

		fetchMock.restore();
		fetchMock.mock(/identity$/, (_url: string, options: RequestInit) => {
			identityBody = JSON.parse(options.body as string);

			return 200;
		});
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [
				{name: 'firstName', value: 'John'},
				{name: 'lastName', value: 'Doe'},
			],
			name: 'John Doe',
		});

		await wait(FLUSH_INTERVAL);

		expect(identityBody.fields).toEqual([
			{name: 'firstName', value: 'John'},
			{name: 'lastName', value: 'Doe'},
		]);
	});

	it('does not request the Identity Service when only the field order changed', async () => {
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [
				{name: 'firstName', value: 'John'},
				{name: 'lastName', value: 'Doe'},
			],
			name: 'John Doe',
		});

		await wait(FLUSH_INTERVAL);

		let identityCalled = 0;

		fetchMock.restore();
		fetchMock.mock(/identity$/, () => {
			identityCalled += 1;

			return '';
		});

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [
				{name: 'lastName', value: 'Doe'},
				{name: 'firstName', value: 'John'},
			],
			name: 'John Doe',
		});

		await wait(FLUSH_INTERVAL);

		expect(identityCalled).toBe(0);
	});

	it('requests the Identity Service when a field value changed', async () => {
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [
				{name: 'firstName', value: 'John'},
				{name: 'lastName', value: ''},
			],
			name: 'John Doe',
		});

		await wait(FLUSH_INTERVAL);

		let identityCalled = 0;

		fetchMock.restore();
		fetchMock.mock(/identity$/, () => {
			identityCalled += 1;

			return '';
		});

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [
				{name: 'firstName', value: 'John'},
				{name: 'lastName', value: 'Doe'},
			],
			name: 'John Doe',
		});

		await wait(FLUSH_INTERVAL);

		expect(identityCalled).toBe(1);
	});

	it('does not add fields to the payload when none are given', async () => {
		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		let identityBody: {[key: string]: any} = {};

		fetchMock.restore();
		fetchMock.mock(/identity$/, (_url: string, options: RequestInit) => {
			identityBody = JSON.parse(options.body as string);

			return 200;
		});
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));

		await Analytics.setIdentity(ANALYTICS_IDENTITY);

		await wait(FLUSH_INTERVAL);

		expect(identityBody).not.toHaveProperty('fields');
	});

	it('sends the fields as an anonymous identity when the email is omitted', async () => {
		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		let identityBody: {[key: string]: any} = {};

		fetchMock.restore();
		fetchMock.mock(/identity$/, (_url: string, options: RequestInit) => {
			identityBody = JSON.parse(options.body as string);

			return 200;
		});
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));

		await Analytics.setIdentity({
			fields: [{name: 'emailAddress', value: 'john@liferay.com'}],
		});

		await wait(FLUSH_INTERVAL);

		expect(identityBody.emailAddressHashed).toBe('');
		expect(identityBody.fields).toEqual([
			{name: 'emailAddress', value: 'john@liferay.com'},
		]);
	});

	it("does not request the Identity Service when identity hasn't changed", async () => {
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		Analytics.reset();
		AnalyticsClient.dispose();

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		let identityCalled = 0;

		await Analytics.setIdentity(ANALYTICS_IDENTITY);

		fetchMock.restore();
		fetchMock.mock(/identity$/, () => {
			identityCalled += 1;

			return 200;
		});

		await Analytics.setIdentity(ANALYTICS_IDENTITY);

		expect(identityCalled).toBe(0);
	});

	it('preserves the user id whenever the set identity is called after a anonymous navigation', async () => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		Analytics.reset();
		AnalyticsClient.dispose();

		localStorage.removeItem(AnalyticsType.Keys.PrevEmailAddressHash);

		Analytics = AnalyticsClient.create(INITIAL_CONFIG);

		sendDummyEvents(Analytics, 1);

		await wait(FLUSH_INTERVAL * 2);

		// Flush should have happened at least once

		const userId = getItem(AnalyticsType.Keys.UserId);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			name: 'John',
		});

		expect(getItem(AnalyticsType.Keys.UserId)).toEqual(userId);
	});

	it('replace the user id whenever the set identity hash is changed', async () => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			name: 'John',
		});

		const firstUserId = getItem(AnalyticsType.Keys.UserId);

		await Analytics.setIdentity({
			email: 'brian@liferay.com',
			name: 'Brian',
		});

		const secondUserId = getItem(AnalyticsType.Keys.UserId);

		expect(firstUserId).not.toEqual(secondUserId);
	});

	it('does not replace the user id whenever the set identity hash is the same', async () => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			name: 'John',
		});

		const firstUserId = getItem(AnalyticsType.Keys.UserId);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			name: 'John',
		});

		const secondUserId = getItem(AnalyticsType.Keys.UserId);

		expect(firstUserId).toEqual(secondUserId);
	});

	it('does not replace the user id whenever the set identity hash is the same and emailAddress is uppercase', async () => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		await Analytics.setIdentity({
			email: 'JOHN@LIFERAY.COM',
			name: 'John',
		});

		const firstUserId = getItem(AnalyticsType.Keys.UserId);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			name: 'John',
		});

		const secondUserId = getItem(AnalyticsType.Keys.UserId);

		expect(firstUserId).toEqual(secondUserId);
	});

	it('does not replace the user id whenever only the fields changed', async () => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [{name: 'lastName', value: ''}],
			name: 'John',
		});

		const firstUserId = getItem(AnalyticsType.Keys.UserId);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [{name: 'lastName', value: 'Doe'}],
			name: 'John',
		});

		const secondUserId = getItem(AnalyticsType.Keys.UserId);

		expect(firstUserId).toEqual(secondUserId);
	});

	it('replaces the user id whenever the email changed and fields are sent', async () => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			fields: [{name: 'firstName', value: 'John'}],
			name: 'John',
		});

		const firstUserId = getItem(AnalyticsType.Keys.UserId);

		await Analytics.setIdentity({
			email: 'brian@liferay.com',
			fields: [{name: 'firstName', value: 'Brian'}],
			name: 'Brian',
		});

		const secondUserId = getItem(AnalyticsType.Keys.UserId);

		expect(firstUserId).not.toEqual(secondUserId);
	});

	it('sends the queued messages when flush is called', async () => {
		let identityCalled = 0;

		Analytics.reset();
		AnalyticsClient.dispose();

		// A long interval keeps the flush loop out of the way, so the send can
		// only be attributed to the explicit flush() call below.

		Analytics = AnalyticsClient.create({
			...INITIAL_CONFIG,
			flushInterval: 60000,
		});

		// Earlier tests leave items behind in this queue, and the flush below
		// would send every one of them.

		Analytics[AnalyticsType.Queues.IdentityMessage].reset();

		fetchMock.restore();
		fetchMock.mock(/identity$/, () => {
			identityCalled += 1;

			return '';
		});
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));

		await Analytics.setIdentity({
			email: 'flush@liferay.com',
			name: 'Flush',
		});

		expect(identityCalled).toBe(0);

		await Analytics.flush();

		expect(identityCalled).toBe(1);
	});

	it('regenerates the user id on logouts or session expirations ', async () => {
		fetchMock.mock(/ac-server/i, () => Promise.resolve(200));
		fetchMock.mock(/identity$/, () => Promise.resolve(200));

		sendDummyEvents(Analytics, 1);

		await Analytics._queueFlushService?.flush();

		const userId = getItem(AnalyticsType.Keys.UserId);

		await Analytics.setIdentity({
			email: 'john@liferay.com',
			name: 'John',
		});

		Analytics.reset();
		AnalyticsClient.dispose();

		sendDummyEvents(Analytics, 1);

		await Analytics._queueFlushService?.flush();

		expect(getItem(AnalyticsType.Keys.UserId)).not.toEqual(userId);
	});

	describe('Demandbase account message', () => {
		const COMPANY_PROFILE = {
			company_name: 'Acme Corp',
			industry: 'Software',
			web_site: 'acme.com',
		};

		afterEach(() => {
			delete (window as any).Demandbase;
			localStorage.removeItem(AnalyticsType.Keys.DemandbaseAccount);
			localStorage.removeItem(AnalyticsType.Queues.AccountMessage);
		});

		it('builds the demandbase-account endpoint from endpointUrl', () => {
			expect(Analytics.config.demandbaseAccountEndpoint).toBe(
				'https://ac-server.io/demandbase-account'
			);
		});

		it('enqueues an account message when CompanyProfile is present', async () => {
			(window as any).Demandbase = {
				IpApi: {CompanyProfile: COMPANY_PROFILE},
			};

			await Analytics.setIdentity(ANALYTICS_IDENTITY);
			await wait(10);

			const items = Analytics[
				AnalyticsType.Queues.AccountMessage
			].getItems() as any[];

			expect(items.length).toBe(1);
			expect(items[0].userId).toBe(getItem(AnalyticsType.Keys.UserId));
			expect(items[0]).toMatchObject(COMPANY_PROFILE);
			expect(items[0].emailAddressHashed).toBe(
				Analytics.config.identity.emailAddressHashed
			);
			expect(items[0].emailAddressHashed).toBeTruthy();
		});

		it('does not enqueue an account message when Demandbase is absent', async () => {
			await Analytics.setIdentity(ANALYTICS_IDENTITY);
			await wait(10);

			const items =
				Analytics[AnalyticsType.Queues.AccountMessage].getItems();

			expect(items.length).toBe(0);
		});

		it('does not re-enqueue the account message for the same userId and profile', async () => {
			(window as any).Demandbase = {
				IpApi: {CompanyProfile: COMPANY_PROFILE},
			};

			await Analytics.setIdentity(ANALYTICS_IDENTITY);
			await wait(10);

			Analytics[AnalyticsType.Queues.AccountMessage].reset();

			await Analytics.setIdentity(ANALYTICS_IDENTITY);
			await wait(10);

			const items =
				Analytics[AnalyticsType.Queues.AccountMessage].getItems();

			expect(items.length).toBe(0);
		});

		it('clears stored account state when Demandbase becomes unavailable', async () => {
			(window as any).Demandbase = {
				IpApi: {CompanyProfile: COMPANY_PROFILE},
			};

			await Analytics.setIdentity(ANALYTICS_IDENTITY);
			await wait(10);

			expect(getItem(AnalyticsType.Keys.DemandbaseAccount)).toBeTruthy();
			expect(
				Analytics[AnalyticsType.Queues.AccountMessage].getItems().length
			).toBe(1);

			delete (window as any).Demandbase;
			jest.spyOn(
				Analytics.demandbase,
				'waitForReadiness'
			).mockResolvedValue(null);

			Analytics.demandbase.sendAccountMessage('any-user-id');
			await wait(10);

			expect(getItem(AnalyticsType.Keys.DemandbaseAccount)).toBeNull();
			expect(
				Analytics[AnalyticsType.Queues.AccountMessage].getItems().length
			).toBe(0);
		});

		it('does not throw when reading Demandbase throws', async () => {
			Object.defineProperty(window, 'Demandbase', {
				configurable: true,
				get() {
					throw new Error('boom');
				},
			});

			await expect(
				Analytics.setIdentity(ANALYTICS_IDENTITY)
			).resolves.toBeDefined();

			await wait(10);
		});

		it('demandbase.waitForReadiness resolves immediately when CompanyProfile is available', async () => {
			(window as any).Demandbase = {
				IpApi: {CompanyProfile: COMPANY_PROFILE},
			};

			await expect(
				Analytics.demandbase.waitForReadiness(100)
			).resolves.toEqual(COMPANY_PROFILE);
		});

		it('demandbase.waitForReadiness resolves null on timeout when Demandbase never loads', async () => {
			await expect(
				Analytics.demandbase.waitForReadiness(50)
			).resolves.toBeNull();
		});

		it('demandbase.waitForReadiness resolves via registerCallback when invoked', async () => {
			let registered: ((data: unknown) => void) | undefined;

			(window as any).Demandbase = {
				IpApi: {CompanyProfile: undefined},
				Utilities: {
					Callbacks: {
						registerCallback: (fn: (data: unknown) => void) => {
							registered = fn;
						},
					},
				},
			};

			const promise = Analytics.demandbase.waitForReadiness(2000);

			setTimeout(() => {
				(window as any).Demandbase.IpApi.CompanyProfile =
					COMPANY_PROFILE;

				registered?.(COMPANY_PROFILE);
			}, 50);

			await expect(promise).resolves.toEqual(COMPANY_PROFILE);
		});
	});

	describe('send()', () => {
		it('is exposed as an Analytics method', () => {
			expect(typeof Analytics.send).toBe('function');
		});

		it('adds the given event to the event queue', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const properties = {a: 1, b: 2, c: 3};

			await Analytics.send(
				AnalyticsType.EventId.BlogViewed,
				AnalyticsType.ApplicationId.Blog,
				properties
			);

			const events = Analytics.getEvents();

			expect(events).toEqual([
				expect.objectContaining({
					applicationId: AnalyticsType.ApplicationId.Blog,
					eventId: AnalyticsType.EventId.BlogViewed,
					properties,
				}),
			]);
		});

		it('persists the given events to the LocalStorage', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);
			const eventsNumber = 5;

			await sendDummyEvents(Analytics, eventsNumber);

			const events = Analytics.getEvents();

			expect(events.length).toBeGreaterThanOrEqual(eventsNumber);
		});
	});

	describe('getBatchSegmentExternalReferenceCodes()', () => {
		it('is exposed as an Analytics method', () => {
			expect(typeof Analytics.getBatchSegmentExternalReferenceCodes).toBe(
				'function'
			);
		});

		it('gets batch segment ids for the first time', async () => {
			fetchMock.mock(/ac-backend-server/i, () =>
				Promise.resolve([1, 2, 3])
			);

			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			let analyticsBatchSegmentExternalReferenceCode =
				getItem<SegmentCachedData>(
					ANALYTICS_BATCH_SEGMENT_EXTERNAL_REFERENCE_CODES
				);

			expect(analyticsBatchSegmentExternalReferenceCode).toBeNull();

			const result =
				await Analytics.getBatchSegmentExternalReferenceCodes();

			expect(result).toEqual([1, 2, 3]);

			analyticsBatchSegmentExternalReferenceCode = getItem(
				ANALYTICS_BATCH_SEGMENT_EXTERNAL_REFERENCE_CODES
			);

			const individualId = (Analytics as any)._getUserId();

			expect(
				analyticsBatchSegmentExternalReferenceCode?.[individualId]
					?.segmentExternalReferenceCodes
			).toEqual([1, 2, 3]);

			const date = new Date();

			const createDate =
				analyticsBatchSegmentExternalReferenceCode?.[individualId]
					?.createDate ?? 0;

			expect(date.getTime()).toBeLessThan(
				createDate + THREE_HOURS_IN_MILLISECONDS
			);
		});

		it('gets batch segment ids when data is expired', async () => {
			fetchMock.mock(/ac-backend-server/i, () =>
				Promise.resolve([1, 2, 3])
			);

			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const individualId = (Analytics as any)._getUserId();

			const date = new Date();

			date.setHours(date.getHours() - 5);

			setItem(ANALYTICS_BATCH_SEGMENT_EXTERNAL_REFERENCE_CODES, {
				[individualId]: {
					createDate: date.getTime(),
					segmentExternalReferenceCodes: [1, 2],
				},
			});

			const result =
				await Analytics.getBatchSegmentExternalReferenceCodes();

			expect(result).toEqual([1, 2, 3]);

			const analyticsBatchSegmentExternalReferenceCode =
				getItem<SegmentCachedData>(
					ANALYTICS_BATCH_SEGMENT_EXTERNAL_REFERENCE_CODES
				);

			expect(
				analyticsBatchSegmentExternalReferenceCode?.[individualId]
					?.segmentExternalReferenceCodes
			).toEqual([1, 2, 3]);

			const createDate =
				analyticsBatchSegmentExternalReferenceCode?.[individualId]
					?.createDate ?? 0;

			expect(date.getTime()).toBeLessThan(createDate);
		});

		it('gets batch segment ids when data is not expired', async () => {
			fetchMock.mock(/ac-backend-server/i, () =>
				Promise.resolve([1, 2, 3])
			);

			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const individualId = (Analytics as any)._getUserId();

			const date = new Date();

			date.setHours(date.getHours() - 1);

			setItem(ANALYTICS_BATCH_SEGMENT_EXTERNAL_REFERENCE_CODES, {
				[individualId]: {
					createDate: date.getTime(),
					segmentExternalReferenceCodes: [1, 2],
				},
			});

			const result =
				await Analytics.getBatchSegmentExternalReferenceCodes();

			expect(result).toEqual([1, 2]);

			const analyticsBatchSegmentExternalReferenceCode =
				getItem<SegmentCachedData>(
					ANALYTICS_BATCH_SEGMENT_EXTERNAL_REFERENCE_CODES
				);

			expect(
				analyticsBatchSegmentExternalReferenceCode?.[individualId]
					?.segmentExternalReferenceCodes
			).toEqual([1, 2]);

			const createDate =
				analyticsBatchSegmentExternalReferenceCode?.[individualId]
					?.createDate ?? 0;

			expect(date.getTime()).toEqual(createDate);
		});
	});

	describe('getRealTimeSegmentExternalReferenceCodes()', () => {
		it('is exposed as an Analytics method', () => {
			expect(
				typeof Analytics.getRealTimeSegmentExternalReferenceCodes
			).toBe('function');
		});

		it('gets real time segment ids and never caches data', async () => {
			fetchMock.mock(/ac-backend-server/i, () =>
				Promise.resolve([1, 2, 3])
			);

			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const result1 =
				await Analytics.getRealTimeSegmentExternalReferenceCodes();

			expect(result1).toEqual([1, 2, 3]);

			fetchMock.restore();

			fetchMock.mock(/ac-backend-server/i, () =>
				Promise.resolve([4, 5, 6])
			);

			const result2 =
				await Analytics.getRealTimeSegmentExternalReferenceCodes();

			expect(result2).toEqual([4, 5, 6]);
		});
	});

	describe('track()', () => {
		afterEach(() => {
			const error = console.error as any;

			if (error.mockRestore) {
				error.mockRestore();
			}
		});

		it('is exposed as an Analytics method', () => {
			expect(typeof Analytics.track).toBe('function');
		});

		it('adds the given event to the event queue', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const properties = {a: 1, b: 2, c: 3};

			await Analytics.track(AnalyticsType.EventId.BlogViewed, properties);

			const events = Analytics.getEvents();

			expect(events).toEqual([
				expect.objectContaining({
					applicationId: 'CustomEvent',
					eventId: AnalyticsType.EventId.BlogViewed,
					properties,
				}),
			]);
		});

		it('returns a type error if the eventId is not a string', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const eventId = {test: 'test'};

			console.error = jest.fn((val) => val);

			await Analytics.track(eventId as any);

			expect(console.error).toHaveBeenCalledTimes(1);
		});

		it('returns a type error if the attribute type is not valid', () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			console.error = jest.fn((val) => val);

			Analytics.track(
				AnalyticsType.EventId.AssetClicked,
				{bar: [], type: null} as any,
				{applicationId: 'Any'}
			);

			expect(console.error).toHaveBeenCalledTimes(2);
		});

		it('does not returns a type error if the attribute type is not valid and applicationId is from DXP', () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			console.error = jest.fn((val) => val);

			DXP_APPLICATION_IDS.forEach((applicationId) => {
				Analytics.track(
					AnalyticsType.EventId.AssetClicked,
					{bar: [], type: null} as any,
					{applicationId}
				);

				expect(console.error).toHaveBeenCalledTimes(0);
			});
		});

		it('uses the applicationId from options', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const eventId = AnalyticsType.EventId.BlogViewed;
			const applicationId = AnalyticsType.ApplicationId.Blog;
			const properties = {a: 1, b: 2, c: 3};

			await Analytics.track(eventId, properties, {applicationId});

			const events = Analytics.getEvents();

			expect(events).toEqual([
				expect.objectContaining({
					applicationId,
					eventId,
					properties,
				}),
			]);
		});

		it('uses the assetType from properties over the applicationId from options', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const eventId = AnalyticsType.EventId.BlogViewed;
			const properties = {
				a: 1,
				assetType: AnalyticsType.ApplicationId.Blog,
			};

			await Analytics.track(eventId, properties, {applicationId: 'Page'});

			const events = Analytics.getEvents();

			expect(events).toEqual([
				expect.objectContaining({
					applicationId: AnalyticsType.ApplicationId.Blog,
					eventId,
					properties: {a: 1},
				}),
			]);
		});

		it('uses CustomEvent as default applicationId', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const eventId = AnalyticsType.EventId.AssetClicked;
			const properties = {a: 1, b: 2, c: 3};

			await Analytics.track(eventId, properties);

			const events = Analytics.getEvents();

			expect(events).toEqual([
				expect.objectContaining({
					applicationId: AnalyticsType.ApplicationId.CustomEvent,
					eventId,
					properties,
				}),
			]);
		});

		it('uses applicationId from options', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);

			const eventId = AnalyticsType.EventId.BlogViewed;
			const properties = {a: 1, b: 2, c: 3};
			const options = {applicationId: AnalyticsType.ApplicationId.Blog};

			await Analytics.track(eventId, properties, options);

			const events = Analytics.getEvents();

			expect(events).toEqual([
				expect.objectContaining({
					applicationId: AnalyticsType.ApplicationId.Blog,
					eventId,
					properties,
				}),
			]);
		});

		it('persists the given events to the LocalStorage', async () => {
			Analytics = AnalyticsClient.create(INITIAL_CONFIG);
			const eventsNumber = 5;

			await trackDummyEvents(Analytics, eventsNumber);

			const events = Analytics.getEvents();

			expect(events.length).toBeGreaterThanOrEqual(eventsNumber);
		});
	});
});
