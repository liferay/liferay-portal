/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function isRecycleBinRootPage(items: any[]): boolean {
	return (
		items.length === 1 &&
		items[0].label === Liferay.Language.get('recycle-bin')
	);
}
