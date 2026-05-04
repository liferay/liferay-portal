/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface ItemData {
	actions: {
		copy: Action;
		'copy-replace': Action;
		delete: Action;
		duplicate: Action;
		expire: Action;
		get: Action;
		'get-by-scope': Action;
		move: Action;
		'move-replace': Action;
		replace: Action;
		restore: Action;
		update: Action;
	};
	embedded: {
		content: string;
		content_i18n: {[locale: string]: string};
		creator: {
			contentType: string;
			id: number;
			image?: string;
			name: string;
		};
		defaultLanguageId: string;
		externalReferenceCode: string;
		file?: any;
		id: number;
		keywords?: string[];
		objectEntryFolderExternalReferenceCode?: string;
		objectEntryFolderId: number;
		parentObjectEntryFolderExternalReferenceCode?: string;
		scopeId: number;
		systemProperties?: any;
		title: string;
		title_i18n: {[locale: string]: string};
	};
	entryClassName: string;
	id: number;
	title: string;
}
