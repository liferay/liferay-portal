/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {config} from '../config';

export function itemSelectorValueFromFragmentCollection(value) {
	const url = new URL(config.fragmentCollectionPreviewURL);

	url.searchParams.set(
		`${config.namespace}fragmentCollectionKey`,
		value.fragmentCollectionKey
	);
	url.searchParams.set(`${config.namespace}groupId`, value.groupId);

	return {
		fragmentCollectionKey: value.fragmentCollectionKey,
		groupId: value.groupId,
		name: value.name,
		private: false,
		url: url.toString(),
	};
}
