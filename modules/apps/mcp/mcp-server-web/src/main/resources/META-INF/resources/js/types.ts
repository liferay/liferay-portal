/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type DataMaskTypeKey = 'system' | 'custom';

export type DataMaskPicklistValue = {
	key: DataMaskTypeKey;
	name: string;
};

export type DataMask = {
	dateModified?: string;
	description?: string;
	detectionRegex: string;
	externalReferenceCode?: string;
	id?: number;
	maskType: DataMaskPicklistValue;
	name: string;
	replacementRegex?: string;
	replacementValue: string;
};

export type DataMaskPayload = {
	description: string;
	detectionRegex: string;
	maskType: {key: DataMaskTypeKey};
	name: string;
	replacementRegex: string;
	replacementValue: string;
};

export type ActionContext = {
	itemData: DataMask;
	loadData: () => void;
};
