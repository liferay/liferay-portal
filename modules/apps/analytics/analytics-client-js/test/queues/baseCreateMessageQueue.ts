/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../../src/analytics';
import BaseCreateMessageQueue from '../../src/queues/baseCreateMessageQueue';
import BaseQueue from '../../src/queues/baseQueue';
import {STORAGE_KEY_CONTEXTS} from '../../src/utils/constants';
import {setItem} from '../../src/utils/storage';
import {INITIAL_ANALYTICS_CONFIG, getDummyEvent} from '../helpers';

type Context = {
	channelId?: string;
	page?: string;
};

type Message = {
	channelId?: string;
	context: Context;
	dataSourceId: string;
	userId: string;
};

const analyticsInstance = Analytics.create(INITIAL_ANALYTICS_CONFIG);
const flushTo = 'TEST_STORAGE_MESSAGE';

describe('BaseCreateMessageQueue', () => {
	let baseCreateMessageQueue: BaseCreateMessageQueue;

	afterEach(() => {
		baseCreateMessageQueue.reset();
	});

	beforeEach(() => {
		analyticsInstance[flushTo] = new BaseQueue({
			analyticsInstance,
			name: 'flushToQueue',
		});

		baseCreateMessageQueue = new BaseCreateMessageQueue({
			analyticsInstance,
			flushTo,
			name: 'BaseCreateMessageQueue',
		});
	});

	it('Create Message', async () => {
		await baseCreateMessageQueue.addItem(
			getDummyEvent(1, {
				contextHash: 'context',
			})
		);

		const message: Message = baseCreateMessageQueue._createMessage({
			context: {channelId: '4321', page: 'Test Page'},
			events: [
				getDummyEvent(1, {
					contextHash: 'context',
				}),
			],
			userId: 'userIdTest',
		});

		expect(message).toHaveProperty('channelId', '4321');
		expect(message).toHaveProperty('context', {
			page: 'Test Page',
		});
		expect(message).toHaveProperty('dataSourceId', '1234');
		expect(message).toHaveProperty('userId', 'userIdTest');
	});

	it('On Flush', async () => {
		expect(baseCreateMessageQueue.getItems().length).toEqual(0);

		setItem(STORAGE_KEY_CONTEXTS, [['context', {}]]);

		await baseCreateMessageQueue.addItem(
			getDummyEvent(1, {
				contextHash: 'context',
			})
		);

		const messages: Message[] = await Promise.all(
			baseCreateMessageQueue.onFlush()
		);

		expect(messages.length).toEqual(1);
	});

	it('On Flush should return messages grouped by contexts', async () => {
		expect(baseCreateMessageQueue.getItems().length).toEqual(0);

		setItem(STORAGE_KEY_CONTEXTS, [
			['context', {}],
			['context2', {}],
		]);

		await baseCreateMessageQueue.addItem(
			getDummyEvent(1, {
				contextHash: 'context',
			})
		);

		await baseCreateMessageQueue.addItem(
			getDummyEvent(2, {
				contextHash: 'context2',
			})
		);

		const messages: Message[] = await Promise.all(
			baseCreateMessageQueue.onFlush()
		);

		expect(messages.length).toEqual(2);
	});
});
