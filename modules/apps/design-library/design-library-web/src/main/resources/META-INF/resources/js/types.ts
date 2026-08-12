/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface ActionItem {
	data: {id: string};
	href?: string;
}

export interface ConfirmationMessage {
	bodyHTML: string;
	partialSuccessMessage: string;
	successMessage: string;
	title: string;
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

interface DeleteAction {
	href: string;
	method: string;
}

export interface DesignAsset {
	actions?: EntryActions;
	dateModified: string;
	embedded: {externalReferenceCode: string; name: string};
	entryClassName: string;
}

export interface DesignLibrary {
	actions?: EntryActions;
	assetLibraryKey: string;
	creator: Creator;
	dateModified: string;
	description: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	siteId: number;
}

export interface EntryActions {
	delete?: DeleteAction;
}

export interface Site {
	descriptiveName: string;
	externalReferenceCode: string;
	id: string;
	logo: string;
	name: string;
	searchable: boolean;
}
