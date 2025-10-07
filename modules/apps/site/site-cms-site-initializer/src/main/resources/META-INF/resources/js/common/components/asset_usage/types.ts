/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MimeTypes} from '../AssetIcon';

export type BulkActionItem = {
	attributes: {
		deletionType: 'RECYCLE_BIN' | 'PERMANENT_DELETION';
		itemsCount?: number;
		mimeType: MimeTypes;
		type: 'ASSET' | 'FOLDER';
		usages: 0;
	};
	classExternalReferenceCode: string;
	className: string;
	classPK: number;
	name: string;
};

export type BulkActionItemResponse = {
	items: BulkActionItem[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
};
