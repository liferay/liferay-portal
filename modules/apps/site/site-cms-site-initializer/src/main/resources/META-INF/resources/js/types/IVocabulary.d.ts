/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface IVocabulary {
	actions?: Actions;
	assetLibraries: AssetLibraryType[];
	assetTypes: AssetType[];
	description?: string;
	description_i18n?: {
		[key: string]: string;
	};
	id?: number;
	multiValued: boolean;
	name: string;
	name_i18n: {
		[key: string]: string;
	};
	numberOfCategories?: number;
	siteId?: number;
	visibilityType: 'PUBLIC' | 'INTERNAL';
}
