/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ProcessLock from 'browser-tabs-lock';

import {QUEUE_STORAGE_LIMIT} from '../utils/constants';
import {getRetryDelay} from '../utils/delay';
import {getItem, setItem, verifyStorageLimitForKey} from '../utils/storage';

class BaseQueue {
	constructor({analyticsInstance, name}) {
		this.maxSize = QUEUE_STORAGE_LIMIT;
		this.name = name;
		this.lock = new ProcessLock();
		this.analyticsInstance = analyticsInstance;

		if (!getItem(this.name)) {
			setItem(this.name, []);
		}

		this.addItem = this.addItem.bind(this);
	}

	/**
	 * Adds an item to the queue.
	 *
	 * @param {AnalyticsMessage} item
	 * @returns {Promise}
	 */
	addItem(item) {
		this._enqueue(item);

		verifyStorageLimitForKey(this.name, this.maxSize);
	}

	/**
	 * Remove an item from the queue by id.
	 *
	 * @param {string} id - The Message ID.
	 */
	_dequeue(id) {
		const queue = this.getItems();

		setItem(
			this.name,
			queue.filter(({id: idMessage, item}) => {
				if (item) {
					return item.id !== id;
				}

				return id !== idMessage;
			})
		);
	}

	/**
	 * Add a message to the queue and process messages.
	 *
	 * @param {Message} entry
	 * @param {boolean} - Whether _processMessages should run immediately after enqueuing message.
	 */
	_enqueue(entry) {
		const queue = this.getItems();

		queue.push(entry);

		setItem(this.name, queue);
	}

	/**
	 * Get queued messages.
	 *
	 * @returns {Array.<AnalyticsMessage>}
	 */
	getItems() {
		return getItem(this.name) || [];
	}

	hasItems() {
		return !!this.getItems().length;
	}

	acquireLock() {
		return this.lock.acquireLock(this.name);
	}

	releaseLock() {
		return this.lock.releaseLock(this.name);
	}

	reset() {
		setItem(this.name, []);
	}

	shouldFlush() {
		if (this.analyticsInstance._isTrackingDisabled()) {
			this.analyticsInstance._disposeInternal();

			return false;
		}
		else {
			return true;
		}
	}

	onFlush() {
		throw Error('onFlush should be implemented on the new class');
	}

	onFlushFail() {}

	onFlushSuccess() {}
}

export {BaseQueue, getRetryDelay};
export default BaseQueue;
