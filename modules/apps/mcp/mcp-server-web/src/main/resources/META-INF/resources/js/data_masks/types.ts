/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type DataMaskTypeKey = 'system' | 'custom';

export interface DataMaskPicklistValue {
	key: DataMaskTypeKey;
	name: string;
}

export interface DataMask {
	dateModified?: string;
	description?: string;
	detectionRegex: string;
	externalReferenceCode?: string;
	id?: number;
	maskType: DataMaskPicklistValue;
	name: string;
	replacementRegex?: string;
	replacementValue: string;
}

export type Mode =
	| {kind: 'list'}
	| {dataMask: DataMask | null; kind: 'form'; readOnly?: boolean};

export interface ActionContext {
	itemData: DataMask;
	loadData: () => void;
}
