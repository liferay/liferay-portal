/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const SLOW_CONNECTION_TYPES = ['slow-2g', '2g', '3g'];

export function isLowEndDevice() {
	const {
		navigator: {
			connection: {
				downlink = 10,
				effectiveType = '4g',
				saveData = false,
			} = {},
			deviceMemory = 8,
		} = {},
	} = window;

	return (
		deviceMemory <= 2 ||
		downlink <= 2 ||
		saveData ||
		SLOW_CONNECTION_TYPES.includes(effectiveType)
	);
}
