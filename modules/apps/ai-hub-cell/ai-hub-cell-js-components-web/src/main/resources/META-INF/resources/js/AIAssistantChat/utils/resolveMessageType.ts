/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Message} from '../types';

export type MessageType = 'select-component';

export default function resolveMessageType(item: Message): MessageType | null {
	if (item.component?.type === 'select') {
		return 'select-component';
	}

	return null;
}
