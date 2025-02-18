/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AJAX from '../../utilities/AJAX/index';

const RETURNS_PATH = '/o/commerce/returns';

function resolveReturnsPath(returnId) {
	return `${RETURNS_PATH}/${returnId}`;
}

export default function Return() {
	return {
		createItem: (jsonProps) => AJAX.POST(RETURNS_PATH, jsonProps),

		deleteItemById: (itemId) => AJAX.DELETE(resolveReturnsPath(itemId)),

		getItemById: (itemId) => AJAX.GET(resolveReturnsPath(itemId)),

		updateItemById: (itemId, jsonProps) =>
			AJAX.PATCH(resolveReturnsPath(itemId), jsonProps),
	};
}
