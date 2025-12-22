/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MimeTypes} from '../components/AssetIcon';
import {SharingPermission} from './SharingPermission';

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
	mimeType?: string | MimeTypes;
	name: string;
	previewURL: string;
	thumbnailURL: string;
}

export interface IAssetObjectEntry {
	actions: {
		[action: string]: {
			href: string;
			method: 'DELETE' | 'GET' | 'PATCH' | 'POST' | 'PUT';
		};
	};
	content?: string;
	contentRawText?: string;
	creator: {
		additionalName: string;
		contentType: string;
		externalReferenceCode: string;
		familyName: string;
		givenName: string;
		id: number;
		name: string;
	};
	dateCreated: string;
	dateModified: string;
	displayDate: string;
	expirationDate: string;
	externalReferenceCode: string;
	file?: IAssetFile;
	friendlyUrlPath: string;
	friendlyUrlPath_i18n: {
		[lang: string]: string;
	};
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
	scopeId: number | -1;
	scopeKey: string;
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
	systemProperties: IAssetObjectDefinitionBrief & IAssetScope & IAssetVersion;
	taxonomyCategoryBriefs: ITaxonomyCategoryBrief[];
	taxonomyCategoryIds: number[];
	title: string;
	title_i18n: any;
}

export interface IAssetObjectDefinitionBrief {
	objectDefinitionBrief?: {
		classNameId: number | -1;
		externalReferenceCode?: string;
		label?: string;
		objectFolderExternalReferenceCode?: string;
	};
}

export interface IAssetScope {
	scope?: {
		externalReferenceCode: string;
		type: string;
	};
}

export interface IAssetVersion {
	version: {
		number: number;
	};
}

export interface ISearchAssetObjectEntry {
	actionIds?: SharingPermission[];
	actions: IAssetObjectEntry['actions'];
	dateCreated: string;
	dateModified: string;
	description: string;
	embedded: IAssetObjectEntry;
	entryClassName: string;
	score: number;
	title: string;
}

export interface IGroupedTaxonomies {
	taxonomyCategoryIds: number[];
	taxonomyVocabularies: {
		[taxonomyVocabularyId: number]: ITaxonomyCategoryFacade[];
	};
}

export interface ITaxonomyCategoryFacade {
	id: number | string;
	name?: string;
	parentTaxonomyVocabulary: ITaxonomyVocabulary;
	taxonomyVocabularyId: number;
}

export interface ITaxonomyVocabulary {
	id: number;
	name: string;
}

export interface IAssetTaxonomyCategory {
	taxonomyCategoryId: number;
	taxonomyCategoryName: string;
}

export interface IAssetTaxonomyVocabulary {
	multiValued: boolean;
	name: string;
	required: boolean;
	taxonomyCategories: Array<IAssetTaxonomyCategory>;
	taxonomyVocabularyId: number;
}

export interface ITaxonomyCategoryBrief {
	embeddedTaxonomyCategory: {
		id: number | string;
		name: string;
		parentTaxonomyVocabulary: {
			id: number;
			name: string;
		};
		taxonomyVocabularyId: number;
	};
	taxonomyCategoryId: number;
	taxonomyCategoryName?: string;
}
