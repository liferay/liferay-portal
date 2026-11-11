/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export default function PagesTree(props: {
	config: {
		isLayoutSetPrototype: boolean;
		loadMoreItemsURL: string;
		maxPageSize: number;
		moveItemURL: string;
		namespace: string;
		stagingEnabled: boolean;
	};
	isPrivateLayoutsTree: boolean;
	items: Array<unknown>;
	selectedLayoutId: number;
	selectedLayoutPath: Array<number>;
}): React.ReactElement;
