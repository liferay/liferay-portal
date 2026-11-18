/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {StickerColor} from '../utils/stickerUtil';

export type LogoColor = StickerColor;

export type MapProviderKey = 'GoogleMaps' | 'OpenStreetMap';

export type Space = {
	assetLibraryKey: string;
	creatorUserId: string;
	description: string;
	externalReferenceCode: string;
	friendlyURL: string;
	id: number;
	name: string;
	settings?: SpaceSettings;
	siteId: number;
};

export type SpaceSettings = {
	availableLanguageIds?: string[];
	defaultLanguageId?: string;
	googleMapsAPIKey?: string;
	logoColor?: LogoColor;
	mapProviderKey?: MapProviderKey;
	sharingEnabled?: boolean;
	trashEnabled?: boolean;
	trashEntriesMaxAge?: number;
	useCustomLanguages?: boolean;
};

export type LabelValueObject = {
	label: string;
	value: string;
};
