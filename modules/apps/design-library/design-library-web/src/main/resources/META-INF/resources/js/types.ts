/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface ActionItem {
	data: {id: string};
	href?: string;
}

export type DesignLibraryCreationItem = {label: string; onClick: () => void};

export type DesignLibraryCreationItemsFactory = (
	props: Record<string, any>
) => DesignLibraryCreationItem[];

export interface DesignLibraryResourceType {
	color: string;
	creationItemsModule?: string;
	creationItemsProps?: Record<string, any>;
	defaultActionId: string;
	entryClassName: string;
	label: string;
	symbol: string;
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
