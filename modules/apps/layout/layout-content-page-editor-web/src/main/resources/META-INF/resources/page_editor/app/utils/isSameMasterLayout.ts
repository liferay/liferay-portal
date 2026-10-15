/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type MasterLayout = {
	masterLayoutPageTemplateEntryERC: string;
	masterLayoutPageTemplateEntryScopeERC?: string;
};

export default function isSameMasterLayout(
	masterLayout1: MasterLayout,
	masterLayout2: MasterLayout
) {
	return (
		masterLayout1.masterLayoutPageTemplateEntryERC ===
			masterLayout2.masterLayoutPageTemplateEntryERC &&
		(masterLayout1.masterLayoutPageTemplateEntryScopeERC || '') ===
			(masterLayout2.masterLayoutPageTemplateEntryScopeERC || '')
	);
}
