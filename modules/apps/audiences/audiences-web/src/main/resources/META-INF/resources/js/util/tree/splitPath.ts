/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function splitPath(path: number[]): [number[], number] {
	if (!path.length) {
		throw new Error('Path cannot be empty');
	}

	return [path.slice(0, -1), path[path.length - 1]];
}
