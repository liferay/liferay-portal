/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {buildFragment} from 'frontend-js-web';

import LiferaySurface from '../surface/Surface';
import {openToast} from '../util/openToast';
import {getPortletBoundaryId, getUid, resetAllPortlets} from '../util/utils';
import App from './App';

const MAX_TIMEOUT = Math.pow(2, 31) - 1;
const PROPAGATED_PARAMS = ['bodyCssClass'];

const CSS = `
@keyframes shift-rightwards {
	0% {
		transform: translateX(-100%);
	}
	40% {
		transform: translateX(0%);
	}
	60% {
		transform: translateX(0%);
	}
	100% {
		transform: translateX(100%);
	}
}

.lfr-spa-loading-bar {
	background: var(--primary);
	display: none;
	height: 2px;
	left: 0;
	position: fixed;
	right: 0;
	top: 0;
	transform: translateX(100%);
	z-index: 2000;
}

.lfr-spa-loading .lfr-spa-loading-bar {
	animation: shift-rightwards 1s ease-in-out infinite;
	animation-delay: 0.4s;
	display: block;
}`;

/**
 * LiferayApp
 *
 * Inherits from `senna/src/app/App` and adds the following Liferay specific
 * behavior to Senna's default App:
 * <ul>
 *   <li> Makes cache expiration time configurable from System Settings</li>
 *   <li>Lets you set valid status codes (Liferay's default valid status codes
 *   are listed in {@link https://docs.liferay.com/portal/7.1/javadocs/portal-kernel/com/liferay/portal/kernel/servlet/ServletResponseConstants.html|ServletResponseConstants.java})
 *   <li>Shows alert notifications when requests take too long or when they fail</li>
 *   <li>Adds a portletBlacklist option that lets you exclude specific portlets
 *   from the SPA lifecycle.</li>
 * </ul>
 */

class LiferayApp extends App {

	/**
	 * @inheritDoc
	 */

	constructor({
		cacheExpirationTime,
		clearScreensCache,
		debugEnabled,
		navigationExceptionSelectors,
		portletsBlacklist,
		preloadCSS,
		requestTimeout,
		userNotification,
		validStatusCodes,
	}) {
		super({navigationExceptionSelectors, preloadCSS});

		this._cacheExpirationTime = cacheExpirationTime;
		this._clearScreensCache = clearScreensCache;
		this._debugEnabled = debugEnabled;

		this.portletsBlacklist = portletsBlacklist;
		this.userNotification = userNotification;
		this.validStatusCodes = validStatusCodes;

		this.setShouldUseFacade(true);

		this.timeout = Math.max(requestTimeout, 0) || MAX_TIMEOUT;
		this.timeoutAlert = null;

		this.setLoadingCssClass('lfr-spa-loading');

		this.on('beforeNavigate', this.onBeforeNavigate);
		this.on('endNavigate', this.onEndNavigate);
		this.on('navigationError', this.onNavigationError);
		this.on('startNavigate', this.onStartNavigate);

		Liferay.on('beforeScreenFlip', resetAllPortlets);
		Liferay.on('beforeScreenFlip', Liferay.destroyUnfulfilledPromises);
		Liferay.on('io:complete', this.onLiferayIOComplete, this);

		const body = document.body;

		if (!body.id) {
			body.id = 'senna_surface' + getUid();
		}

		this.addSurfaces(new LiferaySurface(body.id));

		let styleHTML = `<style`;

		if (Liferay.CSP && Liferay.CSP.nonce) {
			styleHTML += ` nonce="${Liferay.CSP.nonce}">`;
		}

		styleHTML += ` type="text/css">${CSS}</style>`;

		document.head.appendChild(buildFragment(styleHTML));
		body.appendChild(
			buildFragment('<div class="lfr-spa-loading-bar"></div>')
		);

		this.setLinkSelector(this.getLinkSelector() + ':not(.ck-content *)');
	}

	/**
	 * Retrieves or create a screen instance to a path. This method overrides
	 * the default one to avoid ActionURLScreens to be cached and reused across
	 * navigations causing different lifecycle mechanisms to be called on live
	 * documents instead of on inert fragments
	 * @param {!string} path Path containing the querystring part.
	 * @return {Screen}
	 */
	createScreenInstance(path, route) {
		if (path === this.activePath) {
			const uri = new URL(path, window.location.origin);

			if (uri.searchParams.get('p_p_lifecycle') === '1') {
				this.activePath = this.activePath + `__${getUid()}`;

				this.screens[this.activePath] = this.screens[path];

				delete this.screens[path];
			}
		}

		return super.createScreenInstance(path, route);
	}

	/**
	 * Returns the cache expiration time configuration. This value comes from
	 * System Settings. The configuration is set upon App initialization
	 * @See {@link https://github.com/liferay/liferay-portal/blob/7.1.x/modules/apps/frontend-js/frontend-js-spa-web/src/main/resources/META-INF/resources/init.tmpl|init.tmpl}
	 * @return {!Number} The `cacheExpirationTime` value
	 */

	getCacheExpirationTime() {
		return this._cacheExpirationTime;
	}

	/**
	 * Returns the valid status codes accepted by Liferay. These values
	 * come from {@link https://docs.liferay.com/portal/7.1/javadocs/portal-kernel/com/liferay/portal/kernel/servlet/ServletResponseConstants.html|ServletResponseConstants.java}.
	 * @return {!Array} The `validStatusCodes` property
	 */

	getValidStatusCodes() {
		return this.validStatusCodes;
	}

	/**
	 * Returns whether the cache is enabled. Cache is considered enabled
	 * when {@link LiferayApp#getCacheExpirationTime|getCacheExpirationTime} is
	 * greater than zero.
	 * @return {!Boolean} True if cache is enabled
	 */

	isCacheEnabled() {
		return this.getCacheExpirationTime() > -1;
	}

	/**
	 * Returns whether a given portlet element is in a blacklisted portlet
	 * that should not behave like a SPA
	 * @param  {!String} element The portlet boundary DOM node
	 * @return {!Boolean} True if portlet element is blacklisted
	 */

	isInPortletBlacklist(element) {
		const portletsBlacklistSelector = this.portletsBlacklist
			.map((portletId) => `[id^="${getPortletBoundaryId(portletId)}"]`)
			.join();

		return !!element.closest(portletsBlacklistSelector);
	}

	/**
	 * Returns whether a given Screen's cache is expired. The expiration timeframe
	 * is based on the value returned by {@link LiferayApp#getCacheExpirationTime|getCacheExpirationTime}.
	 * @param  {!Screen} screen The Senna Screen
	 * @return {!Boolean} True if the cache has expired
	 */

	isScreenCacheExpired(screen) {
		let screenCacheExpired = false;

		if (this.getCacheExpirationTime() !== 0) {
			const lastModifiedInterval =
				new Date().getTime() - screen.getCacheLastModified();

			screenCacheExpired =
				lastModifiedInterval > this.getCacheExpirationTime();
		}

		return screenCacheExpired;
	}

	/**
	 * A callback for Senna's `beforeNavigate` event. The cache is cleared
	 * for all screens when the flag `clearScreensCache` is set or when a form
	 * submission is about to occur. This method also exposes the
	 * `beforeNavigate` event to the Liferay global object so anyone can listen
	 * to it.
	 * @param  {!Object} data Data about the event
	 * @param  {!Event} event The event object
	 */

	onBeforeNavigate(data, event) {
		if (this._clearScreensCache || data.form) {
			this.clearScreensCache();
		}

		this._clearLayoutData();

		data.path = this._propagateParams(data);

		Liferay.fire('beforeNavigate', {
			app: this,
			originalEvent: event,
			path: data.path,
		});
	}

	/**
	 * A private event handler function, called when the
	 * `dataLayoutConfigReady` event is fired on the Liferay object,
	 * that initializes `Liferay.Layout`
	 * @param  {!Event} event The event object
	 */

	onDataLayoutConfigReady_() {
		if (Liferay.Layout && Liferay.Data.layoutConfig) {
			Liferay.Layout.init(Liferay.Data.layoutConfig);
		}
		else {
			this.dataLayoutConfigReadyHandle_ = Liferay.once(
				'dataLayoutConfigReady',
				this.onDataLayoutConfigReady_
			);
		}
	}

	/**
	 * @inheritDoc
	 * Overrides Senna's default `onDocClickDelegate_ handler`. Halts
	 * SPA behavior if the click target is inside a blacklisted portlet.
	 * Reduces navigations from multiple clicks to a single navigation.
	 *
	 * @param  {!Event} event The event object
	 */

	onDocClickDelegate_(event) {
		if (
			this.isInPortletBlacklist(
				event.target.closest(this.getLinkSelector())
			)
		) {
			return;
		}

		super.onDocClickDelegate_(event);
	}

	/**
	 * @inheritDoc
	 * Overrides Senna's default `onDocSubmitDelegate_ handler` and
	 * halts SPA behavior if the form is inside a blacklisted
	 * portlet
	 * @param  {!Event} event The event object
	 */

	onDocSubmitDelegate_(event) {
		if (
			this.isInPortletBlacklist(
				event.target.closest(this.getFormSelector())
			)
		) {
			return;
		}

		super.onDocSubmitDelegate_(event);
	}

	/**
	 * Callback for Senna's `endNavigate` event that exposes it
	 * to the Liferay global object
	 * @param  {!Event} event The event object
	 */

	onEndNavigate(event) {
		Liferay.fire('endNavigate', {
			app: this,
			error: event.error,
			path: event.path,
		});

		if (!this.pendingNavigate) {
			this._clearRequestTimer();
			this._hideTimeoutAlert();
		}

		if (!event.error) {
			this.onDataLayoutConfigReady_();
		}

		AUI().Get._insertCache = {};

		Liferay.DOMTaskRunner.reset();
	}

	/**
	 * Callback for Liferay's `io:complete` event that clears screens cache when
	 * an async request occurs
	 */

	onLiferayIOComplete() {
		this.clearScreensCache();
	}

	/**
	 * Callback for Senna's `navigationError` event that displays
	 * an alert message to the user with information about the error
	 * @param  {!Event} event The event object
	 */

	onNavigationError(event) {
		if (event.error.requestPrematureTermination) {
			window.location.href = event.path;
		}
		else if (
			event.error.invalidStatus ||
			event.error.requestError ||
			event.error.timeout
		) {
			let message = Liferay.Language.get(
				'there-was-an-unexpected-error.-please-refresh-the-current-page'
			);

			if (this._debugEnabled) {
				console.error(event.error);

				if (event.error.invalidStatus) {
					message = Liferay.Language.get(
						'the-spa-navigation-request-received-an-invalid-http-status-code'
					);
				}
				if (event.error.requestError) {
					message = Liferay.Language.get(
						'there-was-an-unexpected-error-in-the-spa-request'
					);
				}
				if (event.error.timeout) {
					message = Liferay.Language.get('the-spa-request-timed-out');
				}
			}

			Liferay.Data.layoutConfig = this.dataLayoutConfig_;

			openToast('error', Liferay.Language.get('error'), message);
		}
	}

	/**
	 * Callback for Senna's `startNavigate` event that exposes it
	 * to the Liferay global object
	 * @param  {!Event} event The event object
	 */

	onStartNavigate(event) {
		Liferay.fire('startNavigate', {
			app: this,
			path: event.path,
		});

		this._startRequestTimer(event.path);
	}

	/**
	 * Sets the `portletsBlacklist` property
	 * @param  {!Object} portletsBlacklist
	 */

	setPortletsBlacklist(portletsBlacklist) {
		this.portletsBlacklist = portletsBlacklist;
	}

	/**
	 * Sets the `validStatusCodes` property
	 * @param  {!Array} validStatusCodes
	 */

	setValidStatusCodes(validStatusCodes) {
		this.validStatusCodes = validStatusCodes;
	}

	/**
	 * Clears and detaches event handlers for Liferay's `dataLayoutConfigReady`
	 * event
	 */

	_clearLayoutData() {
		this.dataLayoutConfig_ = Liferay.Data.layoutConfig;

		Liferay.Data.layoutConfig = null;

		if (this.dataLayoutConfigReadyHandle_) {
			this.dataLayoutConfigReadyHandle_.detach();
			this.dataLayoutConfigReadyHandle_ = null;
		}
	}

	/**
	 * Clears the timer that notifies the user when the SPA request
	 * takes longer than the thresshold time configured in the
	 * `this.userNotification.timeout` System Settings property
	 */

	_clearRequestTimer() {
		if (this.requestTimer) {
			clearTimeout(this.requestTimer);
		}
	}

	/**
	 * Hides the request timeout alert
	 */

	_hideTimeoutAlert() {
		if (this.timeoutAlert) {
			this.timeoutAlert.dispose();
		}
	}

	_propagateParams(data) {
		const activeUri = this.activePath
			? new URL(this.activePath, window.location.origin)
			: new URL(window.location.href);

		const activePpid = activeUri.searchParams.get('p_p_id');

		const nextUri = new URL(data.path, window.location.origin);

		const nextPpid = nextUri.searchParams.get('p_p_id');

		if (nextPpid && nextPpid === activePpid) {
			PROPAGATED_PARAMS.forEach((paramKey) => {
				const paramName = `_${nextPpid}_${paramKey}`;
				const paramValue = activeUri.searchParams.get(paramName);

				if (paramValue) {
					nextUri.searchParams.set(paramName, paramValue);
				}
			});
		}

		return nextUri.toString();
	}

	/**
	 * Starts the timer that shows the user a notification when the SPA
	 * request takes longer than the threshold time configured in the
	 * `userNotification.timeout` System Settings property
	 * @param  {!String} path The path that may time out
	 */

	_startRequestTimer(path) {
		this._clearRequestTimer();

		if (this.userNotification.timeout > 0) {
			this.requestTimer = setTimeout(() => {
				Liferay.fire('spaRequestTimeout', {
					path,
				});

				this._hideTimeoutAlert();

				this.timeoutAlert = openToast(
					'warn',
					this.userNotification.title,
					this.userNotification.message
				);
			}, this.userNotification.timeout);
		}
	}

	/**
	 * @inheritDoc
	 */

	updateHistory_(title, path, state, opt_replaceHistory) {
		if (state && state.redirectPath) {
			state.path = state.redirectPath;
		}

		super.updateHistory_(title, path, state, opt_replaceHistory);
	}
}

export default LiferayApp;
