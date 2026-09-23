/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type PropertyType =
	| 'asset-categories'
	| 'asset-tags'
	| 'boolean'
	| 'date'
	| 'date-time'
	| 'decimal'
	| 'integer'
	| 'keyword'
	| 'numeric'
	| 'picklist'
	| 'text';

export interface PropertyOption {
	label: string;
	value: string;
}

export interface FilterProperty {
	classNameId?: number;
	classTypeId?: number;
	label: string;
	name: string;
	options?: PropertyOption[];
	sortable?: boolean;
	type?: PropertyType;
}

export interface FilterPropertyGroup {
	items: FilterProperty[];
	label: string;
}

export interface FilterOperator {
	label: string;
	value: string;
}

export interface FilterCondition {
	classNameId?: number;
	classTypeId?: number;
	id: string;
	operatorName?: string;
	propertyName?: string;
	quantifier?: string;
	value?: string | Array<string | object>;
}

export function getCombinationKey(
	propertyKey: string,
	operatorName: string | undefined,
	quantifier: string | undefined
): string {
	return `${propertyKey}|${operatorName ?? ''}|${quantifier ?? ''}`;
}

export function getPropertyKey(
	classNameId: number | undefined,
	classTypeId: number | undefined,
	name: string | undefined
): string {
	return `${classNameId ?? ''}|${classTypeId ?? ''}|${name ?? ''}`;
}
