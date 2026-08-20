/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {Demandbase} from './demandbase';
import middlewares from './middlewares/defaults';
import defaultPlugins from './plugins/defaults';
import QueueFlushService from './queueFlushService';
import AccountMessageQueue from './queues/accountMessageQueue';
import EventMessageQueue from './queues/eventMessageQueue';
import EventQueue from './queues/eventsQueue';
import IdentityMessageQueue from './queues/identityMessageQueue';
import {Segment} from './segment';
import {Analytics as AnalyticsType} from './types';
import {
	ANALYTICS_CLIENT_VERSION,
	FLUSH_INTERVAL,
	QUEUE_PRIORITY_ACCOUNT,
	QUEUE_PRIORITY_DEFAULT,
	QUEUE_PRIORITY_IDENTITY,
	VALIDATION_CONTEXT_VALUE_MAXIMUM_LENGTH,
} from './utils/constants';
import {getContexts, setContexts} from './utils/contexts';
import {normalizeEvent} from './utils/events';
import hash from './utils/hash';
import {getItem, removeItem, setItem} from './utils/storage';
import {upgradeStorage} from './utils/storage_version';
import {isValidEvent} from './utils/validators';

// Constants

export const ENV: any = window || global;

/**
 * Analytics class that is designed to collect events that are captured
 * for later processing. It persists the events in localStorage
 * and flushes it to the defined endpoint at regular intervals.
 */
class Analytics {
	[AnalyticsType.Queues.AccountMessage]!: AccountMessageQueue;
	[AnalyticsType.Queues.Events]!: EventQueue;
	[AnalyticsType.Queues.Messages]!: EventMessageQueue;
	[AnalyticsType.Queues.IdentityMessage]!: IdentityMessageQueue;

	_disposed: boolean = false;
	_pluginDisposers: any[] = [];
	_queueFlushService!: QueueFlushService;

	config: AnalyticsType.Config = {
		channelId: '',
		dataSourceId: '',
		demandbaseAccountEndpoint: '',
		endpointUrl: '',
		faroBackendUrl: '',
		flushInterval: 0,
		identity: {
			emailAddressHashed: '',
		},
		identityEndpoint: '',
		projectId: '',
		userId: '',
	};
	demandbase!: Demandbase;
	middlewares: AnalyticsType.Middleware[] = [];
	segment!: Segment;
	version: string = '';

	/**
	 * Returns an Analytics instance and triggers the automatic flush loop
	 */
	constructor(
		config: AnalyticsType.Config,
		middlewares: AnalyticsType.Middleware[]
	) {
		if (this._isTrackingDisabled()) {
			return this;
		}

		this._disposed = false;

		const endpointUrl = (config.endpointUrl || '').replace(/\/$/, '');

		const faroBackendUrl = (config.faroBackendUrl || '').replace(/\/$/, '');

		this.config = Object.assign(config, {
			demandbaseAccountEndpoint: `${endpointUrl}/demandbase-account`,
			endpointUrl,
			faroBackendUrl,
			flushInterval: config.flushInterval || FLUSH_INTERVAL,
			identityEndpoint: `${endpointUrl}/identity`,
		});

		this.version = ANALYTICS_CLIENT_VERSION;

		// Register initial middlewares

		middlewares.forEach((middleware) =>
			this.registerMiddleware(middleware)
		);

		this._queueFlushService = new QueueFlushService(this.config);

		this._initializeEventQueue();
		this._initializeEventMessageQueue();
		this._initializeIdentityMessageQueue();
		this._initializeAccountMessageQueue();

		this.demandbase = new Demandbase(this);
		this.segment = new Segment(this);

		// Upgrade storage

		upgradeStorage();

		// Initializes default plugins

		this._pluginDisposers = defaultPlugins.map((plugin) => plugin(this));

		this._ensureIntegrity();

		return this;
	}

	/**
	 * Creates a singleton instance of Analytics
	 * @example
	 * Analytics.create(
	 *   {
	 *     channelId: '123456789',
	 *     dataSourceId: 'MyDataSourceId',
	 *     endpointUrl: 'https://osbasahpublisher-projectid.lfr.cloud'
	 *     flushInterval: 2000,
	 *     projectId: '123456'
	 *     userId: 'id-s7uatimmxgo',
	 *   }
	 * );
	 */
	static create(
		config: AnalyticsType.Config,
		middlewares: AnalyticsType.Middleware[] = []
	) {
		const self = new Analytics(config, middlewares);
		const Liferay = window.Liferay;

		ENV.Analytics = self;
		ENV.Analytics.create = Analytics.create;
		ENV.Analytics.dispose = Analytics.dispose;

		let email = '';
		let name = '';

		if (
			Liferay &&
			Liferay.ThemeDisplay &&
			Liferay.ThemeDisplay.getUserEmailAddress &&
			!!Liferay.ThemeDisplay.getUserEmailAddress().length &&
			Liferay.ThemeDisplay.getUserName &&
			!!Liferay.ThemeDisplay.getUserName().length
		) {
			email = Liferay.ThemeDisplay.getUserEmailAddress();
			name = Liferay.ThemeDisplay.getUserName();
		}

		self.setIdentity({
			email,
			name,
		});

		return self;
	}

	/**
	 * Disposes events and stops interval timer
	 */
	static dispose() {
		const self = ENV.Analytics;

		if (self && !self._isTrackingDisabled()) {
			self._disposeInternal();
		}
	}

	/**
	 * Sends every queued message now instead of waiting for the next flush
	 * interval, and returns a Promise that settles once the in-flight requests
	 * settle. Each request is already bounded by the client adapter's
	 * REQUEST_TIMEOUT, so a stalled endpoint cannot hold the Promise open
	 * indefinitely.
	 */
	flush() {
		if (!this._queueFlushService) {
			return Promise.resolve();
		}

		return this._queueFlushService.flush();
	}

	getEvents() {
		return this[
			AnalyticsType.Queues.Events
		].getItems<AnalyticsType.Event>();
	}

	getBatchSegmentExternalReferenceCodes() {
		return this.segment.getBatchSegmentExternalReferenceCodes();
	}

	getRealTimeSegmentExternalReferenceCodes() {
		return this.segment.getRealTimeSegmentExternalReferenceCodes();
	}

	/**
	 * Registers the given plugin and executes its initialization logic
	 */
	registerPlugin(plugin: (analytics: Analytics) => void) {
		if (typeof plugin === 'function') {
			plugin(this);
		}
	}

	/**
	 * Registers the given middleware. This middleware will be later on called
	 * with the request object and this Analytics instance
	 * @example
	 * AnalyticsType.registerMiddleware(
	 *   (request) => {
	 *     ...
	 *   }
	 * );
	 */
	registerMiddleware(middleware: AnalyticsType.Middleware) {
		if (this._isTrackingDisabled()) {
			return;
		}

		if (typeof middleware === 'function') {
			middlewares.push(middleware);
		}
	}

	/**
	 * Clear event queue and set stored context to the current context.
	 */
	reset() {
		if (this._isTrackingDisabled()) {
			return;
		}

		this[AnalyticsType.Queues.Events].reset();

		this.resetContext();
	}

	/**
	 * Set stored context to the current context.
	 */
	resetContext() {
		const context = this._getContext();

		const contextsMap = new Map();
		contextsMap.set(hash(context), context);

		setContexts(contextsMap);
	}

	/**
	 * Registers an event that is to be sent to Analytics Cloud
	 */
	track(
		eventId: AnalyticsType.EventId,
		eventProps?: AnalyticsType.EventProps,
		options = {}
	) {
		const {assetType, ...otherEventProps} = eventProps || {};

		const mergedOptions = {
			...{applicationId: AnalyticsType.ApplicationId.CustomEvent},
			...options,
		};

		const applicationId = assetType || mergedOptions.applicationId;

		if (
			this._isTrackingDisabled() ||
			this._disposed ||
			!isValidEvent({
				applicationId,
				eventId,
				eventProps: otherEventProps,
			})
		) {
			return;
		}

		const currentContextHash = this._getCurrentContextHash();

		this[AnalyticsType.Queues.Events].addItem(
			normalizeEvent(
				eventId,
				applicationId as AnalyticsType.ApplicationId,
				otherEventProps,
				currentContextHash
			)
		);
	}

	/**
	 * Registers an event that is to be sent to Analytics Cloud
	 */
	send(
		eventId: AnalyticsType.EventId,
		applicationId: AnalyticsType.ApplicationId,
		eventProps?: AnalyticsType.EventProps
	) {
		if (!applicationId) {
			return;
		}

		this.track(eventId, eventProps, {applicationId});
	}

	/**
	 * Sets the current user identity in the system. This is meant to be invoked
	 * by consumers every time an identity change is detected. If the identity is
	 * different than the previously stored one, we will save this new identity and
	 * send a request updating the Identity Service.
	 */
	setIdentity(identity: AnalyticsType.SetIdentity) {
		if (this._isTrackingDisabled()) {
			return;
		}

		const hashedIdentity = {
			emailAddressHashed: identity.email
				? hash(identity.email.toLowerCase())
				: '',
			fields: this._getNormalizedFields(identity.fields),
		};

		this.config.identity = hashedIdentity;

		const userId = this._getUserId();

		this._sendIdentity(hashedIdentity, userId);

		this.demandbase.sendAccountMessage(userId);

		return Promise.resolve(userId);
	}

	/**
	 * Clears interval and calls plugins disposers if available
	 */
	_disposeInternal() {
		this._disposed = true;

		if (this._queueFlushService) {
			this._queueFlushService.dispose();
		}

		if (this._pluginDisposers.length) {
			this._pluginDisposers
				.filter((disposer) => typeof disposer === 'function')
				.forEach((disposer) => disposer());
		}
	}

	_ensureIntegrity() {
		const userId = getItem<string>(AnalyticsType.Keys.UserId);

		if (userId) {
			this._setCookie(AnalyticsType.Keys.UserId, userId);
		}
	}

	_getCurrentContextHash() {
		const currentContext = this._getContext();
		const currentContextHash = hash(currentContext);
		const contextsMap = getContexts();

		if (!contextsMap.has(currentContextHash)) {
			contextsMap.set(currentContextHash, currentContext);

			setContexts(contextsMap);
		}

		return currentContextHash;
	}

	_getContext() {
		const {context} = middlewares.reduce(
			(request, middleware) => middleware(request),
			{
				context: {
					channelId: this.config.channelId,
				} as AnalyticsType.Context,
			}
		);

		const clonedContext = {...context};

		for (const key in clonedContext) {
			clonedContext[key] = String(clonedContext[key]).slice(
				0,
				VALIDATION_CONTEXT_VALUE_MAXIMUM_LENGTH
			);
		}

		return clonedContext;
	}

	_getIdentityHash(
		dataSourceId: string,
		identity: AnalyticsType.Config['identity'],
		userId: string
	) {
		const bodyData = {
			dataSourceId,
			identity,
			userId,
		};

		return hash(bodyData);
	}

	/**
	 * Returns the given fields sorted by name, so that the same data always
	 * produces the same identity hash regardless of the order the caller used.
	 * The array is copied because sorting is done in place and the caller's
	 * array must not be modified.
	 */
	_getNormalizedFields(fields?: AnalyticsType.Field[]) {
		if (!fields) {
			return fields;
		}

		return [...fields].sort((fieldA, fieldB) => {
			if (fieldA.name === fieldB.name) {
				return 0;
			}

			return fieldA.name < fieldB.name ? -1 : 1;
		});
	}

	/**
	 * Gets the userId for the existing analytics user. Previously generated ids
	 * are stored and retrieved before generating a new one. If an anonymous
	 * navigation is started after an identified navigation, the user ID token
	 * is regenerated.
	 */
	_getUserId() {
		let userId = getItem<string>(AnalyticsType.Keys.UserId);

		const {emailAddressHashed} = this.config.identity;
		const previousEmailAddressHashed = getItem<string>(
			AnalyticsType.Keys.PrevEmailAddressHash
		);

		if (!userId) {
			userId = this._generateUserId();
		}

		if (
			emailAddressHashed &&
			emailAddressHashed !== previousEmailAddressHashed
		) {
			if (previousEmailAddressHashed) {
				userId = this._generateUserId();
			}

			setItem(
				AnalyticsType.Keys.PrevEmailAddressHash,
				emailAddressHashed
			);
		}

		return userId;
	}

	/**
	 * Returns a unique identifier for a user, additionally it stores
	 * the generated token to the local storage cache and clears
	 * previously stored identity hash.
	 */
	_generateUserId() {
		const userId: string = uuidv4();

		setItem(AnalyticsType.Keys.UserId, userId);
		this._setCookie(AnalyticsType.Keys.UserId, userId);

		removeItem(AnalyticsType.Keys.Identity);

		return userId;
	}

	_isTrackingDisabled() {
		return (
			ENV[AnalyticsType.Keys.DisableTracking] ||
			navigator.doNotTrack === '1' ||
			navigator.doNotTrack === 'yes'
		);
	}

	/**
	 * Sends the identity information and user id to the Identity Service.
	 */
	_sendIdentity(identity: AnalyticsType.Config['identity'], userId: string) {
		const {dataSourceId} = this.config;
		const {channelId} = this._getContext();

		const identityHash = this._getIdentityHash(
			dataSourceId,
			identity,
			userId
		);
		const storedIdentityHash = getItem<string>(AnalyticsType.Keys.Identity);
		const storedChannelId = getItem<string>(AnalyticsType.Keys.ChannelId);

		if (
			identityHash !== storedIdentityHash ||
			channelId !== storedChannelId
		) {
			const {emailAddressHashed, fields} = identity;

			setItem(AnalyticsType.Keys.ChannelId, channelId);
			setItem(AnalyticsType.Keys.Identity, identityHash);

			this[AnalyticsType.Queues.IdentityMessage].addItem({
				channelId,
				dataSourceId,
				emailAddressHashed,
				fields,
				id: identityHash,
				userId,
			});
		}
	}

	/**
	 * Sets a browser cookie
	 * @protected
	 */
	_setCookie(key: string, data: string) {
		const Liferay = window.Liferay;
		const expires = new Date();

		expires.setDate(expires.getDate() + 365);

		// Checks if the client is being loaded with the Liferay global
		// variable and if there is a Cookie method because the client
		// is Liferay Portal agnostic and may have versions that do not
		// yet have the Cookie method.

		if (Liferay?.Util?.Cookie) {
			Liferay.Util.Cookie.set?.(
				key,
				data,
				Liferay?.Util?.Cookie?.TYPES?.PERSONALIZATION,
				{
					expires,
					secure: true,
				}
			);
		}
		else {
			const path = Liferay?.ThemeDisplay?.getPathContext?.() || '/';

			document.cookie = `${key}=${data}; expires=${expires.toUTCString()}; path=${path}; Secure`;
		}

		return;
	}

	/**
	 * Create member instance of EventQueue to store events.
	 */
	_initializeEventQueue() {
		const eventQueue = new EventQueue({
			analyticsInstance: this,
		});

		this[AnalyticsType.Queues.Events] = eventQueue;

		this._queueFlushService.addQueue(eventQueue, {
			priority: QUEUE_PRIORITY_DEFAULT,
		});
	}

	/**
	 * Create member instance of EventMessageQueue to store event messages.
	 */
	_initializeEventMessageQueue() {
		const eventMessageQueue = new EventMessageQueue({
			analyticsInstance: this,
		});

		this[AnalyticsType.Queues.Messages] = eventMessageQueue;

		this._queueFlushService.addQueue(eventMessageQueue, {
			priority: QUEUE_PRIORITY_DEFAULT,
		});
	}

	/**
	 * Create member instance of IdentityMessageQueue to store identity messages.
	 */
	_initializeIdentityMessageQueue() {
		const identityMessageQueue = new IdentityMessageQueue({
			analyticsInstance: this,
		});

		this[AnalyticsType.Queues.IdentityMessage] = identityMessageQueue;

		this._queueFlushService.addQueue(identityMessageQueue, {
			priority: QUEUE_PRIORITY_IDENTITY,
		});
	}

	/**
	 * Create member instance of AccountMessageQueue to store Demandbase
	 * account messages.
	 */
	_initializeAccountMessageQueue() {
		const accountMessageQueue = new AccountMessageQueue({
			analyticsInstance: this,
		});

		this[AnalyticsType.Queues.AccountMessage] = accountMessageQueue;

		this._queueFlushService.addQueue(accountMessageQueue, {
			priority: QUEUE_PRIORITY_ACCOUNT,
		});
	}
}

// Exposes Analytics.create to the global scope

ENV.Analytics = {
	create: Analytics.create,
};

export {Analytics};
export default Analytics;
