/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function parseJSONLToJSON(content: string) {
	const lines = content.split('\n').filter((line) => line.trim() !== '');

	return lines.map((line) => JSON.parse(line));
}
