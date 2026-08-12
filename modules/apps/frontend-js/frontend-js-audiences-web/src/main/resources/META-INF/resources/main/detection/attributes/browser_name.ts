/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UAParser} from 'ua-parser-js';

export function getBrowserName(uaParser: UAParser): string {
	return uaParser.getBrowser().name ?? '';
}
