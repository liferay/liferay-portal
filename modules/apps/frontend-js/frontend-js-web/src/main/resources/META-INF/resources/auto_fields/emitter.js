/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Minimal replacement for the event support AutoFields used to inherit from
 * <code>A.Base</code>. Only the subset the component and its callers rely on is
 * implemented: <code>on</code>, <code>once</code>, <code>detach</code> and
 * <code>fire</code>.
 */
export default class Emitter {
	constructor() {
		this._listeners = {};
	}

	detach(type, fn) {
		const listeners = this._listeners[type];

		if (!listeners) {
			return;
		}

		this._listeners[type] = listeners.filter(
			(listener) => listener.fn !== fn
		);
	}

	fire(type, data) {
		const listeners = this._listeners[type];

		if (!listeners) {
			return;
		}

		for (const listener of [...listeners]) {
			if (listener.once) {
				this.detach(type, listener.fn);
			}

			listener.fn.call(this, data);
		}
	}

	on(type, fn) {
		this._listeners[type] = this._listeners[type] || [];

		this._listeners[type].push({fn, once: false});

		return {detach: () => this.detach(type, fn)};
	}

	once(type, fn) {
		this._listeners[type] = this._listeners[type] || [];

		this._listeners[type].push({fn, once: true});

		return {detach: () => this.detach(type, fn)};
	}
}
