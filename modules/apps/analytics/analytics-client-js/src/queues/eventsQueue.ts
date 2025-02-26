/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {STORAGE_KEY_EVENTS, STORAGE_KEY_MESSAGES} from '../utils/constants';
import BaseCreateMessageQueue from './baseCreateMessageQueue';

class EventsQueue extends BaseCreateMessageQueue {
	constructor({analyticsInstance, name = STORAGE_KEY_EVENTS}) {
		super({analyticsInstance, flushTo: STORAGE_KEY_MESSAGES, name});
	}
}

export {EventsQueue};
export default EventsQueue;
