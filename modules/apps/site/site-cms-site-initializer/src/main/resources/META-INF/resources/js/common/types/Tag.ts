/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type Tag = {
	actions: Actions;
	assetLibraries: string[];
	creator: {
		additionalName: string;
		contentType: string;
		externalReferenceCode: string;
		familyName: string;
		givenName: string;
		id: number;
		name: string;
		profileURL: string;
	};
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	keywordUsageCount: number;
	name: string;
	siteExternalReferenceCode: string;
	siteId: number;
	subscribed: boolean;
};
