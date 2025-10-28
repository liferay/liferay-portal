/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

export function getScopeExternalReferenceCode(item: any): string {
	if (item?.entryClassName !== OBJECT_ENTRY_FOLDER_CLASS_NAME) {
		return item.embedded.systemProperties?.scope?.externalReferenceCode;
	}

	return item.embedded.scope?.externalReferenceCode;
}
