/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function itemSelectorValueFromLayout(value) {
	const url = new URL(value.previewURL);

	url.searchParams.set('styleBookEntryPreview', 'true');

	const id = value.plid ?? value.layoutPageTemplateEntryId;

	return {
		id: id === undefined || id === null ? undefined : String(id),
		name: value.name,
		private: value.private,
		url: url.toString(),
	};
}
