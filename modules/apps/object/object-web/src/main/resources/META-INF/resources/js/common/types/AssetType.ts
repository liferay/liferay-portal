/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface IAssetFile {
	alternativeText?: string;
	externalReferenceCode: string;
	id: number;
	link: {
		href: string;
		label: string;
	};
	metadata?: {
		numberOfPages?: number;
	};
	mimeType?: string;
	name: string;
	previewURL: string;
	thumbnailURL: string;
}

export interface IAssetObjectEntry {
	actions: any;
	creator: any;
	dateCreated: string;
	dateModified: string;
	displayDate: string;
	expirationDate: string;
	externalReferenceCode: string;
	file?: IAssetFile;
	friendlyUrlPath: string;
	id: number;
	keywords: string[];
	numberOfObjectEntries: number;
	numberOfObjectEntryFolders: number;
	objectEntryFolderExternalReferenceCode: string;
	objectEntryFolderId: number;
	reviewDate: string;
	scope?: {
		externalReferenceCode: string;
		type: string;
	};
	scopeId: number;
	scopeKey: string;
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
	systemProperties: IAssetVersion;
	taxonomyCategoryBriefs: any[];
	taxonomyCategoryIds?: number[];
	title: string;
	title_i18n: any;
}

export interface IAssetVersion {
	scope?: {
		externalReferenceCode: string;
		type: string;
	};
	version: {
		number: number;
	};
}

export interface IGroupedTaxonomies {
	taxonomyCategoryIds: number[];
	taxonomyVocabularies: {
		[taxonomyVocabularyId: number]: ITaxonomyCategoryFacade[];
	};
}

export interface ISearchAssetTypeInformation {
	externalReferenceCode?: string | null;
	icon?: string | null;
	id?: number | null;
	objectEntryFolderExternalReferenceCode?: string | null;
	title?: string | null;
	title_i18n?:
		| {
				[key: string]: string;
		  }
		| {};
	type?: string | null;
}

export interface ITaxonomyCategoryFacade {
	id: string;
	name?: string;
	parentTaxonomyVocabulary: ITaxonomyVocabulary;
	taxonomyVocabularyId: number;
}

export interface ITaxonomyVocabulary {
	id: number;
	name: string;
}
