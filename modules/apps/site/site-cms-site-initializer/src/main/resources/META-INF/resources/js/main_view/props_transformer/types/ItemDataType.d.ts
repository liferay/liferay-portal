/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface ItemData {
	actions?: {
		delete: Action;
		expire: Action;
		get: Action;
		replace: Action;
		restore: Action;
		update: Action;
	};
	embedded: {
		content: string;
		creator: {
			contentType: string;
			id: number;
			image?: string;
			name: string;
		};
		externalReferenceCode: string;
		file?: any;
		id: number;
		objectEntryFolderId: number;
		scopeId: number;
		title: string;
	};
	entryClassName: string;
	id: number;
	title: string;
}
