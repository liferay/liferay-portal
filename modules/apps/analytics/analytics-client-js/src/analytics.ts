/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// TODO: check possibility to install uuid ts version

import {v4 as uuidv4} from 'uuid';

import middlewares from './middlewares/defaults';
import defaultPlugins from './plugins/defaults';
import QueueFlushService from './queueFlushService';
import EventMessageQueue from './queues/eventMessageQueue';
import EventQueue from './queues/eventsQueue';
import IdentityMessageQueue from './queues/identityMessageQueue';
import {
	ANALYTICS_CLIENT_VERSION,
	FLUSH_INTERVAL,
	QUEUE_PRIORITY_DEFAULT,
	QUEUE_PRIORITY_IDENTITY,
	STORAGE_KEY_CHANNEL_ID,
	STORAGE_KEY_EVENTS,
	STORAGE_KEY_IDENTITY,
	STORAGE_KEY_MESSAGES,
	STORAGE_KEY_MESSAGE_IDENTITY,
	STORAGE_KEY_PREV_EMAIL_ADDRESS_HASHED,
	STORAGE_KEY_USER_ID,
	TRACK_DEFAULT_OPTIONS,
	VALIDATION_CONTEXT_VALUE_MAXIMUM_LENGTH,
} from './utils/constants';
import {getContexts, setContexts} from './utils/contexts';
import {removeCookiesFromUserBrowser} from './utils/cookies';
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
	[STORAGE_KEY_EVENTS]: EventQueue | null = null;
	[STORAGE_KEY_MESSAGES]: EventMessageQueue | null = null;
	[STORAGE_KEY_MESSAGE_IDENTITY]: IdentityMessageQueue | null = null;

	_disposed: boolean = false;
	_pluginDisposers: Analytics.Plugin[] = [];
	_queueFlushService: QueueFlushService | null = null;

	config: Analytics.Config = {
		channelId: '',
		dataSourceId: '',
		endpointUrl: '',
		flushInterval: 0,
		identity: {
			emailAddressHashed: '',
		},
		identityEndpoint: '',
		projectId: '',
		userId: '',
	};
	middlewares: Analytics.Middleware[] = [];
	version: string = '';

	/**
	 * Returns an Analytics instance and triggers the automatic flush loop
	 */
	constructor(config: Analytics.Config, middlewares: Analytics.Middleware[]) {
		if (this._isTrackingDisabled()) {
			return this;
		}

		this._disposed = false;

		const endpointUrl = (config.endpointUrl || '').replace(/\/$/, '');

		this.config = Object.assign(config, {
			endpointUrl,
			flushInterval: config.flushInterval || FLUSH_INTERVAL,
			identityEndpoint: `${endpointUrl}/identity`,
		});

		this.version = ANALYTICS_CLIENT_VERSION;

		removeCookiesFromUserBrowser();

		// Register initial middlewares

		middlewares.forEach((middleware) =>
			this?.registerMiddleware(middleware)
		);

		this._queueFlushService = new QueueFlushService(this.config);

		this._initializeEventQueue();
		this._initializeEventMessageQueue();
		this._initializeIdentityMessageQueue();

		// Upgrade storage

		upgradeStorage();

		// Initializes default plugins

		this._pluginDisposers = defaultPlugins.map((plugin) => plugin(this));

		this._ensureIntegrity();

		return this;
	}

	/**
	 * Creates a singleton instance of Analytics
	 * @param {Object} config Configuration object
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
		config: Analytics.Config,
		middlewares: Analytics.Middleware[] = []
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

	getEvents() {

		// @ts-ignore

		return this[STORAGE_KEY_EVENTS].getItems();
	}

	/**
	 * Registers the given plugin and executes its initialization logic
	 */
	registerPlugin(plugin: Analytics.Plugin) {
		if (typeof plugin === 'function') {
			plugin(this);
		}
	}

	/**
	 * Registers the given middleware. This middleware will be later on called
	 * with the request object and this Analytics instance
	 * @param {Function} middleware A function that will be invoked on every request
	 * @example
	 * Analytics.registerMiddleware(
	 *   (request, analytics) => {
	 *     ...
	 *   }
	 * );
	 */
	registerMiddleware(middleware: Analytics.Middleware) {
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

		// @ts-ignore

		this[STORAGE_KEY_EVENTS].reset();

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
	track(eventId: string, eventProps: Analytics.EventProps, options = {}) {
		const {assetType, ...otherEventProps} = eventProps || {};

		// eslint-disable-next-line
		const mergedOptions = Object.assign({}, TRACK_DEFAULT_OPTIONS, options);

		const applicationId = assetType || mergedOptions.applicationId;

		if (
			this._isTrackingDisabled() ||
			this?._disposed ||
			!isValidEvent({applicationId, eventId, eventProps: otherEventProps})
		) {
			return;
		}

		const currentContextHash = this._getCurrentContextHash();

		// @ts-ignore

		this?.[STORAGE_KEY_EVENTS].addItem(
			normalizeEvent(
				eventId,
				applicationId,
				otherEventProps,
				currentContextHash
			)
		);
	}

	/**
	 * Registers an event that is to be sent to Analytics Cloud
	 * @param {string} eventId Id of the event
	 * @param {string} applicationId ID of the application that triggered the event
	 * @param {Object} eventProps Complementary information about the event
	 */
	send(
		eventId: string,
		applicationId: string,
		eventProps: Analytics.EventProps
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
	setIdentity(identity: Analytics.Identity) {
		if (this._isTrackingDisabled()) {
			return;
		}

		const hashedIdentity = {
			emailAddressHashed: identity.email
				? hash(identity.email.toLowerCase())
				: '',
		};

		this.config.identity = hashedIdentity;

		const userId = this._getUserId();

		this._sendIdentity(hashedIdentity, userId);

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

		if (this._pluginDisposers) {
			this._pluginDisposers
				.filter((disposer) => typeof disposer === 'function')
				.forEach((disposer) => disposer());
		}
	}

	_ensureIntegrity() {
		const userId = getItem(STORAGE_KEY_USER_ID);

		if (userId) {
			this._setCookie(STORAGE_KEY_USER_ID, userId);
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
			(request, middleware) => middleware(request, this),
			{context: {channelId: this.config.channelId}}
		);

		for (const key in context) {
			context[key] = String(context[key]).slice(
				0,
				VALIDATION_CONTEXT_VALUE_MAXIMUM_LENGTH
			);
		}

		return context;
	}

	_getIdentityHash(
		dataSourceId: string,
		identity: Analytics.Config['identity'],
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
	 * Gets the userId for the existing analytics user. Previously generated ids
	 * are stored and retrieved before generating a new one. If an anonymous
	 * navigation is started after an identified navigation, the user ID token
	 * is regenerated.
	 */
	_getUserId() {
		let userId = getItem(STORAGE_KEY_USER_ID);

		const {emailAddressHashed} = this.config.identity;
		const previousEmailAddressHashed = getItem(
			STORAGE_KEY_PREV_EMAIL_ADDRESS_HASHED
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

			setItem(STORAGE_KEY_PREV_EMAIL_ADDRESS_HASHED, emailAddressHashed);
		}

		return userId;
	}

	/**
	 * Returns a unique identifier for a user, additionally it stores
	 * the generated token to the local storage cache and clears
	 * previously stored identity hash.
	 */
	_generateUserId() {
		const userId = uuidv4();

		setItem(STORAGE_KEY_USER_ID, userId);
		this._setCookie(STORAGE_KEY_USER_ID, userId);

		removeItem(STORAGE_KEY_IDENTITY);

		return userId;
	}

	_isTrackingDisabled() {
		return (
			ENV.ac_client_disable_tracking ||
			navigator.doNotTrack === '1' ||
			navigator.doNotTrack === 'yes'
		);
	}

	/**
	 * Sends the identity information and user id to the Identity Service.
	 */
	_sendIdentity(identity: Analytics.Config['identity'], userId: string) {
		const {dataSourceId} = this.config;
		const {channelId} = this._getContext();

		const identityHash = this._getIdentityHash(
			dataSourceId,
			identity,
			userId
		);
		const storedIdentityHash = getItem(STORAGE_KEY_IDENTITY);
		const storedChannelId = getItem(STORAGE_KEY_CHANNEL_ID);

		if (
			identityHash !== storedIdentityHash ||
			channelId !== storedChannelId
		) {
			const {emailAddressHashed} = identity;

			setItem(STORAGE_KEY_CHANNEL_ID, channelId);
			setItem(STORAGE_KEY_IDENTITY, identityHash);

			this[STORAGE_KEY_MESSAGE_IDENTITY]?.addItem({
				channelId,
				dataSourceId,
				emailAddressHashed,
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

		this[STORAGE_KEY_EVENTS] = eventQueue;
		this._queueFlushService?.addQueue(eventQueue, {
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

		this[STORAGE_KEY_MESSAGES] = eventMessageQueue;
		this._queueFlushService?.addQueue(eventMessageQueue, {
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

		this[STORAGE_KEY_MESSAGE_IDENTITY] = identityMessageQueue;
		this._queueFlushService?.addQueue(identityMessageQueue, {
			priority: QUEUE_PRIORITY_IDENTITY,
		});
	}
}

// Exposes Analytics.create to the global scope

ENV.Analytics = {
	create: Analytics.create,
};

export {Analytics};
export default Analytics;
