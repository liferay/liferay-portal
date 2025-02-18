/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AJAX from '../../utilities/AJAX/index';

const RETURN_ITEMS_PATH = '/o/commerce/return-items';

function resolveReturnsPath(returnItemId) {
	return `${RETURN_ITEMS_PATH}/${returnItemId}`;
}

export default function ReturnItem() {
	return {
		deleteItemById: (itemId) => AJAX.DELETE(resolveReturnsPath(itemId)),

		getItemById: (itemId) => AJAX.GET(resolveReturnsPath(itemId)),

		updateItemById: (itemId, jsonProps) =>
			AJAX.PATCH(resolveReturnsPath(itemId), jsonProps),
	};
}
