/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type Action = {
	href: string;
	method: string;
};

type Actions = {
	create?: Action;
	createBatch?: Action;
	delete?: Action;
	deleteBatch?: Action;
	get?: Action;
	replace?: Action;
	update?: Action;
	updateBatch?: Action;
}

