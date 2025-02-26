/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FLUSH_INTERVAL,
	LIMIT_FAILED_ATTEMPTS,
	QUEUE_PRIORITY_DEFAULT,
} from './utils/constants';
import {getRetryDelay} from './utils/delay';

/**
 * A Queue Config.
 *
 * @typedef {Object} QueueConfig
 * @property {string} endpointUrl - Url to send the items
 * @property {string} name - The name to idetify the queue, it must be unique.
 * @property {number} [priority=1] - Priority of this queue.
 */

/**
 * QueueFlushService is used to organize the queues and set the properly time a queue should send their events.
 * It exposes methods to add and remove queues.
 * Queues are ordered by priority and flushed synchronously. What means if one queue fail the other will not
 * be proccessed and a delay will be added to loop.
 */
class QueueFlushService {
	constructor(config = {}) {
		this.attemptNumber = 1;
		this.initialFlushInterval = config.flushInterval || FLUSH_INTERVAL;
		this.processing = false;
		this.queues = [];

		this.flushInterval = this.initialFlushInterval;

		this._startsFlushLoop();
	}

	/**
	 * Add a queue to be processed by the client
	 * @param {MessageQueue} queueInstance
	 * @param {QueueConfig} config
	 */
	addQueue(queueInstance, config = {}) {
		this.queues.push(
			Object.assign(config, {
				instance: queueInstance,
				name: queueInstance.name,
			})
		);
		this.queues.sort(this._prioritize);
	}

	/**
	 * Remove a queue
	 * @param {string} queueName
	 */
	removeQueue(queueName) {
		this.queues = this.queues.filter(({name}) => queueName !== name);
	}

	/**
	 * Function to order queues by priority
	 * @param {Object} queueA
	 * @param {Object} queueB
	 */
	_prioritize(
		{priority: pA = QUEUE_PRIORITY_DEFAULT},
		{priority: pB = QUEUE_PRIORITY_DEFAULT}
	) {
		return pB - pA;
	}

	/**
	 * Increase the interval time and restart processing loop
	 */
	_onFlushFail() {
		this.flushInterval = getRetryDelay(
			++this.attemptNumber,
			LIMIT_FAILED_ATTEMPTS
		);
		this._startsFlushLoop();
	}

	/**
	 * Reset interval time and restart processing loop
	 */
	_onFlushSuccess() {
		this.attemptNumber = 1;

		if (this.flushInterval !== this.initialFlushInterval) {
			this.flushInterval = this.initialFlushInterval;
			this._startsFlushLoop();
		}
	}

	/**
	 * Go through queues and execute their callbacks.
	 * If a queue fail, the processing loop is delayed
	 * and the next queues are not processed.
	 *
	 * Note: Because we are using a ProcessLock, no other process should
	 * be able to acquire a lock for a particular key to run its callback
	 * until the process with the active lock releases it.
	 *
	 */
	flush() {
		if (this.processing) {
			return;
		}

		this.queues
			.reduce((previousPromise, {instance: queue}) => {
				return previousPromise
					.then(() => {
						if (!queue.hasItems() || !queue.shouldFlush()) {
							return Promise.resolve();
						}

						return queue.acquireLock().then((success) => {
							if (!success) {
								return Promise.resolve();
							}

							this.processing = true;

							const releaseLock = () =>
								queue.releaseLock().then(() => {
									this.processing = false;
								});

							return Promise.allSettled(queue.onFlush()).then(
								(result) => {
									this._onFlushSuccess();
									queue.onFlushSuccess(result);
									releaseLock();

									return Promise.resolve();
								}
							);
						});
					})
					.catch(() => {
						this._onFlushFail();
						queue.onFlushFail();

						return Promise.reject();
					});
			}, Promise.resolve())
			.catch(() => {});
	}

	/**
	 * Start a timer to process messages at a specified interval.
	 */
	_startsFlushLoop() {
		if (this.processInterval) {
			clearInterval(this.processInterval);
		}

		this.processInterval = setInterval(
			() => this.flush(),
			this.flushInterval
		);
	}

	/**
	 * Method for clearing queues and any scheduled processing.
	 */
	dispose() {
		if (this.processInterval) {
			clearInterval(this.processInterval);
		}

		this.flushInterval = this.initialFlushInterval;
		this.queues = [];
	}
}

export {QueueFlushService};
export default QueueFlushService;
