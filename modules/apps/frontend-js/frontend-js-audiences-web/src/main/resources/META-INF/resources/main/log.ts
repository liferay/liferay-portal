/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {indent} from './util';

const MARKER = '%c@liferay/audiences:';

export function log(...things: any[]): void {
	if (!log.enabled) {
		return;
	}

	// eslint-disable-next-line no-console
	console.log(
		MARKER,
		'color: blue; font-weight: bold;',
		indent(20, false, things.map((thing) => String(thing)).join(' '))
	);
}

log.enabled = false;
