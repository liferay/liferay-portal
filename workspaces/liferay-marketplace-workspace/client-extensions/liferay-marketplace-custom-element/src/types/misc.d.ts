/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

declare module '*.svg' {
	const content: any;
	export default content;
}

declare module 'warning';

type ActionMap<M extends {[index: string]: any}> = {
	[Key in keyof M]: M[Key] extends undefined
		? {
				type: Key;
			}
		: {
				payload: M[Key];
				type: Key;
			};
};

type APIResponse<Query = any> = {
	actions: ObjectActions;
	facets: Facets[];
	items: Query[];
	lastPage: number;
	page: number;
	pageSize: number;
	results: Query[];
	totalCount: number;
};

type ContactSales = {
	accountName: string;
	additionalAppsRequested?: string | undefined;
	comments?: string | undefined;
	email: string;
	name: string;
};

type ListTypeDefinition = {
	externalReferenceCode: string;
	id: number;
	listTypeEntries: {
		externalReferenceCode: string;
		key: string;
		name: string;
		name_i18n: {
			[key: string]: string;
		};
	}[];
	name: string;
};

type PublisherRequestInfo = {
	creator: {name: string};
	dateCreated: string;
	emailAddress?: string;
	extension?: string;
	firstName?: string;
	id?: number;
	lastName?: string;
	phone?: {
		code: string;
		flag: string;
	};
	phoneNumber?: string;
	publisherType: string[];
	requestDescription?: string;
	requestStatus?: {
		key: string;
		name: string;
	};
};

type PublisherSalesSummaryEntry = {
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	objectEntryFolderExternalReferenceCode: string;
	objectEntryFolderId: number;
	paidBy: string;
	paidDate: string;
	paymentStatus: {
		key: string;
		name: string;
	};
	publisherName: string;
	publisherToAccountERC: string;
	publisherToCommerceOrder: Order[];
	quarter: string;
	r_accountToPublisher_accountEntryERC: string;
	r_accountToPublisher_accountEntryId: number;
	scopeId: number;
};

type RadioOption<T> = {
	index: number;
	value: T;
};
