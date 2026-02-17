/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface IRoom {
	actions: {
		[action: string]: {
			href: string;
			method: string | 'DELETE' | 'GET' | 'PATCH' | 'POST' | 'PUT';
		};
	};
	dateCreated: string;
	dateModified: string;
	embedded: IRoomObjectEntry;
	entryClassName: string;
	score: number;
}

export interface IRoomObjectEntry {
	actions: any;
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
	description: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	r_accountToDSRRooms_accountEntry: {
		description: string;
		externalReferenceCode: string;
		id: number;
		logoId: number;
		logoURL: string;
		name: string;
	};
	r_accountToDSRRooms_accountEntryId: number;
	siteId: number;
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
}
