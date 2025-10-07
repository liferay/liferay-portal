/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type LogoColor =
	| 'outline-0'
	| 'outline-1'
	| 'outline-2'
	| 'outline-3'
	| 'outline-4'
	| 'outline-5'
	| 'outline-6'
	| 'outline-7'
	| 'outline-8'
	| 'outline-9';

export type MimeTypeLimit = {
	id?: string;
	maximumSize: number | '';
	mimeType: string;
};

export type Space = {
	creatorUserId: string;
	description: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	settings?: SpaceSettings;
};

export type SpaceSettings = {
	availableLanguageIds?: string[];
	defaultLanguageId?: string;
	logoColor?: LogoColor;
	mimeTypeLimits?: MimeTypeLimit[];
	sharingEnabled?: boolean;
	trashEnabled?: boolean;
	trashEntriesMaxAge?: number;
	useCustomLanguages?: boolean;
};

export type LabelValueObject = {
	label: string;
	value: string;
};
