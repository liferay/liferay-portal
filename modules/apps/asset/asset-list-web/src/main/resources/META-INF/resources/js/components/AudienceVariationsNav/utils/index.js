/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const VARIATIONS_PRIORITY_PARAM_NAME = 'variationsPriority';

const buildItemsPriorityURL = ({items, namespace, url}) => {
	const urlWithPriorityParam = `${url}&${namespace}${VARIATIONS_PRIORITY_PARAM_NAME}=${items
		.map((item) => item.id)
		.join(',')}`;

	return urlWithPriorityParam;
};

export {buildItemsPriorityURL};
