/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type BoxType = 'categorization' | 'regular' | 'seo';

export type TObjectLayout = {
	defaultObjectLayout: boolean;
	name: LocalizedValue<string>;
	objectDefinitionExternalReferenceCode: string;
	objectLayoutTabs: TObjectLayoutTab[];
};

export type TObjectLayoutTab = {
	name: LocalizedValue<string>;
	objectLayoutBoxes: TObjectLayoutBox[];
	objectRelationshipId: number;
	priority: number;
};

export type TObjectLayoutBox = {
	collapsable: boolean;
	name: LocalizedValue<string>;
	objectLayoutRows: TObjectLayoutRow[];
	priority: number;
	type: BoxType;
};

export type TObjectLayoutRow = {
	objectLayoutColumns: TObjectLayoutColumn[];
	priority: number;
};

export type TObjectLayoutColumn = {
	objectFieldName: string;
	priority: number;
	size: number;
};

export interface TObjectField extends ObjectField {
	inLayout?: boolean;
}

export interface TObjectRelationship extends ObjectRelationship {
	inLayout?: boolean;
}
