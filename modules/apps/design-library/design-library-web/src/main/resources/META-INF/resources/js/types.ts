/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface ActionItem {
	data: {id: string};
	href?: string;
}

interface Creator {
	additionalName: string;
	contentType: string;
	externalReferenceCode: string;
	familyName: string;
	givenName: string;
	id: number;
	name: string;
}

export interface DesignLibrary {
	actions?: {delete?: {href: string; method: string}};
	assetLibraryKey: string;
	creator: Creator;
	dateModified: string;
	description: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	siteId: number;
}

export interface Site {
	descriptiveName: string;
	externalReferenceCode: string;
	id: string;
	logo: string;
	name: string;
	searchable: boolean;
}
