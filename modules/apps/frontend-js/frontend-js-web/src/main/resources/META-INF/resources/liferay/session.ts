/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const SCRIPT_URL = (document.currentScript as HTMLScriptElement).src;

interface SessionConfig {
	autoExtend: boolean;
	redirectOnExpire: boolean;
	redirectUrl: string;
	rememberMe: boolean;
	sessionLength: number;
	sessionTimeoutOffset: number;
	warningLength: number;
}

type TSessionState = 'active' | 'expired' | 'warned';

const TOAST_ID = 'sessionToast';

const BUFFER_TIME: Array<any> = [];

export class Session {
	autoExtend: boolean;
	redirectOnExpire: boolean;
	redirectUrl: string;
	sessionLength: number;
	sessionTimeoutOffset: number;

	private _alertClosed: any;
	private _banner: any;
	private _timestampKey: string;
	private _expiredText: string;
	private _initPageTitle: string;
	private _intervalId?: number | NodeJS.Timeout;
	private _pageTitle: string;
	private _warningLength: number;
	private _warningText: string;
	private sessionState: TSessionState;

	constructor(config: SessionConfig) {
		this.autoExtend = config.autoExtend || false;
		this.redirectOnExpire = config.redirectOnExpire ?? true;
		this.redirectUrl = config.redirectUrl || '';
		this.sessionLength = config.sessionLength * 1000 || 0;
		this.sessionTimeoutOffset = config.sessionTimeoutOffset * 1000 || 0;

		this._alertClosed = '';
		this._banner = null;
		this._timestampKey =
			'LFR_SESSION_STATE_' + Liferay.ThemeDisplay.getRealUserId();
		this._expiredText = Liferay.Language.get(
			'due-to-inactivity-your-session-has-expired'
		);
		this._initPageTitle = document.title;
		this._pageTitle = document.title;
		this._setTimestamp();
		this._warningLength = config.warningLength * 1000 || 0;
		this._warningText = Liferay.Util.sub(
			Liferay.Language.get('due-to-inactivity-your-session-will-expire'),
			[
				'<span class="countdown-timer">{0}</span>',
				(this.sessionLength / 60000).toString(),
				`<a class="alert-link" href="javascript:void(0);">${Liferay.Language.get('extend')}</a>`,
			]
		);
		this.sessionState = 'active';

		if (!config.rememberMe) {
			this._startTimer();
		}
	}

	destructor() {
		clearInterval(this._intervalId);

		this._destroyBanner();
	}

	expire() {
		this._expireSession();
		this._uiSetExpired();

		this.sessionState = 'expired';

		clearInterval(this._intervalId);
	}

	extend() {
		this._removeAlert();
		this._extendSession();

		this.sessionState = 'active';
	}

	async openToast(args: any) {
		const {openToast} = await import(
			Liferay.FrontendESM.buildURL(
				SCRIPT_URL,
				'frontend-js-components-web',
				'index'
			)
		);

		openToast(args);
	}

	warn() {
		this._uiSetWarned();

		this.sessionState = 'warned';
	}

	private _destroyBanner() {
		const toast = document.getElementById(TOAST_ID);

		const toastRootElement = toast?.parentElement;

		Liferay.destroyComponent(TOAST_ID);

		if (toastRootElement) {
			toastRootElement.remove();
		}

		this._banner = false;
	}

	private _expireSession() {
		Liferay.Util.fetch(
			Liferay.ThemeDisplay.getPathMain() + '/portal/' + 'expire_session'
		).then((response) => {
			if (response.ok) {
				Liferay.fire('sessionExpired');

				if (this.redirectOnExpire) {
					location.href = this.redirectUrl;
				}
			}
			else {
				setTimeout(() => {
					this._expireSession;
				}, 1000);
			}
		});
	}

	private _extendSession() {
		this._setTimestamp();

		Liferay.Util.fetch(
			Liferay.ThemeDisplay.getPathMain() + '/portal/' + 'extend_session'
		).then((response) => {
			if (response.status === 500) {
				this.expire();
			}
		});
	}

	private _formatNumber(value: number) {
		return Math.floor(value).toString().padStart(2, '0');
	}

	private _formatTime(time: string | number) {
		time = Number(time);

		if (Number.isInteger(time) && time > 0) {
			time /= 1000;

			BUFFER_TIME[0] = this._formatNumber(time / 3600);

			time %= 3600;

			BUFFER_TIME[1] = this._formatNumber(time / 60);

			time %= 60;

			BUFFER_TIME[2] = this._formatNumber(time);

			time = BUFFER_TIME.join(':');
		}
		else {
			time = 0;
		}

		return time.toString();
	}

	private _getBanner() {
		let banner = this._banner;

		if (!banner) {
			const openToast = this.openToast;

			const toastDefaultConfig = {
				onClick: ({event}: any) => {
					if (event.target.classList.contains('alert-link')) {
						this.extend();
					}
				},
				renderData: {
					__reactDOMFlushSync: true,
					componentId: TOAST_ID,
				},
				toastProps: {
					autoClose: false,
					id: TOAST_ID,
					role: 'alert',
				},
			};

			openToast({
				message: this._warningText,
				type: 'warning',
				...toastDefaultConfig,
			});

			const toastComponent = Liferay.component(TOAST_ID);

			banner = {
				open: (props: any) => {
					this._destroyBanner();

					openToast({
						...props,
						...toastDefaultConfig,
					});
				},
				...toastComponent,
			};

			this._banner = banner;
		}

		return banner;
	}

	private _getTimestamp() {
		return parseInt(
			Liferay.Util.LocalStorage.getItem(
				this._timestampKey,
				Liferay.Util.LocalStorage.TYPES.NECESSARY
			)!,
			10
		);
	}

	private _setTimestamp() {
		Liferay.Util.LocalStorage.setItem(
			this._timestampKey,
			Date.now().toString(),
			Liferay.Util.LocalStorage.TYPES.NECESSARY
		);
	}

	private _startTimer() {
		this._intervalId = setInterval(() => {
			const elapsed =
				Math.floor((Date.now() - this._getTimestamp()) / 1000) * 1000;

			const shouldExpire = elapsed >= this.sessionLength;
			const shouldWarn =
				this._warningLength > 0 &&
				elapsed >= this.sessionLength - this._warningLength;

			const expiredTimeoutOffset =
				elapsed >= this.sessionLength - this.sessionTimeoutOffset;

			if (shouldExpire && this.sessionState !== 'expired') {
				this.expire();
			}
			else if (this.autoExtend && expiredTimeoutOffset) {
				this.extend();
			}
			else if (
				!this.autoExtend &&
				shouldWarn &&
				this.sessionState !== 'warned'
			) {
				this.warn();
			}

			if (this._banner) {
				if (!shouldWarn) {
					this._removeAlert();
				}
				else if (!shouldExpire) {
					this._uiSetRemainingTime(
						this.sessionLength - elapsed,
						document.querySelector(`#${TOAST_ID} .countdown-timer`)
					);
				}
			}
		}, 1000);

		const handle = Liferay.on('beforeNavigate', () => {
			clearInterval(this._intervalId);
			handle.detach();
		});
	}

	private _removeAlert() {
		document.title = this._initPageTitle;

		if (this._banner) {
			this._destroyBanner();
		}

		this.sessionState = 'active';
	}

	private _uiSetExpired() {
		if (this._banner) {
			this._banner.open({
				message: this._expiredText,
				title: Liferay.Language.get('danger'),
				type: 'danger',
			});

			document.title = this._pageTitle;
		}
	}

	private _uiSetRemainingTime(
		remainingTime: number | string,
		counterTextNode: HTMLElement | null
	) {
		remainingTime = this._formatTime(remainingTime);

		if (!this._alertClosed && counterTextNode) {
			const alert = counterTextNode.closest('div[role="alert"]');

			// Prevent screen reader from rereading alert

			if (alert) {
				alert.removeAttribute('role');
			}

			counterTextNode.innerHTML = remainingTime;
		}

		document.title =
			Liferay.Util.sub(Liferay.Language.get('session-expires-in-x'), [
				remainingTime,
			]) +
			' | ' +
			this._pageTitle;
	}

	private _uiSetWarned() {
		const sessionLength = this.sessionLength;
		const timestamp = this._getTimestamp();
		const warningLength = this._warningLength;

		let elapsed = sessionLength;

		if (timestamp) {
			elapsed = Math.floor((Date.now() - timestamp) / 1000) * 1000;
		}

		let remainingTime = sessionLength - elapsed;

		if (remainingTime > warningLength) {
			remainingTime = warningLength;
		}

		this._getBanner();

		setTimeout(() => {
			this._uiSetRemainingTime(
				remainingTime,
				document.querySelector(`#${TOAST_ID} .countdown-timer`)
			);
		}, 60);
	}
}
