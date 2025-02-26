/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import ClientAdapter from '../clientAdapter';
import BaseQueue from './baseQueue';

class BaseSendMessageQueue extends BaseQueue {
	clientAdapter: ClientAdapter;

	constructor({
		analyticsInstance,
		flushTo,
		name,
	}: {
		analyticsInstance: Analytics;
		flushTo: string;
		name: string;
	}) {
		super({analyticsInstance, name});

		this.clientAdapter = new ClientAdapter({
			endpointUrl: flushTo,
			projectId: analyticsInstance.config.projectId,
		});
	}

	onFlush() {
		return this.getItems().map((message: {id: string}) =>
			this.clientAdapter
				.sendWithTimeout(message)
				.then(() => {
					this._dequeue(message.id);
				})
				.catch((error: {status: number}) => {
					if (error.status === 400) {
						this._dequeue(message.id);
					}

					return Promise.reject(error);
				})
		);
	}
}

export {BaseSendMessageQueue};
export default BaseSendMessageQueue;
